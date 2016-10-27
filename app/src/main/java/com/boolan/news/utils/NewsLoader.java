package com.boolan.news.utils;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.boolan.news.beans.Article;
import com.boolan.news.beans.NewsListData;
import com.boolan.news.beans.NewsRequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 《新闻数据获取类》
 *
 * 数据从网络请求到本地数据库，使用到两个类：NewsApi（网络）、NewsDb（本地）
 *
 * 我们最终是要把数据库存到数据库，然后取出并交给UI（Activity、Fragment）
 *
 * 所以可以把NewsApi、NewsDb封装起来，把流程操作也封装起来，这个点类似于一个设计模式，忘记叫啥了，有一系列关联的对象组合，流程也是一致的，
 * 为了防止后续使用出错，把这些对象、算法都封装起来，留出最后的接口。
 */

public class NewsLoader {

    private Context context;
    private NewsApi newsApi;
    private NewsDb newsDb;

    private OnLoadNewsDataListener onLoadNewsDataListener;

    /**
     * 初始化
     * @param context
     */
    public NewsLoader(Context context) {
        this.context = context;
        this.newsApi = new NewsApi(context);
        this.newsDb = new NewsDb(context);
    }

    /**
     * 对于数据的使用者（Activity、Fragment等），并不关心数据到底从网络还是数据库读取的
     * 所以只需要暴露一个loadNewsData()方法
     *
     * 方法中根据网络状态来选择
     * @param params
     */
    public void loadNewsData(NewsRequestParams params) {
        if (NetworkState.isNetworkConnected(context)) {
            loadNewsDataOnline(params);
        } else {
            loadNewsDataOffline(params);
        }
    }

    /**
     * 网络获取新闻
     * @param params
     */
    public void loadNewsDataOnline(final NewsRequestParams params) {
        newsApi.getNewsList(params, new Response.Listener<String>() {
            /**
             * 接口回调：当网络请求成功返回数据，会回调该方法
             *
             * 我们可以从该回调方法中获得 response （原始数据字符串）
             * 进行后续操作
             *
             * 这里我们先把response的Json数据通过Gson实例化一个是Bean实体类，
             * 最后回调自己设计的接口，让数据交给Activity处理
             * @param response
             */
            @Override
            public void onResponse(String response) {

                if (onLoadNewsDataListener != null) {
                    NewsListData newsListData = new Gson().fromJson(response, NewsListData.class);
                    if (newsListData.getCode() == 0) {
                        onLoadNewsDataListener.onLoadNewsDataSuccess(params.getChannelId(),
                                newsListData.getBody().getPage().getArticleList());
                    } else {
                        onLoadNewsDataListener.onLoadNewsDataError(newsListData.getError());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (onLoadNewsDataListener != null) {
                    onLoadNewsDataListener.onLoadNewsDataError(error.getMessage());
                }
            }
        });
    }

    /**
     * 使用离线数据
     * @param params
     */
    public void loadNewsDataOffline(NewsRequestParams params) {
        String channelId = params.getChannelId();
        Type dataType = new TypeToken<List<Article>>() {
        }.getType();
        List<Article> articles = new Gson().fromJson(newsDb.getNewsData(channelId), dataType);
        if (onLoadNewsDataListener != null) {
            onLoadNewsDataListener.onLoadNewsDataSuccess(channelId, articles);
        }
    }

    /**
     * 并暴露一个setOnLoadNewsDataListener（listener）给Activity传入该接口的实现实例引用
     * @param onLoadNewsDataListener
     */
    public void setOnLoadNewsDataListener(OnLoadNewsDataListener onLoadNewsDataListener) {
        this.onLoadNewsDataListener = onLoadNewsDataListener;
    }

    /**
     * 因为Volley的请求数据的方法是异步的
     *
     * 所以需要设计一个接口回调给Activity
     *
     * 返回的结果由成功、失败，所以写两个接口方法
     *
     *
     */
    public interface OnLoadNewsDataListener {
        //给Activity或Fragment返回channel id 以及对应的请求到的新闻list
        void onLoadNewsDataSuccess(String channelId, List<Article> articles);

        void onLoadNewsDataError(String error);
    }
}
