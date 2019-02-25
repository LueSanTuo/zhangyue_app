package com.example.asus.zhangyue.pageflip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.asus.zhangyue.R;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 加载位图工具
 */

public final class LoadBitmapTask {
    private final static String TAG = "LoadBitmapTask";
    /** 单例 */
    private static LoadBitmapTask __object;

    // 做分辨率选择
    final static int SMALL_BG = 0;
    final static int MEDIUM_BG = 1;
    final static int LARGE_BG = 2;
    // 一些图片id参考
    public final static int DEFAULT_BG = 2;
    public final static int NIGHT_MODE = 0;

    /** 当前分辨率下对应资源数组的下标 */
    int mBGSizeIndex;
    /** 是否水平？？ */
    boolean mIsLandscape;
    boolean mStop;
    Resources mResources;
    Thread mThread;
    /** 缓存 */
    HashMap<String, Bitmap> bpMap;
    /** 资源数组 存放各种分辨率的bitmap的id */
    int[][] mPortraitBGs;

    /**
     * Get an unique task object
     *
     * @param context Android context
     * @return unique task object
     */
    public static LoadBitmapTask get(Context context) {
        if (__object == null) {
            __object = new LoadBitmapTask(context);
        }
        return __object;
    }

    /**
     * Constructor
     *
     * @param context Android context
     */
    private LoadBitmapTask(Context context) {
        mResources = context.getResources();
        mBGSizeIndex = SMALL_BG;
        mStop = false;
        mThread = null;
        mIsLandscape = false;
        bpMap = new HashMap<>();

        // init all available bitmaps
        mPortraitBGs = new int[][] {
                new int[] {R.drawable.p1_480, R.drawable.p2_480, R.drawable.p3_480, R.drawable.p4_480},
                new int[] {R.drawable.p1_720, R.drawable.p2_720, R.drawable.p3_720, R.drawable.p4_720},
                new int[] {R.drawable.p1_1080, R.drawable.p2_1080, R.drawable.p3_1080, R.drawable.p4_1080}
        };
    }

    /** 获得背景资源id
     * @param id 在PageRender中的背景Id */
    public int getBackgroundResId (int id) {
        return mPortraitBGs[mBGSizeIndex][id];
    }

    /**
     * Acquire a bitmap to show
     * <p>If there is no cached bitmap, it will load one immediately</p>
     * @param bitmapId
     * @return bitmap
     */
    public Bitmap getBitmap(int bitmapId) {
        Bitmap b = null;
        Integer resId = new Integer(bitmapId);
        String keyStr = mBGSizeIndex + "_" + resId;
        if (bpMap.containsKey(keyStr)) {
            b = bpMap.get(keyStr);
        } else {
            int id = mPortraitBGs[mBGSizeIndex][resId.intValue()];
            b = BitmapFactory.decodeResource(mResources, id);
            bpMap.put(mBGSizeIndex + "_" + resId, b);
        }
        if (mIsLandscape) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap lb = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            return lb;
        }
        if (b == null) {
            Log.d(TAG, "Load bitmap instantly!");
        }
        return b;
    }

    /**
     * Set bitmap width , height and maximum size of cache queue
     *
     * @param w width of bitmap
     * @param h height of bitmap
     */
    public void set(int w, int h) {
        int newIndex = LARGE_BG;
        if ((w <= 480 && h <= 854) ||
                (w <= 854 && h <= 480)) {
            newIndex = SMALL_BG;
        }
        else if ((w <= 800 && h <= 1280) ||
                (h <= 800 && w <= 1280)) {
            newIndex = MEDIUM_BG;
        }

        mIsLandscape = w > h;

        if (newIndex != mBGSizeIndex) {
            mBGSizeIndex = newIndex;
            cleanCache();
        }
    }

    /**
     * Clear cache queue
     */
    private void cleanCache() {
        Iterator iter = bpMap.entrySet().iterator();
        while (iter.hasNext()) {
            ((Bitmap)iter.next()).recycle();
        }
        bpMap.clear();
    }

}
