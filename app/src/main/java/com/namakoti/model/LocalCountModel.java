package com.namakoti.model;

/**
 * Created by anusha on 12/24/2017.
 */

public class LocalCountModel {

    public String user_id;
    public String namakoti_id;
    public String total_count;

    public LocalCountModel(String user_id, String namakoti_id, String total_count) {
        super();
        this.user_id = user_id;
        this.namakoti_id = namakoti_id;
        this.total_count = total_count;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNamakoti_id() {
        return namakoti_id;
    }

    public void setNamakoti_id(String namakoti_id) {
        this.namakoti_id = namakoti_id;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }


}
