package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 2/11/2018.
 */

public class CauseBean extends ErrorBean {

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public String id;

    public CauseBean(String id, String name) {
        this.name = name;
        this.id = id;
    }
}
