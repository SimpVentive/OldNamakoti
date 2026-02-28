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
import com.unitol.namakoti.RegisterActivity;
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

public class VerifyOtpAsyncUrlConnection extends AsyncTask<String, Void, String> {

    private static final String TAG = "VerifyOtpAsyncUrlConnection";
    private ProgressDialog progress;
    private VerifyOtpActivity ctx;
    private String eMobile;
    private String eOtp;
    String responseString = "";
    private int responseCode;

    public VerifyOtpAsyncUrlConnection(VerifyOtpActivity c) {
        ctx = c;
        createProgresDialog(ctx);
    }

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

    @SuppressLint({"StringFormatInvalid", "LongLogTag"})
    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);

        String msg = null;
        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            msg = jsonObject.optString("message");

            if (msg.length() == 0) {// successful login

                ctx.finish();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_email_key), eMobile).commit();
//		    	MyApplication.mEditor.putString(ctx.getString(R.string.pref_pwd_key), pwd).commit();
                MyApplication.mEditor.putBoolean(ctx.getResources().getString(R.string.pref_login_flag_key, ""), true).commit();

                if (MainActivity.getInstance() != null)
                    MainActivity.getInstance().finish();

                if (SignInActivity.getInstance() != null)
                    SignInActivity.getInstance().finish();

                if (RegisterActivity.getInstance() != null)
                    RegisterActivity.getInstance().finish();

                ctx.startActivity(new Intent(ctx, Container.class));
            } else {
                Log.e(TAG, "msg: " + msg);
//				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signin_error), msg);
//				ctx.startActivity(new Intent(ctx, MainActivityPhone.class));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.e(TAG, "onPostExecute === " + progress);
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

		/*try {
			JSONObject jsonObject = new JSONObject(isSuccess);
			msg = jsonObject.optString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
        eOtp = inputs[1].trim();
        try {
            URL url = new URL(WebConstants.CHECK_VERIFY_SMS);
            Debug.e(TAG, "url:" + WebConstants.CHECK_VERIFY_SMS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(eMobile, eOtp, connection);
            Log.e(TAG, "responseString useHttpUrl ====== " + responseString);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                JSONObject array = new JSONObject(responseString);
                String userID = array.getString("user_id");
                String user_name = array.getString("user_name");
                String userNamas = array.getString("namas");
                String fullname = array.getString("fullname");
                String upgradeuseramt = array.getString("upgradeuseramt");
                int upgrade = array.getInt("upgrade");

                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_namas_key), userNamas).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_id_key), userID).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_name_key), user_name).commit();
                MyApplication.mEditor.putInt(ctx.getString(R.string.pref_user_upgrade_key), upgrade).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_fullname_key), fullname).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_upgrade_amount_key), upgradeuseramt).commit();

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
    public String getResponseByWithOutNameValuePair(String mobile, String otp, HttpURLConnection connection) {

        String response = "";
        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(getParamsHashMap(mobile, otp)));
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
    @NonNull
    private HashMap<String, String> getParamsHashMap(String mobile, String otp) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("mobile", mobile);
        params.put("otp", otp);
        return params;
    }
}
