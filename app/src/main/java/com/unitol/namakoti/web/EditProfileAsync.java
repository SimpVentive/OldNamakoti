package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.ui.EditProfileFragmentUrlConnection;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.WebConstants;

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

public class EditProfileAsync extends AsyncTask<String, Void, Integer>{

	private static final String TAG = "EditProfileAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
//	private Spinner chant_spinner;
//	private EditProfileFragment editProfileFragment;
	String responseString = "";
	private EditProfileFragmentUrlConnection editProfileFragment;
	private int responseCode;

	public EditProfileAsync(Activity c, EditProfileFragmentUrlConnection editProfileFragment) {
		ctx = c;
		createProgresDialog(ctx);
		this.editProfileFragment = editProfileFragment;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
//		String keyForChant = params[0]+params[1]+ctx.getString(R.string.pref_type_of_namas_with_language_key);
//		String noOfChants = MyApplication.mPref.getString(keyForChant, null);

		String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");

			try {

				responseString = useHttpurl(userID);
				Log.e(TAG, "responseCode: "+responseCode);
//				Log.e(TAG, "responseString: "+responseString);
				
				return responseCode;
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e: "+e.toString());
			}
		
		
		
		return null;
	}

	private String useHttpurl(String user_id) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.EDIT_PROFILE_DETAILS);
			Debug.e(TAG, "url:" + WebConstants.EDIT_PROFILE_DETAILS);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(60000);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
			responseString = getResponseByWithOutNameValuePair(user_id, connection);
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

		params.put("user_id", handset_name);

		return params;
	}
	
	@Override
	protected void onPostExecute(Integer isSuccess) {
		super.onPostExecute(isSuccess);
		
		if(isSuccess == 200){// successful
//			setMyAdapter(isSuccess);
			editProfileFragment.updateView(responseString);
//			((ctx)).onBackPressed();
		} else {
//		   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), ctx.getResources().getString(R.string.email_exist));
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
