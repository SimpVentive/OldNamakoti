package com.namakoti.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.CheckPhoneBean;
import com.namakoti.beans.RegisterBean;
import com.namakoti.utils.Constants;
import com.namakoti.utils.PermissionListener;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 12/5/2017.
 */
public class SignupActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, PermissionListener{

    @BindView(R.id.editText_userName)
    EditText mUsername;

    @BindView(R.id.editText_userPhoneNumber)
    EditText mPhone;

    @BindView(R.id.editText_userAddress)
    EditText mEmailAddress;

    @BindView(R.id.editText_userPincode)
    EditText mPincode;

    @BindView(R.id.advancedOption)
    CheckBox mAdvancedCb;

    @BindView(R.id.advancedLL)
    LinearLayout mAdvancedLL;

    @BindView(R.id.checkbox_selfChanting)
    CheckBox mSelfChantCb;

    @BindView(R.id.checkbox_chanting_others)
    CheckBox mSelfChantOthersCb;

    @BindView(R.id.checkbox_attending_person)
    CheckBox mAttendingPersonCb;

    @BindView(R.id.checkbox_social_cause)
    CheckBox mChantForSocialCb;

    @BindView(R.id.button_signUp)
    Button mSignupBtn;

    @BindView(R.id.imageView_userProfile)
    ImageView mProfileImage;

