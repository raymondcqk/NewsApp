package com.boolan.news.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boolan.news.R;
import com.boolan.news.beans.Article;

import java.util.List;

/**
 * Created by SpaceRover on 2016/9/25.
 */

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private List<Article> articles;
    private OnItemViewClickListener onItemViewClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .news_list_item, parent, false));
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .news_list_footer, parent, false));
        }
        return null;
    }

    /**
     * 给每个item（holder）绑定数据
     * 类似于listadapter里面的getItem
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder && position < articles.size()) {
            final ItemViewHolder mHolder = (ItemViewHolder) holder;
            Article article = articles.get(position);
            mHolder.titleTv.setText(article.getTitle());
            mHolder.descTv.setText(article.getDesc());

            /**
             * 在一个类中为某控件绑定点击事件但该监听器又在另一个调用该类的类中实现
             * 则需要为该点击事件添加一个"传递接口"
             *
             * 点击监听接口实现中调用自己定义的回调接口
             * 而不是亲自弄一个点击事件接口
             */
            //给viwe holder（item)绑定点击事件
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemViewClickListener != null) {
                        onItemViewClickListener.onItemViewClick(position, mHolder.itemView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (articles != null) {
            return articles.size() + 1;//因为有footer
        } else {
            return 0;
        }
    }

    /**
     * 根据其返回view type来进行操作
     * 若当前position为文章列表的长度，即文章最后一个index+1，则为footer的位置，返回footer type，然后进行刷新加载操作
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == articles.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    /**
     * 新闻文章数据列表是存在ListAdapter当中的
     * 暴露以下接口，用以外部完成新闻数据读取之后，更新其articles
     * @param articles
     */
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void addArticles(List<Article> articles) {
        this.articles.addAll(articles);
    }

    public List<Article> getArticles(){
        return this.articles;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView titleTv;
        private TextView descTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            descTv = (TextView) itemView.findViewById(R.id.descTv);
            titleTv.setText("标题");
            descTv.setText("描述");
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 设置 "传递接口"
     *
     * 在这个Adapter中自己创建点击事件接口？是什么情况
     *
     * 原来是ViewHolder有点击事件！
     * 相当于一个listviwe item
     *
     *
     * @param onItemViewClickListener
     */
    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    /**
     * 点击事件 "传递接口"
     */
    public interface OnItemViewClickListener {
        //接口方法 参数（当前item（view holder）的位置 ， 该holder view（item） 的布局的view实例可以获取到当前view里面的子控件哦）
        void onItemViewClick(int position, View itemView);
    }
}
