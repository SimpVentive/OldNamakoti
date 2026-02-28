package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.unitol.namakoti.R;
import com.unitol.namakoti.RegisterActivity;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckPhoneAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "CheckPhoneAsync";
    private ProgressDialog progressDialog;
    private RegisterActivity ctx;
    private String eMail;
    String responseString = "";

    public CheckPhoneAsync(RegisterActivity c, String phone) {
        ctx = c;
        createProgresDialog(ctx);
    }

    public CheckPhoneAsync(RegisterActivity c) {
        ctx = c;
        createProgresDialog(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        eMail = params[0].trim();

        responseString = null;
        HttpClient httpclient = NamaKotiUtils.getHttpclient();
        HttpPost httpPost = null;
        HttpResponse response;
        int responseCode;
        httpPost = new HttpPost(WebConstants.CHECK_PHONE + "?mobile=" + eMail);
        Debug.e(TAG, "url:" + WebConstants.CHECK_PHONE  + "?mobile=" + eMail);

        try {

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();

            responseString = EntityUtils.toString(response.getEntity());
            Log.e(TAG, "responseCode: " + responseCode);
            Log.e(TAG, "responseString: " + responseString);

            if (responseCode == 200) {
                return responseString;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);

        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            String status = jsonObject.optString("status");
            String msg = jsonObject.optString("message");
            if (status.equals("success")) {
                ctx.register();
            } else {
                NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), msg);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isSuccess != null) {// successful
        } else {
        }

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }
}
