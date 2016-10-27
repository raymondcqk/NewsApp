package com.boolan.news.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.boolan.news.beans.Article;
import com.boolan.news.beans.Channel;
import com.boolan.news.beans.ImageUrls;
import com.boolan.news.beans.NewsRequestParams;
import com.boolan.news.utils.NewsDb;
import com.boolan.news.utils.NewsLoader;
import com.boolan.news.utils.ResourceStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于离线下在的Service
 */

public class DownloadService extends Service {

    // 既然要下在到数据库，当然要我们封装好的DB工具类喇
    private NewsDb newsDb;
    // 文件读写操作离不开线程，使用线程池是内存优化的方案
    private ExecutorService threadPool;
    // 首先我们还是要通过封装好的NewsLoader来从网络载入数据
    private NewsLoader newsLoader;
    // 这个Volley的请求队列?
    private RequestQueue requestQueue;
    // 别忘了还有图片数据要存储为图片文件
    private ResourceStorage resourceStorage;
    private int imgNumber;

    /**
     * Service创建的时候，初始化：
     * 1. 数据库操作类
     * 2. 线程池
     * 3. 新闻数据载入类
     * 4. Volley请求队列
     * 5. 图片存储工具类
     */
    @Override
    public void onCreate() {
        super.onCreate();
        newsDb = new NewsDb(this);
        threadPool = Executors.newFixedThreadPool(5);///？
        newsLoader = new NewsLoader(this);
        requestQueue = Volley.newRequestQueue(this);
        resourceStorage = new ResourceStorage(this);
        imgNumber = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "开始下载！", Toast.LENGTH_SHORT).show();
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获得数据库中已被订阅的channel id
     * @return
     */
    private List<String> getSubscribedChannelId() {
        List<String> channelIdList = new ArrayList<>();
        List<Channel> channels = newsDb.getSubscribedChannelList();
        for (Channel channel : channels) {
            channelIdList.add(channel.getId());
        }
        return channelIdList;
    }

    /**
     * 下载
     *
     * 遍历每一个被订阅的channel id
     * 根据id，用NewsLoader请求对应channel的news 数据
     */
    private void startDownload() {
        for (String channelId : getSubscribedChannelId()) {
            newsLoader.setOnLoadNewsDataListener(new NewsLoader.OnLoadNewsDataListener() {
                @Override
                public void onLoadNewsDataSuccess(final String channelId, final List<Article>
                        articles) {
                    //线程池执行异步操作
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            //把json数据存入数据库
                            saveData(channelId, articles);
                        }
                    });
                    // 记录该channel下，所有文章的图片数量
                    for (Article article : articles) {
                        imgNumber = imgNumber + article.getImgList().size();
                    }

                    //下载每一篇文章的图片
                    for (Article article : articles) {
                        downloadImg(article.getImgList());
                    }
                }

                @Override
                public void onLoadNewsDataError(String error) {

                }
            });
            newsLoader.loadNewsDataOnline(new NewsRequestParams().setChannelId(channelId));
        }
    }

    /**
     * 保存json数据到数据库
     *
     * 反射机制，获得数据的bean类，把实体类转为json字符串
     *
     * 这部分的反射不太懂
     * @param channelId
     * @param articles
     */
    private void saveData(String channelId, List<Article> articles) {
        //反射机制

        Type srcType = new TypeToken<List<Article>>() {
        }.getType();
        String data = new Gson().toJson(articles, srcType);
        newsDb.setNewsData(channelId, data);
    }

    /**
     * 下载图片
     *
     * 根据URL，下载图片
     * ImageRequest Volley中专门用来根据url下载图片，转为Bitmap的
     *
     * @param imageUrls
     */
    private void downloadImg(List<ImageUrls> imageUrls) {
        for (final ImageUrls img : imageUrls) {
            //根据URL，下载图片
            ImageRequest imageRequest = new ImageRequest(img.getUrl(), new Response
                    .Listener<Bitmap>() {

                @Override
                public void onResponse(final Bitmap response) {

                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {

                            resourceStorage.saveImg(resourceStorage.getFileName(img.getUrl()),
                                    response);
                        }
                    });
                    if (--imgNumber == 0) {
                        Toast.makeText(DownloadService.this, "下载完成！", Toast.LENGTH_SHORT).show();
                    }
                }
            }, img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888, new Response
            .ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(imageRequest);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