    @BindView(R.id.mainLL)
    LinearLayout mainLL;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap finalBitmap;
    private String path;
    private String TAG = SignupActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setToolbar();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mainLL.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        mAdvancedCb.setChecked(false);
        mSignupBtn.setOnClickListener(this);
        mProfileImage.setOnClickListener(this);
        mAdvancedCb.setOnCheckedChangeListener(this);
    }

    private void setToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.sign_up));

    }

    private void performValidation() {
        String username = mUsername.getText().toString();
        String phone = mPhone.getText().toString();
        String email = mEmailAddress.getText().toString();
        String pincode = mPincode.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_username));
            mUsername.requestFocus();
        }
        else if (username != null && username.length() > 0 && (username.length() < 4 )) {
            mUsername.setError(getString(R.string.error_name_min));
            mUsername.requestFocus();
        }
        else if (TextUtils.isEmpty(phone)) {
            mPhone.setError(getString(R.string.error_phone));
            mPhone.requestFocus();
        }
        else if (phone != null && phone.length() > 0 && !mPhonePattern.matcher(phone).matches()){
            mPhone.setError(getString(R.string.error_valid_phone));
            mPhone.requestFocus();
        }
        else if (phone.length() < 10){
            mPhone.setError(getString(R.string.error_valid_phone));
            mPhone.requestFocus();
        }
        else if (TextUtils.isEmpty(email)) {
            mEmailAddress.setError(getString(R.string.error_address));
            mEmailAddress.requestFocus();
        }
        else if (TextUtils.isEmpty(pincode)) {
            mPincode.setError(getString(R.string.error_pincode));
            mPincode.requestFocus();
        }
        else if (pincode != null && pincode.length() > 0 && (pincode.length() < 6)) {
            mPincode.setError(getString(R.string.error_pincode_max));
            mPincode.requestFocus();
        }
        /*else if (path == null){
            Utils.showAlertDialog(SignupActivity.this, getString(R.string.error), getString(R.string.please_upload_image), null, null, false, true);
        }*/
        else {
            checkPhoneRequest();
//            signUpUserRequest();
        }
    }

    private void signUpUserRequest() {
        showProgress(true, SignupActivity.this);
        String ip = "172.17.68.12"; //getLocalIpAddress();
        // name,mobile,address,zip,selfchant,otherchant,attendinperson,socialissue,ip,file

        Map<String,String> params = new HashMap<String, String>();
        params.put("mobile", mPhone.getText().toString());
        params.put("ip", ip);
        params.put("name", mUsername.getText().toString());
        params.put("zip", mPincode.getText().toString());
        params.put("selfchant", (mAdvancedCb.isChecked())? mSelfChantCb.isChecked() + "" : ""+false);
        params.put("otherchant", (mAdvancedCb.isChecked())? mSelfChantOthersCb.isChecked() + "" : ""+false);
        params.put("attendinperson", (mAdvancedCb.isChecked())? mAttendingPersonCb.isChecked() + "" : ""+false);
        params.put("socialissue", (mAdvancedCb.isChecked())? mChantForSocialCb.isChecked() + "" : ""+false);
        params.put("file",(finalBitmap != null) ? getStringImage(finalBitmap) : "");

        VolleyUtil.getInstance().
                volleyStringRequest(this,Constants.REGISTER_URL, params,
                        ServiceMethod.REGISTER, Request.Method.POST, this);

    }


    private void checkPhoneRequest() {
        showProgress(true, SignupActivity.this);

        Map<String,String> params = new HashMap<String, String>();
        params.put("mobile", mPhone.getText().toString());

        VolleyUtil.getInstance().
                volleyStringRequest(this,Constants.CHECKPHONE_URL, params,
                        ServiceMethod.CHECKPHONE, Request.Method.POST, this);

    }

    private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
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

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        path = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, SignupActivity.this);
        if (serviceMethod == ServiceMethod.UPLOAD_IMAGE){
            signUpUserRequest();
        }
        if (object != null && object instanceof RegisterBean){
            RegisterBean bean = (RegisterBean) object;
            if (bean.status.equalsIgnoreCase("success")){
                Constants.CURRENT_USER_PHONE_NUMBER = mPhone.getText().toString();
                finish();
                startActivity(new Intent(SignupActivity.this, OtpActivity.class));
            }
            else{
                Utils.showToast(SignupActivity.this,bean.message);
            }
        }
        else if (serviceMethod == ServiceMethod.CHECKPHONE) {
            CheckPhoneBean bean = (CheckPhoneBean) object;
            if (bean.status.equalsIgnoreCase("success")) {
                signUpUserRequest();
            } else {
                Utils.showToast(SignupActivity.this, bean.message);
            }
        }
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, SignupActivity.this);
        handleErrorMessage(object);
    }

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_signUp:
                performValidation();
                break;
            case R.id.imageView_userProfile:
                selectImage(REQUEST_CAMERA,SELECT_FILE, this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bm;
                if (requestCode == SELECT_FILE){
                    bm = onSelectFromGalleryResult(data);
                    finalBitmap = bm;
                    mProfileImage.setImageBitmap(bm);
//                    Uri selectedImageUri1 = data.getData();
//                    path = getPath(selectedImageUri1);
                }
                else if (requestCode == REQUEST_CAMERA){
                    bm = onCaptureImageResult(data);
                    finalBitmap = bm;
                    mProfileImage.setImageBitmap(bm);
//                    Uri selectedImageUri1 = data.getData();
//                    path = getPath(selectedImageUri1);
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()){
            case R.id.advancedOption:
                if (checked){
                    mAdvancedLL.setVisibility(View.VISIBLE);
                }
                else{
                    mAdvancedLL.setVisibility(View.GONE);
                    mSelfChantCb.setChecked(false);
                    mSelfChantOthersCb.setChecked(false);
                    mAttendingPersonCb.setChecked(false);
                    mChantForSocialCb.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onPermissionAccepted(int code) {
        if(code == Constants.REQUEST_CAMERA_PERMISSION || code == Constants.REQUEST_MULTIPLE_PERMISSION){
            cameraIntent(REQUEST_CAMERA, this);
        }else if(code == Constants.REQUEST_GALLERY_PERMISSION){
            galleryIntent(SELECT_FILE, this);
        }
    }

    @Override
    public void onPermissionDenied(int code) {
        if(code == Constants.REQUEST_CAMERA_PERMISSION){
            Utils.showToast(this, "This permission is required to capture the picture");
        }else if(code == Constants.REQUEST_GALLERY_PERMISSION){
            Utils.showToast(this, "This permission is required to select a picture");
        }else if(code == Constants.REQUEST_MULTIPLE_PERMISSION){
            Utils.showToast(this, "Please accept all permissions to proceed");
        }
    }

}
