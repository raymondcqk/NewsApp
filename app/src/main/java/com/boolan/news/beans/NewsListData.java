package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class NewsListData {

    @SerializedName("showapi_res_code")
    private int code;
    @SerializedName("showapi_res_error")
    private String error;
    @SerializedName("showapi_res_body")
    private NewsListBody body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public NewsListBody getBody() {
        return body;
    }

    public void setBody(NewsListBody body) {
        this.body = body;
    }
}
