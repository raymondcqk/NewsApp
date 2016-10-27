package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class Article {

    private String pubData;
    private String title;
    private String desc;
    private String source;
    private String link;
    private String html;
    @SerializedName("imageurls")
    private List<ImageUrls> imgList;

    public String getPubData() {
        return pubData;
    }

    public void setPubData(String pubData) {
        this.pubData = pubData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<ImageUrls> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImageUrls> imgList) {
        this.imgList = imgList;
    }
}
