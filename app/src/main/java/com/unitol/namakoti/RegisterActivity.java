package com.unitol.namakoti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.unitol.namakoti.web.CheckPhoneAsyncUrlConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends Activity implements OnClickListener {

    public static final String TAG = "RegisterActivity";
    EditText fname_edittext;
    EditText mobie_edittext;

    ProgressDialog mProgressDialog;
    Button create_my_account;
    private TextView country_code;
    private TextView sign_in_as_different_user;
    private int responseCode;

    private static RegisterActivity instance;

    public static RegisterActivity getInstance() {
        return instance;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_new);

        instance = this;

        ImageView back = findViewById(R.id.back);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.sign_up_hdr);

        NamaKotiUtils.setupUI(findViewById(R.id.parent), this);

        mobie_edittext = findViewById(R.id.mobie_edittext);
        create_my_account = findViewById(R.id.create_my_account_id);
        sign_in_as_different_user = findViewById(R.id.sign_in_as_different_user);

        fname_edittext = findViewById(R.id.fname_edittext);
        country_code = findViewById(R.id.country_code);

        create_my_account.setOnClickListener(this);
        sign_in_as_different_user.setOnClickListener(this);

        back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        Log.i(TAG, "onActivityResult");
        if (requestCode == ConstantsManager.COUNTRY_CODE) {
            String jsonString = data.getExtras().getString(ConstantsManager.COUNTRY_JSON);
            try {
                JSONObject obj = new JSONObject(jsonString);
                String c_code = obj.getString("c_code");
                String c_isd = obj.getString("c_isd");
                country_code.setText(c_isd);
                country_code.setTag(c_code);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent in = null;
        int id = v.getId();
        if (id == R.id.back_image) {
            finish();
        } else if (id == R.id.home_image) {
            finish();
        } else if (id == R.id.create_my_account_id) {
            String mobile = mobie_edittext.getText().toString().trim();
            String fname = fname_edittext.getText().toString().trim();


            if (fname.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Please Enter first name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mobile.length() == 0) {
                Toast.makeText(getApplicationContext(),
                                "Please Enter Mobile number", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (!haveNetworkConnection()) {
                NamaKotiUtils.enableWifiSettings(RegisterActivity.this);
                Log.i(TAG, "We do not have the network connection");
                return;
            }

            Log.i(TAG, "We have the network connection");

            String phoneNo = mobie_edittext.getText().toString().trim();
            if (phoneNo.length() > 0) {
                CheckPhoneAsyncUrlConnection async = new CheckPhoneAsyncUrlConnection(RegisterActivity.this);
                async.execute(phoneNo);
            }

        } else if (id == R.id.sign_in_as_different_user) {
            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
        }
    }

    public void register() {
        if (Networking.isNetworkAvailable(getApplicationContext())) {
            new RegisterTask().execute(fname_edittext.getText().toString().trim(), mobie_edittext.getText().toString().trim());
        } else {
            Toast.makeText(RegisterActivity.this, "Please Check Yout Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private class RegisterTask extends AsyncTask<String, String, Boolean> {

        String isSuccess = "";
        private String eMobile = "";

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setTitle("Processing");
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            eMobile = params[1];

            try {
                // Log.e("RegActivity", "Venues detailes ValuePairs-->" +
                // nameValuePairs);
                isSuccess = useHttpurl(params);
                if (responseCode == 200) {
                    return true;
                }

                if (isSuccess.length() != 0) {
//					strResult = response;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute ============= " + result);
            try {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(isSuccess);
                String status = jsonObject.optString("status");
                String msg = jsonObject.optString("message");
                Log.e(TAG, "msg: " + msg);
                if (status.equals("success")) {
                    if (!msg.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        MyApplication.mEditor.putString(RegisterActivity.this.getString(R.string.pref_mobile_otp_key), eMobile).commit();
                        startActivity(new Intent(RegisterActivity.this, VerifyOtpActivity.class));
                    } else {
                        NamaKotiUtils.showDialog(RegisterActivity.this, getResources().getString(R.string.signup_error), msg);
                    }
                } else {
                    if (!msg.isEmpty()) {
                        NamaKotiUtils.showDialog(RegisterActivity.this, getResources().getString(R.string.signup_error), msg);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String useHttpurl(String[] inputs) {
            String ip = getLocalIpAddress();
            Log.e(TAG, "ip ======= " + ip);
            String responseString = "";

            try {
                URL url = new URL(WebConstants.INSET_USER);
                Debug.e(TAG, "url:" + WebConstants.INSET_USER);
                Log.e(TAG, "url ======= " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(20000);
                connection.setConnectTimeout(60000);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                /* Passing Name and Telephone Number to HTTP Reqest API */
                responseString = getResponseByWithOutNameValuePair(inputs[0], inputs[1], ip, connection);
                Log.e(TAG, "responseString ====== " + responseString);

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    return responseString;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        /**
         * Gets the response code to the given number.
         */
        public String getResponseByWithOutNameValuePair(String name, String mobile, String ip, HttpURLConnection connection) {

            String response = "";
            try {
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(getPostDataString(getParamsHashMap(name, mobile, ip)));
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
            Log.e(TAG, "params ======= " + result.toString());
            return result.toString();
        }

        /**
         * Params to send API in HashMap format.
         */
        @NonNull
        private HashMap<String, String> getParamsHashMap(String name, String mobile, String ip) {
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("mobile", mobile);
            params.put("name", name);
            params.put("ip", ip);
            String referrerUrl = MyApplication.mPref.getString(getString(R.string.pref_referrer_url_key), "");
            if (!referrerUrl.isEmpty()) {
                try {
                    Uri uri = Uri.parse("https://example.com?" + referrerUrl);

                    String utmSource = uri.getQueryParameter("utm_source");
                    if (utmSource != null && !utmSource.isEmpty()) {
                        Debug.e(TAG, "UTM Source: " + utmSource);
                        params.put("utm_source", utmSource);
                    }

                    String utmMedium = uri.getQueryParameter("utm_medium");
                    if (utmMedium != null && !utmMedium.isEmpty()) {
                        Debug.e(TAG, "UTM Medium: " + utmMedium);
                        params.put("utm_medium", utmMedium);
                    }

                    String utmTerm = uri.getQueryParameter("utm_term");
                    if (utmTerm != null && !utmTerm.isEmpty()) {
                        Debug.e(TAG, "UTM Term: " + utmTerm);
                        params.put("utm_term", utmTerm);
                    }

                    String utmContent = uri.getQueryParameter("utm_content");
                    if (utmContent != null && !utmContent.isEmpty()) {
                        Debug.e(TAG, "UTM Content: " + utmContent);
                        params.put("utm_content", utmContent);
                    }

                    String utmCampaign = uri.getQueryParameter("utm_campaign");
                    if (utmCampaign != null && !utmCampaign.isEmpty()) {
                        Debug.e(TAG, "UTM Campaign: " + utmCampaign);
                        params.put("utm_campaign", utmCampaign);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return params;
        }


        public String getLocalIpAddress() {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                     en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return Formatter.formatIpAddress(inetAddress.hashCode());
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("IP Address", ex.toString());
            }
            return "";
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
