package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.unitol.namakoti.R;
import com.unitol.namakoti.ui.ForGotPassWord;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForGotPasswordAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "ForGotPasswordAsync";
	private ProgressDialog progressDialog;
	private ForGotPassWord ctx;
	private String eMail;
	String responseString = "";

	public ForGotPasswordAsync(ForGotPassWord c, String phone) {
		ctx = c;
		createProgresDialog(ctx);
	}

	public ForGotPasswordAsync(ForGotPassWord c) {
		ctx = c;
		createProgresDialog(ctx);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(@NonNull String... params) {//parameters:mobile,isd_code,username,password
		String mobile = params[0];
		String isdcode = params[1];
		String username = params[2];
		String password = params[3];
		HttpPost postMethod = new HttpPost(WebConstants.FORGOT_PASSWORD);
		Debug.e(TAG, "url:" + WebConstants.FORGOT_PASSWORD);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
		nameValuePairs.add(new BasicNameValuePair("isd_code", isdcode));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		String response = "";
		try {
			// Log.e("RegActivity", "Venues detailes ValuePairs-->" +
			// nameValuePairs);
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 50000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 50000;
			HttpConnectionParams
					.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(
					httpParameters);
			HttpResponse httpResponse = httpClient.execute(postMethod);
			int httpResponseCode = httpResponse.getStatusLine()
					.getStatusCode();

			response = EntityUtils.toString(httpResponse.getEntity());
			Log.i(TAG, "httpResponseCode: ======" + httpResponseCode);
			Log.e(TAG, "response-->" + response);
			//response-->{"status":"success","message":"Username Successfully Added!"}

			if (httpResponseCode == 200) {
				return response;
			}

			if (response.length() != 0) {
//				strResult = response;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
		}
		
		try {
//			{"status":"failed","message":"username, mobile number and isd code doesn't matched"}
			JSONObject jsonObject = new JSONObject(isSuccess);
			String status = jsonObject.optString("status");
			String msg = jsonObject.optString("message");
			if (status.equals("success")) {
				Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
				ctx.finish();
//				ctx.register();
			} else {
				   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.pwd_error), msg);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(isSuccess != null){// successful
			
		} else {
		}
		
		
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}
