package com.example.asus.zhangyue.myutil;

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
}
