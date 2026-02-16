package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 2/11/2018.
 */

public class PushPayloadBean extends ErrorBean {

    @SerializedName("body")
    public String body;

    @SerializedName("priority")
    public String priority;

    @SerializedName("title")
    public String title;

    public String getBody() {
        return body;
    }

    public String getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String isContent_available() {
        return content_available;
    }

    @SerializedName("content_available")
    public String content_available;


    public PushPayloadBean(String priority,String body, String title, String content_available) {
        this.content_available = content_available;
        this.title = title;
        this.body = body;
        this.message = message;
    }

}
