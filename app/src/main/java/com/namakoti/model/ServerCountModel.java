package com.namakoti.model;

/**
 * Created by anusha on 12/24/2017.
 */

public class ServerCountModel {

    public String user_id;
    public String namakoti_id;
    public String total_count;
    public String running_count;

    public ServerCountModel(String user_id, String namakoti_id,String total_count, String running_count) {
        super();
        this.user_id = user_id;
        this.namakoti_id = namakoti_id;
        this.total_count = total_count;
        this.running_count = running_count;
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

    public String getRunning_count() {
        return running_count;
    }

    public void setRunning_count(String running_count) {
        this.running_count = running_count;
    }

}
