package com.unitol.namakoti.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SignInActivity;
import com.unitol.namakoti.VerifyOtpActivity;
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

public class SignInAsyncHttpUrlConnection extends AsyncTask<String, Void, String> {

    private static final String TAG = "SignInAsyncHttpUrlConnection";
    private SignInActivity ctx;
    private String eMobile = "";
    String responseString = "";
    private int responseCode;


    public SignInAsyncHttpUrlConnection(SignInActivity c) {
        ctx = c;
        createProgresDialog(ctx);
    }

    //	private final Context context;
    private ProgressDialog progress;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.show();
    }


    @SuppressLint("LongLogTag")
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

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);

        try {
            Log.e(TAG, "onPostExecute === " + progress);
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            String status = jsonObject.optString("status");
            String msg = jsonObject.optString("message");
            Log.e(TAG, "msg: " + msg);
            if (status.equals("success")) {
                if (!msg.isEmpty()) {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                    MyApplication.mEditor.putString(ctx.getString(R.string.pref_mobile_otp_key), eMobile).commit();
                    ctx.startActivity(new Intent(ctx, VerifyOtpActivity.class));
                } else {
                    NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signin_error), msg);
                }
            } else {
                if (!msg.isEmpty()) {
                    NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signin_error), msg);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createProgresDialog(Activity c) {
        progress = new ProgressDialog(c);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setMessage("Please wait...");
    }

    @SuppressLint("LongLogTag")
    private String useHttpurl(@NonNull String[] inputs) {
        eMobile = inputs[0].trim();
        String responseString = "";

        try {
            URL url = new URL(WebConstants.SIGN_IN_NEW);
            Debug.e(TAG, "url:" + WebConstants.SIGN_IN_NEW);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            Log.e(TAG, "url ====== " + url);
            responseString = getResponseByWithOutNameValuePair(eMobile, connection);
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
    @SuppressLint("LongLogTag")
    public String getResponseByWithOutNameValuePair(String phone, HttpURLConnection connection) {

        String response = "";
        Log.i(TAG, "phone ==== " + phone);
        try {
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
        params.put("mobile", handset_name);
        return params;
    }

}
