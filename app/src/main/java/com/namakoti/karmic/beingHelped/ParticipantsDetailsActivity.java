package com.namakoti.karmic.beingHelped;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class ParticipantsDetailsActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener {

    public static final String PARTICIPANTS_BEAN_KEY = "PARTICIPANTS_BEAN_KEY";

    @BindView(R.id.userProfileIV)
    CircularNetworkImageView mUserProfileIV;

    @BindView(R.id.nameTV)
    TextView mNameTV;

    @BindView(R.id.cityTV)
    TextView mCityTV;

    @BindView(R.id.attendingButton)
    Button mAttendingButton;

    @BindView(R.id.totalChantRequestedTV)
    TextView mTotalChantRequestedTV;

    @BindView(R.id.totalChantCompleteTV)
    TextView mTotalChantCompleteTV;

    @BindView(R.id.noOfChantsLeftTV)
    TextView mNoOfChantsLeftTV;

    @BindView(R.id.daysLeftTV)
    TextView mDaysLeftTV;

    @BindView(R.id.cancelRemainder)
    Button mCancelRemainder;

    @BindView(R.id.sendRemainder)
    Button mSendRemainder;

    private String mSetupId;
    private int mPage = 1;
    private ParticipantsDetailsActivity mActivity;
    private ParticipantsBean.ParticipantsItemBean mParticipantsBean;
    private String mDaysLeft;
    private ImageLoader mIimageLoader;
    private boolean isShowCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participants_list_details);
        ButterKnife.bind(this);
        mActivity = this;
        mIimageLoader = VolleyUtil.getInstance().imageLoader(this, com.intuit.sdp.R.dimen._65sdp);

        setToolbarWithBack(getString(R.string.participant_details));
        getBundle();
        setListeners();
    }

    private void setListeners() {
        mCancelRemainder.setOnClickListener(this);
        mSendRemainder.setOnClickListener(this);
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mParticipantsBean = (ParticipantsBean.ParticipantsItemBean) intent.getSerializableExtra(PARTICIPANTS_BEAN_KEY);
            mDaysLeft = intent.getStringExtra(ParticipantsListActivity.COUNT_DAYS_LEFT_KEY);
            isShowCancelBtn = intent.getBooleanExtra(ParticipantsListActivity.SHOW_CANCEL_BTN_KEY, false);
            String mCreatedBy = intent.getStringExtra(ParticipantsListActivity.CREATED_BY_KEY);
            /**
             * created user can only access to the cancel or send reminder. For others users we are not showing buttons
             */
            if (UserInfoBean.getInstance().getUserName(mActivity) == Long.parseLong(mCreatedBy) && isShowCancelBtn){
                showCancelButtons(true, mCancelRemainder,mSendRemainder);
                /*if (mParticipantsBean.status.equalsIgnoreCase("I accept the request")){
                    mCancelRemainder.setVisibility(View.GONE);
                }*/
            }else{
                showCancelButtons(false, mCancelRemainder,mSendRemainder);
            }
        }

        int willdo = 0;
        int chantcount = 0;
        if (mParticipantsBean != null) {
            if (mParticipantsBean.willdo != null && !TextUtils.isEmpty(mParticipantsBean.willdo))
                willdo = Integer.parseInt(mParticipantsBean.willdo);
            if (mParticipantsBean.chantcount != null && !TextUtils.isEmpty(mParticipantsBean.chantcount))
                chantcount = Integer.parseInt(mParticipantsBean.chantcount);

            mNameTV.setText(mParticipantsBean.name);
            mCityTV.setText(mParticipantsBean.city);
            mAttendingButton.setText(mParticipantsBean.status);
            mTotalChantRequestedTV.setText("" + willdo);
            mTotalChantCompleteTV.setText("" + chantcount);
            mNoOfChantsLeftTV.setText("" + (willdo - chantcount));
            mTotalChantRequestedTV.setText(mParticipantsBean.willdo);
            mDaysLeftTV.setText(mDaysLeft);
            mUserProfileIV.setImageUrl(Constants.GOD_IMAGE_BASE_URL + mParticipantsBean.userphoto, mIimageLoader);
        }
    }

    private void showCancelButtons(boolean show, Button mCancelRemainder, Button mSendRemainder) {
        if (show){
            mCancelRemainder.setVisibility(View.VISIBLE);
            mSendRemainder.setVisibility(View.VISIBLE);
        }else {
            mCancelRemainder.setVisibility(View.GONE);
            mSendRemainder.setVisibility(View.GONE);
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

    private void cancelRequest() {
        showProgress(true, mActivity);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", mParticipantsBean.id);
        params.put("mobile", mParticipantsBean.mobile);
        params.put("gkc_setup_id", mParticipantsBean.setup_id);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CANCEL_REQUEST_URL, params, ServiceMethod.CANCEL_REQUEST, Request.Method.POST, this);
    }


    private void sendRemainder() {
        showProgress(true, mActivity);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", mParticipantsBean.id);
        params.put("mobile", mParticipantsBean.mobile);
        params.put("gkc_setup_id", mParticipantsBean.setup_id);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.SEND_REMINDER_URL, params, ServiceMethod.SEND_REMINDER, Request.Method.POST, this);
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object != null && object instanceof ErrorBean) {
            ErrorBean bean = (ErrorBean) object;
            Utils.showToast(mActivity, bean.message);
            if (serviceMethod == ServiceMethod.CANCEL_REQUEST){
                sendIntentFinish();
            }
        }
    }
    public void sendIntentFinish() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
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
            case R.id.cancelRemainder:
                cancelRequest();
                break;
            case R.id.sendRemainder:
                sendRemainder();
                break;
        }
    }
}
