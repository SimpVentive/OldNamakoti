package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;

import com.unitol.namakoti.CountriesAdapter;
import com.unitol.namakoti.util.Debug;
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

public class CountryISDCodesForSignUpAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "CountryISDAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	private Spinner language_spinner;
	private ListView countryList;
	private int responseCode;
//	private Spinner namam_type_spinner;

	public CountryISDCodesForSignUpAsync(Activity c, Spinner language_spinner, Spinner namam_type_spinner) {
		ctx = c;
		this.language_spinner = language_spinner;
//		this.namam_type_spinner = namam_type_spinner;
		createProgresDialog(ctx);
	}

	public CountryISDCodesForSignUpAsync(Activity c, ListView countryList) {
		ctx = c;
		this.countryList = countryList;
		createProgresDialog(ctx);
	}

	public CountryISDCodesForSignUpAsync(Activity c) {
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
			Log.e(TAG, "responseString: "+responseString);

			if (responseCode == 200) {
				return responseString;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "e: "+e.toString());
		}
		return null;
	}

	private String useHttpurl(String[] inputs) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.COUNTRY_CODES);
			Debug.e(TAG, "url:" + WebConstants.COUNTRY_CODES);
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


		return params;
	}


	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		
		if(isSuccess != null){// successful
			setMyAdapter(isSuccess);
//			editProfileFragment.updateCountryISD(isSuccess);
		} else {
//		   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), ctx.getResources().getString(R.string.email_exist));
		}

		try {
			progressDialog.dismiss();
		} catch (Exception e) {
		}
	}
	
	private void setMyAdapter(String noOfLanguages) {
		List<JSONObject> languagesWithIDList = new ArrayList<JSONObject>();
		try {
			JSONArray array = new JSONArray(noOfLanguages);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
//				String language_id = jsonObject.getString("language_id");
//				String language_name = jsonObject.getString("language_name");
//				String languagesWithID = language_id+ConstantsManager.SPLIT_KEY+language_name;
				languagesWithIDList.add(jsonObject);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CountriesAdapter adapter = new CountriesAdapter(ctx, languagesWithIDList);
		countryList.setAdapter(adapter);
		
//		SaveNamaFragment.getInstace().updateChatsAsync();
		
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}
