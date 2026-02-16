package com.namakoti.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.namakoti.beans.AddParticipantsBean;
import com.namakoti.beans.CauseBean;
import com.namakoti.beans.ChantsBean;
import com.namakoti.beans.CheckPhoneBean;
import com.namakoti.beans.EditProfileDetailsBean;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.KarmicRequestBean;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.beans.LanguagesBean;
import com.namakoti.beans.LoginBean;
import com.namakoti.beans.OtpBean;
import com.namakoti.beans.PangchangBean;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.beans.ProfileBean;
import com.namakoti.beans.RegisterBean;
import com.namakoti.beans.RequestCauseForBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.SubmitRequestBean;
import com.namakoti.beans.SyncChantKarmicBean;
import com.namakoti.beans.SyncChantsBean;
import com.namakoti.beans.TempleBean;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is used to parse the response and store the data in corresponding beans
 */
public class GsonUtil {

    public static Object parseResponse(Context context, ServiceMethod serviceMethod, String responseStr, HashMap<String,
            String> paramsMap) throws JSONException {

        Gson gson = new Gson();

        //Based on enum key we can store the response.
        if (serviceMethod == ServiceMethod.LOGIN) {
            return gson.fromJson(responseStr, LoginBean.class);
        } else if (serviceMethod == ServiceMethod.CHECKPHONE) {
            return gson.fromJson(responseStr, CheckPhoneBean.class);
        } else if (serviceMethod == ServiceMethod.REGISTER) {
            return gson.fromJson(responseStr, RegisterBean.class);
        }else if (serviceMethod == ServiceMethod.PROFILE) {
            return gson.fromJson(responseStr, ProfileBean.class);
        }else if (serviceMethod == ServiceMethod.EDIT_PROFILE_DETAILS) {
            return gson.fromJson(responseStr, EditProfileDetailsBean.class);
        }else if (serviceMethod == ServiceMethod.GET_GOD_NAMES) {
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<GodNamesBean>>(){}.getType();
                List<GodNamesBean> godList = new Gson().fromJson(responseStr, listType);
                return godList;
            }
        }else if (serviceMethod == ServiceMethod.GET_LANGUAGES) {
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<LanguagesBean>>(){}.getType();
                List<LanguagesBean> langList = new Gson().fromJson(responseStr, listType);
                return langList;
            }

        }else if (serviceMethod == ServiceMethod.GET_CHANTS) {
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<ChantsBean>>(){}.getType();
                List<ChantsBean> subThemeList = new Gson().fromJson(responseStr, listType);
                return subThemeList;
            }
        }
        else if (serviceMethod == ServiceMethod.SAVE_CHANT) {
//            {"status":"failed","message":"user already chanting nama of god."}
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<SaveChantsBean>>(){}.getType();
                List<SaveChantsBean> yourClassList = new Gson().fromJson(responseStr, listType);
                return yourClassList;
            }
        }
        else if (serviceMethod == ServiceMethod.SYNC_CHANT) {
            return gson.fromJson(responseStr, SyncChantsBean.class);
        }
        else if (serviceMethod == ServiceMethod.VERIFY_SMS){
            return gson.fromJson(responseStr, OtpBean.class);
        }
        else if (serviceMethod == ServiceMethod.UPDATE_PROFILE){
            return gson.fromJson(responseStr, ErrorBean.class);
        }
        else if (serviceMethod == ServiceMethod.TRANSACTIONS){
//            return gson.fromJson(responseStr, TransactionBean.class);
        }
        else if (serviceMethod == ServiceMethod.CAUSE_OPTIONS){
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<CauseBean>>(){}.getType();
                List<CauseBean> yourClassList = new Gson().fromJson(responseStr, listType);
                return yourClassList;
            }
        }
        else if (serviceMethod == ServiceMethod.KARMIC_SELF){
            return gson.fromJson(responseStr, KarmicSelfBean.class);
        }
        else if (serviceMethod == ServiceMethod.KARMIC_CAUSE_FOR){
            if (responseStr.contains("failed")){
                return gson.fromJson(responseStr, ErrorBean.class);
            }
            else{
                Type listType = new TypeToken<ArrayList<RequestCauseForBean>>(){}.getType();
                List<RequestCauseForBean> yourClassList = new Gson().fromJson(responseStr, listType);
                return yourClassList;
            }
        }
        else if (serviceMethod == ServiceMethod.CREATE_KARMIC_CHANT){
            return gson.fromJson(responseStr, KarmicSelfBean.SelfGkcDetails.class);
        }
        else if (serviceMethod == ServiceMethod.ADD_PARTICIPANTS){
            return gson.fromJson(responseStr, AddParticipantsBean.class);
        }
        else if (serviceMethod == ServiceMethod.GET_PARTICIPANTS){
            return gson.fromJson(responseStr, ParticipantsBean.class);
        }
        else if (serviceMethod == ServiceMethod.KARMIC_OTHERS){
            return gson.fromJson(responseStr, KarmicOthersBean.class);
        }
        else if (serviceMethod == ServiceMethod.KARMIC_REQUEST_STATUS){
            return gson.fromJson(responseStr, KarmicRequestBean.class);
        }
        else if (serviceMethod == ServiceMethod.CANCEL_REQUEST){
            return gson.fromJson(responseStr, ErrorBean.class);
        }
        else if (serviceMethod == ServiceMethod.SEND_REMINDER){
            return gson.fromJson(responseStr, ErrorBean.class);
        }
        else if (serviceMethod == ServiceMethod.SUBMIT_REQUEST){
            return gson.fromJson(responseStr, SubmitRequestBean.class);
        }
        else if (serviceMethod == ServiceMethod.TEMPLE_PRINTS){
            return gson.fromJson(responseStr, TempleBean.class);
        }
        else if (serviceMethod == ServiceMethod.PUSH_TOKEN){
            return gson.fromJson(responseStr, ErrorBean.class);
        }
        else if (serviceMethod == ServiceMethod.PUSH_PAYLOAD){
//            return gson.fromJson(responseStr, PushPayloadBean.class);
        }
        else if (serviceMethod == ServiceMethod.PANGCHANG){
            return gson.fromJson(responseStr, PangchangBean.class);
        }
        else if (serviceMethod == ServiceMethod.SYNC_KARMIC_CHANT){
            return gson.fromJson(responseStr, SyncChantKarmicBean.class);
        }
        else if (serviceMethod == ServiceMethod.GET_KARMIC_DETAILS_URL){
            return gson.fromJson(responseStr, SyncChantKarmicBean.class);
        }
        return null;

    }

}
