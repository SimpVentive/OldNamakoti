package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 12/5/2017.
 */

public class ProfileBean extends  ErrorBean{
    @SerializedName("user_id")
    public int user_id;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("userphoto")
    public String userphoto;

    @SerializedName("title")
    public String title;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("gender")
    public String gender;

    @SerializedName("date_of_birth")
    public String date_of_birth;

    @SerializedName("date_of_birth2")
    public String date_of_birth2;

    @SerializedName("gothram")
    public String gothram;

    @SerializedName("address1")
    public String address1;

    @SerializedName("address2")
    public String address2;

    @SerializedName("zip_code")
    public String zip_code;

    @SerializedName("city")
    public String city;

    @SerializedName("state")
    public String state;

    @SerializedName("country_name")
    public String country_name;

    @SerializedName("email")
    public String email;

    @SerializedName("isd_code")
    public String isd_code;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("self_chant")
    public boolean self_chant;

    @SerializedName("other_chant")
    public boolean other_chant;

    @SerializedName("attending_in_person")
    public boolean attending_in_person;

    @SerializedName("chant_for_social_issue")
    public boolean chant_for_social_issue;


}
