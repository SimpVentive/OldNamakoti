package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anusha on 12/5/2017.
 */

public class SyncChantKarmicBean extends  ErrorBean implements Serializable{

    @SerializedName("profile")
    public SyncProfileBean profile;

    @SerializedName("askinghelp")
    public AskingHelpBean askinghelp;

    @SerializedName("helpingoters")
    public HelpingOtersBean helpingoters;

    @SerializedName("toplist")
    public ArrayList<TopListDetailsBean> toplist;

    @SerializedName("chantsdetails")
    public ChantsDetailsBean chantsdetails;

    @SerializedName("mychants")
    public MyChantsBean mychants;

    @SerializedName("karmicchantdetails")
    public KarmicChantDetails karmicchantdetails;

    public class SyncProfileBean implements Serializable{
        @SerializedName("name")
        public String name;
        @SerializedName("city")
        public String city;
    }

    public class AskingHelpBean implements Serializable{
        @SerializedName("askinghelping")
        public String askinghelping;
    }
    public class HelpingOtersBean implements Serializable{
        @SerializedName("helpingoters")
        public String helpingoters;
    }
    public class TopListDetailsBean implements Serializable{
        @SerializedName("chants_count_will_do")
        public int chants_count_will_do;
        @SerializedName("orginal_chants_count")
        public int orginal_chants_count;
        @SerializedName("name")
        public String name;
    }
    public class ChantsDetailsBean implements Serializable{
        @SerializedName("totalchants")
        public int totalchants;
        @SerializedName("presentchants")
        public int presentchants;
    }
    public class MyChantsBean implements Serializable{
        @SerializedName("participant_id")
        public String participant_id;
        @SerializedName("gkc_setup_id")
        public String gkc_setup_id;
        @SerializedName("part_user_id")
        public String part_user_id;
        @SerializedName("gkc_chants_status")
        public String gkc_chants_status;
        @SerializedName("chants_count_by_you")
        public String chants_count_by_you;
        @SerializedName("chants_count_will_do")
        public int chants_count_will_do;
        @SerializedName("orginal_chants_count")
        public int orginal_chants_count;
        @SerializedName("created_by")
        public String created_by;
        @SerializedName("created_date")
        public String created_date;
        @SerializedName("modified_by")
        public String modified_by;
        @SerializedName("modified_date")
        public String modified_date;
        @SerializedName("last_modified_id")
        public String last_modified_id;

    }
    public class KarmicChantDetails  implements Serializable {

        @SerializedName("cause_name")
        public String cause_name;

        @SerializedName("lang")
        public String lang;

        @SerializedName("requestfor")
        public String requestfor;

        @SerializedName("music")
        public String music;

        @SerializedName("printing_service")
        public String printing_service;

        @SerializedName("theme_name")
        public String theme_name;

        @SerializedName("theme_photo")
        public String theme_photo;

        @SerializedName("sub_theme_name")
        public String sub_theme_name;

        @SerializedName("gkc_setup_id")
        public String gkc_setup_id;

        @SerializedName("user_id")
        public String user_id;

        @SerializedName("cause_type")
        public String cause_type;

        @SerializedName("cause_id")
        public String cause_id;

        @SerializedName("theme_id")
        public String theme_id;

        @SerializedName("language_id")
        public String language_id;

        @SerializedName("sub_theme_id")
        public String sub_theme_id;

        @SerializedName("no_of_chants")
        public String no_of_chants;

        @SerializedName("self_chant_no")
        public String self_chant_no;

        @SerializedName("no_of_participants")
        public String no_of_participants;

        @SerializedName("participant_chant_no")
        public String participant_chant_no;

        @SerializedName("start_datetime")
        public String start_datetime;

        @SerializedName("end_datetime")
        public String end_datetime;

        @SerializedName("puja_type")
        public String puja_type;

        @SerializedName("photo")
        public String photo;

        @SerializedName("archana")
        public String archana;

        @SerializedName("description")
        public String description;

        @SerializedName("message")
        public String message;

        @SerializedName("participant_add")
        public String participant_add;

        @SerializedName("created_by")
        public String created_by;

        @SerializedName("created_date")
        public String created_date;

        @SerializedName("modified_by")
        public String modified_by;

        @SerializedName("modified_date")
        public String modified_date;

        @SerializedName("last_modified_id")
        public String last_modified_id;

    }
}
