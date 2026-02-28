package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.unitol.namakoti.ChantsAdapter;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
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

public class SaveNamaAsync extends AsyncTask<String, Void, String>{

	private static final String TAG = "SaveNamaAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	private Spinner chant_spinner;
	private int responseCode;
//	private String keyToSaveNama;

	public SaveNamaAsync(Activity c, Spinner chant_spinner) {
		ctx = c;
		this.chant_spinner = chant_spinner;
		createProgresDialog(ctx);
	}

	public SaveNamaAsync(Activity c) {
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
		String response = "";

		try {
			response = useHttpurl(params);
			Log.e(TAG, "response-->" + response);
			//response-->{"status":"success","message":"Username Successfully Added!"}

			if (responseCode == 200) {
				MyApplication.mEditor.putString(ctx.getResources().getString(R.string.pref_user_namas_key), response).commit();
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private String useHttpurl(String[] inputs) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.CREATE_NAMA);
			Debug.e(TAG, "url:" + WebConstants.CREATE_NAMA);
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
			Log.e(TAG, "responseCode ======= " + getPostDataString(getParamsHashMap(params)));
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
	private HashMap<String, String> getParamsHashMap(@NonNull String[] params) {
		HashMap<String, String> paramss = new HashMap<String, String>();

		paramss.put("user_id", params[0]);
		paramss.put("language_id", params[1]);
		paramss.put("selectedgod", params[2]);
		paramss.put("selectedchant", params[3]);
		paramss.put("username", params[4]);

		return paramss;
	}
	
	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);

		try {
			if(!isSuccess.isEmpty()){// unsuccessful
				JSONObject jsonObject = new JSONObject(isSuccess);
				String msg = jsonObject.getString("message");
				NamaKotiUtils.showDialog(ctx, ctx.getResources().getString(R.string.save_nama_error), msg);
			} else { //successful
                ctx.onBackPressed();
                ((Container)ctx).manageFragments(ConstantsManager.TAB_2);
			}
			
			progressDialog.dismiss();
		} catch (Exception e) {
			progressDialog.dismiss();
		}
	}
	
	 /**Show message with dialog*/
		public static void showDialog(final Activity context, final String title, final String msg) {

			Log.i(TAG, "showDialog: ");
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setIcon(R.drawable.alert_dialog_icon);
			alertDialog.setTitle(title);
			alertDialog.setMessage(msg);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			/*alertDialog.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// context.finish();
				}
			});*/
			AlertDialog alert = alertDialog.create();
			alert.setCancelable(false);
			alert.setCanceledOnTouchOutside(false);
			if (context != null && !context.isFinishing() && !alert.isShowing()) {
				alert.show();
			}
		}
	
	private void setMyAdapter(String noOfLanguages) {
		List<String> chantsWithIDList = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(noOfLanguages);
//			responseString: [{"sub_theme_id":"4","sub_theme_name":"Om Sai Ram"}]
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String sub_theme_id = jsonObject.getString("sub_theme_id");
				String sub_theme_name = jsonObject.getString("sub_theme_name");
				String chantssWithID = sub_theme_id+ConstantsManager.SPLIT_KEY+sub_theme_name;
				chantsWithIDList.add(chantssWithID);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ChantsAdapter adapter = new ChantsAdapter(ctx, chantsWithIDList);
		chant_spinner.setAdapter(adapter);
		
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	}
}
