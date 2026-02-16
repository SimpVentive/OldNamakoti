package com.namakoti.utils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by anusha on 12/5/2017.
 */

public interface VolleyResponseListener {
    void successResponse(ServiceMethod serviceMethod, Object object);

    void errorResponse(ServiceMethod serviceMethod, Object object);

    HashMap<String, String> getParamsMap(ServiceMethod serviceMethod);

    JSONObject getParamsJson(ServiceMethod serviceMethod);

}
