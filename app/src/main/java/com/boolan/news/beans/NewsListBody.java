package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class NewsListBody {

    private int retCode;
    @SerializedName("pagebean")
    private NewsPage page;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public NewsPage getPage() {
        return page;
    }

    public void setPage(NewsPage page) {
        this.page = page;
    }
}
