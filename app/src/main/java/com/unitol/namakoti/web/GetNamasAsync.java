package com.unitol.namakoti.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.adapters.GodsAdapter;
import com.unitol.namakoti.model.ErrorBean;
import com.unitol.namakoti.model.GodNamesBean;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
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

@SuppressLint("StaticFieldLeak")
public class GetNamasAsync extends AsyncTask<TextView, Void, String> {

    private static final String TAG = "GetNamasAsync";
    private ProgressDialog progressDialog;

    private Activity ctx;
    private List<GodNamesBean> mGodList;
    private GodsAdapter adapter;
    private int responseCode;

    public GetNamasAsync(Activity c, List<GodNamesBean> cGodList, GodsAdapter cAdapter) {
        ctx = c;
        mGodList = cGodList;
        adapter = cAdapter;
        createProgresDialog(ctx);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected String doInBackground(@NonNull TextView... params) {

        if (NamaKotiUtils.isNetworkAvailable(ctx)) {
            String responseString = useHttpurl();
            try {
                Log.e(TAG, "responseCode: " + responseCode);

                if (responseCode == 200) {
                    return responseString;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String useHttpurl() {
        String responseString = "";
        String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");
        Log.e(TAG, "userID ======= " + userID);
        try {
            URL url = new URL(WebConstants.GODS);
            Debug.e(TAG, "url:" + WebConstants.GODS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(userID, connection);
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
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", handset_name);
        return params;
    }

    @Override
    protected void onPostExecute(String responseStr) {
        super.onPostExecute(responseStr);
        if (responseStr != null) {// successful
            try {
                if (responseStr.contains("failed")) {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    String status = "";
                    String message = "";
                    if (jsonObject.has("status"))
                        status = jsonObject.getString("status");
                    if (jsonObject.has("message"))
                        message = jsonObject.getString("message");
                    ErrorBean errorBean = new ErrorBean(status, message);
                    NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.error), errorBean.message);
                } else {
                    MyApplication.mEditor.putString(ctx.getString(R.string.pref_gods_key), responseStr).commit();
                    parseAndSetTags(responseStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.error), ctx.getResources().getString(R.string.get_god_list_error));
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
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait...");
    }

    private void parseAndSetTags(String godsAvailable) {
        try {
            if (godsAvailable != null) {
                List<GodNamesBean> godsWithIDList = new ArrayList<>();
                JSONArray array = new JSONArray(godsAvailable);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String theme_id = "";
                    String theme_name = "";
                    String printing_service = "";
                    String photo = "";
                    if (jsonObject.has("theme_id"))
                        theme_id = jsonObject.getString("theme_id");
                    if (jsonObject.has("theme_name"))
                        theme_name = jsonObject.getString("theme_name");
                    if (jsonObject.has("printing_service"))
                        printing_service = jsonObject.getString("printing_service");
                    if (jsonObject.has("photo"))
                        photo = jsonObject.getString("photo");
                    GodNamesBean godNamesBeanWithID = new GodNamesBean(theme_id, theme_name, printing_service, photo);
                    godsWithIDList.add(godNamesBeanWithID);
                }
                mGodList.clear();
                mGodList.addAll(godsWithIDList);
                adapter.updateList(mGodList, -1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
