package com.namakoti.utils;

import android.content.Context;

import com.namakoti.base.BaseActivity;
import com.namakoti.beans.CreateBeingHelpBean;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.karmic.beingHelped.InviteParticipantsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anusha on 1/18/2018.
 */

public class JsonObjectUtils {
    private static JsonObjectUtils mJsonUtil;
    private final Context mContext;
    private final String mUserID;

    public JsonObjectUtils(Context context) {
        mUserID = String.valueOf(UserInfoBean.getInstance().getUserId(context));
        this.mContext = context;
    }

    public static JsonObjectUtils getInstance(Context context) {

        if (mJsonUtil == null)
            mJsonUtil = new JsonObjectUtils(context);
        return mJsonUtil;
    }

    public Map<String, String> getSaveNormalChantParams(Context context, String mSelectedLangCode, String mSelectedThemeId, String mSelectedChnatsSubThemeid, String chantCount, String name) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserID);
        params.put("language_id", ""+mSelectedLangCode);
        params.put("selectedgod", ""+mSelectedThemeId);//1
        params.put("selectedchant", ""+ mSelectedChnatsSubThemeid);//2
//        params.put("username", ""+UserInfoBean.getInstance().getUserName(context));
//        params.put("voiceover", chantCount);
        params.put("username", name);
        return params;
    }

    public Map<String, String> getChantListParams(String mSelectedLangCode, String mSelectedThemeId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("language_id", mSelectedLangCode);
        params.put("theme_id", ""+mSelectedThemeId);

        return params;
    }

    public Map<String, String> getSaveChantCauseParams(BaseActivity activity, String causeID, String langCode, String themeId, String subThemeid, String countEt, String exact, String personEt) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserID);
        params.put("language_id", "" + langCode);
        params.put("cause_id", ""+causeID);
        params.put("selectedgod", "" + themeId);
        params.put("selectedchant", "" + subThemeid);
        params.put("username", ""+personEt/*UserInfoBean.getInstance().getUserName(activity)*/);
        params.put("no_of_chants", countEt);
        params.put("exactcause", exact);
        return params;
    }

    public Map<String, String> createKarmicChantJson(InviteParticipantsActivity activity,String msg, String des, String archana, CreateBeingHelpBean bean) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserID);
        params.put("username", ""+UserInfoBean.getInstance().getUserName(activity));
        params.put("causefor_id", bean.getRequestId());
        params.put("cause_id", bean.getCauseId());
        params.put("selectedgod", bean.getGodId());
        params.put("selectedchant", bean.getNamam());
        params.put("language_id", bean.getLanguageId());
        params.put("no_of_chants", bean.getNoOfChants());
        params.put("self_chant_no", bean.getSelfChantNo());
        params.put("no_of_participants", bean.getNoOfPeopleHelp());
        params.put("participant_chant_no", bean.getPeopleChantNo());
        params.put("start_datetime", bean.getStartDate()+" "+bean.getStartTime());
        params.put("end_datetime", bean.getEndDate()+" "+bean.getEndTime());
        params.put("puja_type", bean.getPujaType());
        params.put("archana", archana);
        params.put("participant_add", bean.getAddParticipantsType());
        params.put("message", msg);
        params.put("description", des);

        return params;
    }
    public JSONObject createKarmicJson(InviteParticipantsActivity activity, JSONArray jArray, String msg, String yesOrNo, CreateBeingHelpBean bean) {
        JSONObject createObj = new JSONObject();
        long userName = UserInfoBean.getInstance().getUserName(activity);
        int phno = (int) userName;
        try {
            createObj.put("user_id",Integer.parseInt(mUserID));
            createObj.put("username", userName);
            createObj.put("causefor_id",bean.getRequestId());
            createObj.put("cause_id", Integer.parseInt(bean.getCauseId()));
            createObj.put("selectedgod", Integer.parseInt(bean.getGodId()));
            createObj.put("selectedchant", Integer.parseInt(bean.getNamam()));
            createObj.put("language_id", bean.getLanguageId());
            createObj.put("no_of_chants", Integer.parseInt(bean.getNoOfChants()));
            createObj.put("self_chant_no", Integer.parseInt(bean.getSelfChantNo()));
            createObj.put("no_of_participants", Integer.parseInt(bean.getNoOfPeopleHelp()));
            createObj.put("participant_chant_no", Integer.parseInt(bean.getPeopleChantNo()));
            createObj.put("start_datetime", bean.getStartDate() +" 01:20:00");
            createObj.put("end_datetime", bean.getEndDate()+" 05:10:00");
            createObj.put("puja_type", bean.getPujaType());
            createObj.put("participant_add", bean.getAddParticipantsType());
            createObj.put("archana", yesOrNo);
            createObj.put("message", msg);
            createObj.put("description", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  createObj;
    }
    public Map<String, String> appParticipants(BaseActivity activity, JSONArray jArray, String setupId) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", UserInfoBean.getInstance().getUserId(activity));
            params.put("username", UserInfoBean.getInstance().getUserName(activity));
            params.put("gkc_setup_id", setupId);
            if (jArray != null && jArray.length()> 0)
                params.put("participant", jArray);
            else
                params.put("participant", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("data", params.toString());

        return params2;
    }

    public Map<String, String> sendRemainder(BaseActivity activity, JSONArray jArray, String setupId) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", UserInfoBean.getInstance().getUserId(activity));
            params.put("username", UserInfoBean.getInstance().getUserName(activity));
            params.put("gkc_setup_id", setupId);
            if (jArray != null && jArray.length()> 0)
                params.put("participant", jArray);
            else
                params.put("participant", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("data", params.toString());

        return params2;
    }

    public JSONObject cancelRequest(ParticipantsBean.ParticipantsItemBean mParticipantsBean) {
        JSONObject params = new JSONObject();
        try {
            params.put("id", mParticipantsBean.id);
            params.put("mobile", mParticipantsBean.mobile);
            params.put("gkc_setup_id", mParticipantsBean.setup_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("data", params.toString());

        return params;
    }
}
