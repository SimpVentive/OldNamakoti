package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anusha on 12/5/2017.
 */
public class ErrorBean {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

}
