package com.unitol.namakoti.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.unitol.namakoti.MainActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = "SignInAsync";
    private ProgressDialog progressDialog;
    private Activity ctx;
    //	private String pwd;
    private String eMail = "";
    String responseString = "";

	/*public SignInAsync(Activity c, String phone) {
		ctx = c;
		createProgresDialog(ctx);
	}*/

    public SignInAsync(Activity c) {
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
        eMail = params[0].trim();
//		pwd = params[1].trim();

        HttpClient httpclient = NamaKotiUtils.getHttpclient();
        HttpPost httpPost = null;
        HttpResponse response;
        int responseCode;
        httpPost = new HttpPost(WebConstants.SIGN_IN + "?username=" + eMail/*+"&password="+pwd*/);
        Debug.e(TAG, "url:" + WebConstants.SIGN_IN + "?username=" + eMail/*+"&password="+pwd*/);
//		httpPost.setHeader("Content-Type",  "application/json");

//		Log.e(TAG,"NUMBER_PLANCODE_URL"+WebConstants.BASE_URL+WebConstants.SIGN_IN);
//		String key = eMail+WebConstants.SECRET_KEY;
//		final String hash = new String(Hex.encodeHex(DigestUtils.md5(key)));

        try {
			/*JSONObject json = new JSONObject();
//			json.put("hash", hash);
//			json.put("nickName", nickName);
			json.put("username", eMail);
			json.put("password", pwd);
			
			Log.i(TAG, "eMail: "+eMail);
			Log.i(TAG, "eMail: "+eMail);
	
			StringEntity se = new StringEntity(json.toString());
	
		    //sets the post request as the resulting string
			httpPost.setEntity(se);*/

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();

            responseString = EntityUtils.toString(response.getEntity());
            Log.e(TAG, "responseCode: " + responseCode);
            Log.e(TAG, "responseString:===> " + responseString);
            if (responseCode == 200) {
                JSONObject array = new JSONObject(responseString);
                String userID = array.getString("user_id");
                String userNamas = array.getString("namas");
                String fullname = array.getString("fullname");
                String upgradeuseramt = array.getString("upgradeuseramt");
                int upgrade = array.getInt("upgrade");

                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_namas_key), userNamas).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_user_id_key), userID).commit();
                MyApplication.mEditor.putInt(ctx.getString(R.string.pref_user_upgrade_key), upgrade).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_fullname_key), fullname).commit();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_upgrade_amount_key), upgradeuseramt).commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e: " + e.toString());
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String isSuccess) {
        super.onPostExecute(isSuccess);


        String msg = null;
        try {
            JSONObject jsonObject = new JSONObject(isSuccess);
            msg = jsonObject.optString("message");

            if (msg.length() == 0) {// successful login

                ctx.finish();
                MyApplication.mEditor.putString(ctx.getString(R.string.pref_email_key), eMail).commit();
//		    	MyApplication.mEditor.putString(ctx.getString(R.string.pref_pwd_key), pwd).commit();
                MyApplication.mEditor.putBoolean(ctx.getResources().getString(R.string.pref_login_flag_key, ""), true).commit();
                MainActivity.getInstance().finish();
                ctx.startActivity(new Intent(ctx, Container.class));
            } else {
                Log.e(TAG, "msg: " + msg);
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
//				ctx.startActivity(new Intent(ctx, MainActivityPhone.class));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
		
		/*try {
			JSONObject jsonObject = new JSONObject(isSuccess);
			msg = jsonObject.optString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }

}
