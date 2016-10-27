package com.boolan.news.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boolan.news.R;
import com.boolan.news.beans.Article;
import com.boolan.news.beans.NewsRequestParams;
import com.boolan.news.ui.adapter.NewsListAdapter;
import com.boolan.news.utils.NewsLoader;

import java.util.List;

public class NewsListFragment extends Fragment implements NewsListAdapter
        .OnItemViewClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    // ？
    private int page;
    private String channelId;//根据id 来loadnews
    private String title;
    private NewsLoader newsLoader; // newload作为新闻数据的获取接口，可从数据库或网络读取新闻数据

    //UI相关
    private SwipeRefreshLayout refreshLayout;   // 可下拉刷新的布局容器
    private LinearLayoutManager layoutManager;  // RecycleView特有的LinearLayoutManager，用以实现类似于ListView的效果。 分页的？
    private NewsListAdapter newsListAdapter;    // 给RecycleView的LinearLayoutManager绑定item及数据用的ListAdapter


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news_list, container, false);
        init();
        return view;
    }

    private void init() {
        //?
        page = 1;
        //getArguments()获得在Activity里面新建fragment实例时，传入的bundle
        channelId = getArguments().getString("channelId", "");
        title = getArguments().getString("title", "");
        //实例化NewsLoader，用于获取news数据
        newsLoader = new NewsLoader(getContext());

        /**
         * 下拉刷新布局 SwipeRefreshLayout
         */
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        //通过颜色资源文件设置进度动画的颜色资源
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        //设置下拉刷新动作的监听器
        refreshLayout.setOnRefreshListener(this);

        /**
         * 封装了Item可回收的ListView：RecyclerView
         *
         * 分页形式？
         */
        //相当于listview
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //相当于item ？？（待确认）
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //若当前滚动状态为 IDLE（空闲？） The RecyclerView is not currently scrolling
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //若当前item的数量 等于 可见item的索引+1，代表到底了，需要加载下一页
                    if (layoutManager.getItemCount() ==
                            layoutManager.findLastVisibleItemPosition() + 1) {
                        loadNextPage();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //相当于listview的listadapter （RecyclerView.Adapter<RecyclerView.ViewHolder>）
        newsListAdapter = new NewsListAdapter();
        newsListAdapter.setOnItemViewClickListener(this);
        recyclerView.setAdapter(newsListAdapter);

        //到目前为止，Fragment会加载默认的recyclerView数据，这个默认数据可以在“NewsListAdapter”类中得知

        refreshData();
    }

    /**
     * 下拉刷新，刷新第一页，更新数据，获得最新的news list
     * SwipeRefreshLayout
     *
     * setOnRefreshListener!
     */
    @Override
    public void onRefresh() {
        refreshData();
    }

    /**
     * 刷新数据
     *
     * 1. 刷新界面
     *
     * 刷新第一页，更新数据，获得最新的news list
     *
     */
    private void refreshData() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);//开始刷新工作
        }
        // 设置新闻数据获取后的回调接口（监听者模式，在网络数据请求方面使用不少）
        newsLoader.setOnLoadNewsDataListener(new NewsLoader.OnLoadNewsDataListener() {
            @Override
            public void onLoadNewsDataSuccess(String channelId, List<Article> articles) {
                newsListAdapter.setArticles(articles); // 因为是刷新数据，所以articles要换新，而不是添加
                newsListAdapter.notifyDataSetChanged();//通知 数据中心 更新数据
                refreshLayout.setRefreshing(false); //刷新结束
                page = 1; //page重置为1 下拉刷新，重新获取最新的news list
            }

            @Override
            public void onLoadNewsDataError(String error) {

            }
        });
        //开始从网络或数据库获取新闻数据，结果会回调到上方的接口实现中
        newsLoader.loadNewsData(new NewsRequestParams().setChannelId(channelId).setTitle(title)
                .setPage(1));
        /**
         * new NewsRequestParams().setChannelId(channelId).setTitle(title).setPage(1)
         *
         * 牛叉的写法：
         * 因为这个对象我们只使用一次，没必要给其命名，所以使用“匿名对象”
         *
         * 匿名对象为其添加数据的方法，可以是构造函数，可以是new 之后，连续调用方法
         */
    }

    /**
     * 刷新数据
     *
     *
     * 2. 加载下一页
     */
    private void loadNextPage() {
        newsLoader.setOnLoadNewsDataListener(new NewsLoader.OnLoadNewsDataListener() {
            @Override
            public void onLoadNewsDataSuccess(String channelId, List<Article> articles) {
                newsListAdapter.addArticles(articles); // 因为是添加，所以在原articles列表添加新的articles
                newsListAdapter.notifyDataSetChanged();
                page++;
            }

            @Override
            public void onLoadNewsDataError(String error) {

            }
        });
        newsLoader.loadNewsData(new NewsRequestParams().setChannelId(channelId).setTitle(title)
                .setPage(page + 1));
    }

    @Override
    public void onItemViewClick(int position, View itemView) {
        Intent intent = new Intent(getContext(), NewsActivity.class);
        intent.putExtra("title", newsListAdapter.getArticles().get(position).getTitle());
        intent.putExtra("html", newsListAdapter.getArticles().get(position).getHtml());
        startActivity(intent);
    }
}
