package com.unitol.namakoti.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.apache.http.util.TextUtils;
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
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ViewProfileFragment extends BaseFragment {
    private static final String TAG = "ViewProfileFragment";
    String userId, password, re_confirm_pwd, first_name,
            last_name, dob, address1, address2, po_box, postal_code, state,
            country, email, mobile;
    TextView username1, password1, re_confirm_pwd1, first_name1,
            last_name1, dob1, address11, address21, postal_code1,
            state1, country1, email1, mobile1;
    private TextView mobile_country_code1;
    private TextView city1;
    private TextView name_profile;
    private TextView gender_profile;
    private TextView gothram_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_profile_new, container, false);

        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.view_profile_hdr, R.drawable.selector_4_edit, "viewProfile", View.VISIBLE);

        username1 = (TextView) v.findViewById(R.id.username_profile);
        password1 = (TextView) v.findViewById(R.id.password_profile);
        gender_profile = (TextView) v.findViewById(R.id.gender_profile);
        gothram_profile = (TextView) v.findViewById(R.id.gothram_profile);
        re_confirm_pwd1 = (TextView) v.findViewById(R.id.re_confirm_pwd_profile);
//		salution1 = (TextView) v.findViewById(R.id.salution_profile);
        first_name1 = (TextView) v.findViewById(R.id.first_name_profile);
        last_name1 = (TextView) v.findViewById(R.id.last_name_profile);
        name_profile = (TextView) v.findViewById(R.id.name_profile);
        dob1 = (TextView) v.findViewById(R.id.dob_profile);
        address11 = (TextView) v.findViewById(R.id.address1_profile);
        address21 = (TextView) v.findViewById(R.id.address2_profile);
        postal_code1 = (TextView) v.findViewById(R.id.postal_profile);
        state1 = (TextView) v.findViewById(R.id.state_profile);
        country1 = (TextView) v.findViewById(R.id.country_profile);
        email1 = (TextView) v.findViewById(R.id.email_profile);
        mobile1 = (TextView) v.findViewById(R.id.mobile_profile);
        mobile_country_code1 = (TextView) v.findViewById(R.id.mobile_country_code);
        city1 = (TextView) v.findViewById(R.id.city_profile);

        Button editProfile = (Button) v.findViewById(R.id.edit_profile_id);
        editProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!mActivity.haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(mActivity);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");
                mActivity.pushFragments(ConstantsManager.TAB_3, new EditProfileFragmentUrlConnection(), false, true, null);
            }
        });


        ProfileAsync profileAsync = new ProfileAsync();
        profileAsync.execute();

        return v;
    }

    public class ProfileAsync extends AsyncTask<String, Void, String> {

        // private static final String TAG = "SignInAsync";
        private ProgressDialog progressDialog;
        private Activity ctx;
        String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");
        String responseString = "";
        private String gender;
        private String gothram;
        private String user_name = "";
        private String salution = "";
        private int responseCode;

        public ProfileAsync() {
            createProgresDialog(mActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            String profileAvailable = MyApplication.mPref.getString(userID + getString(R.string.pref_view_profile_key), null);

            if (!mActivity.haveNetworkConnection()) {
//				NamaKotiUtils.enableWifiSettings(mActivity);
                Log.i(TAG, "We do not have the network connection");
                return profileAvailable;
            }

            Log.i(TAG, "We have the network connection");

            responseString = useHttpurl(userID);
            // httpPost.setHeader("Content-Type", "application/json");

//			Log.e(TAG, "NUMBER_PLANCODE_URL" + WebConstants.PROFILE_DETAILS);

            try {
                Log.e(TAG, "responseCode: " + responseCode);
//				Log.e(TAG, "responseString: " + responseString);

                if (responseCode == 200) {
                    result = responseString;
                    MyApplication.mEditor.putString(userID + getString(R.string.pref_view_profile_key), responseString).commit();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "e: " + e.toString());
            }

            return result;
        }

        private String useHttpurl(String user_id) {
            String responseString = "";

            try {
                URL url = new URL(WebConstants.PROFILE_DETAILS);
                Debug.e(TAG, "url:" + WebConstants.PROFILE_DETAILS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(20000);
                connection.setConnectTimeout(60000);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                /* Passing Name and Telephone Number to HTTP Reqest API */
                responseString = getResponseByWithOutNameValuePair(user_id, connection);
                Log.e(TAG, "responseString useHttpurl ====== " + responseString);

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
        public String getResponseByWithOutNameValuePair(String user_id, HttpURLConnection connection) {

            String response = "";
            try {
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(getPostDataString(getParamsHashMap(user_id)));
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
            Log.e(TAG, "response ======= " + response);
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

        /**
         * Params to send API in HashMap format.
         */
        @NonNull
        private HashMap<String, String> getParamsHashMap(String handset_name) {
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("user_id", handset_name);

            return params;
        }

        @Override
        protected void onPostExecute(String isSuccess) {
            super.onPostExecute(isSuccess);
            Log.e(TAG, "onPostExecute ===== " + isSuccess);

            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }

            if (isSuccess != null) {// successful login
//				MyApplication.mEditor.putString(userID+getString(R.string.pref_view_profile_key), isSuccess).commit();
                setTheValuesToView(isSuccess);
            } else {

            }

        }

        private void setTheValuesToView(String responseString) {
            Log.e(TAG, "setTheValuesToView ========= " + responseString);

            JSONObject jsonObject;
            String title = "";
            try {
                jsonObject = new JSONObject(responseString);
                Log.i(TAG, "jsonObject: " + jsonObject);
//				int user_id = Integer.parseInt(jsonObject.getString("user_id").toString());

                user_name = jsonObject.getString("user_name");
                String userName = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_name_key), "");
                if (TextUtils.isEmpty(userName)) {
                    if (!TextUtils.isEmpty(user_name)) {
                        MyApplication.mEditor.putString(getString(R.string.pref_user_name_key), user_name).commit();
                    }
                }
                title = jsonObject.getString("title").toString();
                title = title.equals("null") ? "" : title;

                gender = jsonObject.getString("gender").toString();
                gender = gender.equals("null") ? "" : gender;

                gothram = jsonObject.getString("gothram").toString();
                gothram = gothram.equals("null") ? "" : gothram;

                first_name = jsonObject.getString("first_name").toString();
                last_name = jsonObject.getString("last_name").toString();
//				String middle_name = jsonObject.getString("middle_name").toString();

                dob = jsonObject.getString("date_of_birth");
                dob = dob.equals("null") ? "" : dob;

//				String door_no = jsonObject.getString("door_no").toString();
//jsonObject: {"zip_code":null,"status":"success","state":null,"date_of_birth":null,"address1":null,"address2":null,"country_name":"India","city":null,
//"first_name":"Ga","user_name":"ga@gmail.com","isdcode":"91","title":"","email":"ga@gmail.com","last_name":"Ya","gender":null,"gothram":null,"user_id":"82","mobile":"9966332211"}

                address1 = jsonObject.getString("address1");
                address1 = address1.equals("null") ? "" : address1;

                address2 = jsonObject.getString("address2");
                address2 = address2.equals("null") ? "" : address2;

                postal_code = jsonObject.getString("zip_code");
                postal_code = postal_code.equals("null") ? "" : postal_code;
                postal_code = postal_code.equals("0") ? "" : postal_code;

                String city_name = jsonObject.getString("city").toString();
                city_name = city_name.equals("null") ? "" : city_name;
                city1.setText(city_name);


                state = jsonObject.getString("state");
                state = state.equals("null") ? "" : state;

                email = jsonObject.getString("email").toString();
                mobile = jsonObject.getString("mobile").toString();

                String mobile_country_code = jsonObject.getString("isdcode").toString();
                mobile_country_code1.setText(mobile_country_code);

                country = jsonObject.getString("country_name");
                country = country.equals("null") ? "" : country;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //jsonObject: {"zip_code":null,"status":"success","middle_name":null,"state":null,"date_of_birth":null,"address1":null,"address2":null,"country_name":null,
            //"allsalute":[{"id":"1","name":"Mr."},{"id":"2","name":"Ms."},{"id":"3","name":"Mrs."},{"id":"4","name":"Dr."}],
            //"allcountries":[{"country_name":"Afghanistan","country_id":"1"},{"country_name":"Albania","country_id":"2"},{"country_name":"Algeria","country_id":"3"},{"country_name":"American Samoa","country_id":"4"},{"country_name":"Andorra","country_id":"5"},{"country_name":"Angola","country_id":"6"},{"country_name":"Anguilla","country_id":"7"},{"country_name":"Antigua and Barbuda","country_id":"8"},{"country_name":"Argentina","country_id":"9"},{"country_name":"Armenia","country_id":"10"},{"country_name":"Aruba","country_id":"11"},{"country_name":"Australia","country_id":"12"},{"country_name":"Austria","country_id":"13"},{"country_name":"Azerbaijan","country_id":"14"},{"country_name":"Bahamas","country_id":"15"},{"country_name":"Bahrain","country_id":"16"},{"country_name":"Bangladesh","country_id":"17"},{"country_name":"Barbados","country_id":"18"},{"country_name":"Belarus","country_id":"19"},{"country_name":"Belgium","country_id":"20"},{"country_name":"Belize","country_id":"21"},{"country_name":"Benin","country_id":"22"},{"country_name":"Bermuda","country_id":"23"},{"country_name":"Bhutan","country_id":"24"},{"country_name":"Bolivia","country_id":"25"},{"country_name":"Bosnia and Herzegowina","country_id":"26"},{"country_name":"Botswana","country_id":"27"},{"country_name":"Bouvet Island","country_id":"28"},{"country_name":"Brazil","country_id":"29"},{"country_name":"British Indian Ocean Territory","country_id":"30"},{"country_name":"Brunei Darussalam","country_id":"31"},{"country_name":"Bulgaria","country_id":"32"},{"country_name":"Burkina Faso","country_id":"33"},{"country_name":"Burundi","country_id":"34"},{"country_name":"Cambodia","country_id":"35"},{"country_name":"Cameroon","country_id":"36"},{"country_name":"Canada","country_id":"37"},{"country_name":"Cape Verde","country_id":"38"},{"country_name":"Cayman Islands","country_id":"39"},{"country_name":"Central African Republic","country_id":"40"},{"country_name":"Chad","country_id":"41"},{"country_name":"Chile","country_id":"42"},{"country_name":"China","country_id":"43"},{"country_name":"Christmas Island","country_id":"44"},{"country_name":"Cocos (Keeling) Islands","country_id":"45"},{"country_name":"Colombia","country_id":"46"},{"country_name":"Comoros","country_id":"47"},{"country_name":"Congo Brazzaville","country_id":"49"},{"country_name":"Congo Republic of the Democratic","country_id":"48"},{"country_name":"Cook Islands","country_id":"50"},{"country_name":"Costa Rica","country_id":"51"},{"country_name":"Croatia (Hrvatska)","country_id":"52"},{"country_name":"Cuba","country_id":"53"},{"country_name":"Cyprus","country_id":"54"},{"country_name":"Czech Republic","country_id":"55"},{"country_name":"Denmark","country_id":"56"},{"country_name":"Djibouti","country_id":"57"},{"country_name":"Dominica","country_id":"58"},{"country_name":"Dominican Republic","country_id":"59"},{"country_name":"East Timor","country_id":"60"},{"country_name":"Ecuador","country_id":"61"},{"country_name":"Egypt","country_id":"62"},{"country_name":"El Salvador","country_id":"63"},{"country_name":"Equatorial Guinea","country_id":"64"},{"country_name":"Eritrea","country_id":"65"},{"country_name":"Estonia","country_id":"66"},{"country_name":"Ethiopia","country_id":"67"},{"country_name":"Falkland Islands (Malvinas)","country_id":"68"},{"country_name":"Faroe Islands","country_id":"69"},{"country_name":"Fiji","country_id":"70"},{"country_name":"Finland","country_id":"71"},{"country_name":"France","country_id":"72"},{"country_name":"French Guiana","country_id":"73"},{"country_name":"French Polynesia","country_id":"74"},{"country_name":"French Southern Territories","country_id":"75"},
            //{"country_name":"Gabon","country_id":"76"},{"country_name":"Gambia","country_id":"77"},{"country_name":"Georgia","country_id":"78"

            username1.setText(user_name);
            name_profile.setText((salution + " " + first_name + " " + last_name).trim());
            gothram_profile.setText(gothram);
            gender_profile.setText(gender);
//			re_confirm_pwd1.setText(re_confirm_pwd);
//			salution1.setText(title);
//			first_name1.setText(first_name);
//			last_name1.setText(last_name);
            dob1.setText(dob);
            address11.setText(address1);
            address21.setText(address2);
            postal_code1.setText(postal_code);
            state1.setText(state);
            country1.setText(country);
            email1.setText(email);
            mobile1.setText(mobile);
        }

        private void createProgresDialog(Activity c) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
        }
    }
}
