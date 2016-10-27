package com.boolan.news.beans;

import java.util.List;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class ChannelListBody {

    private int totalNum;
    private List<Channel> channelList;

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
