package com.namakoti.karmic.helpingOthers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.AddParticipantsBean;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.SubmitRequestBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.namakoti.karmic.helpingOthers.OthersDetailsActivity.OTHERS_BEAN_KEY;

/**
 * Created by anusha on 3/22/2018.
 */

public class RequestAcceptActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.acceptRb)
    RadioButton mAcceptRb;

    @BindView(R.id.doChantRb)
    RadioButton mDoChantRb;

    @BindView(R.id.countLL)
    LinearLayout mCountLL;

    @BindView(R.id.countEtv)
    EditText mCountEtv;

    @BindView(R.id.notAbleToHelpRb)
    RadioButton mNotAbleToHelpRb;

    @BindView(R.id.knowOfRb)
    RadioButton mKnowOfRb;

    @BindView(R.id.requestRg)
    RadioGroup mRequestRG;

    @BindView(R.id.includeEmailLL)
    LinearLayout mIncludeEmailLL;

    @BindView(R.id.parentEmailLL)
    LinearLayout mParentEmailLL;


    @BindView(R.id.doneTV)
    TextView mDoneTV;

    @BindView(R.id.cancelTV)
    TextView mCancelTV;

    private RequestAcceptActivity mActivity;
    private EditText mAddNameEtv;
    private EditText mAddEmailEtv;
    private ImageView mAddIV;
    private ImageView mPreviousImageView;
    private KarmicOthersBean.RequestGkcDetails mOthersDetailsBean;
    private String mRadioButtonType;
    private JSONArray mParticipantsJArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_dialog);
        ButterKnife.bind(this);
        mActivity = this;

        setToolbarWithBack(getString(R.string.karmic_chanting));
        mRadioButtonType = Constants.I_ACCEPT;
        mAddNameEtv = mIncludeEmailLL.findViewById(R.id.addNameEtv);
        mAddEmailEtv = mIncludeEmailLL.findViewById(R.id.addEmailEtv);
        mAddIV = mIncludeEmailLL.findViewById(R.id.addIV);
        mPreviousImageView = mAddIV;
        setListeners();
        getBundle();
    }

    private void setListeners() {
        mAddIV.setOnClickListener(this);
        mRequestRG.setOnCheckedChangeListener(this);
        mCancelTV.setOnClickListener(this);
        mDoneTV.setOnClickListener(this);
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mOthersDetailsBean = (KarmicOthersBean.RequestGkcDetails) intent.getSerializableExtra(OTHERS_BEAN_KEY);
        }
    }

    private void getRequestStatus() {
        showProgress(true, mActivity);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.KARMIC_REQUESTSTATUS_URL, null,
                        ServiceMethod.KARMIC_REQUEST_STATUS, Request.Method.GET, this);

    }

    private void addParticipants() {
        showProgress(true, mActivity);
        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).appParticipants(mActivity,
                mParticipantsJArray, mOthersDetailsBean.gkc_setup_id);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.ADD_PARTICIPANTS_URL, params, ServiceMethod.ADD_PARTICIPANTS, Request.Method.POST, this);
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);

        if (object != null && object instanceof SubmitRequestBean) {
            SubmitRequestBean bean = (SubmitRequestBean) object;
            String msg = bean.message;
            if (mRadioButtonType.equalsIgnoreCase(Constants.I_WILL_NOT_KNOW_OF)) {
                addParticipants();
            } else {
                Utils.showToast(mActivity, "Request submitted successfully");
                sendIntentFinish();
            }
        } else if (object != null && object instanceof AddParticipantsBean) {
            AddParticipantsBean bean = (AddParticipantsBean) object;

            if (bean.participants.size() > 0) {
                Utils.showToast(mActivity, "successfully participants added.");
            }
            sendIntentFinish();
        }
    }

    public void sendIntentFinish() {
        Intent intent = new Intent();
        intent.setAction(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
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
            case R.id.addIV:
                Drawable imageDrawable = ((ImageView) view).getDrawable();
                Drawable plus = ContextCompat.getDrawable(mActivity, R.drawable.ic_add_orange);
                Drawable.ConstantState constantStateDrawableA = imageDrawable.getConstantState();
                Drawable.ConstantState constantStateDrawableB = plus.getConstantState();

                if (constantStateDrawableA.equals(constantStateDrawableB)) {
                    mPreviousImageView = (ImageView) view;
                    mPreviousImageView.setImageResource(R.drawable.ic_cross);
                    onAddField(view);

                } else {
                    onDelete(view);
                }
                break;
            case R.id.cancelTV:
                finish();
                break;
            case R.id.doneTV:
                acceptRequest();
                break;
        }
    }

    public void onAddField(final View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.add_phone_layout, null);
        ImageView addIv = rowView.findViewById(R.id.addIV);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        rowView.setLayoutParams(params);
        addIv.setOnClickListener(this);
        // Add the new row before the add field button.
        mParentEmailLL.addView(rowView, mParentEmailLL.getChildCount());
    }

    public void onDelete(View v) {
        mParentEmailLL.removeView((View) v.getParent());
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checked) {
        switch (checked) {
            case R.id.acceptRb:
                mRadioButtonType = Constants.I_ACCEPT;
                mCountLL.setVisibility(View.GONE);
                mParentEmailLL.setVisibility(View.GONE);
                break;
            case R.id.doChantRb:
                mRadioButtonType = Constants.I_DO_CHANT;
                mCountLL.setVisibility(View.VISIBLE);
                mParentEmailLL.setVisibility(View.GONE);
                break;
            case R.id.notAbleToHelpRb:
                mRadioButtonType = Constants.I_WILL_NOT_HELP;
                mCountLL.setVisibility(View.GONE);
                mParentEmailLL.setVisibility(View.GONE);
                break;
            case R.id.knowOfRb:
                mRadioButtonType = Constants.I_WILL_NOT_KNOW_OF;
                mCountLL.setVisibility(View.GONE);
                mParentEmailLL.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void acceptRequest() {
        boolean isValid = false;
        Map<String, String> params = new HashMap<String, String>();
        String chant = "";
        if (mRadioButtonType.equalsIgnoreCase(Constants.I_ACCEPT)) {
            isValid = true;
            chant = mOthersDetailsBean.participant_chant_no;
        } else if (mRadioButtonType.equalsIgnoreCase(Constants.I_DO_CHANT)) {
            String count = mCountEtv.getText().toString();
            if (TextUtils.isEmpty(count)) {
                mCountEtv.requestFocus();
                mCountEtv.setError(getString(R.string.error_count));
            } else {

                double enterNumber = Double.parseDouble(count);
                double partChant = Double.parseDouble(mOthersDetailsBean.participant_chant_no);
                double result = (enterNumber / 100.0f) * 10;

                if (enterNumber > result && enterNumber < partChant) {
                    isValid = true;
                    chant = count;
                } else {
                    Utils.showAlertDialog(this, "Error", "Please enter number 10% " +
                            "greater than of participant chant number (" + result + ")and less than participant chant number (" + partChant + ")", null, null, false, true);
                }
            }

        } else if (mRadioButtonType.equalsIgnoreCase(Constants.I_WILL_NOT_HELP)) {
            isValid = true;
            chant = "0";
        } else if (mRadioButtonType.equalsIgnoreCase(Constants.I_WILL_NOT_KNOW_OF)) {
            mParticipantsJArray = Utils.participantsPhoneNumberValidation(mParentEmailLL, mActivity);
            if (Constants.IS_EMPTY_EMAIL) {
                Constants.IS_EMPTY_EMAIL = false;
                isValid = true;
                chant = "0";
            }
        }
        if (isValid) {
            showProgress(true, mActivity);
            params.put("status", mRadioButtonType);
            params.put("user_id", mUserIdString);
            params.put("username", "" + UserInfoBean.getInstance().getUserName(mActivity));
            params.put("gkc_setup_id", mOthersDetailsBean.gkc_setup_id);
            params.put("requested_participant_id", mOthersDetailsBean.requested_participant_id);
            params.put("participant_chant_no", mOthersDetailsBean.participant_chant_no);
            params.put("chants", chant);

            VolleyUtil.getInstance().
                    volleyStringRequest(mActivity, Constants.GKC_REQUEST_SUBMIT_URL, params, ServiceMethod.SUBMIT_REQUEST, Request.Method.POST, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
