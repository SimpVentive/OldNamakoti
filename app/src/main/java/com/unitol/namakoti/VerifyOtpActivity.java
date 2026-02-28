package com.unitol.namakoti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unitol.namakoti.ui.ForGotPassWord;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.web.VerifyOtpAsyncUrlConnection;

public class VerifyOtpActivity extends Activity {

    protected static final String TAG = "VerifyOtpActivity";
    Button verify_otp_button;
    EditText otp_digit_edit;
    String logInUserName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        logInUserName = MyApplication.mPref.getString(getString(R.string.pref_mobile_otp_key), "");

        otp_digit_edit = findViewById(R.id.otp_digit_edit);
        otp_digit_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    otp_digit_edit.setText(result);
                    otp_digit_edit.setSelection(result.length());
                    Toast.makeText(VerifyOtpActivity.this, "Spaces are not allowed", Toast.LENGTH_LONG).show();
                }
            }
        });

        verify_otp_button = findViewById(R.id.verify_otp_button);
        verify_otp_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String otp = otp_digit_edit.getText().toString().trim().replaceAll(" ", "");

                if (otp.isEmpty()) {
                    otp_digit_edit.setError("OTP required");
                    return;
                }

                if (!haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(VerifyOtpActivity.this);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                VerifyOtpAsyncUrlConnection signInAsync = new VerifyOtpAsyncUrlConnection(VerifyOtpActivity.this);
                signInAsync.execute(logInUserName, otp);
            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        TextView resend_otp = findViewById(R.id.resend_otp);
        resend_otp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(VerifyOtpActivity.this, ForGotPassWord.class);
                startActivity(i);
            }
        });

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
