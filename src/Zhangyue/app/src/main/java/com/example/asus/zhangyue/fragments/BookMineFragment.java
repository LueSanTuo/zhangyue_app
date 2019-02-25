package com.example.asus.zhangyue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.zhangyue.BookCityActivity;
import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.LoginActivity;
import com.example.asus.zhangyue.R;

/**
 * 我的
 */

public class BookMineFragment extends Fragment {

    /** 用户头像 */
    private ImageView userFace;
    /** 用户名 */
    private TextView userName;
    /** 我的书籍 */
    private TextView btnBookShelf;
    /** 点击登录 */
    private View btnLogin;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bookmine, container, false);
        userFace = (ImageView)view.findViewById(R.id.userFace);
        userName = (TextView)view.findViewById(R.id.userName);
        btnBookShelf = (TextView)view.findViewById(R.id.btnBookShelf);
        btnLogin = (View)view.findViewById(R.id.btnLogin);
        initEvent ();
        return view;
    }

    /** 初始化 */
    private void initEvent () {
        User user = User.get();
        userName.setText(user.getUserName());
        // 判断是否登录
        if (user.isLoaded())
            userFace.setImageResource(R.drawable.bookmine_red);
        else
            userFace.setImageResource(R.drawable.bookmine_grey);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "btnLogin", Toast.LENGTH_SHORT).show();
                User user = User.get();
                if (user.isLoaded()) {
                    // 登出
                    user.Logout();
                    refresh ();
                } else {
                    // 跳转到登录界面
                    Intent intent = new Intent(BookMineFragment.this.getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnBookShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "btnBookShelf", Toast.LENGTH_SHORT).show();
                // 跳转到我的书籍
                BookCityActivity bookCityActivity = (BookCityActivity)BookMineFragment.this.getActivity();
                bookCityActivity.gotoFirstLayout();
            }
        });
    }

    /** 刷新 */
    public void refresh () {
        User user = User.get();
        userName.setText(user.getUserName());
        // 判断是否登录
        if (user.isLoaded())
            userFace.setImageResource(R.drawable.bookmine_red);
        else
            userFace.setImageResource(R.drawable.bookmine_grey);
    }
}
