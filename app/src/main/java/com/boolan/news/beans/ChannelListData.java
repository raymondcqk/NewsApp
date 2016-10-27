package com.boolan.news.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SpaceRover on 2016/9/17.
 */
public class ChannelListData {

    @SerializedName("showapi_res_code")
    private int code;
    @SerializedName("showapi_res_error")
    private String error;
    @SerializedName("showapi_res_body")
    private ChannelListBody resBody;

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

    public ChannelListBody getBody() {
        return resBody;
    }

    public void setBody(ChannelListBody resBody) {
        this.resBody = resBody;
    }
}
