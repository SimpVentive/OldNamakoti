package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 12/5/2017.
 */

public class GodNamesBean extends ErrorBean {

    @SerializedName("photo")
    private String photo;

    @SerializedName("theme_id")
    private String theme_id;

    @SerializedName("theme_name")
    private String theme_name;

    @SerializedName("printing_service")
    private String printing_service;


    public String getPhoto() {
        return photo;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public String getPrinting_service() {
        return printing_service;
    }

    public GodNamesBean(String theme_id, String theme_name, String printing_service, String photo) {
//        this.status = status;
        this.theme_id = theme_id;
        this.theme_name = theme_name;
        this.printing_service = printing_service;
        this.photo = photo;
    }

}
