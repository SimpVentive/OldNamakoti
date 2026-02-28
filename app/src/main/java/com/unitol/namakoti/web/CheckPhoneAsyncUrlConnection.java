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

public class CheckPhoneAsyncUrlConnection extends AsyncTask<String, Void, String> {

    private static final String TAG = "CheckPhoneAsync";
    private ProgressDialog progressDialog;
    private RegisterActivity ctx;
    private String eMail;
    String responseString = "";
    private int responseCode;

    public CheckPhoneAsyncUrlConnection(RegisterActivity c, String phone) {
        ctx = c;
        createProgresDialog(ctx);
    }

    public CheckPhoneAsyncUrlConnection(RegisterActivity c) {
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
        responseString = useHttpurl(params);


        try {


            Log.e(TAG, "responseCode: " + responseCode);
            Log.e(TAG, "responseString doInbag: " + responseString);

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

    private String useHttpurl(String[] inputs) {
        eMail = inputs[0].trim();

        try {
            URL url = new URL(WebConstants.CHECK_PHONE);
            Debug.e(TAG, "url:" + WebConstants.CHECK_PHONE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(eMail, connection);
            Log.e(TAG, "responseString useHttpUrl ====== " + responseString);

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
    public String getResponseByWithOutNameValuePair(String mobile, HttpURLConnection connection) {

        String response = "";
        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(getParamsHashMap(mobile)));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            responseCode = connection.getResponseCode();

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

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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
        Log.e(TAG, "result params========" + result.toString());
        return result.toString();
    }

    /**
     * Params to send API in HashMap format.
     */
    private HashMap<String, String> getParamsHashMap(String handset_name) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("mobile", handset_name);

        return params;
    }
}
