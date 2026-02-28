package com.unitol.namakoti.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
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

public class ChangePasswordFrag extends BaseFragment {
	
	protected static final String TAG = "ChangePasswordFrag";
	EditText old_pwd,new_pwd,con_pwd;
	Button apply_change;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.change_password, container,false);
		mActivity.onFragmentTabChange(View.VISIBLE , R.drawable.change_password_hdr, 0, "", View.GONE);
		
		
		old_pwd=(EditText)v.findViewById(R.id.old_password);
		new_pwd=(EditText)v.findViewById(R.id.new_pwd);
		con_pwd=(EditText)v.findViewById(R.id.con_password_edit);
		
		apply_change=(Button)v.findViewById(R.id.apply_change_btn);
		
		apply_change.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newPwd = new_pwd.getText().toString().trim();
				String conPwd = con_pwd.getText().toString().trim();
				String oldPwd = old_pwd.getText().toString().trim();
				
				if (oldPwd.length() == 0 ) {
					Toast.makeText(mActivity, "Please enter Old password", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (newPwd.length() == 0 ) {
					Toast.makeText(mActivity, "Please enter New password ", Toast.LENGTH_SHORT).show();
					return;
				} else if (newPwd.length() < 5) {
					Toast.makeText(mActivity, "New password must have atleast five characters", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (conPwd.length() == 0 ) {
					Toast.makeText(mActivity, "Please enter Confirm password", Toast.LENGTH_SHORT).show();
					return;
				} else if (conPwd.length() < 5) {
					Toast.makeText(mActivity, "Confirm password must have atleast five characters", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (!newPwd.equals(conPwd)) {
					Toast.makeText(mActivity, "New password and Confirm password must be same", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (!oldPwd.equals(MyApplication.mPref.getString(getResources().getString(R.string.pref_pwd_key), ""))) {
					Toast.makeText(mActivity, "Please enter correct Old password", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (oldPwd.equals(newPwd)) {
					Toast.makeText(mActivity, "Old password and New password must not be same", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!mActivity.haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(mActivity);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
				Log.i(TAG, "We have the network connection");
				
				new ChangePasswordAsync().execute();
			}
		});
		
		if (new_pwd==null) {
			Toast.makeText(getActivity(), "Please Enter password", Toast.LENGTH_SHORT).show();
		}
		
		
		return v;
	}
	
	public class ChangePasswordAsync extends AsyncTask<String, Void, String>{

		private static final String TAG = "ChangePasswordAsync";
		private ProgressDialog progressDialog;
		String responseString = "";
		private int responseCode;

		public ChangePasswordAsync() {
			createProgresDialog(mActivity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			responseString = "";
			
			try {

				responseString = useHttpurl(params);
				Log.e(TAG, "responseString: "+responseString);
				if (responseCode == 200) {
			    	MyApplication.mEditor.putString(getString(R.string.pref_pwd_key), new_pwd.getText().toString().trim()).commit();
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
				URL url = new URL(WebConstants.CHANGE_PASSWORD);
				Debug.e(TAG, "url:" + WebConstants.CHANGE_PASSWORD);
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

				if (responseCode == HttpsURLConnection.HTTP_OK ) {
					String line;
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}
				} else {
					response = "";
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

			String userID = MyApplication.mPref.getString(getString(R.string.pref_user_id_key), "");
			params.put("user_id", userID);
			params.put("password", new_pwd.getText().toString().trim());

			return params;
		}

		@Override
		protected void onPostExecute(String isSuccess) {
			super.onPostExecute(isSuccess);
			try {
				Log.i(TAG, "isSuccess: "+isSuccess);
				if(!isSuccess.isEmpty()){// un successful
					JSONObject json = new JSONObject(isSuccess);
					String status = json.getString("status");
					String msg = json.getString("message");
					if (status.equals("success")) {
						old_pwd.setText("");
						new_pwd.setText("");
						con_pwd.setText("");
					}
					
					Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mActivity, "Change password is failed", Toast.LENGTH_SHORT).show();
				}
				
				progressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e: "+e.toString());
			}
		}
		
		private void createProgresDialog(Activity c) {
			progressDialog = new ProgressDialog(c);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Please wait...");
		}
}


}
