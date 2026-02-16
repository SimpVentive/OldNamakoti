package com.namakoti.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.messaging.FirebaseMessaging;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseNavigationActivity;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.LoginBean;
import com.namakoti.gcm.FcmReceiverService;
import com.namakoti.home.EditProfileActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener {
    private String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.button_continue)
    Button mLoginButton;

    @BindView(R.id.edit_text_mobile)
    EditText mMobileNumET;

    @BindView(R.id.facebookImage)
    ImageView mFacebookImageView;

    @BindView(R.id.twitterImage)
    ImageView mTwitterImageView;

    @BindView(R.id.signupButton)
    TextView mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        mLoginButton.setOnClickListener(this);
        mFacebookImageView.setOnClickListener(this);
        mTwitterImageView.setOnClickListener(this);
        mSignupButton.setOnClickListener(this);
    }

    private void callLoginRequest() {
        String phoneNumber = mMobileNumET.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mMobileNumET.setError(getString(R.string.error_user_phone_required));
            mMobileNumET.requestFocus();
        } else if (phoneNumber.length() < 10) {
            mMobileNumET.setError(getString(R.string.error_phone_valid));
            mMobileNumET.requestFocus();
        } else {
            showProgress(true, LoginActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", phoneNumber);

            VolleyUtil.getInstance().
                    volleyStringRequest(this, Constants.LOGIN_URL, params, ServiceMethod.LOGIN, Request.Method.POST, this);
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, LoginActivity.this);
        if (object instanceof LoginBean) {
            LoginBean bean = (LoginBean) object;
            if (bean.message == null) {// successful login
                storeLoginResponse(bean, this);
            } else {
                Utils.showAlertDialog(this, "Error", " " + bean.message, null, null, false, true);
            }
        } else if (object instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) object;
            if (!list.isEmpty() && list.get(0) instanceof GodNamesBean) {
                List<GodNamesBean> mGodList = (List<GodNamesBean>) object;
                if (mGodList.size() > 0) {
                    insertGodDetails(mGodList);
                }
            }
            navigateToBaseNavigation();

            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d(TAG, "retrieve token successful : " + token);
                    sendRegistrationToServer(token, LoginActivity.this);
                } else {
                    Log.w(TAG, "token should not be null...");
                }
            }).addOnFailureListener(e -> {

                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task -> Log.v(TAG, "This is the token : " + task.getResult()));

            if (Utils.getStringFromSP(getApplicationContext(), Constants.FCM_DEVICE_TOKEN_KEY, null) != null) {
//                sendRegistrationToServer(Utils.getStringFromSP(getApplicationContext(), Constants.FCM_DEVICE_TOKEN_KEY));
            }
        } else if (object != null && object instanceof ErrorBean) {
            ErrorBean bean = (ErrorBean) object;
//            Utils.showToast(mActivity,bean.message);
        }
    }

    private void navigateToBaseNavigation() {
        Intent intent = new Intent(this, BaseNavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * This methodis used to send the device token to server
     *
     * @param
     */
   /* public void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e("LoginActivity", "sendRegistrationToServer: " + token);
        if (TextUtils.isEmpty(token)) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserInfoBean.getInstance().getUserId(this));
        params.put("token", token);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.PUSH_TOKEN_URL, params, ServiceMethod.PUSH_TOKEN, Request.Method.POST, this);
    }*/
    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, LoginActivity.this);
        if (object instanceof ErrorBean) {
            ErrorBean error = (ErrorBean) object;
            if (ServiceMethod.PUSH_TOKEN != serviceMethod) {
                handleErrorMessage(object);
            }
        }
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
        switch (view.getId()) {
            case R.id.button_continue:
                callLoginRequest();
                break;
            case R.id.facebookImage:
                Intent intent1 = new Intent(LoginActivity.this, EditProfileActivity.class);
                startActivity(intent1);
                break;
            case R.id.twitterImage:
                break;
            case R.id.signupButton:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
        }
    }
}
