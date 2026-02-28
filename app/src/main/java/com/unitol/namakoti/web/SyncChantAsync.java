package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.unitol.namakoti.EnternamaFragment;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.PublishnamaFragment;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
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

public class SyncChantAsync extends AsyncTask<TextView, Void, String>{

	private static final String TAG = "SyncChantAsync";
	private ProgressDialog progressDialog;
	private Activity ctx;
	private TextView tc, pc, rc;
	private String userNamakotiId;
//	private String keyToSaveNama;
	private boolean isItFromEnterNamaFragament;
	private boolean isItFromUpgradePopUpFragment;
	private int responseCode;

	public SyncChantAsync(Activity c, String user_namakoti_id, boolean isItFromEnterNamaFragament, boolean isItFromUpgradePopUpFragment ) {
		ctx = c;
		this.userNamakotiId = user_namakoti_id;
		this.isItFromEnterNamaFragament = isItFromEnterNamaFragament;
		this.isItFromUpgradePopUpFragment = isItFromUpgradePopUpFragment;
		createProgresDialog(ctx);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(TextView... params) {
		String responseString = null;
		tc = params[0];
		pc = params[1];
		rc = params[2];

		String userID = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_id_key), "");

		String userNamas = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_namas_key), "");
		String keyLocal = ctx.getResources().getString(R.string.pref_namas_local_running_count_key);

		try {

			List<String> userNamakotiIDs = new ArrayList<String>();
			List<Integer> userNamakotiCounts = new ArrayList<Integer>();
			JSONArray namasArray = new JSONArray(userNamas);
			for (int i = 0; i < namasArray.length(); i++) {
				JSONObject object = (JSONObject) namasArray.get(i);
				String user_namakoti_id = object.getString("user_namakoti_id");
				String keyLocalFinal = userID+user_namakoti_id+keyLocal;
				int runningCountOfLocal = NamaKotiUtils.getCountForNama(keyLocalFinal);
				userNamakotiIDs.add(user_namakoti_id);
				userNamakotiCounts.add(runningCountOfLocal);
			}


			responseString = useHttpurl(userNamakotiIDs.toString(), userNamakotiCounts.toString(), userID.toString());
			Log.e(TAG, "responseString: "+responseString);
			
			if (responseCode == 200) {
				for (int i = 0; i < userNamakotiIDs.size(); i++) {
					//After success, make the running count 0 in local memory
					String keyToRemoveLocalCount = userID+userNamakotiIDs.get(i)+keyLocal;
					Log.i(TAG, "keyToRemoveLocalCount: "+keyToRemoveLocalCount);
					NamaKotiUtils.setCountForNama(keyToRemoveLocalCount, 0);
					Log.i(TAG, "local count after sync: "+NamaKotiUtils.getCountForNama(keyToRemoveLocalCount));
				}
				MyApplication.mEditor.putString(ctx.getResources().getString(R.string.pref_user_namas_key), responseString).commit();
				return "";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "e: "+e.toString());
		}
		
		return responseString;
	}

	private String useHttpurl(String one, String two, String three) {
		String responseString = "";

		try {
			URL url = new URL(WebConstants.SYNC_CHANT);
			Debug.e(TAG, "url:" + WebConstants.SYNC_CHANT);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(60000);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
			responseString = getResponseByWithOutNameValuePair(one, two, three, connection);
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
	public String getResponseByWithOutNameValuePair(String one, String two, String three, HttpURLConnection connection) {

		String response = "";
		try {
			OutputStream outputStream = connection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			bufferedWriter.write(getPostDataString(getParamsHashMap(one, two, three)));
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
	private HashMap<String, String> getParamsHashMap(String one, String two, String three) {
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("user_namakoti_id", one);
		params.put("count", two);
		params.put("user_id", three);

		return params;
	}
	
	@Override
	protected void onPostExecute(String isSuccess) {
		super.onPostExecute(isSuccess);
		Log.e(TAG, "isSuccess ==== "+isSuccess);
		try {
			if (isSuccess == null) {
				progressDialog.dismiss();
				return;
			}
			
			if (!isSuccess.isEmpty()) {// unsuccessful
				Log.e(TAG, "if condition satisfied ==== ");
			} else { // success
				Log.i(TAG, "Sync done");
				String userNamas = MyApplication.mPref.getString(ctx.getResources().getString(R.string.pref_user_namas_key), "");
				JSONArray jsonArray = new JSONArray(userNamas);
				Log.i(TAG, "user_namakoti_id: "+userNamakotiId);
				
				if(isItFromEnterNamaFragament){
					
					for (int i = 0; i < jsonArray.length(); i++) {
						
						JSONObject nama = jsonArray.getJSONObject(i);
						String userNamakotiIdFromNama = nama.getString("user_namakoti_id");
						Log.i(TAG, "nama: "+nama);
						Log.i(TAG, "userNamakotiId: "+userNamakotiIdFromNama);
						if (userNamakotiId.equals(userNamakotiIdFromNama)) { //If it is present namakoti what we are chanting, update the view
							Log.e(TAG, "userNamakotiIdFromNama: ====="+userNamakotiIdFromNama);
							EnternamaFragment.getInstance().setTheCountValuesWithUserNamas(true, nama, tc, pc, rc,"");
							break;
						}
					}
					
				} else {
					Log.i(TAG, "instance ====="+Container.getInstance());
					Log.i(TAG, "isItFromUpgradePopUpFragment ===== "+isItFromUpgradePopUpFragment);
					
					Bundle b = null;
					if (isItFromUpgradePopUpFragment) {
						b = new Bundle();
						b.putBoolean(ConstantsManager.IS_IT_FROM_POP_UP, true);
					}
					
					Container.getInstance().pushFragments(ConstantsManager.TAB_3, new PublishnamaFragment(), false, true, b);
				}
			}
			progressDialog.dismiss();

		} catch (Exception e) {
			Log.e(TAG, ""+e.toString());
			progressDialog.dismiss();
		}
	}
	
	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Syncing\nPlease wait...");
	}
}
