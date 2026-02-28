package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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

public class LanguagesAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "LanguagesAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	private int responseCode;
//	private Spinner language_spinner;
//	private Spinner namam_type_spinner;

	public LanguagesAsync(Activity c, Spinner language_spinner) {
		ctx = c;
//		this.language_spinner = language_spinner;
//		this.namam_type_spinner = namam_type_spinner;
		createProgresDialog(ctx);
	}

	public LanguagesAsync(Activity c) {
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
				Log.e(TAG, "responseString: "+responseString);
				responseString = useHttpurl();
				
				if (responseCode == 200) {
					return responseString;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e: "+e.toString());
			}
//		}
		
		
		
		return null;
	}

	private String useHttpurl() {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.LANGUAGES);
			Debug.e(TAG, "url:" + WebConstants.LANGUAGES);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(60000);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
			responseString = getResponseByWithOutNameValuePair(connection);
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
	public String getResponseByWithOutNameValuePair(HttpURLConnection connection) {

		String response = "";
		try {
			OutputStream outputStream = connection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			bufferedWriter.write(getPostDataString(getParamsHashMap()));
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

	/** Params to send API in HashMap format. */
	@NonNull
	private HashMap<String, String> getParamsHashMap() {
		HashMap<String, String> params = new HashMap<String, String>();
		return params;
	}

	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		
		if(isSuccess != null){// successful
			MyApplication.mEditor.putString(ctx.getString(R.string.pref_number_of_languages_key), isSuccess).commit();
			List<String> list = setMyAdapter(isSuccess);
			preferForNationalLanguage(list);
		} else {
//		   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), ctx.getResources().getString(R.string.email_exist));
		}

		try {
			progressDialog.dismiss();
		} catch (Exception e) {
		}
	}

	private void preferForNationalLanguage(List<String> list) {
		Log.e(TAG, "list ===== "+list);
		int index = 0;
		String english = "EN-:-English";
		String hindi = "HI-:-Hindi";
		if(list.contains(english)) {
			index = list.indexOf(english);
		} else if (list.contains(hindi)) {
			index = list.indexOf(hindi);
		}
		SaveNamaFragment.getInstace().onLanguageSelected(list.get(index));
	}

	private List<String> setMyAdapter(String noOfLanguages) {
		List<String> languagesWithIDList = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(noOfLanguages);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String language_id = jsonObject.getString("language_id");
				String language_name = jsonObject.getString("language_name");
				String languagesWithID = language_id+ConstantsManager.SPLIT_KEY+language_name;
				languagesWithIDList.add(languagesWithID);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return languagesWithIDList;
		
//		LanugaugeAdapter adapter = new LanugaugeAdapter(ctx, languagesWithIDList);
//		language_spinner.setAdapter(adapter);
		
//		SaveNamaFragment.getInstace().updateChatsAsync();
		
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}
