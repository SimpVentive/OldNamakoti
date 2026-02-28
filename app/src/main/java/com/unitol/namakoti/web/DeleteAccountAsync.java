package com.unitol.namakoti.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MainActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DeleteAccountAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "DeleteAccountAsync";
    private ProgressDialog progressDialog;
    private Activity ctx;
    String responseString = "";
    private int responseCode;

    public DeleteAccountAsync(Activity c, String phone) {
        ctx = c;
        createProgresDialog(ctx);
    }

    public DeleteAccountAsync(Activity c) {
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

        try {
            responseString = useHttpurl(params);
            Log.e(TAG, "responseString: " + responseString);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
        }

        return responseString;
    }

    private String useHttpurl(String[] inputs) {
        String responseString = "";

        try {
            URL url = new URL(WebConstants.DELETE_ACCOUNT);
            Debug.e(TAG, "url:" + WebConstants.DELETE_ACCOUNT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair("", connection);
            Log.e(TAG, "responseString ====== " + responseString);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return responseString;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

    /**
     * Gets the response code to the given number.
     */
    public String getResponseByWithOutNameValuePair(String phone, HttpURLConnection connection) {

        String response = "";
        try {
            Log.e(TAG, "responseCode ======= " + getPostDataString(getParamsHashMap(phone)));
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(getParamsHashMap(phone)));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            responseCode = connection.getResponseCode();
            Log.e(TAG, "responseCode ======= " + responseCode);

            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @NonNull
    private String getPostDataString(@NonNull HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Params to send API in HashMap format.
     */
    @NonNull
    private HashMap<String, String> getParamsHashMap(String handset_name) {
        HashMap<String, String> params = new HashMap<String, String>();

        String user_name = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_name_key), "");
        params.put("username", user_name);

        return params;
    }


    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);

        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            String status = jsonObject.optString("status");
            String msg = jsonObject.optString("message");

            if (status.equals("success")) {

                MyApplication.mEditor.putBoolean(ctx.getResources().getString(R.string.pref_login_flag_key, ""), false).commit();
                MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_dispatches_key));
                MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_temples_key));
                MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_fullname_key));
                MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_gods_key));
                MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_edit_profile_key));
                ctx.startActivity(new Intent(ctx, MainActivity.class));
                ctx.finish();
            } else {
                Log.e(TAG, "msg: " + msg);
                NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.delete_account_error), msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }
}
