package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anusha on 2/11/2018.
 */

public class AddParticipantsBean extends ErrorBean {

    @SerializedName("participants")
    public ArrayList<ParticipantsBean.ParticipantsItemBean> participants;

    @SerializedName("total")
    public ArrayList<Total> total;

    public class Total{
        @SerializedName("total")
        public String total;
    }

    /*public class ParticipantsDetails{
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

    }*/
}
