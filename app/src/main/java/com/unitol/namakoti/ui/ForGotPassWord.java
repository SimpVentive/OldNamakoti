package com.unitol.namakoti.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unitol.namakoti.CoutryCodeActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.web.ForGotPasswordAsync;

import org.json.JSONException;
import org.json.JSONObject;

public class ForGotPassWord extends Activity{

	private static final String TAG = "ForGotPassWord";
	private TextView country_code;
	private Button country_signup;
	private EditText username_edittext;
	private EditText password_edittext;
	private EditText cpassword_edittext;
	private EditText mobie_edittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.forgot_pwd);
		NamaKotiUtils.setupUI(findViewById(R.id.parent), this);
		
		ImageView logo = (ImageView) findViewById(R.id.logo);
		country_code = (TextView) findViewById(R.id.country_code);
		logo.setImageResource(R.drawable.forgot_password);
    	String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");
		

		username_edittext = (EditText) findViewById(R.id.username_edit);
		username_edittext.setText(logInUserName);
		password_edittext = (EditText) findViewById(R.id.password_edit);
		cpassword_edittext = (EditText) findViewById(R.id.con_password_edit);
		mobie_edittext = (EditText) findViewById(R.id.mobile_profile);
		
		country_signup = (Button) findViewById(R.id.country_signup);
		country_signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				if (!haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(ForGotPassWord.this);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
				Log.i(TAG, "We have the network connection");
				
				Intent i = new Intent(ForGotPassWord.this, CoutryCodeActivity.class);
				startActivityForResult(i, ConstantsManager.COUNTRY_CODE);
			}
		});
		
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String user = username_edittext.getText().toString().trim();
				String pwd = password_edittext.getText().toString().trim();
				String cPwd = cpassword_edittext.getText().toString().trim();
				String mobile = mobie_edittext.getText().toString().trim();
				String isdCode = country_code.getText().toString().trim();
				
				if (user.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please Enter username", Toast.LENGTH_SHORT).show();
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
				
				if (!pwd.equals(cPwd)) {
//					Toast.makeText(getApplicationContext(), "Password and confirm password must be same", Toast.LENGTH_SHORT).show();

					Toast.makeText(ForGotPassWord.this, getResources().getString(R.string.pwd_not_same), Toast.LENGTH_SHORT).show();
					password_edittext.setFocusable(true);
					
					return;
				}
				
				if (isdCode.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please select your country", Toast.LENGTH_SHORT).show();
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

				if (!haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(ForGotPassWord.this);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
				Log.i(TAG, "We have the network connection");
				
				String phoneNo = mobie_edittext.getText().toString().trim();
				String isd = country_code.getTag().toString();
				
				if (phoneNo.length() > 0) { //parameters:mobile,isd_code,username,password
					ForGotPasswordAsync async = new ForGotPasswordAsync(ForGotPassWord.this);
					async.execute(phoneNo, isd, user, pwd);
					// return;
				} else {

					return;
				}
				
				/*String usr = username_edit.getText().toString().trim();
				String pwd = password_edit.getText().toString().trim();
				
				if (usr.length() <= 0) {
					username_edit.setError("Username required");
					return;
				}
				
				if (pwd.length() <= 0) {
					password_edit.setError("Password required");
					return;
				}
				
				SignInAsync signInAsync = new SignInAsync(SignInActivity.this);
				signInAsync.execute(usr, pwd);*/
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
