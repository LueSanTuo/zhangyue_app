package com.example.asus.zhangyue.pageflip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import com.eschao.android.widget.pageflip.Page;
import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipState;

import java.util.List;

/**
 * 单页绘制
 */

public class SinglePageRender extends PageRender{

    /**
     * Constructor
     * @see {@link #PageRender(Context, PageFlip, Handler, int)}
     */
    public SinglePageRender(Context context, PageFlip pageFlip,
                            Handler handler, int pageNo) {
        super(context, pageFlip, handler, pageNo);
    }

    /**
     * Draw frame
     */
    public void onDrawFrame() {
        // 1. delete unused textures
        mPageFlip.deleteUnusedTextures();
        Page page = mPageFlip.getFirstPage();

        // 2. handle drawing command triggered from finger moving and animating
        if (mDrawCommand == DRAW_MOVING_FRAME ||
                mDrawCommand == DRAW_ANIMATING_FRAME) {
            // is forward flip
            if (mPageFlip.getFlipState() == PageFlipState.FORWARD_FLIP) {
                // check if second texture of first page is valid, if not,
                // create new one
                if (!page.isSecondTextureSet() || refreshFlagSecondPage) {
                    drawPage(mPageNo + 1);
                    page.setSecondTexture(mBitmap);
                    refreshFlagSecondPage = false;
                }
            }
            // in backward flip, check first texture of first page is valid
            else if (!page.isFirstTextureSet() || refreshFlagFirstPage) {
                drawPage(--mPageNo);
                page.setFirstTexture(mBitmap);
                refreshFlagFirstPage = false;
            }

            // draw frame for page flip
            mPageFlip.drawFlipFrame();
        }
        // draw stationary page without flipping
        else if (mDrawCommand == DRAW_FULL_PAGE) {
            if (!page.isFirstTextureSet() || refreshFlagFirstPage) {
                drawPage(mPageNo);
                //System.out.println("DRAW_FULL_PAGE");
                page.setFirstTexture(mBitmap);
                refreshFlagFirstPage = false;
            }

            mPageFlip.drawPageFrame();
        }

        // 3. send message to main thread to notify drawing is ended so that
        // we can continue to calculate next animation frame if need.
        // Remember: the drawing operation is always in GL thread instead of
        // main thread
        Message msg = Message.obtain();
        msg.what = MSG_ENDED_DRAWING_FRAME;
        msg.arg1 = mDrawCommand;
        mHandler.sendMessage(msg);
    }

    /**
     * Handle GL surface is changed
     *
     * @param width surface width
     * @param height surface height
     */
    public void onSurfaceChanged(int width, int height) {
        // recycle bitmap resources if need
        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
        }

        if (mBitmap != null) {
            mBitmap.recycle();
        }

        // create bitmap and canvas for page
        //mBackgroundBitmap = background;
        Page page = mPageFlip.getFirstPage();
        mBitmap = Bitmap.createBitmap((int)page.width(), (int)page.height(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        refresh();
        LoadBitmapTask.get(mContext).set(width, height);
    }

    /**
     * Handle ended drawing event
     * In here, we only tackle the animation drawing event, If we need to
     * continue requesting render, please return true. Remember this function
     * will be called in main thread
     *
     * @param what event type
     * @return ture if need render again
     */
    public boolean onEndedDrawing(int what) {
        if (what == DRAW_ANIMATING_FRAME) {
            boolean isAnimating = mPageFlip.animating();
            // continue animating
            if (isAnimating) {
                mDrawCommand = DRAW_ANIMATING_FRAME;
                return true;
            }
            // animation is finished
            else {
                final PageFlipState state = mPageFlip.getFlipState();
                // update page number for backward flip
                if (state == PageFlipState.END_WITH_BACKWARD) {
                    // don't do anything on page number since mPageNo is always
                    // represents the FIRST_TEXTURE no;
                }
                // update page number and switch textures for forward flip
                else if (state == PageFlipState.END_WITH_FORWARD) {
                    mPageFlip.getFirstPage().setFirstTextureWithSecond();
                    mPageNo++;
                }

                mDrawCommand = DRAW_FULL_PAGE;
                return true;
            }
        }
        return false;
    }

    /**
     * Draw page content
     *
     * @param number page number
     */
    private void drawPage(int number) {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint p = new Paint();
        p.setFilterBitmap(true);

        // 1. 绘制背景
        Bitmap background;
        background = LoadBitmapTask.get(mContext).getBitmap(getCurBackground());
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, p);
        //background.recycle();
        background = null;

        PageFactory pf =  PageFactory.get();
        // 2. 绘制章节名称 和 当前进度
        int fontSize = calcFontSize(14);
        p.setColor(getPaintColor());
        p.setStrokeWidth(1); // 设置画笔的宽度
        p.setAntiAlias(true); // 设置防锯齿
        //p.setShadowLayer(5.0f, 8.0f, 8.0f, Color.BLACK);
        p.setTextSize(fontSize);
        int rate = (number - 1) * 100 / Math.max(1, MAX_PAGES);
        String text = String.format("%d%%", rate);
        float y = p.getTextSize();
        mCanvas.drawText(pf.chapterName, SPACING_LR, y, p);
        mCanvas.drawText(text, p.measureText(pf.chapterName) + p.measureText(text), y, p);
        // 3. 绘制页面
        pf.drawPage(mCanvas, number - 1);
    }

    /**
     * If page can flip forward
     *
     * @return true if it can flip forward
     */
    public boolean canFlipForward() {
        return (mPageNo < MAX_PAGES);
    }

    /**
     * If page can flip backward
     *
     * @return true if it can flip backward
     */
    public boolean canFlipBackward() {
        if (mPageNo > 1) {
            mPageFlip.getFirstPage().setSecondTextureWithFirst();
            return true;
        }
        else {
            return false;
        }
    }
}
