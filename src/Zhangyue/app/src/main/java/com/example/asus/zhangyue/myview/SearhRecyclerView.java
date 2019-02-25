package com.example.asus.zhangyue.myview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.zhangyue.Data.Book;
import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.SearchActivity;

import java.util.List;

/**
 * 搜索滚动列表
 */

public class SearhRecyclerView {

    private SearchActivity searchActivity;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    public SearhRecyclerView (RecyclerView recyclerView, Context context, SearchActivity searchActivity) {
        this.searchActivity = searchActivity;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(layoutManager);
    }

    /** 添加适配器 */
    public void setAdapter (ItemAdapter adapter) {
        if (recyclerView == null)
            return;
        itemAdapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    /** 添加适配器 */
    public void setAdapter (List<Item> lsit) {
        if (recyclerView == null)
            return;
        itemAdapter = new ItemAdapter(lsit);
        recyclerView.setAdapter(itemAdapter);
    }

    /** 重置 */
    public void reset () {
    }

    /** 刷新 */
    public void notifyItem () {
        itemAdapter.notifyDataSetChanged();
    }

    /** 刷新 */
    public void notifyItemChanged (int position) {
        itemAdapter.notifyItemChanged(position);
    }

    /** 目录元素类 */
    public class Item {
        public String content;
        public String author;
        public String bookName;

        public Book book;

        public Item() {
            content = "";
            author = "";
            bookName = "";
        }

        public Item(String content) {
            this.content = content;
            this.author = "";
            this.bookName = "";
        }

        public Item(String content, String author, String bookName, Book book) {
            this.content = content;
            this.author = author;
            this.bookName = bookName;
            this.book = book;
        }

        public class ItemClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                // 目录项目点击会跳转
                searchActivity.gotoReading(book);
            }
        }
    }

    /** 适配器 */
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        public List<Item> mItemList;

        class ViewHolder extends RecyclerView.ViewHolder {
            /** 整体 */
            private View whole;
            /** 内容 */
            private TextView content;
            /** 作者 */
            private TextView author;
            /** 书名 */
            private TextView bookName;

            public ViewHolder(View view) {
                super(view);
                // 获取
                content = (TextView)view.findViewById(R.id.content);
                author = (TextView)view.findViewById(R.id.author);
                bookName = (TextView)view.findViewById(R.id.bookName);
                whole = view.findViewById(R.id.whole);
            }
        }

        public ItemAdapter (List<Item> itemList) {
            mItemList = itemList;
        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        @Override
        public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
            Item item = mItemList.get(position);
            holder.content.setText(item.content);
            holder.author.setText(item.author);
            holder.bookName.setText(item.bookName);
            // 设置字颜色
            holder.whole.setOnClickListener(item.new ItemClickListener());
        }
    }
}
