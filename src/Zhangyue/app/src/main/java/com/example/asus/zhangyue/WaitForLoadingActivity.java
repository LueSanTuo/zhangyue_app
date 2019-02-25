package com.example.asus.zhangyue;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.myutil.FileOperation;
import com.example.asus.zhangyue.myutil.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/** 等待书籍加载进入的界面 */
public class WaitForLoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_loading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WaitForLoadingActivity.this.finish();
                Toast.makeText(WaitForLoadingActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        },4000);
        /*
        Intent intent = getIntent();
        String mode = intent.getStringExtra("Mode");
        if (mode.equals("getBookContent")) {
            String bookId = intent.getStringExtra("bookId");
            getBookContent(bookId);
        }*/
    }

    /** 获取书本的目录 */
    /*private void getBookContent(final String bookId) {
        ReadingActivity.chapterFileNameContent = new ArrayList<>();
        ReadingActivity.chapterContent = new ArrayList<>();
        // 再从网络下载目录如果没有的话就从本地获取
        RequestBody requestBody = new FormBody.Builder().add("bookId", bookId).build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_LOAD_BOOK_CONTENT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 获取本地目录文件？？
                ReadingActivity.chapterContent = FileOperation.loadBookContent(bookId, ReadingActivity.chapterFileNameContent);
                Toast.makeText(WaitForLoadingActivity.this, "网络数据获取失败", Toast.LENGTH_SHORT).show();
                endActivity ();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                setBookNetContet(response.body().string());
            }
        }, requestBody);
    }
*/
    /** 连接成功后设置当前书籍的目录 */
    /*private void setBookNetContet(final String bookName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bookName == null || bookName.equals("")) {
                    finish();
                    return;
                }
                String[] temp = bookName.split("\n|\r");
                ReadingActivity.chapterContent = Arrays.asList(temp);
                finish();
            }
        });
    }*/

    /** 结束 */
   /* private void endActivity () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }*/

}
