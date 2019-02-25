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
 * 书签滚动列表
 */

public class BookMarkRecyclerView {
    private ReadingActivity readingActivity;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    public BookMarkRecyclerView (RecyclerView recyclerView, Context context, ReadingActivity readingActivity) {
        this.readingActivity = readingActivity;
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
        for (Item i : itemAdapter.mItemList) {
            if (i.txtContent == null)
                continue;
            for (int j = 0; j < i.txtContent.length; j++) {
                if (i.txtContent[j] != null)
                    i.txtContent[j].setTextColor(PageRender.getPaintColor());
            }
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
        public int chapterId;
        public String firstLine;
        public float process;
        public String date;

        public TextView[] txtContent;

        public Item() {
            firstLine = "";
            process = 0f;
            date = "";
        }

        public Item(String content) {
            this.firstLine = content;
            this.process = 0f;
            this.date = "";
        }

        public Item(int chapterId, String firstLine, float process, String date) {
            this.chapterId = chapterId;
            this.firstLine = firstLine;
            this.process = process;
            this.date = date;
        }

        public class ItemClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                // 目录项目点击会跳转
                readingActivity.gotoPageByBookMark(Item.this);
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
            private TextView firstLine;
            /** 进度 */
            private TextView process;
            /** 日期时间 */
            private TextView date;

            public ViewHolder(View view) {
                super(view);
                // 获取
                firstLine = (TextView)view.findViewById(R.id.pageFirstLine);
                process = (TextView)view.findViewById(R.id.chapterProcess);
                date = (TextView)view.findViewById(R.id.date);
                whole = view.findViewById(R.id.whole);
            }
        }

        public ItemAdapter (List<Item> itemList) {
            mItemList = itemList;
        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
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
            item.txtContent = new TextView[3];
            item.txtContent[0] = holder.firstLine;
            item.txtContent[1] = holder.process;
            item.txtContent[2] = holder.date;
            holder.firstLine.setText(item.firstLine);
            holder.process.setText(String.format("%.2f%%", item.process * 100f));
            holder.date.setText(item.date);
            // 设置字颜色
            holder.firstLine.setTextColor(PageRender.getPaintColor());
            holder.process.setTextColor(PageRender.getPaintColor());
            holder.date.setTextColor(PageRender.getPaintColor());
            holder.whole.setOnClickListener(item.new ItemClickListener());
        }
    }
}
