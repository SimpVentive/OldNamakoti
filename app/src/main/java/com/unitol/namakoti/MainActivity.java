package com.unitol.namakoti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.web.Container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = "MainActivity2";
    private static MainActivity instance;
    TextView signin, register;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate =========");

        boolean isLoggedIn = MyApplication.mPref.getBoolean(getResources().getString(R.string.pref_login_flag_key, ""), false);
        Log.i(TAG, "" + isLoggedIn);

        setContentView(R.layout.main_new2);

        initTracking();

        LinearLayout login_layout = (LinearLayout) findViewById(R.id.login_layout);
        LinearLayout start_nama = (LinearLayout) findViewById(R.id.start_nama);
        ImageView introduction = (ImageView) findViewById(R.id.introduction);
        introduction.setOnClickListener(this);

        ImageView sendLogs = (ImageView) findViewById(R.id.imageView5);
        sendLogs.setOnClickListener(this);

        if (isLoggedIn) {
            login_layout.setVisibility(View.GONE);
            start_nama.setVisibility(View.VISIBLE);

            start_nama.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MainActivity.this, Container.class);
                    startActivity(in);
                    finish();
                }
            });

        } else {
            login_layout.setVisibility(View.VISIBLE);
            start_nama.setVisibility(View.GONE);

            instance = this;
            signin = (TextView) findViewById(R.id.signin_id);
            register = (TextView) findViewById(R.id.register_id);

            signin.setOnClickListener(this);
            register.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent in = null;
        switch (v.getId()) {
            case R.id.signin_id:
                in = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(in);
//			finish();
                break;
            case R.id.register_id:
                in = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(in);
//			finish();
                break;

            case R.id.introduction:
                in = new Intent(MainActivity.this, IntroductionActivity.class);
                startActivity(in);
                break;
            case R.id.imageView5:
                getLog();
                break;
            default:
                break;
        }
    }


    private static final String processId = Integer.toString(android.os.Process.myPid());

    public static StringBuilder getLog() {

        StringBuilder builder = new StringBuilder();

        try {
            String[] command = new String[]{"logcat", "-d", "-v", "threadtime"};

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processId)) {
                    builder.append(line);
                    //Code here
                }
            }

            String logs = builder.toString();
            Log.e(TAG, "logs == " + logs);
        } catch (IOException ex) {
            Log.e(TAG, "getLog failed", ex);
        }

        return builder;
    }

    private ProgressDialog mProgressDialog;
    private InstallReferrerClient referrerClient;

    public void initTracking() {
        Debug.e(TAG, "initTracking Call");
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Processing");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        // Initialize the Install Referrer Client
        referrerClient = InstallReferrerClient.newBuilder(MainActivity.this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Debug.e(TAG, "responseCode - " + responseCode);
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        obtainReferrerDetails();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        MyApplication.mEditor.putString(MainActivity.this.getString(R.string.pref_referrer_url_key), "").commit();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        MyApplication.mEditor.putString(MainActivity.this.getString(R.string.pref_referrer_url_key), "").commit();
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Debug.e(TAG, "onInstallReferrerServiceDisconnected Call");
            }
        });
    }

    private void obtainReferrerDetails() {
        ReferrerDetails response = null;
        try {
            response = referrerClient.getInstallReferrer();
            Debug.e(TAG, "response - " + response.toString());
            String referrerUrl = response.getInstallReferrer();
            Debug.e(TAG, "referrerUrl - " + referrerUrl);
            long referrerClickTime = response.getReferrerClickTimestampSeconds();
            Debug.e(TAG, "referrerClickTime - " + referrerClickTime);
            long appInstallTime = response.getInstallBeginTimestampSeconds();
            Debug.e(TAG, "appInstallTime - " + appInstallTime);
            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
            Debug.e(TAG, "instantExperienceLaunch - " + instantExperienceLaunched);
            if (referrerUrl != null && !referrerUrl.isEmpty()) {
                MyApplication.mEditor.putString(MainActivity.this.getString(R.string.pref_referrer_url_key), referrerUrl).commit();
                parseReferrerUrl(referrerUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void parseReferrerUrl(String referrerUrl) {
        try {
            Uri uri = Uri.parse("https://example.com?" + referrerUrl);

            String utmSource = uri.getQueryParameter("utm_source");
            if (utmSource != null && !utmSource.isEmpty()) {
                Debug.e(TAG, "UTM Source: " + utmSource);
            }

            String utmMedium = uri.getQueryParameter("utm_medium");
            if (utmMedium != null && !utmMedium.isEmpty()) {
                Debug.e(TAG, "UTM Medium: " + utmMedium);
            }

            String utmTerm = uri.getQueryParameter("utm_term");
            if (utmTerm != null && !utmTerm.isEmpty()) {
                Debug.e(TAG, "UTM Term: " + utmTerm);
            }

            String utmContent = uri.getQueryParameter("utm_content");
            if (utmContent != null && !utmContent.isEmpty()) {
                Debug.e(TAG, "UTM Content: " + utmContent);
            }

            String utmCampaign = uri.getQueryParameter("utm_campaign");
            if (utmCampaign != null && !utmCampaign.isEmpty()) {
                Debug.e(TAG, "UTM Campaign: " + utmCampaign);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
