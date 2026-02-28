package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SaveNamaFragment;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.WebConstants;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ChantAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "ChantAsync";
    private ProgressDialog progressDialog;
    private Activity ctx;
    private int responseCode;
//	private Spinner chant_spinner;

    public ChantAsync(Activity c, Spinner chant_spinner) {
        ctx = c;
//		this.chant_spinner = chant_spinner;
        createProgresDialog(ctx);
    }

    public ChantAsync(Activity c) {
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
        String responseString = null;

        try {
            responseString = useHttpurl(params);
            Log.e(TAG, "responseString: " + responseString);

            if (responseCode == 200) {
                return responseString;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
        }
        return null;
    }

    private String useHttpurl(String[] inputs) {
        String responseString = "";

        try {
            URL url = new URL(WebConstants.CHANTS);
            Debug.e(TAG, "url:" + WebConstants.CHANTS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(inputs, connection);
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
    public String getResponseByWithOutNameValuePair(String[] params, HttpURLConnection connection) {

        String response = "";
        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(getParamsHashMap(params)));
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
    private HashMap<String, String> getParamsHashMap(@NonNull String[] param) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("language_id", param[0]);
        params.put("theme_id", param[1]);

        return params;
    }

    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);

        if (isSuccess != null) {// successful
            MyApplication.mEditor.putString(ctx.getString(R.string.pref_chants_with_languages_key), isSuccess).commit();
            List<String> chantsWithIDList = setMyAdapter(isSuccess);
            SaveNamaFragment.getInstace().onNamamSelected(chantsWithIDList.get(0));
        } else {
//		   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), ctx.getResources().getString(R.string.email_exist));
        }

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
    }

    @NonNull
	private List<String> setMyAdapter(String noOfLanguages) {
        List<String> chantsWithIDList = new ArrayList<String>();
        try {
            JSONArray array = new JSONArray(noOfLanguages);
//			responseString: [{"sub_theme_id":"4","sub_theme_name":"Om Sai Ram"}]

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String sub_theme_id = jsonObject.getString("sub_theme_id");
                String sub_theme_name = jsonObject.getString("sub_theme_name");
//				sub_theme_name = ""+android.text.Html.fromHtml(sub_theme_name);
                sub_theme_name = "" + Html.fromHtml(sub_theme_name);
                String chantssWithID = sub_theme_id + ConstantsManager.SPLIT_KEY + sub_theme_name;
                chantsWithIDList.add(chantssWithID);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chantsWithIDList;
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }
}
