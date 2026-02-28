package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.PublishnamaFragment;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.WebConstants;

import org.json.JSONArray;
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

public class TemplesAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "TemplesAsync";
    private ProgressDialog progressDialog;
    private Activity ctx;
    private int responseCode;
//	private Spinner choose_the_temple;
//	private Spinner print_type;
//	private Spinner noOfNamasToPrint;

    public TemplesAsync(Activity c, Spinner choose_the_temple, Spinner print_type, Spinner noOfNamasToPrint) {
        ctx = c;
//		this.choose_the_temple = choose_the_temple;
//		this.print_type = print_type;
//		this.noOfNamasToPrint = noOfNamasToPrint;
        createProgresDialog(ctx);
    }

    public TemplesAsync(Activity c) {
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
            Log.e(TAG, "responseString: " + responseString);
            responseString = useHttpurl(params);

            if (responseCode == 200) {
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_temples_key), responseString).commit();
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
            URL url = new URL(WebConstants.TEMPLE_PRINTS);
            Debug.e(TAG, "url:" + WebConstants.TEMPLE_PRINTS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
            responseString = getResponseByWithOutNameValuePair(inputs[0], connection);
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
            Log.e(TAG, "url ====== " + getPostDataString(getParamsHashMap(phone)));
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

        return result.toString();
    }

    /**
     * Params to send API in HashMap format.
     */
    @NonNull
    private HashMap<String, String> getParamsHashMap(String themeId) {
        HashMap<String, String> params = new HashMap<String, String>();

        String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");

        params.put("user_id", userID);
        params.put("theme_id", themeId);

        return params;
    }


    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);


        try {
            JSONObject obj = new JSONObject(isSuccess);
            String temples = obj.getJSONArray("temples").toString();
            String printTypes = obj.getJSONArray("print_type").toString();
            String countNumbers = obj.getJSONArray("count_number").toString();
            // successful
            MyApplication.mEditor.putString(ctx.getResources().getString(R.string.pref_count_numbers_pd_key), countNumbers).commit();
            MyApplication.mEditor.putString(ctx.getResources().getString(R.string.pref_print_types_pd_key), printTypes).commit();
            MyApplication.mEditor.putString(ctx.getResources().getString(R.string.pref_temples_pd_key), temples).commit();
            PublishnamaFragment.getInstance().onTempleSelected(setMyAdapterForTemples(temples));
            PublishnamaFragment.getInstance().onPrintTypesSelected(setMyAdapterForPrintType(printTypes));
            PublishnamaFragment.getInstance().onCountSelected(setMyAdapterForCount(countNumbers));
//				 setMyAdapterForTemples(temples);
//				 setMyAdapterForPrintType(printTypes);
//				 setMyAdapterForCount(countNumbers);

            progressDialog.dismiss();
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e(TAG, "e: " + e.toString());
            e.printStackTrace();
            Log.e(TAG, "e: " + e.getMessage());
        }
    }

    private JSONObject setMyAdapterForTemples(String noOfLanguages) {
//		Log.i(TAG, "noOfLanguages: "+noOfLanguages);
        List<JSONObject> templesWithIDList = new ArrayList<JSONObject>();
        JSONArray array;
        JSONObject jsonObject = null;
        try {
            array = new JSONArray(noOfLanguages);
            if (array.length() > 0)
                jsonObject = array.getJSONObject(0);
			/*for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
//				Log.i(TAG, "jsonObject: "+jsonObject);
				templesWithIDList.add(jsonObject);
			}*/

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
            Log.e(TAG, "e: " + e.getMessage());
        }

//		TemplesAdapter templesAdapter = new TemplesAdapter(ctx, templesWithIDList);
//		choose_the_temple.setAdapter(templesAdapter);
        return jsonObject;
    }

    private JSONObject setMyAdapterForPrintType(String noOfLanguages) {
//		Log.i(TAG, "noOfLanguages: "+noOfLanguages);
        List<JSONObject> templesWithIDList = new ArrayList<JSONObject>();
        JSONObject jsonObject = null;
        try {
            JSONArray array = new JSONArray(noOfLanguages);
            if (array.length() > 0)
                jsonObject = array.getJSONObject(0);
			/*for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
//				Log.i(TAG, "jsonObject: "+jsonObject);
				templesWithIDList.add(jsonObject);
			}*/

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
            Log.e(TAG, "e: " + e.getMessage());
        }

//		PrintTypesAdapter templesAdapter = new PrintTypesAdapter(ctx, templesWithIDList);
//		print_type.setAdapter(templesAdapter);

        return jsonObject;
    }

    private JSONObject setMyAdapterForCount(String noOfLanguages) {
//		Log.i(TAG, "noOfLanguages: "+noOfLanguages);
        List<JSONObject> templesWithIDList = new ArrayList<JSONObject>();
        JSONObject jsonObject = null;
        try {
            JSONArray array = new JSONArray(noOfLanguages);
            if (array.length() > 0)
                jsonObject = array.getJSONObject(0);
			/*for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
//				Log.i(TAG, "jsonObject: "+jsonObject);
				templesWithIDList.add(jsonObject);
			}*/

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
            Log.e(TAG, "e: " + e.getMessage());
        }

//		CountAdapter templesAdapter = new CountAdapter(ctx, templesWithIDList);
//		noOfNamasToPrint.setAdapter(templesAdapter);
        return jsonObject;
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }
}
