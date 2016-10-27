package com.boolan.news.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.boolan.news.beans.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 1 通过DpOpenHelper获得数据库实例
 * 2 数据库增删改查操作方法
 */

public class NewsDb {

    private SQLiteDatabase db;

    /**
     * 构造函数实例化 SQLDatabase
     * @param context
     */
    public NewsDb(Context context) {
        this.db = new DbOpenHelper(context).getWritableDatabase();
    }

    /**
     * 实例方法：获取被订阅的频道列表 的接口
     * @return
     */
    public List<Channel> getSubscribedChannelList() {
        //实际返回频道列表的方法（类方法），数据库中subscribed = 1代表被订阅
        return getChannelList("subscribed=1");//直接传入selection参数，可以不传selectionArgs(null)
        // "subscribed=1",因为是int，所以1不需要要'1'单引号。 后面有关于channelId的，因为是String，所以要带单引号
        /**
         * 以前我的写法是 subscribed = ? ,new String[]{"1"}
         */
    }

    /**
     * 返回所有channel list
     * @return
     */
    public List<Channel> getAllChannelList() {
        return getChannelList(null);//selection = null 代表查询所有行
    }

    /**
     * 因为我们除了查询已被订阅的channel list，还要查询所有的channel list
     * 代码也就只是selection不同，所以将selection抽象为参数，封装为一个函数，在不同的接口中传入不同的selection即可
     * 完成代码复用
     * @param selection
     * @return
     */
    private List<Channel> getChannelList(String selection) {
        List<Channel> channels = new ArrayList<>();
        Cursor cursor = db.query("channel", new String[]{"id", "name", "subscribed"},
                selection, null, null, null, null);//直接传入selection参数，可以不传selectionArgs(null)
        while (cursor.moveToNext()) {
            Channel channel = new Channel();
            channel.setId(cursor.getString(cursor.getColumnIndex("id")));
            channel.setName(cursor.getString(cursor.getColumnIndex("name")));
            channel.setSubscribed(cursor.getInt(cursor.getColumnIndex("subscribed")));
            channels.add(channel);
        }
        cursor.close();//切勿忘记关闭cursor
        return channels;
    }

    /**
     * 设置数据库中的特定（被选中）channel的subscribed属性（0、1）
     *
     * 通过update方式，更新
     * @param channelId
     * @param subscribed
     */
    public void setChannelSubscribed(String channelId, boolean subscribed) {
        ContentValues cv = new ContentValues();
        if (subscribed) {
            cv.put("subscribed", 1);
        } else {
            cv.put("subscribed", 0);
        }
        db.update("channel", cv, "id='" + channelId + "'", null);// "id = 'x'",注意双引号里使用单引号
        /**
         * update(String table, ContentValues values, String whereClause, String[] whereArgs)\
         *
         * Convenience method for updating rows in the database.
         */
    }

    /**
     * 存放离线数据到数据库
     * @param channelId
     * @param data
     */
    public void setNewsData(String channelId, String data) {
        ContentValues cv = new ContentValues();
        cv.put("data", data);
        db.update("newsdata", cv, "channelId='" + channelId + "'", null);
    }

    /**
     * 读取数据库离线新闻json数据
     * @param channelId
     * @return
     */
    public String getNewsData(String channelId) {
        Cursor cursor = db.query("newsdata", new String[]{"data"}, "channelId='" + channelId +
                "'", null, null, null, null);
        cursor.moveToFirst();
        String data = cursor.getString(cursor.getColumnIndex("data"));
        cursor.close();
        return data;
    }
}
