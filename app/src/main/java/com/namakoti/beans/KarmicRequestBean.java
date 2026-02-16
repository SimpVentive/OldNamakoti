package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anusha on 12/6/2017.
 */

public class KarmicRequestBean extends ErrorBean implements Serializable{

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;
}


