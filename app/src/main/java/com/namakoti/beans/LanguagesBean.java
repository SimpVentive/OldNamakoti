package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 12/5/2017.
 */

public class LanguagesBean extends  ErrorBean{
    @SerializedName("language_id")
    private String language_id;
    @SerializedName("language_name")
    private String language_name;

    public String getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public LanguagesBean(String language_id, String language_name) {
        this.language_id = language_id;
        this.language_name = language_name;
    }

}
