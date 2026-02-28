package com.unitol.namakoti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.util.Networking;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.SignInAsync;

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

public class RegisterActivityOld extends Activity implements OnClickListener {

	public static final String TAG = "RegisterActivity";
	EditText username_edittext, password_edittext, cpassword_edittext,
			salution_edittext, fname_edittext, lname_edittext, mname_edittext,
			dob_edittext;
	EditText address1_edittext, address2_edittext, pobox_edittext,
			postal_edittext, city_edittext, state_edittext, country_edittext,
			email_edittext, mobie_edittext;
	String username_edittext1, password_edittext1, cpassword_edittext1,
			salution_edittext1, fname_edittext1, lname_edittext1,
			mname_edittext1, dob_edittext1, address1_edittext1,
			address2_edittext1, pobox_edittext1, postal_edittext1,
			city_edittext1, state_edittext1, country_edittext1,
			email_edittext1, mobie_edittext1;;

	ImageView save_imageView;
	ProgressDialog mProgressDialog;
	ImageView back_image;// , home_image, edit_image, settings_image,
							// exit_image;
	Button create_my_account;
	private Button country_signup;
	private TextView country_code;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_old);
		ImageView back = (ImageView) findViewById(R.id.back);
		ImageView logo = (ImageView) findViewById(R.id.logo);
		logo.setImageResource(R.drawable.sign_up_hdr);

		NamaKotiUtils.setupUI(findViewById(R.id.parent), this);
		
		username_edittext = (EditText) findViewById(R.id.username_edittext);
		password_edittext = (EditText) findViewById(R.id.password_edittext);
		cpassword_edittext = (EditText) findViewById(R.id.cpassword_edittext);
		mobie_edittext = (EditText) findViewById(R.id.mobie_edittext);
		create_my_account = (Button) findViewById(R.id.create_my_account_id);
		country_signup = (Button) findViewById(R.id.country_signup);

		fname_edittext = (EditText) findViewById(R.id.fname_edittext);
		lname_edittext = (EditText) findViewById(R.id.lname_edittext);
		country_code = (TextView) findViewById(R.id.country_code);
		 
		create_my_account.setOnClickListener(this);
		/*username_edittext1 = "Rajendhar1";
		password_edittext1 = "raju1";
		cpassword_edittext1 = "raju1";
		mobie_edittext1 = "9703964111";
		fname_edittext1 = "Katta";
		lname_edittext1 = "Rajendhar";*/

		username_edittext.setText(username_edittext1);
		password_edittext.setText(password_edittext1);
		cpassword_edittext.setText(cpassword_edittext1);
		mobie_edittext.setText(mobie_edittext1);
		fname_edittext.setText(fname_edittext1);
		lname_edittext.setText(lname_edittext1);

		username_edittext.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String result = s.toString().replaceAll(" ", "");
			    if (!s.toString().equals(result)) {
			    	username_edittext.setText(result);
			    	username_edittext.setSelection(result.length());
			    	Toast.makeText(RegisterActivityOld.this, "Spaces are not allowed", Toast.LENGTH_SHORT).show();
			    }
			}
		});
		
		password_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				String userName = username_edittext.getText().toString().trim();

				if(userName.length() >= 5){
					
				} else{
					Toast.makeText(RegisterActivityOld.this, "Enter minmum of five characters", Toast.LENGTH_SHORT).show();
				}
				
				/*if(hasFocus && onFocusChanged(username_edittext, "In valid email")){ //It is valid email CheckEmailAsync async
			 		
					 CheckEmailAsync asyncEmail = new CheckEmailAsync(RegisterActivity.this);
					 asyncEmail.execute(username_edittext.getText().toString().trim()); }
				 else {
//					 passwordEditText.setCursorVisible(false); 
				 }*/
				
				/*if (hasFocus && userName.length() > 0) {
					// Do nothing
					// It is valid email
					CheckEmailAsync async = new CheckEmailAsync(RegisterActivity.this);
					async.execute(userName);
				} else if(hasFocus){
					Toast.makeText(RegisterActivity.this,getResources().getString(R.string.username_exist), Toast.LENGTH_SHORT).show();
					// password_edittext.setFocusable(true);
				}*/

			}
		});

		cpassword_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						String pwd = password_edittext.getText().toString().trim();

						if (hasFocus && pwd.length() > 0) {
							// Do nothing
						} else if(hasFocus){
							Toast.makeText(RegisterActivityOld.this, getResources().getString(R.string.enter_pwd), Toast.LENGTH_SHORT).show();
//							password_edittext.setFocusable(true);
							password_edittext.clearFocus();
							password_edittext.requestFocus();
						}
					}
				});
		
		fname_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				String cPwd = cpassword_edittext.getText().toString().trim();

				if (hasFocus && cPwd.length() > 0 && cPwd.equals(password_edittext.getText().toString().trim())) {
					// Do nothing
				} else if(hasFocus){
					Toast.makeText(RegisterActivityOld.this, getResources().getString(R.string.pwd_not_same),
							Toast.LENGTH_SHORT).show();
					password_edittext.setFocusable(true);
				}
			}
		});

		mobie_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				String mobileNo = mobie_edittext.getText().toString();
				if (hasFocus) {
					
				}
				
				// String eMail = email_edittext.getText().toString().trim();

				/*
				 * if (hasFocus) { if(onFocusChanged(email_edittext,
				 * "In valid email")){ //It is valid email CheckEmailAsync async
				 * = new CheckEmailAsync(RegisterActivity.this);
				 * async.execute(email_edittext.getText().toString().trim()); }
				 * else { // passwordEditText.setCursorVisible(false); } }
				 */
			}
		});
		
		country_signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				if (!haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(RegisterActivityOld.this);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
				Log.i(TAG, "We have the network connection");
				
				Intent i = new Intent(RegisterActivityOld.this, CoutryCodeActivity.class);
				startActivityForResult(i, ConstantsManager.COUNTRY_CODE);
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		Log.i(TAG, "onActivityResult");
		switch (requestCode) {
		case ConstantsManager.COUNTRY_CODE:
			String jsonString = data.getExtras().getString(ConstantsManager.COUNTRY_JSON);
			try {
				JSONObject obj = new JSONObject(jsonString);
				 String c_code = obj.getString("c_code");
				String c_isd = obj.getString("c_isd");
				country_code.setText(c_isd);
				country_code.setTag(c_code);
				country_signup.setText(obj.getString("c_name"));
				country_signup.setTextColor(getResources().getColor(R.color.black));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	private boolean onFocusChanged(EditText et, String msg) {
		if (et.length() <= 0) {
			return false;
		} else if (!et.getText().toString().matches(ConstantsManager.emailPattern)) {
			NamaKotiUtils.showDialog(RegisterActivityOld.this, getString(R.string.signup_error), msg);
			et.clearFocus();
			et.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent in = null;
		switch (v.getId()) {
		case R.id.back_image:

			finish();
			break;
		case R.id.home_image:

			finish();
		
			break;
		case R.id.create_my_account_id:

			String user = username_edittext.getText().toString().trim().replaceAll(" ", "").trim();
			String pwd = password_edittext.getText().toString().trim();
			String cPwd = cpassword_edittext.getText().toString().trim();
			String mobile = mobie_edittext.getText().toString().trim();
			String fname = fname_edittext.getText().toString().trim();
			String lname = lname_edittext.getText().toString().trim();
			

			if (user.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter username", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (user.length() < 5) {

				Toast.makeText(RegisterActivityOld.this, "Username must be minimum of five characters", Toast.LENGTH_SHORT).show();
				return;
			}

			if (pwd.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter password", Toast.LENGTH_SHORT).show();
				return;
			} else if (pwd.length() < 5) {
				Toast.makeText(getApplicationContext(), "Password must have atleast five characters", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (cPwd.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter confirm password", Toast.LENGTH_SHORT).show();
				return;
			}

			if (fname.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter first name", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (lname.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter last name", Toast.LENGTH_SHORT).show();
				return;
			}
			

			String isdcode = (String) country_code.getTag();
			if (isdcode == null || isdcode.length() == 0) {
				Log.i(TAG, "isdcode: "+isdcode);
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.please_select_ur_country), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			
			if (mobile.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter Mobile number", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			/*if (lname.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Enter last name", Toast.LENGTH_SHORT).show();
				return;
			}*/
			 
			if (!pwd.equals(cPwd)) {
//				Toast.makeText(getApplicationContext(), "Password and confirm password must be same", Toast.LENGTH_SHORT).show();

				Toast.makeText(RegisterActivityOld.this, getResources().getString(R.string.pwd_not_same), Toast.LENGTH_SHORT).show();
				password_edittext.setFocusable(true);
				
				return;
			}
			

			if (!haveNetworkConnection()) {
				NamaKotiUtils.enableWifiSettings(RegisterActivityOld.this);
				Log.i(TAG, "We do not have the network connection");
				return;
			} 
			
			Log.i(TAG, "We have the network connection");
			
			String phoneNo = mobie_edittext.getText().toString().trim();
			if (phoneNo.length() > 0) {
//				CheckPhoneAsync async = new CheckPhoneAsync(RegisterActivityOld.this);
//				async.execute(phoneNo);
				// return;
			} else {

				return;
			}

			/*if (Networking.isNetworkAvailable(getApplicationContext())) {
				new RegisterTask().execute();
			} else {
				Toast.makeText(RegisterActivity.this,
						"Please Check Yout Internet", Toast.LENGTH_LONG).show();
			}
*/
			break;

		default:
			break;
		}
	}
	
	public void register() {
		if (Networking.isNetworkAvailable(getApplicationContext())) {
			new RegisterTask().execute();
		} else {
			Toast.makeText(RegisterActivityOld.this, "Please Check Yout Internet", Toast.LENGTH_SHORT).show();
		}
	}

	private class RegisterTask extends AsyncTask<String, String, Boolean> {
		
		String response = "";

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(RegisterActivityOld.this);
			mProgressDialog.setTitle("Processing");
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}

		@NonNull
		@Override
		protected Boolean doInBackground(String... params) {
			String isdcode = country_code.getTag().toString();
			HttpPost postMethod = new HttpPost(WebConstants.INSET_USER);
			Debug.e(TAG, "url:" + WebConstants.INSET_USER);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", username_edittext.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("password", password_edittext.getText().toString().trim()));

//			nameValuePairs.add(new BasicNameValuePair("first_name", fname_edittext.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("last_name", lname_edittext.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("mobile", mobie_edittext.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("isdcode", isdcode));

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
					return true;
				}

				if (response.length() != 0) {
//					strResult = response;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {

			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}

			if (result) {
//				finish();
				MyApplication.mEditor.putString(getString(R.string.pref_email_key), username_edittext.getText().toString().trim()).commit();
				MyApplication.mEditor.putString(getString(R.string.pref_pwd_key), password_edittext.getText().toString().trim()).commit();
				MyApplication.mEditor.putBoolean(getResources().getString(R.string.pref_login_flag_key, ""), true).commit();
				MainActivity.getInstance().finish();
				/*startActivity(new Intent(RegisterActivity.this, MainActivityPhone.class));*/
				

				SignInAsync signInAsync = new SignInAsync(RegisterActivityOld.this);
				signInAsync.execute(username_edittext.getText().toString().trim(), password_edittext.getText().toString().trim());
				
				/*Intent i = new Intent(RegisterActivity.this, SignInActivity.class);
				i.putExtra(ConstantsManager.FROM_REGISTER, ConstantsManager.FROM_REGISTER);
				startActivity(i);
				NamaKotiUtils.showSignUpDialog(RegisterActivity.this, getResources().getString(R.string.signup_success), getResources().getString(R.string.signup_success));*/
			} else {
//				NamaKotiUtils.showDialog(ctx, ctx., msg);

				try {
					if (response != null && response.length() > 0) {
						JSONObject jsonObject = new JSONObject(response);
						String msg = jsonObject.getString("message");
						NamaKotiUtils.showDialog(RegisterActivityOld.this, getResources().getString(R.string.signup_error), msg);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	
	public boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}

}
