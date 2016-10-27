package com.boolan.news.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.boolan.news.beans.Channel;
import com.boolan.news.beans.ChannelListData;
import com.google.gson.Gson;

import java.util.List;

/**
 *
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DbOpenHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "news.db";
    public static final int DATABASE_VERSION = 1;

    private Context context;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE newsdata(channelId TEXT,data TEXT)");
        db.execSQL("CREATE TABLE channel(id TEXT,name TEXT,subscribed INT)");
        initTable(db);
    }

    private void initTable(SQLiteDatabase db) {
        //实例化网络请求类
        NewsApi newsApi = new NewsApi(context);
        //要请求的“频道数据”实体bean类。使用Gson库，将请求返回的response string转为对应实体类对象
        ChannelListData data = new Gson().fromJson(newsApi.getChannelListSync(), ChannelListData.class);

        if (data != null) {
            if (data.getCode() == 0) {
                List<Channel> channels = data.getBody().getChannelList();
                String insertChannel = "INSERT INTO channel(id,name,subscribed) VALUES('%s','%s',%s)";
                int i = 0;
                for (Channel channel : channels) {
                    if (i < 5) {
                        //默认订阅前5个频道
                        db.execSQL(String.format(insertChannel, channel.getId(), channel.getName(), 1));
                    } else {
                        db.execSQL(String.format(insertChannel, channel.getId(), channel.getName(), 0));
                    }
                    i++;
                }
            } else {
                Log.e(TAG, data.getError());
                Looper.prepare();
                Toast.makeText(context, data.getError(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
