package com.example.asus.zhangyue.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.fragments.BookShelfFragment;

import java.util.List;

/**
 * 书架列表 用于显示用户的阅读记录的
 */

public class BookShelfRecyclerView {

    private BookShelfFragment bookShelfFragment;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    public BookShelfRecyclerView (RecyclerView recyclerView, Context context, BookShelfFragment bookShelfFragment) {
        this.bookShelfFragment = bookShelfFragment;
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
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

    /** 刷新图片的功能 */
    public void refreshBookCover (int index, Bitmap picture) {
        if (picture == null || index >= recyclerView.getAdapter().getItemCount())
            return;
        ItemAdapter.ViewHolder viewHolder = (ItemAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(index);
        viewHolder.bookCover.setImageBitmap(picture);
    }

    /** 目录元素类 */
    public class Item {

        /** 书名 */
        public String bookName;
        /** 作者 */
        public String author;
        /** 阅读记录 */
        public User.BookMark readRecord;
        /** 书面 */
        public Bitmap bookCover;

        public Item() {
            bookName = "";
            author = "";
        }

        public Item(String bookName, String author) {
            this.bookName = bookName;
            this.author = author;
        }

        public Item(String bookName, String author, User.BookMark readRecord) {
            this.bookName = bookName;
            this.author = author;
            this.readRecord = readRecord;
        }

        public class ItemClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                // 目录项目点击会跳转
                bookShelfFragment.gotoReading(readRecord, bookName);
            }
        }
    }

    /** 适配器 */
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        public List<Item> mItemList;

        class ViewHolder extends RecyclerView.ViewHolder {
            /** 整体 */
            private View whole;
            /** 书名 */
            private TextView bookName;
            /** 作者名 */
            private TextView author;
            /** 书面 */
            private ImageView bookCover;

            public ViewHolder(View view) {
                super(view);
                // 获取
                bookName = (TextView)view.findViewById(R.id.bookName);
                author = (TextView)view.findViewById(R.id.author);
                whole = view.findViewById(R.id.whole);
                bookCover = (ImageView)view.findViewById(R.id.bookCover);
            }
        }

        public ItemAdapter (List<Item> itemList) {
            mItemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fg_bookshelf_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = mItemList.get(position);
            holder.bookName.setText(item.bookName);
            holder.author.setText(item.author);
            if (item.bookCover != null)
                holder.bookCover.setImageBitmap(item.bookCover);
            else
                holder.bookCover.setImageResource(R.drawable.bookmall_red);
            // 设置点击响应
            holder.whole.setOnClickListener(item.new ItemClickListener());
        }


    }
}
