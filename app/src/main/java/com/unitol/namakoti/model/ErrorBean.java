package com.unitol.namakoti.model;

/**
 * Created by anusha on 12/5/2017.
 */
public class ErrorBean{

    public String status;

    public String message;
    public ErrorBean(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
