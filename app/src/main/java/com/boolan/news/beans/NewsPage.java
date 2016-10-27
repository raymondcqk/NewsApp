package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class NewsPage {

    private int allPages;
    @SerializedName("contentlist")
    private List<Article> articleList;
    private int currentPage;
    private int allNum;
    private int maxResult;

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }
}
