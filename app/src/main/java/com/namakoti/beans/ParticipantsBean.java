package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anusha on 12/5/2017.
 */

public class ParticipantsBean extends  ErrorBean{

    @SerializedName("participants")
    public ArrayList<ParticipantsItemBean> participants;

    public class ParticipantsItemBean implements Serializable{

        @SerializedName("id")
        public String id;

        @SerializedName("setup_id")
        public String setup_id;

        @SerializedName("name")
        public String name;

        @SerializedName("mobile")
        public String mobile;

        @SerializedName("status")
        public String status;

        @SerializedName("willdo")
        public String willdo;

        @SerializedName("chantcount")
        public String chantcount;

        @SerializedName("userphoto")
        public String userphoto;

        @SerializedName("city")
        public String city;
    }

}
