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
import com.unitol.namakoti.utils.ConstantsManager;
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

public class PrintListAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "PrintListAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	String responseString = "";
	private int responseCode;

	public PrintListAsync(Activity c, String phone) {
		ctx = c;
		createProgresDialog(ctx);
	}

	public PrintListAsync(Activity c) {
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
			Log.e(TAG, "responseString: "+responseString);
			if (responseCode == 200) {
				JSONObject array = new JSONObject(responseString);
				String userNamas = array.getString("namas");
				MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_namas_key), userNamas).commit();	
				
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
			URL url = new URL(WebConstants.PRINT_LIST);
			Debug.e(TAG, "url:" + WebConstants.PRINT_LIST);
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
	private HashMap<String, String> getParamsHashMap(String handset_name) {
		HashMap<String, String> params = new HashMap<String, String>();

		String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");

		params.put("user_id", userID);

		return params;
	}


	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		
		
		String msg = null;
		try {
			JSONObject jsonObject = new JSONObject(isSuccess);
			msg = jsonObject.optString("message");
			
			if(msg.isEmpty()){// successful login

				MainActivityPhone.getInstance().finish();
				ctx.finish();
				Intent i = new Intent(ctx, MainActivityPhone.class);
				i.putExtra(ConstantsManager.PRINT_SUCCESFUL, true);
				ctx.startActivity(i);
				
			} else {
				Log.e(TAG, "msg: "+msg);
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
