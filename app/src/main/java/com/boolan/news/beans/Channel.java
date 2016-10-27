package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class Channel {

    @SerializedName("channelId")
    private String id;
    private String name;
    private int subscribed;
    private int position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
