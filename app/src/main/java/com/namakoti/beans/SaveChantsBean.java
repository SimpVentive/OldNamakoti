package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anusha on 12/5/2017.
 */

public class SaveChantsBean extends  ErrorBean implements Serializable {
    @SerializedName("print_username")
    private String print_username;
    @SerializedName("music")
    private String music;

    public long getNo_chants() {
        return no_chants;
    }

    public void setNo_chants(int no_chants) {
        this.no_chants = no_chants;
    }

    @SerializedName("no_chants")
    private long no_chants;

    @SerializedName("nama_total_count")
    private long nama_total_count;
    @SerializedName("nama_printed_count")
    private long nama_printed_count;
    @SerializedName("nama_running_count")
    private long nama_running_count;
    @SerializedName("printing_service")
    private String printing_service;
    @SerializedName("theme_name")
    private String theme_name;
    @SerializedName("sub_theme_name")
    private String sub_theme_name;
    @SerializedName("user_namakoti_id")
    private int user_namakoti_id;
    @SerializedName("user_theme_id")
    private String user_theme_id;
    @SerializedName("user_sub_theme_id")
    private String user_sub_theme_id;
    @SerializedName("user_language_id")
    private String user_language_id;

    public String getPrint_username() {
        return print_username;
    }

    public String getMusic() {
        return music;
    }

    public long getNama_total_count() {
        return nama_total_count;
    }

    public long getNama_printed_count() {
        return nama_printed_count;
    }

    public long getNama_running_count() {
        return nama_running_count;
    }

    public String getPrinting_service() {
        return printing_service;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public String getSub_theme_name() {
        return sub_theme_name;
    }

    public int getUser_namakoti_id() {
        return user_namakoti_id;
    }

    public String getUser_theme_id() {
        return user_theme_id;
    }

    public String getUser_sub_theme_id() {
        return user_sub_theme_id;
    }

    public String getUser_language_id() {
        return user_language_id;
    }

    /**
     * this bean is used for both normal and other details activities no chant is using only in normal and cause
     * @param print_username
     * @param music
     * @param no_chants
     * @param nama_total_count
     * @param nama_printed_count
     * @param nama_running_count
     * @param printing_service
     * @param theme_name
     * @param sub_theme_name
     * @param user_namakoti_id
     * @param user_theme_id
     * @param user_sub_theme_id
     * @param user_language_id
     */
    public SaveChantsBean(String print_username, String music, long no_chants, long nama_total_count, long nama_printed_count,
                          long nama_running_count, String printing_service,
                          String theme_name, String sub_theme_name,
                          int user_namakoti_id, String user_theme_id,
                          String user_sub_theme_id, String user_language_id) {
        this.print_username = print_username;
        this.music = music;
        this.no_chants = no_chants;
        this.nama_total_count = nama_total_count;
        this.nama_printed_count = nama_printed_count;
        this.nama_running_count = nama_running_count;
        this.printing_service = printing_service;
        this.theme_name = theme_name;
        this.sub_theme_name = sub_theme_name;
        this.user_namakoti_id = user_namakoti_id;
        this.user_theme_id = user_theme_id;
        this.user_sub_theme_id = user_sub_theme_id;
        this.user_language_id = user_language_id;
    }

}
