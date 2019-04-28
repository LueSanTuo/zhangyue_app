package com.example.asus.zhangyue.myutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.asus.zhangyue.Data.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Http工具
 */

public class HttpUtil {
    public static void sendOkHttpRequest (String address, okhttp3.Callback callback, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient();
        Request request;
        if (requestBody != null) {
            request = new Request.Builder()
                    .url(address)
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(address)
                    .build();
        }
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获取位图
     * @param pictureName 网址
     * @return Bitmap位图
     */
    public static Bitmap getURLimage(final String pictureName) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(User.IP_ADD_BOOK_COVER_PICTURE + pictureName);
            /// 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            ///获得图片的数据流
            InputStream is = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
