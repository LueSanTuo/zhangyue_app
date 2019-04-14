package com.example.asus.zhangyue;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.myutil.BrightnessUtils;
import com.example.asus.zhangyue.myutil.FileOperation;
import com.example.asus.zhangyue.myutil.HttpUtil;
import com.example.asus.zhangyue.myview.BookMarkRecyclerView;
import com.example.asus.zhangyue.myview.ContentRecyclerView;
import com.example.asus.zhangyue.pageflip.Page;
import com.example.asus.zhangyue.pageflip.PageFactory;
import com.example.asus.zhangyue.pageflip.PageFlipView;
import com.example.asus.zhangyue.pageflip.PageRender;
import com.example.asus.zhangyue.popupwin.CommonPopupWindow;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/** 阅读界面 所有阅读功能都在里面 */
public class ReadingActivity extends AppCompatActivity implements OnGestureListener {
    public final static String CHAPTER_PREFIX = "chapter";

    private PageFlipView mPageFlipView;
    private GestureDetector mGestureDetector;

    private View activityPopup;
    /** 默认弹窗 */
    private CommonPopupWindow window;
    /** 进程弹窗 */
    private CommonPopupWindow processWin;

    private int screenHeight;
    private int screenWidth;
    /** 判断弹窗是否出现 */
    private boolean isPopup = false;
    /** 判断进度弹窗是否出现 */
    private boolean isProPopup = false;

    /** 进度弹窗里显示章节名称 */
    TextView chapterName;
    /** 进度弹窗里显示当前进度 */
    TextView chapterProcess;
    /** 章节阅读的当前进度值 */
    int chapterProValue;

    /** 亮度调节弹窗 */
    private CommonPopupWindow brightWin;
    /** 当前亮度值 */
    private int mBrightness;
    /** 是否为系统亮度 */
    private boolean isSysBright = false;

    /** 夜间模式 */
    private boolean isNightMode;
    /** 夜间图片 */
    private ImageView imgNight;
    /** 夜间提示 */
    private TextView txtNight;

    /** 目录弹窗 */
    private CommonPopupWindow contentWin;
    /** 设置弹窗 */
    private CommonPopupWindow setWin;
    /** 当前背景颜色字体模式 */
    private int curPaintMode = 0;

    /** 等待弹窗 */
    private CommonPopupWindow waitWin;

    /** 当前书籍id */
    String curBookId;
    /** 当前章节id */
    int curChapterId;
    /** 章节文件目录 */
    List<String> chapterFileNameContent;
    /** 章节标题目录 */
    List<String> chapterContent;
    /** 本地阅读 */
    boolean isLocalReading;
    /** 书名 */
    String bookName;
    /** 存取用章节进度 */
    float chapterReadProcess;

