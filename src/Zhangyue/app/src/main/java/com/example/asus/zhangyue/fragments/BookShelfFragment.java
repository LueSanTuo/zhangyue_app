package com.example.asus.zhangyue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.ReadingActivity;
import com.example.asus.zhangyue.myview.BookShelfRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架
 */

public class BookShelfFragment extends Fragment {

    public BookShelfRecyclerView mRecyclerView;

    private List<BookShelfRecyclerView.Item> dataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bookshelf, container, false);
        RecyclerView rview = (RecyclerView)view.findViewById(R.id.bookShelfRecycler);
        mRecyclerView = new BookShelfRecyclerView(rview, getContext(), this);
        initReadRecord();
        mRecyclerView.setAdapter(dataList);
        return view;
    }

    /** 初始化阅读记录 */
    private void initReadRecord () {
        dataList = new ArrayList<>();
        // 获取本地阅读记录
        List<User.BookMark> list = User.get().getReadRecord();
        if (list == null)
            return;
        User user = User.get();
        for (User.BookMark bm : list) {
            String bookName = user.getBookName(bm.bookId);
            String bookAuthor = user.getBookAuthor(bm.bookId);
            dataList.add(mRecyclerView.new Item(bookName, bookAuthor, bm));
        }
    }

    /** 跳转到阅读 */
    public void gotoReading (User.BookMark bookMark, String bookName) {
        // 获得书的id 将书名传过去
        Intent intent = new Intent(getActivity(), ReadingActivity.class);
        intent.putExtra("bookId", bookMark.bookId);
        intent.putExtra("bookName", bookName);
        intent.putExtra("chapterId", bookMark.chapterId);
        intent.putExtra("process", bookMark.process);
        startActivity(intent);
    }
}
