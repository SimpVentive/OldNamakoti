package com.namakoti.login;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.firebase.messaging.FirebaseMessaging;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseNavigationActivity;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.LoginBean;
import com.namakoti.beans.OtpBean;
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

/**
 * Created by anusha on 12/5/2017.
 */
public class OtpActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher, VolleyResponseListener {
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinFifthDigitEditText;
    private EditText mPinSixthDigitEditText;
    private EditText mPinHiddenEditText;

    @BindView(R.id.button_done)
    Button mDoneButton;
    private TextView mPhoneTV;

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Initialize EditText fields.
     */
    private void init() {
        mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
        mPinFifthDigitEditText = (EditText) findViewById(R.id.pin_fifth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.pin_hidden_edittext);
        mPinSixthDigitEditText = (EditText) findViewById(R.id.pin_sixth_edittext);
        mPhoneTV = (TextView) findViewById(R.id.phone_tv);

        mPinFirstDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mPinSecondDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mPinThirdDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mPinForthDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mPinFifthDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mPinSixthDigitEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MainLayout(this, null));

        ButterKnife.bind(this);
        mDoneButton.setOnClickListener(this);

        setToolbar();
        init();
        setPINListeners();
        mPhoneTV.setText(getString(R.string.phone_code) + " " + Constants.CURRENT_USER_PHONE_NUMBER);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.otp));

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;
            case R.id.pin_fifth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;
            case R.id.pin_sixth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 6)
                            mPinSixthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 5)
                            mPinFifthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;
                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);
        setDefaultPinBackground(mPinFifthDigitEditText);
        setDefaultPinBackground(mPinSixthDigitEditText);


        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setFocusedPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            mPinFifthDigitEditText.setText("");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 5) {
            setFocusedPinBackground(mPinSixthDigitEditText);
            mPinFifthDigitEditText.setText(s.charAt(4) + "");
            mPinSixthDigitEditText.setText("");
        } else if (s.length() == 6) {
            setDefaultPinBackground(mPinSixthDigitEditText);

            mPinSixthDigitEditText.setText(s.charAt(5) + "");
            hideSoftKeyboard(mPinSixthDigitEditText);
        }
        s.length();

    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.gray_button_round));
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.gray_button_rectangle));
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);
        mPinFifthDigitEditText.setOnFocusChangeListener(this);
        mPinSixthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinFifthDigitEditText.setOnKeyListener(this);
        mPinSixthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private void callLogin() {
        showProgress(true, OtpActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", Constants.CURRENT_USER_PHONE_NUMBER);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.LOGIN_URL, params, ServiceMethod.LOGIN, Request.Method.POST, this);
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, OtpActivity.this);
        if (object != null && serviceMethod == ServiceMethod.VERIFY_SMS) {
            OtpBean bean = (OtpBean) object;
            if (bean.status.equalsIgnoreCase("failed")) {
                Toast.makeText(OtpActivity.this, bean.message, Toast.LENGTH_LONG).show();
            } else {
//                finish();
//                Toast.makeText(OtpActivity.this, "Welcome to Namakoti! Please login with your registered mobile number.", Toast.LENGTH_LONG).show();
//                Constants.CURRENT_USER_PHONE_NUMBER = null;
                callLogin();
            }
        } else if (serviceMethod == ServiceMethod.LOGIN) {
            LoginBean bean = (LoginBean) object;
            storeLoginResponse(bean, this);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d(TAG, "retrieve token successful : " + token);
                    sendRegistrationToServer(token, OtpActivity.this);
                } else {
                    Log.w(TAG, "token should not be null...");
                }
            }).addOnFailureListener(e -> {

                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task -> Log.v(TAG, "This is the token : " + task.getResult()));

        } else if (object != null && object instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) object;
            if (!list.isEmpty() && list.get(0) instanceof GodNamesBean) {
                List<GodNamesBean> mGodList = (List<GodNamesBean>) object;
                if (mGodList != null && mGodList.size() > 0) {
                    insertGodDetails(mGodList);
                }
            }
            Intent intent = new Intent(this, BaseNavigationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (object != null && object instanceof ErrorBean && serviceMethod != ServiceMethod.PUSH_TOKEN) {
            ErrorBean error = (ErrorBean) object;
            Utils.showAlertDialog(this, "Error", " " + error.message, null, null, false, true);
        }
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, OtpActivity.this);
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
        switch (view.getId()) {
            case R.id.button_done:
                if (performValidations()) {
                    callOtpRequest();
                }
                break;
        }
    }

    private void callOtpRequest() {
        showProgress(true, OtpActivity.this);

        String first = mPinFirstDigitEditText.getText().toString();
        String second = mPinSecondDigitEditText.getText().toString();
        String third = mPinThirdDigitEditText.getText().toString();
        String fourth = mPinForthDigitEditText.getText().toString();
        String fifth = mPinFifthDigitEditText.getText().toString();
        String sixth = mPinSixthDigitEditText.getText().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", Constants.CURRENT_USER_PHONE_NUMBER);
        params.put("otp", first + second + third + fourth + fifth + sixth);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.VERIFY_SMS, params,
                        ServiceMethod.VERIFY_SMS, Request.Method.POST, this);
    }

    private boolean performValidations() {
        if (TextUtils.isEmpty(mPinFirstDigitEditText.getText().toString())) {
            mPinFirstDigitEditText.setError(getString(R.string.error_otp));
            mPinFirstDigitEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPinSecondDigitEditText.getText().toString())) {
            mPinSecondDigitEditText.setError(getString(R.string.error_otp));
            mPinSecondDigitEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPinThirdDigitEditText.getText().toString())) {
            mPinThirdDigitEditText.setError(getString(R.string.error_otp));
            mPinThirdDigitEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPinForthDigitEditText.getText().toString())) {
            mPinForthDigitEditText.setError(getString(R.string.error_otp));
            mPinForthDigitEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPinFifthDigitEditText.getText().toString())) {
            mPinFifthDigitEditText.setError(getString(R.string.error_otp));
            mPinFifthDigitEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPinSixthDigitEditText.getText().toString())) {
            mPinSixthDigitEditText.setError(getString(R.string.error_otp));
            mPinSixthDigitEditText.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    /**
     * Custom LinearLayout with overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
    public class MainLayout extends LinearLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.otp_layout, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();
            Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

            if (actualHeight >= proposedHeight) {
                // Keyboard is shown
                if (mPinHiddenEditText.length() == 0)
                    setFocusedPinBackground(mPinFirstDigitEditText);
                else
                    setDefaultPinBackground(mPinFirstDigitEditText);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(smsReceiver, new IntentFilter(Constants.OTP_NUMBER_INTENT_FILTER_KEY), Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(smsReceiver, new IntentFilter(Constants.OTP_NUMBER_INTENT_FILTER_KEY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsReceiver);
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.LOG)
                Log.d("", "otp number " + intent.getStringExtra(Constants.OTP_NUMBER_KEY));

            updateOTPinFields(intent.getStringExtra(Constants.OTP_NUMBER_KEY));
        }
    };


    private void updateOTPinFields(String otp) {

        if (TextUtils.isEmpty(otp))
            return;

        int i = 0;
        for (char c : otp.toCharArray()) {
            if (i == 0) {
                mPinFirstDigitEditText.setText(c + "");
            } else if (i == 1) {
                mPinSecondDigitEditText.setText(c + "");
            } else if (i == 2) {
                mPinThirdDigitEditText.setText(c + "");
            } else if (i == 3) {
                mPinForthDigitEditText.setText(c + "");
            } else if (i == 4) {
                mPinFifthDigitEditText.setText(c + "");
            } else if (i == 5) {
                mPinSixthDigitEditText.setText(c + "");
            }

            i++;
        }

    }
}