    // region Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 进入这个界面应由上个活动传送数据到此
        // 传入的数据为某本书
        Intent intent = getIntent();
        curBookId = intent.getStringExtra("bookId");
        // 书名应该在之前的文档中传入
        //  读取用户本机的存储的本书阅读的上一次记录
        curChapterId = intent.getIntExtra("chapterId", 1);
        bookName = intent.getStringExtra("bookName");
        chapterReadProcess = intent.getFloatExtra("process", 0f);
        getBookContent(curBookId);
        //initBook (curBookId, curChapterId);
        mPageFlipView = new PageFlipView(this);
        setContentView(mPageFlipView);
        mGestureDetector = new GestureDetector(this, this);
        setWindowPreference ();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        initPopupWindow();
        if (chapterReadProcess != 0f) {
            //gotoPageByProcess(curChapterId, chapterReadProcess);
        }
        //
        showWaitWin ();
    }

    @Override
    protected void onStart() {
        super.onStart();
        User.get().recordStartReadingTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        User.get().settleEndReadingTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageFlipView.onResume();
    }

    @Override
    protected void onPause() {
        addReadRecord();
        User.get().saveUserData(this);
        super.onPause();
        mPageFlipView.onPause();
    }

    @Override
    protected void onDestroy() {
        addReadRecord();
        System.out.println("ReadingActivity:onDestroy");
        User.get().saveUserData(this);
        super.onDestroy();
    }

    /** 添加阅读记录 */
    private void addReadRecord () {
        // 获得当前页
        Page page = PageFactory.get().getPage(mPageFlipView.getPageNo() - 1);
        if (page == null) {
            return;
        }
        // 获得第一行
        String firstLine = page.getLine(0);
        // 获得当前进度
        float process = page.getLineRate();
        // 添加阅读记录
        User.get().addReadHistory(curBookId, curChapterId, process);
    }

    // endregion

    // region Reading
    /** 获取当前要加载的书 */
    private void initBook (String bookId, int chapterId) {
        PageFactory pf = PageFactory.get();
        String content;
        // 判断该书是否下载到本地，如果下载到本地则判断该书的该章节是否在本地
        // 读取本地数据
        if (FileOperation.isExistsBook(bookId)) {
            String chapterName;
            //if (chapterFileNameContent != null && chapterFileNameContent.size() >= chapterId) {
            //    chapterName = chapterFileNameContent.get(chapterId - 1);
           // } else {
                chapterName = CHAPTER_PREFIX + chapterId;
           // }
            content = FileOperation.loadBook(bookId, chapterName);
            System.out.println("ReadingActivity:-----chapterId------" + chapterId + "-----chapterName------" + chapterName);
            if (content.equals("") || content == null) {
                // 没有此章节
                pf.chapterName = "";
                pf.pageContentStr = " ";
                Toast.makeText(ReadingActivity.this, "本地内容获取失败", Toast.LENGTH_SHORT).show();
            } else {
                // 本地阅读
                isLocalReading = true;
                curChapterId = chapterId;
                pf.pageContentStr = content;
                pf.setChapterName();
                return;
            }
        } else {
            pf.chapterName = "";
            pf.pageContentStr = "";
            Toast.makeText(ReadingActivity.this, "本地没有此书", Toast.LENGTH_SHORT).show();
        }
        // 超出本地存放的章节文件数那么连接网络获取内容
        // 否则读取网络数据
        getBookFromNet (bookId, chapterId);
    }



    /** 获取网络书籍内容 */
    private void getBookFromNet (String bookId, final int chapterId) {
        // 显示等待窗口
        showWaitWin ();
        RequestBody requestBody = new FormBody.Builder().add("bookId", bookId).add("chapterId", "" + chapterId).build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_LOAD_BOOK, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                connectFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                FileOperation.writeBook(content, curBookId, chapterId);
                setBookChpater(content, chapterId);
            }
        }, requestBody);

    }

    /** 连接成功后设置当前章节获得的内容 */
    private void setBookChpater(final String response, final int chapterId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                PageFactory pf = PageFactory.get();
                pf.pageContentStr = response;
                pf.setChapterName();
                isLocalReading = false;
                curChapterId = chapterId;
                // 刷新
                mPageFlipView.refresh();
                // hideWaitWin();

            }
        });
    }

    /** 获取书名 */
    private String getBookName () {
        String name = "" + curBookId;
        return name;
    }

    /** 获取书本的目录 */
    private void getBookContent(final String bookId) {
        chapterFileNameContent = new ArrayList<>();
        // 获取本地目录文件？？
        chapterContent = FileOperation.loadBookContent(bookId, chapterFileNameContent);

        //chapterContent = new ArrayList<>();
        // 再从网络下载目录覆盖
        RequestBody requestBody = new FormBody.Builder().add("bookId", bookId).build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_LOAD_BOOK_CONTENT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                connectFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                setBookNetContent(response.body().string());
            }
        }, requestBody);
    }

    /** 网络连接失败 */
    private void connectFailed () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ReadingActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 连接成功后设置当前书籍的目录 */
    private void setBookNetContent(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (content == null || content.equals(""))
                    return;
                String[] temp = content.split("\n");
                chapterContent = new ArrayList<String>();
                for (String ss : temp) {
                    String sss = ss.trim();
                    if (!sss.equals("\n") && !sss.equals(""))
                        chapterContent.add(sss);
                }
                contentWin.refresh();
                //Toast.makeText(ReadingActivity.this, bookName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 添加书签 */
    private void addBookMark () {
        // 获得当前页
        Page page = PageFactory.get().getPage(mPageFlipView.getPageNo() - 1);
        if (page == null)
            return;
        // 获得第一行
        String firstLine = page.getLine(0);
        // 获得当前进度
        float process = page.getLineRate();
        // 获得时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = sdf.format(new Date());
        User.get().addBookMark(curBookId, curChapterId, firstLine, process, date);
    }

    /** 删除多个书签
     * @param bms 指的是书签们 */
    public void removeBookMarks (List<User.BookMark> bms) {
        User.get().removeBookMarks(bms);
    }

    /** 根据书签跳转到某一页
     * @param bookMask 指的是书签实例
     * */
    public void gotoPageByBookMark (BookMarkRecyclerView.Item bookMask) {
        // 根据书签上的章节id和进度换算当前页的位置
        initBook(curBookId, bookMask.chapterId);
        PageFactory pf = PageFactory.get();
        pf.divPage();
        int pageNo = (int)(bookMask.process * pf.getPagesRealSize());
        mPageFlipView.gotoPage(pageNo + 1);
        contentWin.getPopupWindow().dismiss();
    }

    /** 根据章节和进度跳转到某一页 */
    public void gotoPageByProcess (int chapterId, float process) {
        // 根据书签上的章节id和进度换算当前页的位置
        initBook(curBookId, chapterId);
        PageFactory pf = PageFactory.get();
        pf.divPage();
        int pageNo = (int)(process * pf.getPagesRealSize());
        mPageFlipView.gotoPage(pageNo + 1);
        contentWin.getPopupWindow().dismiss();
    }

    /** 根据书签列表判断当前页是否存在书签 存在返回所有匹配书签 */
    public List<User.BookMark> getCurPageBookMark () {
        List<User.BookMark> bookMarks = new ArrayList<>();
        List<User.BookMark> org = User.get().getBookMarkList();
        PageFactory pf = PageFactory.get();
        for (User.BookMark bm : org) {
            // 不符合的书签排除
            if (!bm.bookId.equals(curBookId))
                continue;
            // 书id和章节号相同的情况下
            if (bm.chapterId != curChapterId)
                continue;
            // 计算是否是当前页
            int pageNo = (int)(bm.process * pf.getPagesRealSize());
            if (mPageFlipView.getPageNo() == pageNo + 1)
                bookMarks.add(bm);
        }
        return bookMarks;
    }

    /** 跳转章节 */
    public void gotoChapter (int index) {
        // 判断下标是否越界
        if (chapterContent.size() <= index || index <= 0) {
            return;
        }
        initBook(curBookId, index);
        // 刷新界面
        mPageFlipView.refreshChapter();
        contentWin.getPopupWindow().dismiss();
    }

    /** 上一章 */
    private void preChapter () {
        // 判断当前章节数是否满足条件
        if (curChapterId <= 1) {
            // 无法去上一章
            Toast.makeText(this, "已经到顶了", Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果是本地阅读的话 直接获取
        if (isLocalReading) {
            int oldId = curChapterId;
            initBook(curBookId, curChapterId - 1);
            if (oldId != curChapterId)
                mPageFlipView.refreshChapter();
            return;
        }
        // 如果章节数在范围内则去往上一章
        initBook(curBookId, curChapterId - 1);
        // 刷新界面
        mPageFlipView.refreshChapter();
        window.getPopupWindow().dismiss();
    }

    /** 下一章 */
    private void nextChapter () {
        // 如果是本地阅读的话 直接获取
        if (isLocalReading) {
            int oldId = curChapterId;
            initBook(curBookId, curChapterId + 1);
            if (oldId != curChapterId)
                mPageFlipView.refreshChapter();
            return;
        }
        // 从数据库里获取该书的章节数
        int maxChapterCount = chapterContent.size();
        // 判断章节数是否满足条件
        if (curChapterId >= maxChapterCount) {
            // 无法去下一章
            Toast.makeText(this, "已经到底了", Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果章节数在范围内则去往下一章
        initBook(curBookId, curChapterId + 1);
        // 刷新界面
        mPageFlipView.refreshChapter();
        window.getPopupWindow().dismiss();
    }

    /** 设置夜间模式 */
    private void setNightMode () {
        isNightMode = !isNightMode;
        PageRender.IS_NIGHT_MODE = isNightMode;
        mPageFlipView.refreshPaintColor();
        if (imgNight != null) {
            if (!isNightMode)
                imgNight.setImageResource(R.drawable.icon_3);
            else
                imgNight.setImageResource(R.drawable.icon_4);
        }
        if (txtNight != null) {
            if (!isNightMode)
                txtNight.setText("夜间");
            else
                txtNight.setText("日间");
        }
        contentWin.refresh();
        Toast.makeText(this, "夜间模式" + (isNightMode? "开启" : "关闭"), Toast.LENGTH_SHORT).show();
    }

    /** 缩放字体
     * @param delta 负数表示缩小 正数表示增大
     * */
    private void zoomFontSize (int delta) {
        int curSize = PageRender.FONT_SIZE;
        curSize = Math.max(10, Math.min(curSize + delta, 60));
        PageRender.FONT_SIZE = curSize;
        mPageFlipView.refresh();
        Toast.makeText(this, "字体更改", Toast.LENGTH_SHORT).show();
    }

    /** 改变背景及字体颜色 */
    private void setPaintMode (int mode) {
        switch (mode) {
            case 0:
                PageRender.BACKGROUND_ID = 0;
                PageRender.FONT_COLOR = Color.WHITE;
                break;
            case 1:
                PageRender.BACKGROUND_ID = 1;
                PageRender.FONT_COLOR = Color.rgb(69, 60, 53);
                break;
            case 2:
                PageRender.BACKGROUND_ID = 2;
                PageRender.FONT_COLOR = Color.BLACK;
                break;
            case 3:
                PageRender.BACKGROUND_ID = 3;
                PageRender.FONT_COLOR = Color.BLACK;
                break;
            default:
                return;
        }
        curPaintMode = mode;
        mPageFlipView.refreshPaintColor();
    }

    /** 设置窗体属性 */
    private void setWindowPreference () {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mPageFlipView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
    // endregion


    /** 点击触碰事件 用于处理翻书动画 */
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isPopup) {
                return false;
            } else {
                mPageFlipView.onFingerUp(event.getX(), event.getY());
                return true; // 不执行了
            }
        }

        return mGestureDetector.onTouchEvent(event);
    }

    // region OnGesture
    @Override
    public boolean onDown(MotionEvent e) {
        // 判断是否点击在屏幕中间
        if ((e.getX() < screenWidth * 0.55f) && (e.getX() > screenWidth * 0.45f)) {
            //Toast.makeText(this, "中心", Toast.LENGTH_SHORT).show();
            onClick();
            return false;
        }
        mPageFlipView.onFingerDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //Toast.makeText(this, "长按", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mPageFlipView.onFingerMove(e2.getX(), e2.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    // endregion

    /** 点击屏幕中部 弹出默认弹窗*/
    private void onClick () {
        PopupWindow win = window.getPopupWindow();
        //PopupWindow tle = tittle.getPopupWindow();
        win.setAnimationStyle(R.style.animTranslate);
        //tle.setAnimationStyle(R.style.animTranslateG);
        window.showAtLocation(mPageFlipView, Gravity.BOTTOM, 0, 0);
        //window.setFocusable(false);
        isPopup = true;
        //tittle.showAtLocation(mPageFlipView, Gravity.TOP, 0, 0);
        //tittle.setFocusable(false);
        //tittle.setOutsideTouchable(false);
        //setWindowPreference ();
    }

    /** 显示等待窗口 */
    private void showWaitWin () {
        Intent intent = new Intent(ReadingActivity.this, WaitForLoadingActivity.class);
        intent.putExtra("bookId", curBookId);
        intent.putExtra("chapterId", curChapterId);
        intent.putExtra("bookName", bookName);
        intent.putExtra("process", chapterReadProcess);
        startActivity(intent);
        /*
        PopupWindow win = waitWin.getPopupWindow();
        win.setAnimationStyle(R.style.animTranslate);
        waitWin.showAtLocation(mPageFlipView, Gravity.BOTTOM, 0, 0);
        waitWin.setOutsideTouchable(false);*/
    }

    /** 关闭等待窗口 */
    private void hideWaitWin () {
        waitWin.getPopupWindow().dismiss();
    }

    /** 初始化弹窗 */
    private void initPopupWindow() {
        // 获取屏幕的高度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int screenHeight = metrics.heightPixels;
        // 等待窗口
        waitWin = new CommonPopupWindow(this, R.layout.activity_wait_for_loading, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) {
            @Override
            protected void initEvent() {

            }

            @Override
            protected void initView() {

            }

            @Override
            protected void initWindow() {
                super.initWindow();
            }
        };
        // 帮助弹窗初始化
        window = new CommonPopupWindow(this, R.layout.help_popup_win, ViewGroup.LayoutParams.MATCH_PARENT, (int)(screenHeight * 0.5)) {

            /** 章节进度 */
            SeekBar seekBar;
            /** 上一章 */
            View btnPreChpt;
            /** 下一章 */
            View btnNextChpt;
            /** 目录 */
            View btnContent;
            /** 亮度 */
            View btnLight;
            /** 夜间 */
            View btnNight;
            /** 设置 */
            View btnSet;
            /** 书签按钮 */
            TextView btnBookMark;
            /** 当前页书签 */
            List<User.BookMark> curPageBookMark;

            /** 设置当前页书签 */
            public void setCurPageBookMark () {
                curPageBookMark = getCurPageBookMark();
                if (btnBookMark != null) {
                    if (curPageBookMark.size() > 0) {
                        btnBookMark.setText("删除书签");
                    } else {
                        btnBookMark.setText("添加书签");
                    }
                }
            }

            @Override
            protected void initView() {
                View view = getContentView();

                TextView btn = (TextView)view.findViewById(R.id.refreshBtn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.getPopupWindow().dismiss();
                        finish();
                    }
                });
                seekBar = (SeekBar)view.findViewById(R.id.seekBar);
                btnPreChpt = (View)view.findViewById(R.id.btnPreChpt);
                btnNextChpt = (View)view.findViewById(R.id.btnNextChpt);
                btnLight = (View)view.findViewById(R.id.btnLight);
                btnNight = (View)view.findViewById(R.id.btnNight);
                btnContent = (View)view.findViewById(R.id.btnContent);
                btnSet = (View)view.findViewById(R.id.btnSet);
                btnBookMark = (TextView)view.findViewById(R.id.btnBookMark);
                txtNight = (TextView)view.findViewById(R.id.txtNight);
                imgNight = (ImageView)view.findViewById(R.id.imgNight);
            }

            @Override
            protected void initEvent() {
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        // 进度发生变化时更改popupwin里的值
                        chapterProValue = seekBar.getProgress();
                        chapterProcess.setText(String.format("%d%%", chapterProValue));
                        chapterName.setText(PageFactory.get().chapterName);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 开始拖动时 显示processWin
                        // 如果已经显示 那么就不处理
                        if (isProPopup)
                            return;
                        isProPopup = true;
                        window.getPopupWindow().setAnimationStyle(R.style.animAlpha);
                        processWin.showAtLocation(mPageFlipView, Gravity.TOP, 0, (int)(screenHeight * 0.5f));
                        processWin.setTouchable(false);
                        processWin.setFocusable(false);
                        processWin.setOutsideTouchable(false);
                        chapterProcess.setText(String.format("%d%%", chapterProValue));
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 停止拖动 计算当前值是否存在页
                        // 如果存在则进行跳转
                        PageFactory pf = PageFactory.get();
                        int newPage = (int)(pf.getPagesRealSize() * (chapterProValue / 100.f));
                        Page p = pf.getPage(newPage);
                        if (p == null){
                            chapterProValue = (int)pf.getPage(mPageFlipView.getPageNo() - 1).getRate();
                        } else {
                            chapterProValue = (int)p.getRate();
                        }
                        seekBar.setProgress(chapterProValue);
                        mPageFlipView.gotoPage(newPage + 1);
                        // 重新获得当前页的书签
                        setCurPageBookMark();
                    }
                });
                btnPreChpt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        preChapter();
                    }
                });
                btnNextChpt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextChapter();
                    }
                });
                btnLight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.getPopupWindow().dismiss();
                        PopupWindow win = brightWin.getPopupWindow();
                        win.setAnimationStyle(R.style.animTranslate);
                        brightWin.showAtLocation(mPageFlipView, Gravity.BOTTOM, 0, 0);
                    }
                });
                btnNight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setNightMode();
                    }
                });
                btnContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.getPopupWindow().dismiss();
                        PopupWindow win = contentWin.getPopupWindow();
                        win.setAnimationStyle(R.style.animTranslateLF);
                        contentWin.showAtLocation(mPageFlipView, Gravity.LEFT, 0, 0);
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 0.3f;
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);
                    }
                });
                btnSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.getPopupWindow().dismiss();
                        PopupWindow win = setWin.getPopupWindow();
                        win.setAnimationStyle(R.style.animTranslate);
                        setWin.showAtLocation(mPageFlipView, Gravity.BOTTOM, 0, 0);
                    }
                });
                btnBookMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 如果已经添加了书签
                        List<User.BookMark> bookMarks = curPageBookMark;
                        if (bookMarks.size() > 0) {
                            // 删除书签
                            removeBookMarks(bookMarks);
                            btnBookMark.setText("添加书签");
                        } else {
                            // 添加书签
                            addBookMark();
                            btnBookMark.setText("删除书签");
                        }
                    }
                });
                /*
                dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        window.getPopupWindow().dismiss();
                    }
                });*/
            }

            @Override
            protected void onShow() {
                // 获取当前章节进度
                PageFactory pf = PageFactory.get();
                //System.out.println("mPageFlipView.getPageNo() " + mPageFlipView.getPageNo());
                Page p = pf.getPage(mPageFlipView.getPageNo() - 1);
                // 如果当前没有页
                if (p == null) {
                    chapterProValue = 0;
                } else {
                    chapterProValue = (int)p.getRate();
                }
                seekBar.setProgress(chapterProValue);
                setCurPageBookMark();
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setWindowPreference ();
                        processWin.getPopupWindow().dismiss();
                        isProPopup = false;
                        isPopup = false;
                    }
                });
            }
        };
        // 进度小弹窗
        processWin = new CommonPopupWindow(this, R.layout.chapter_rate_win, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

            @Override
            protected void initView() {
                View view = getContentView();
                chapterName = (TextView)view.findViewById(R.id.chapterName);
                chapterProcess = (TextView)view.findViewById(R.id.chapterProcess);
            }

            @Override
            protected void onShow() {}

            @Override
            protected void initEvent() {}

            @Override
            protected void initWindow() {
                super.initWindow();
            }
        };
        // 亮度弹窗
        brightWin = new CommonPopupWindow(this, R.layout.help_brightness, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

            /** 是否同步为系统亮度的开关 */
            Switch switchBright;
            /** 亮度条 */
            SeekBar sbBright;
            @Override
            protected void initView() {
                View view = getContentView();
                sbBright = (SeekBar)view.findViewById(R.id.sbBright);
                switchBright = (Switch)view.findViewById(R.id.switchBright);
            }

            @Override
            protected void initEvent() {
                sbBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        mBrightness = seekBar.getProgress();
                        BrightnessUtils.setCurWindowBrightness(ReadingActivity.this, mBrightness);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if (isSysBright || BrightnessUtils.IsAutoBrightness(ReadingActivity.this)) {
                            BrightnessUtils.stopAutoBrightness(ReadingActivity.this);
                            switchBright.setChecked(false);
                            isSysBright = false;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                switchBright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        isSysBright = b;
                        if (b) {
                            BrightnessUtils.startAutoBrightness(ReadingActivity.this);
                        } else {
                            BrightnessUtils.stopAutoBrightness(ReadingActivity.this);
                        }
                    }
                });
            }

            @Override
            protected void onShow() {
                mBrightness = BrightnessUtils.getScreenBrightness(ReadingActivity.this);
                sbBright.setProgress(mBrightness);
            }
        };
        // 目录弹窗
        contentWin = new CommonPopupWindow(this, R.layout.help_content, (int) (screenWidth * 0.8), ViewGroup.LayoutParams.MATCH_PARENT) {

            ViewPager pager;
            /** 书名 */
            TextView bookName;
            /** 每一个界面 */
            List<View> views;
            /** 标签 */
            TextView[] lableBtn;
            /**  目录 */
            ContentRecyclerView contentRview;
            List<ContentRecyclerView.Item> contentList;
            /** 书签 */
            BookMarkRecyclerView bookMarkRview;
            List<BookMarkRecyclerView.Item> bookMarkList;
            /** 背景 */
            View viewContent;

            /** 监听点击类 */
            class MyClickListener implements View.OnClickListener {

                private int index;

                public MyClickListener(int index) {
                    this.index = index;
                }

                @Override
                public void onClick(View v) {
                    //改变ViewPager当前显示页面
                    pager.setCurrentItem(index);
                }
            }

            /** 初始化目录 */
            public void initContent () {
                contentList = new ArrayList<>();
                int size = chapterContent.size();
                for (int i = 0; i < size; i++) {
                    contentList.add(contentRview.new Item(chapterContent.get(i), i + 1));
                }
            }

            /** 重置目录 */
            public void resetContent () {
                int sizeChapterContent = chapterContent.size();
                int sizeContentList = contentList.size();
                int minSize, maxSize;
                if (sizeChapterContent > sizeContentList) {
                    minSize = sizeContentList;
                    maxSize = sizeChapterContent;
                } else {
                    minSize = sizeChapterContent;
                    maxSize = sizeContentList;
                }
                for (int i = 0; i < maxSize; i++) {
                    // 如果i在最小范围内 直接给值
                    if (i < minSize) {
                        ContentRecyclerView.Item item = contentList.get(i);
                        String content = chapterContent.get(i);
                        item.content = content;
                        item.chapterId = i + 1;
                    } else {
                        // 超出最小
                        // 判断是多了还是少了
                        if (sizeContentList < sizeChapterContent) {
                            // 不够加
                            contentList.add(contentRview.new Item(chapterContent.get(i), i + 1));
                        } else {
                            // 多了删除
                            contentList.remove(i);
                            maxSize--;
                            i--;
                        }
                    }
                }
                System.out.println("ReadingActivity:resetContent");
            }

            /** 获取用户书签 */
            private List<BookMarkRecyclerView.Item> getBookMark () {
                List<User.BookMark> bookMarks = User.get().getBookMarkList();
                if (bookMarks == null)
                    bookMarks = new ArrayList<>();
                List<BookMarkRecyclerView.Item> bookItems = new ArrayList<>();
                for (User.BookMark bm : bookMarks) {
                    // 如果是当前书的书签
                    if (bm.bookId.equals(curBookId)) {
                        System.out.println("ReadingActivity:curBookId" + curBookId);
                        bookItems.add(bookMarkRview.new Item(bm.chapterId, bm.firstLine, bm.process, bm.date));
                    }
                }
                System.out.println("ReadingActivity:getBookMark");
                return bookItems;
            }

            /** 重置书签 */
            private void resetBookMark () {
                List<User.BookMark> bookMarks = User.get().getBookMarkList(curBookId);
                if (bookMarks == null)
                    bookMarks = new ArrayList<>();
                int sizebookMarks = bookMarks.size();
                int sizeBookMarkList = bookMarkList.size();
                int minSize, maxSize;
                if (sizeBookMarkList > sizebookMarks) {
                    minSize = sizebookMarks;
                    maxSize = sizeBookMarkList;
                } else {
                    minSize = sizeBookMarkList;
                    maxSize = sizebookMarks;
                }
                for (int i = 0; i < maxSize; i++) {
                    // 如果i在最小范围内 直接给值
                    if (i < minSize) {
                        BookMarkRecyclerView.Item item = bookMarkList.get(i);
                        User.BookMark mark = bookMarks.get(i);
                        item.chapterId = mark.chapterId;
                        item.firstLine = mark.firstLine;
                        item.process = mark.process;
                        item.date = mark.date;
                    } else {
                        // 超出最小
                        // 判断是多了还是少了
                        if (sizeBookMarkList < sizebookMarks) {
                            // 不够加
                            User.BookMark bm = bookMarks.get(i);
                            bookMarkList.add(bookMarkRview.new Item(bm.chapterId, bm.firstLine, bm.process, bm.date));
                        } else {
                            // 多了删除
                            bookMarkList.remove(i);
                            maxSize--;
                            i--;
                        }
                    }
                }
                System.out.println("ReadingActivity:resetBookMark");
            }

            /** 初始化书签 */
            public void initBookMark () {
                // 更新书签的时候记得更新书签的下标
                bookMarkList = getBookMark ();
            }

            @Override
            protected void initView() {
                View view = getContentView();
                pager = (ViewPager) view.findViewById(R.id.pager);
                bookName = (TextView) view.findViewById(R.id.txtBookName);
                lableBtn = new TextView[2];
                lableBtn[0] = (TextView) view.findViewById(R.id.lableBookMark);
                lableBtn[1] = (TextView) view.findViewById(R.id.lableContent);
                lableBtn[0].setOnClickListener(new MyClickListener(0));
                lableBtn[1].setOnClickListener(new MyClickListener(1));
                viewContent = view.findViewById(R.id.viewContent);
            }

            @Override
            protected void initEvent() {
                bookName.setText(getBookName());
                // 自定义table的样式
                views = new ArrayList<View>();
                LayoutInflater li = getLayoutInflater();
                View v0 = li.inflate(R.layout.help_content_bookmark, null);
                View v1 = li.inflate(R.layout.help_content_content, null);
                views.add(v0);
                views.add(v1);
                // 配置RecyclerView
                contentRview = new ContentRecyclerView((RecyclerView)v1.findViewById(R.id.listContent), v1.getContext(), ReadingActivity.this);
                initContent();
                contentRview.setAdapter(contentList);
                bookMarkRview = new BookMarkRecyclerView((RecyclerView)v0.findViewById(R.id.listContent), v0.getContext(), ReadingActivity.this);
                initBookMark();
                bookMarkRview.setAdapter(bookMarkList);
                // 需要给ViewPager设置适配器
                PagerAdapter adapter = new PagerAdapter() {

                    @Override
                    public boolean isViewFromObject(View arg0, Object arg1) {
                        return arg0 == arg1;
                    }

                    @Override
                    public int getCount() {
                        return views.size();
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        //对超出范围的资源进行销毁
                        //super.destroyItem(container, position, object);
                        container.removeView(views.get(position));
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        //对显示的资源进行初始化
                        //return super.instantiateItem(container, position);
                        container.addView(views.get(position));
                        return views.get(position);
                    }

                };
                pager.setAdapter(adapter);
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int index) {
                        for (int i = 0; i < lableBtn.length; i++) {
                            if (i == index) {
                                lableBtn[i].setTextColor(Color.rgb(219, 86, 128));
                            } else {
                                lableBtn[i].setTextColor(PageRender.getPaintColor());
                            }
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                    }
                });
            }

            @Override
            protected void onShow() {
                lableBtn[0].setTextColor(PageRender.getPaintColor());
                lableBtn[1].setTextColor(PageRender.getPaintColor());
                bookName.setTextColor(PageRender.getPaintColor());
                viewContent.setBackgroundResource(mPageFlipView.getCurBackgroundResId());
                resetBookMark ();
                contentRview.notifyItem();
                bookMarkRview.notifyItem();
                contentRview.reset();
                bookMarkRview.reset();
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);
                        setWindowPreference();
                    }
                });
            }

            @Override
            public void refresh() {
                super.refresh();
                //bookMarkList = getBookMark ();
                resetContent ();
                contentRview.reset();
                contentRview.notifyItem();
                bookMarkRview.reset();
                //bookMarkRview.notifyItem();
            }
        };
        // 设置弹窗
        setWin = new CommonPopupWindow(this, R.layout.help_set, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

            /** 缩小字体 */
            TextView btnNarrow;
            /** 放大字体 */
            TextView btnEnlarge;
            /** 当前字体大小 */
            TextView txtCurFontSize;
            /** 颜色按钮 */
            ImageView[] colorBtn;

            /** 监听点击类 */
            class MyClickListener implements View.OnClickListener {

                private int index;

                public MyClickListener(int index) {
                    this.index = index;
                }

                @Override
                public void onClick(View v) {
                    // 改变字体颜色
                    setPaintMode(index);
                }
            }

            @Override
            protected void initView() {
                View view = getContentView();
                btnNarrow = (TextView)view.findViewById(R.id.narrowBtn);
                btnEnlarge = (TextView)view.findViewById(R.id.enlargeBtn);
                txtCurFontSize = (TextView)view.findViewById(R.id.fontSize);
                colorBtn = new ImageView[4];
                colorBtn[0] = (ImageView)view.findViewById(R.id.colorBtn_0);
                colorBtn[1] = (ImageView)view.findViewById(R.id.colorBtn_1);
                colorBtn[2] = (ImageView)view.findViewById(R.id.colorBtn_2);
                colorBtn[3] = (ImageView)view.findViewById(R.id.colorBtn_3);
            }

            @Override
            protected void initEvent() {
                btnNarrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomFontSize(-1);
                        txtCurFontSize.setText("" + PageRender.FONT_SIZE);
                    }
                });
                btnEnlarge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomFontSize(1);
                        txtCurFontSize.setText("" + PageRender.FONT_SIZE);
                    }
                });
                for (int i = 0; i < colorBtn.length; i++) {
                    colorBtn[i].setOnClickListener(new MyClickListener(i));
                }
            }

            @Override
            protected void onShow() {
                txtCurFontSize.setText("" + PageRender.FONT_SIZE);
            }
        };
    }
    // endregion
}
