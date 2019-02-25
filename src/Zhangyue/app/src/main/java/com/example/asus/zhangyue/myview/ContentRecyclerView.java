package com.example.asus.zhangyue.myview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.ReadingActivity;
import com.example.asus.zhangyue.pageflip.PageRender;

import java.util.List;

/**
 * 目录滚动列表
 */
public class ContentRecyclerView {

    private ReadingActivity readingActivity;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    public ContentRecyclerView (RecyclerView recyclerView, Context context, ReadingActivity readingActivity) {
        this.readingActivity = readingActivity;
        LinearLayoutManager layoutManager = new LinearLayoutManager(readingActivity);
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
        for (Item i : itemAdapter.mItemList) {
            if (i.txtContent != null)
                i.txtContent.setTextColor(PageRender.getPaintColor());
        }
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

        /** 内容 */
        public String content;
        /** 章节id */
        public int chapterId;
        public TextView txtContent;

        public Item() {
            content = "";
        }

        public Item(String content) {
            this.content = content;
        }

        public Item(String content, int chapterId) {
            this.content = content;
            this.chapterId = chapterId;
        }

        public class ItemClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                // 目录项目点击会跳转
                readingActivity.gotoChapter(chapterId);
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

            public ViewHolder(View view) {
                super(view);
                // 获取
                content = (TextView)view.findViewById(R.id.chapterName);
                whole = view.findViewById(R.id.whole);
            }
        }

        public ItemAdapter (List<Item> itemList) {
            mItemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);
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
            holder.content.setText(item.content);
            item.txtContent = holder.content;
            // 设置字颜色
            holder.content.setTextColor(PageRender.getPaintColor());
            // 设置点击响应
            holder.whole.setOnClickListener(item.new ItemClickListener());

        }
    }

}
