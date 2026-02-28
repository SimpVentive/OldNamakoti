package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.WebConstants;

import org.apache.http.util.TextUtils;
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

public class ProfileAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "ProfileAsync";
    private ProgressDialog progressDialog;
    private Activity ctx;

    String responseString = "";
    private String gender;
    private String gothram;
    private String user_name = "";
    private String salution = "";
    private int responseCode;

    public ProfileAsync(Activity c) {
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
            responseString = useHttpurl();
            Log.e(TAG, "responseString: " + responseString);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e);
        }

        return responseString;
    }

    private String useHttpurl() {
        String responseString = "";

        try {
            URL url = new URL(WebConstants.PROFILE_DETAILS);
            Debug.e(TAG, "url:" + WebConstants.PROFILE_DETAILS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(connection);
            Log.e(TAG, "responseString useHttpurl ====== " + responseString);
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
    public String getResponseByWithOutNameValuePair(HttpURLConnection connection) {

        String response = "";
        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(getParamsHashMap()));
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
        Log.e(TAG, "response ======= " + response);
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
    private HashMap<String, String> getParamsHashMap() {
        HashMap<String, String> params = new HashMap<String, String>();
        String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");
        params.put("user_id", userID);
        return params;
    }

    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);
        Log.e(TAG, "onPostExecute ===== " + isSuccess);

        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            String user_name = jsonObject.getString("user_name");
            if (!TextUtils.isEmpty(user_name)) {
                Log.e(TAG, "user_name ===== " + user_name);
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_name_key), user_name).commit();
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
