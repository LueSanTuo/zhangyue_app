package com.example.asus.zhangyue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.SearchActivity;

/**
 * 有标题的Fragment基类
 */

public abstract class BasePageTitleFragment extends Fragment {
    /** 存放着标题的 */
    private View mFragmentView;//父控件(由父控件找到子控件)
    private ImageView mIvLogoPage;
    private TextView mTvTitlePage;
    private TextView mTvPaypalPage;
    private FrameLayout mFlTitleContentPage;
    /** 搜索 */
    private ImageView btnSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initTitle(inflater, container);
    }

    /** 设置标题图标 */
    public void setTitleIcon(String msg, boolean show) {    //设置标题和图标
        if (mFragmentView == null)
            return;
        mTvTitlePage.setText(msg);  //设置标题
        mTvTitlePage.setVisibility(show ? View.VISIBLE : View.GONE);     //设置标题显示  true就是显示  false就是不显示
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /** 初始化标题 如果不想要标题的话就重写这个函数 */
    protected View initTitle (LayoutInflater inflater, @Nullable ViewGroup container) {
        mFragmentView = inflater.inflate(R.layout.base_top_title_page, container, false);   // 通用布局(图片 充值)
        btnSearch = (ImageView) mFragmentView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到搜索界面
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        mIvLogoPage = (ImageView) mFragmentView.findViewById(R.id.iv_logo_page);
        mTvTitlePage = (TextView) mFragmentView.findViewById(R.id.tv_title_page);
        mTvPaypalPage = (TextView) mFragmentView.findViewById(R.id.tv_paypal_page);
        mFlTitleContentPage = (FrameLayout) mFragmentView.findViewById(R.id.fl_title_content_page);
        View view = initView();
        mFlTitleContentPage.addView(view);
        return mFragmentView;
    }
    protected abstract View initView();
    protected abstract void initData();
}
