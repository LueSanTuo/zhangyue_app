package com.example.asus.zhangyue.pageflip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;

import com.eschao.android.widget.pageflip.OnPageFlipListener;
import com.eschao.android.widget.pageflip.PageFlip;

/**
 * Created by ASUS on 2018/12/14.
 */

public abstract class PageRender implements OnPageFlipListener {
    public final static int MSG_ENDED_DRAWING_FRAME = 1;
    private final static String TAG = "PageRender";

    final static int DRAW_MOVING_FRAME = 0;
    final static int DRAW_ANIMATING_FRAME = 1;
    final static int DRAW_FULL_PAGE = 2;

    /** 最大页数 */
    public static int MAX_PAGES = 30;
    /** 字体大小 */
    public static int FONT_SIZE = 20;
    /** 字体颜色 */
    public static int FONT_COLOR = Color.WHITE;
    /** 页面左右间距 */
    public static int SPACING_LR = 20;
    /** 页面上下间距 */
    public static int SPACING_UD = 30;
    /** 行距 */
    public static int SPACING_LINE = 10;
    /** 背景图片 */
    public static int BACKGROUND_ID = LoadBitmapTask.DEFAULT_BG;
    /** 夜间模式的字体颜色 */
    public final static int FONT_COLOR_NIGHT = Color.GRAY;
    /** 夜间模式的背景 */
    public final static int BACKGROUND_ID_NIGHT = LoadBitmapTask.NIGHT_MODE;
    /** 是否是夜间模式 */
    public static boolean IS_NIGHT_MODE = false;

    int mPageNo;
    int mDrawCommand;
    Bitmap mBitmap;
    Canvas mCanvas;
    Bitmap mBackgroundBitmap;
    Context mContext;
    Handler mHandler;
    PageFlip mPageFlip;
    // 用于强制刷新界面的标记
    boolean refreshFlagFirstPage;
    boolean refreshFlagSecondPage;

    /** 仅用于绘制所有页面内容的画笔 */
    Paint contentPaint;

    public PageRender(Context context, PageFlip pageFlip,
                      Handler handler, int pageNo) {
        mContext = context;
        mPageFlip = pageFlip;
        mPageNo = pageNo;
        mDrawCommand = DRAW_FULL_PAGE;
        mCanvas = new Canvas();
        mPageFlip.setListener(this);
        mHandler = handler;
        contentPaint = new Paint();
    }

    /** 获得当前笔刷颜色 */
    public static int getPaintColor () {
        if (IS_NIGHT_MODE)
            return FONT_COLOR_NIGHT;
        else
            return FONT_COLOR;
    }

    /** 获得当前背景 */
    public static int getCurBackground () {
        if (IS_NIGHT_MODE)
            return BACKGROUND_ID_NIGHT;
        else
            return BACKGROUND_ID;
    }

    /** 获得当前背景 */
    public int getCurBackgroundResId () {
        if (IS_NIGHT_MODE)
            return LoadBitmapTask.get(mContext).getBackgroundResId(BACKGROUND_ID_NIGHT);
        else
            return LoadBitmapTask.get(mContext).getBackgroundResId(BACKGROUND_ID);
    }

    /** 刷新当前画笔和页面 */
    public void refresh() {
        // 设置
        contentPaint.setFilterBitmap(true);
        contentPaint.setTextSize(calcFontSize(FONT_SIZE));
        refreshPaintColor ();
        contentPaint.setStrokeWidth(1);
        contentPaint.setAntiAlias(true);
        PageFactory pf = PageFactory.get();
        pf.setCanvas(mCanvas.getWidth(), mCanvas.getHeight(), contentPaint);
        pf.divPage();
        MAX_PAGES = pf.getPagesRealSize();
        System.out.println("Refresh");
    }

    /** 刷新画笔颜色 */
    public void refreshPaintColor () {
        if (IS_NIGHT_MODE)
            contentPaint.setColor(FONT_COLOR_NIGHT);
        else
            contentPaint.setColor(FONT_COLOR);
    }

    /**
     * Get page number
     * 获得页数
     * @return page number
     */
    public int getPageNo() {
        return mPageNo;
    }

    /**
     * Release resources
     * 释放资源
     */
    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mPageFlip.setListener(null);
        mCanvas = null;
        mBackgroundBitmap = null;
    }

    /**
     * Handle finger moving event
     * 处理拖动移动等事件
     * @param x x coordinate of finger moving
     * @param y y coordinate of finger moving
     * @return true if event is handled
     */
    public boolean onFingerMove(float x, float y) {
        mDrawCommand = DRAW_MOVING_FRAME;
        return true;
    }

    /**
     * Handle finger up event
     * 处理手指抬起
     * @param x x coordinate of finger up
     * @param y y coordinate of inger up
     * @return true if event is handled
     */
    public boolean onFingerUp(float x, float y) {
        if (mPageFlip.animating()) {
            mDrawCommand = DRAW_ANIMATING_FRAME;
            return true;
        }

        return false;
    }

    /**
     * Calculate font size by given SP unit
     * 计算字体大小
     */
    protected int calcFontSize(int size) {
        return (int)(size * mContext.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * Render page frame
     */
    abstract void onDrawFrame();

    /**
     * Handle surface changing event
     *
     * @param width surface width
     * @param height surface height
     */
    abstract void onSurfaceChanged(int width, int height);

    /**
     * Handle drawing ended event
     *
     * @param what draw command
     * @return true if render is needed
     */
    abstract boolean onEndedDrawing(int what);
}
