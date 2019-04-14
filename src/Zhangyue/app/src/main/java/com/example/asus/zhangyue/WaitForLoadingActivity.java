package com.example.asus.zhangyue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.myutil.FileOperation;
import com.example.asus.zhangyue.myutil.HttpUtil;
import com.example.asus.zhangyue.pageflip.PageFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.asus.zhangyue.ReadingActivity.CHAPTER_PREFIX;

/** 等待书籍加载进入的界面 */
public class WaitForLoadingActivity extends Activity {

    private TextView desText;
    private String curBookId;
    private String bookName;
    private int curChapterId;
    private float chapterReadProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_loading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        desText = (TextView)findViewById(R.id.description);
        desText.setText("等待加载...");
        // 加载异步下载书籍任务 需要外部传入书籍id和章节号
        // 获得
        Intent intent = getIntent();
        curBookId = intent.getStringExtra("bookId");
        curChapterId = intent.getIntExtra("chapterId", 1);
        bookName = intent.getStringExtra("bookName");
        chapterReadProcess = intent.getFloatExtra("process", 0f);

        System.out.println("WaitForLoadingAct: " + curChapterId + " " + bookName);
        new LoadBookAsyncTask().execute(curBookId, "" + curChapterId);
        /*
        Intent intent = getIntent();
        String mode = intent.getStringExtra("Mode");
        if (mode.equals("getBookContent")) {
            String bookId = intent.getStringExtra("bookId");
            getBookContent(bookId);
        }*/
    }

    /** 显示完成 然后等待一会儿关闭界面 */
    public void endActivity () {
        desText.setText("加成完成！");
        //Toast.makeText(WaitForLoadingActivity.this, "完成", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WaitForLoadingActivity.this.finish();
            }
        },1000);
    }

    /** 异步加载书籍 负责将数据下载之后赋值给PageFactory*/
    class LoadBookAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 显示一个遮盖界面
            // 显示等待窗口

        }

        @Override
        /** doInBackground 执行完后会将参数传入该函数 */
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            PageFactory pf = PageFactory.get();
            // 刷新阅读界面
            if (content.equals("") || content == null) {
                // 没有此章节
                pf.chapterName = "";
                pf.pageContentStr = " ";
            } else {
                pf.pageContentStr = content;
            }
            // 获得以上操作获得的书籍章节的字符串
            // 通过工厂将这些文字处理成页
            pf.divPage();
            // 遮盖界面隐藏
            endActivity();
        }

        @Override
        protected String doInBackground(String... strings) {
            // 判断本地是否有该书的某一章
            // 如果本地没有该书则去数据库下载该书的某一章节
            // 等待下载完毕 保存到本地
            String content = "";
            // 判断该书是否下载到本地，如果下载到本地则判断该书的该章节是否在本地
            // 读取本地数据
            String bookId = strings[0];
            int chapterId = Integer.parseInt(strings[1]);
            if (FileOperation.isExistsBook(bookId)) {
                String chapterName;
                chapterName = CHAPTER_PREFIX + chapterId;
                content = FileOperation.loadBook(bookId, chapterName);
                // 超出本地存放的章节文件数那么连接网络获取内容
                if (content.equals("") || content == null) {
                    // 没有此章节 读取网络数据
                    content = getBookFromNet (bookId, chapterId);
                }
                System.out.println("WaitForLoadingActivity:-----chapterId------" + chapterId + "-----chapterName------" + chapterName);
            } else {
                // 没有此书时 直接读取网络数据
                content = getBookFromNet (bookId, chapterId);
            }
            // 等待处理结束
            return content;
        }

        /** 获取网络书籍内容 */
        private String getBookFromNet (String bookId, final int chapterId) {
            String result = "";
            // 显示等待窗口
            RequestBody requestBody = new FormBody.Builder().add("bookId", bookId).add("chapterId", "" + chapterId).build();
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(User.IP_ADD_LOAD_BOOK)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                FileOperation.writeBook(result, curBookId, chapterId);
                // 刷新
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
