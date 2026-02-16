package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anusha on 12/6/2017.
 */

public class LoginBean extends ErrorBean implements Serializable{

    @SerializedName("fullname")
    public String fullname;

    @SerializedName("user_id")
    public long user_id;

    @SerializedName("userphoto")
    public String userphoto;

    @SerializedName("user_name")
    public long user_name;

    @SerializedName("user_password")
    public String user_password;

    @SerializedName("user_confirm_password")
    public String user_confirm_password;

    @SerializedName("ip")
    public String ip;

    @SerializedName("user_type")
    public int user_type;

    @SerializedName("created_by")
    public String created_by;

    @SerializedName("created_date")
    public String created_date;

    @SerializedName("modified_by")
    public String modified_by;

    @SerializedName("modified_date")
    public String modified_date;

    @SerializedName("upgrade")
    public int upgrade;

    @SerializedName("upgradeuseramt")
    public int upgradeuseramt;

    @SerializedName("namas")
    public ArrayList<NamasDetails> namas;

    @SerializedName("causenamas")
    public ArrayList<NamasDetails> causenamas;

    public class NamasDetails {
        public String getPrint_username() {
            return print_username;
        }

        public String getMusic() {
            return music;
        }

        public int getNama_total_count() {
            return nama_total_count;
        }

        public int getNama_running_count() {
            return nama_running_count;
        }

        public int getNama_printed_count() {
            return nama_printed_count;
        }

        public int getPrinting_service() {
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

        public int getUser_theme_id() {
            return user_theme_id;
        }

        public int getUser_sub_theme_id() {
            return user_sub_theme_id;
        }

        public String getUser_language_id() {
            return user_language_id;
        }

        public int getNo_chants() {
            return no_chants;
        }

        public void setNo_chants(int no_chants) {
            this.no_chants = no_chants;
        }

        @SerializedName("print_username")
        public String print_username;

        @SerializedName("music")
        public String music;

        @SerializedName("no_chants")
        public int no_chants;

        @SerializedName("nama_total_count")
        public int nama_total_count;

        @SerializedName("nama_running_count")
        public int nama_running_count;

        @SerializedName("nama_printed_count")
        public int nama_printed_count;

        @SerializedName("printing_service")
        public int printing_service;

        @SerializedName("theme_name")
        public String theme_name;

        @SerializedName("sub_theme_name")
        public String sub_theme_name;

        @SerializedName("user_namakoti_id")
        public int user_namakoti_id;

        @SerializedName("user_theme_id")
        public int user_theme_id;

        @SerializedName("user_sub_theme_id")
        public int user_sub_theme_id;

        @SerializedName("user_language_id")
        public String user_language_id;

    }
}


