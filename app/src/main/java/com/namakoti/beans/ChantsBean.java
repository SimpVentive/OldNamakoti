package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 12/5/2017.
 */

public class ChantsBean extends  ErrorBean{


    @SerializedName("sub_theme_id")
    private String sub_theme_id;
    @SerializedName("sub_theme_name")
    private String sub_theme_name;

    public String getSub_theme_id() {
        return sub_theme_id;
    }

    public String getSub_theme_name() {
        return sub_theme_name;
    }

    public ChantsBean(String sub_theme_id, String sub_theme_name) {
        this.sub_theme_id = sub_theme_id;
        this.sub_theme_name = sub_theme_name;
    }
}
