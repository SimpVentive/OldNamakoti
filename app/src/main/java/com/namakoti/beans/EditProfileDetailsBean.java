package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anusha on 12/8/2017.
 */

public class EditProfileDetailsBean extends ProfileBean {

    @SerializedName("allcountries")
    public ArrayList<AllCountriesList> allcountries;

    @SerializedName("allsalute")
    public ArrayList<CodeValues> allsalute;

    @SerializedName("allgender")
    public ArrayList<CodeValues> allgender;


    public class AllCountriesList {
        @SerializedName("cc")
        public String cc;

        @SerializedName("cname")
        public String cname;

    }

    public class CodeValues {
        @SerializedName("code")
        public String code;

        @SerializedName("name")
        public String name;

    }
}
