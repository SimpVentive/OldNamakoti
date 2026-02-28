package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.PaymentAdapter;
import com.unitol.namakoti.R;
import com.unitol.namakoti.ui.PaymentFragment;
import com.unitol.namakoti.util.Debug;
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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class TransactionsAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "TransactionsAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	private ListView listView;
	private int tag;
	private List<JSONObject> transactionsList;
	private int responseCode;

	public TransactionsAsync(Activity c, ListView listView, List<JSONObject> transactionsList, int tag2) {
		this.transactionsList = transactionsList;
		tag = tag2;
		ctx = c;
		this.listView = listView;
		createProgresDialog(ctx);
	}

	public TransactionsAsync(Activity c) {
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
				
				if (responseCode == 200) {
//					MyApplication.mEditor.putString(ctx.getString(R.string.pref_transactions_key), responseString).commit();
					return responseString;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e: "+e.toString());
			}
//		}
		
		
		
		return null;
	}

	private String useHttpurl(String[] inputs) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.TRANSACTIONS);
			Debug.e(TAG, "url:" + WebConstants.TRANSACTIONS);
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
	private HashMap<String, String> getParamsHashMap(String user_namakoti_id) {
		HashMap<String, String> params = new HashMap<String, String>();
		String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");

		params.put("user_id", userID);
		params.put("user_namakoti_id", user_namakoti_id);
		params.put("page", ""+tag);

		return params;
	}


	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		
		if(isSuccess != null){// successful
			
			String total = "";
			try {
				JSONObject jsonObject = new JSONObject(isSuccess);
				total = jsonObject.getString("total");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			int visibility = View.VISIBLE;
			
			if (total.equals("0")) {
				PaymentFragment.getInstance().onTransactions(false);
			} else {
				int totalCount = Integer.parseInt(total);
				float modulus = totalCount % 5;//gives reminder
				int pages  = totalCount / 5;
				
				if (tag > pages) {
					visibility = View.GONE;
				}
				
				if (modulus > 0) {
					pages = pages +1;
					Log.i(TAG, "Not multiple of 5, modulus: " +modulus+"pages : "+pages);
				}
				
				PaymentFragment.getInstance().totalTransactions(pages, visibility);
				PaymentFragment.getInstance().onTransactions(true);
				PaymentAdapter paymentAdapter = new PaymentAdapter(ctx, getJsonObjects(transactionsList, isSuccess));
				listView.setAdapter(paymentAdapter);
			}
		} else {
//		   NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.signup_error), ctx.getResources().getString(R.string.email_exist));
		}
		
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
		}
	}
	
	private List<JSONObject> getJsonObjects(List<JSONObject> jsonObjects, String noOfLanguages) {
		
//		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		try {
			JSONObject asonArray = new JSONObject(noOfLanguages);
			for (int i = 0; i < asonArray.length(); i++) {
				JSONObject object = asonArray.getJSONObject(""+i);
				jsonObjects.add(object);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObjects;
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}
