package com.namakoti.beans;

import java.io.Serializable;

/**
 * Created by anusha on 2/11/2018.
 */

public class ContactsBean extends ErrorBean implements Serializable{

    public String number;

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int isSelected;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String photo;

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
