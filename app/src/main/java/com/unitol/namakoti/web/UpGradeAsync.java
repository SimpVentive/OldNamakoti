package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.unitol.namakoti.MainActivityPhone;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.WebConstants;

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

public class UpGradeAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "UpGradeAsync";
	private ProgressDialog progressDialog;
	String responseString = "";
	private Activity mActivity;
	private int responseCode;

	public UpGradeAsync(Activity mActivity) {
		this.mActivity = mActivity;
		createProgresDialog(mActivity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
    	
		responseString = null;

		try {

			responseString = useHttpurl(params);
			Log.e(TAG, "responseString: "+responseString);
			if (responseCode == 200) {
		    	MyApplication.mEditor.putInt(mActivity.getString(R.string.pref_user_upgrade_key), 1).commit();	
				return "";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "e: "+e.toString());
		}
		
		return responseString;
	}

	private String useHttpurl(String[] inputs) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.UPDRATE);
			Debug.e(TAG, "url:" + WebConstants.UPDRATE);
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
			Log.e(TAG, "responseString ====== "+responseString);

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				return responseString;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseString;
	}

	/** Gets the response code to the given number. */
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

	/** Params to send API in HashMap format. */
	private HashMap<String, String> getParamsHashMap(String[] params) {
		HashMap<String, String> paramss = new HashMap<String, String>();

		paramss.put("user_id", params[0]);
		paramss.put("signature", params[1]);
		paramss.put("purchasedData", params[2]);

		return paramss;
	}


	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		try {
			
			if(!isSuccess.isEmpty()){// un successful
				JSONObject jsonObject = new JSONObject();
				String msg = jsonObject.getString("message");
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
			} else {
//				((MainActivityPhone)mActivity).initialiseTabHost(null);
				Log.i(TAG, "onPostExecute: "+isSuccess);
				progressDialog.dismiss();
				mActivity.finish();
				mActivity.startActivity(new Intent(mActivity, MainActivityPhone.class));
			}
			
		} catch (Exception e) {
			progressDialog.dismiss();
		}
		
		
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}