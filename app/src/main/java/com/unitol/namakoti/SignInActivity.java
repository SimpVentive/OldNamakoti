package com.unitol.namakoti;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.unitol.namakoti.web.SignInAsyncHttpUrlConnection;

import java.util.List;

public class SignInActivity extends Activity {

    protected static final String TAG = "SignInActivity";
    Button login_button;
    EditText username_edit;
    ProgressDialog mProgressDialog;
    private static SignInActivity instance;

    public static SignInActivity getInstance() {
        return instance;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");

        setContentView(R.layout.activity_signin_new);
        instance = this;

        Log.e(TAG, "onCreate =========");
//		createException();


        TextView forgot_pwd = (TextView) findViewById(R.id.forgot_pwd);

        username_edit = findViewById(R.id.username_edit);
        username_edit.setText(logInUserName);

        username_edit.addTextChangedListener(new TextWatcher() {

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
                    username_edit.setText(result);
                    username_edit.setSelection(result.length());
                    Toast.makeText(SignInActivity.this, "Spaces are not allowed", Toast.LENGTH_LONG).show();
                }
            }
        });

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String usr = username_edit.getText().toString().trim().replaceAll(" ", "");

                if (usr.length() <= 0) {
                    username_edit.setError("Username required");
                    return;
                }

                if (!haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(SignInActivity.this);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                SignInAsyncHttpUrlConnection signInAsync = new SignInAsyncHttpUrlConnection(SignInActivity.this);
                signInAsync.execute(usr);
            }
        });

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        forgot_pwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(SignInActivity.this, ForGotPassWord.class);
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

    public void createException() {
        List<String> strings = null;
        strings.get(1);
    }

}
