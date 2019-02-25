package com.example.asus.zhangyue;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.zhangyue.Data.Book;
import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.myview.SearhRecyclerView;

import java.util.ArrayList;
import java.util.List;

/** 搜索 */
public class SearchActivity extends Activity {

    private SearhRecyclerView mSearhRecyclerView;
    private List<SearhRecyclerView.Item> dataList;
    private ImageView btnBack;
    private EditText editSearch;
    private TextView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.searchRes);
        mSearhRecyclerView = new SearhRecyclerView (recyclerView, this, this);
        btnBack = (ImageView)findViewById(R.id.btnBack);
        editSearch = (EditText)findViewById(R.id.editSearch);
        btnSearch = (TextView)findViewById(R.id.btnSearch);
        initEvent();
        dataList = new ArrayList<>();
        mSearhRecyclerView.setAdapter(dataList);
    }

    /** 初始化事件 */
    private void initEvent () {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 返回
                finish();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSearchRes ();
            }
        });
    }

    /** 获得搜索结果 */
    public void getSearchRes () {
        dataList.clear();
        // 本地搜索
        User user = User.get();
        List<Book> bookList = user.searchBooks(editSearch.getText().toString().trim());
        for(Book b : bookList) {
            dataList.add(mSearhRecyclerView.new Item("", b.getBookAuthor(), b.getBookName(), b));
        }
        mSearhRecyclerView.notifyItem();
    }

    /** 跳转到阅读界面 */
    public void gotoReading (Book book) {
        // 查找是否有此书的本地记录
        User user = User.get();
        User.BookMark mark = user.getBookHistory(book.getBookId());
        Intent intent = new Intent(SearchActivity.this, ReadingActivity.class);
        intent.putExtra("bookName", book.getBookName());
        if (mark == null) {
            // 说明没有阅读过
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("chapterId", 1);
            intent.putExtra("process", 0f);
        } else {
            // 有阅读记录
            intent.putExtra("bookId", mark.bookId);
            intent.putExtra("chapterId", mark.chapterId);
            intent.putExtra("process", mark.process);
        }
        startActivity(intent);
    }

}
