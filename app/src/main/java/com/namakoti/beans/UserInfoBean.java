package com.namakoti.beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.namakoti.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by anusha on 12/11/2017.
 */
public class UserInfoBean {

    private static UserInfoBean mUserInfoBean;

    private long userId;
    private String fullname;
    private long userName;
    private String upgradeuseramt;
    private String upgrade;
    private String userPhoto;
    private ArrayList<ContactsBean> selectedContacts;
    private String password;

    private static final String USER_ID_SP_KEY = "User_ID_Key";
    private static final String USER_NAME_SP_KEY = "User_NAME_Key";
    private static final String FULLNAME_SP_KEY = "Fullname_Key";
    private static final String UPGRADEUSERAMT_SP_KEY = "Upgradeuseramt_Key";
    private static final String UPGRADE_SP_KEY = "Upgrade_Key";
    private static final String CONTACTS_SP_KEY = "CONTACTS_KEY";
    private static final String PROFILE_SP_KEY = "Profile_ID_Key";
    private static final String PASSWORD_SP_KEY = "PASSWORD_Key";

    private UserInfoBean(){

    }

    public static UserInfoBean getInstance(){

        if(mUserInfoBean == null){
            mUserInfoBean = new UserInfoBean();
        }

        return mUserInfoBean;
    }


    public String getProfileImage(Context context) {
        if(userPhoto == null){
            userPhoto= Utils.getStringFromSP(context, PROFILE_SP_KEY);
        }
        return userPhoto;
    }

    public void setProfileImage(String profileImage,Context context) {
        this.userPhoto = profileImage;
        Utils.saveStringInSP(context, PROFILE_SP_KEY, userPhoto);
    }


    public long getUserName(Context context) {
        if(userName == 0){
            userName = Utils.getLongFromSP(context, USER_NAME_SP_KEY);
        }
        return userName;
    }

    public void setUserName(long userName,Context context) {
        this.userName = userName;
        Utils.saveLongInSP(context, USER_NAME_SP_KEY, userName);
    }

    public long getUserId(Context context) {
        if(userId == 0){
            userId = Utils.getLongFromSP(context, USER_ID_SP_KEY);
        }
        return userId;
    }
    public void setPassword(String pwd, Context context) {
        this.password = pwd;
        Utils.saveStringInSP(context, PASSWORD_SP_KEY, password);
    }
    public String getPassword(Context context) {
        if(password == null){
            password = Utils.getStringFromSP(context, PASSWORD_SP_KEY);
        }
        return password;
    }
    public void setUserId(long userId, Context context) {
        this.userId = userId;
        Utils.saveLongInSP(context, USER_ID_SP_KEY, userId);
    }

    public boolean deleteUserInfo(Context context){
        Utils.deleteStringInSP(context, USER_ID_SP_KEY);
        Utils.deleteStringInSP(context, USER_NAME_SP_KEY);
        Utils.deleteStringInSP(context, FULLNAME_SP_KEY);
        Utils.deleteStringInSP(context, UPGRADEUSERAMT_SP_KEY);
        Utils.deleteStringInSP(context, UPGRADE_SP_KEY);
        Utils.deleteStringInSP(context, PROFILE_SP_KEY);
        Utils.deleteStringInSP(context, PASSWORD_SP_KEY);

        userId = 0;
        fullname = null;
        userName = 0;
        upgradeuseramt = null;
        upgrade = null;
        userPhoto = null;
        mUserInfoBean = null;
        password = null;
        return true;
    }

    public String getFullname(Context context) {
        if(fullname == null){
            fullname = Utils.getStringFromSP(context, FULLNAME_SP_KEY);
        }
        return fullname;
    }

    public void setFullname(String fullname,Context context) {
        this.fullname = fullname;
        Utils.saveStringInSP(context, FULLNAME_SP_KEY, fullname);
    }

    public String getUpgradeuseramt(Context context) {
        if(upgradeuseramt == null){
            upgradeuseramt = Utils.getStringFromSP(context, UPGRADEUSERAMT_SP_KEY);
        }
        return upgradeuseramt;
    }

    public void setUpgradeuseramt(String upgradeuseramt,Context context) {
        this.upgradeuseramt = upgradeuseramt;
        Utils.saveStringInSP(context, UPGRADEUSERAMT_SP_KEY, upgradeuseramt);
    }

    public String getUpgrade(Context context) {
        if(upgrade == null){
            upgrade = Utils.getStringFromSP(context, UPGRADE_SP_KEY);
        }
        return upgrade;
    }

    public void setUpgrade(String upgrade,Context context) {
        this.upgrade = upgrade;
        Utils.saveStringInSP(context, UPGRADE_SP_KEY, upgrade);
    }

    public ArrayList<ContactsBean> getContacts(Context context) {
        if(selectedContacts == null){
            selectedContacts = getList(context);
        }
        return selectedContacts;
    }

    public ArrayList<ContactsBean> getList(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(CONTACTS_SP_KEY, null);
        Type type = new TypeToken<ArrayList<ContactsBean>>() {}.getType();
        ArrayList<ContactsBean> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public void setContacts(ArrayList<ContactsBean> namas, Context context) {
        this.selectedContacts = namas;
        saveList(namas,context);
    }

    private void saveList(ArrayList<ContactsBean> namas, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(namas);

        editor.putString(CONTACTS_SP_KEY, json);
        editor.commit();
    }

}
