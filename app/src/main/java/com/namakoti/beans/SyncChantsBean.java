package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anusha on 12/5/2017.
 */

public class SyncChantsBean extends  ErrorBean implements Serializable {
    @SerializedName("namas")
    public ArrayList<SaveNamas> namas;
    @SerializedName("causenamas")
    public ArrayList<SaveNamas> causenamas;

    public class SaveNamas{

        @SerializedName("print_username")
        public String print_username;
        @SerializedName("music")
        public String music;
        @SerializedName("no_chants")
        public int no_chants;
        @SerializedName("nama_total_count")
        public int nama_total_count;
        @SerializedName("nama_printed_count")
        public int nama_printed_count;
        @SerializedName("nama_running_count")
        public int nama_running_count;
        @SerializedName("printing_service")
        public String printing_service;
        @SerializedName("theme_name")
        public String theme_name;
        @SerializedName("sub_theme_name")
        public String sub_theme_name;
        @SerializedName("user_namakoti_id")
        public int user_namakoti_id;
        @SerializedName("user_theme_id")
        public String user_theme_id;
        @SerializedName("user_sub_theme_id")
        public String user_sub_theme_id;
        @SerializedName("user_language_id")
        public String user_language_id;
    }

}
