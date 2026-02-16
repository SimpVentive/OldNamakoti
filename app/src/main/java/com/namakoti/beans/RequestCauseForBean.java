package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anusha on 12/5/2017.
 */

public class RequestCauseForBean extends  ErrorBean implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public RequestCauseForBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
