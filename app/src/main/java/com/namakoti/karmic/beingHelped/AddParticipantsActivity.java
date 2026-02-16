package com.namakoti.karmic.beingHelped;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.AddParticipantsBean;
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

/**
 * Created by anusha on 5/8/2018.
 */

public class AddParticipantsActivity extends BaseActivity implements VolleyResponseListener,
        View.OnClickListener {

    @BindView(R.id.includeEmailLL)
    LinearLayout mIncludeEmailLL;

    @BindView(R.id.parentEmailLL)
    LinearLayout mParentEmailLL;


    @BindView(R.id.doneTV)
    TextView mDoneTV;

    @BindView(R.id.cancelTV)
    TextView mCancelTV;
    private View mAddNameEtv;
    private View mAddEmailEtv;
    private ImageView mAddIV;
    private ImageView mPreviousImageView;
    private AddParticipantsActivity mActivity;
    private String mSetupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_participants_dialog);
        ButterKnife.bind(this);
        mActivity = this;

        setToolbarWithBack(getString(R.string.add_participants));
        mAddNameEtv = mIncludeEmailLL.findViewById(R.id.addNameEtv);
        mAddEmailEtv = mIncludeEmailLL.findViewById(R.id.addEmailEtv);
        mAddIV = mIncludeEmailLL.findViewById(R.id.addIV);
        mPreviousImageView = mAddIV;
        setListeners();
        getBundle();
    }

    private void setListeners() {
        mAddIV.setOnClickListener(this);
        mCancelTV.setOnClickListener(this);
        mDoneTV.setOnClickListener(this);
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mSetupId = (String) intent.getStringExtra(ParticipantsListActivity.GKC_SETUP_ID_KEY);
        }
    }

    private void addParticipants(String setupId, JSONArray participantsJArray) {
        showProgress(true, mActivity);
        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).appParticipants(mActivity, participantsJArray, setupId);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.ADD_PARTICIPANTS_URL, params, ServiceMethod.ADD_PARTICIPANTS, Request.Method.POST, this);
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);

        if (object != null && object instanceof AddParticipantsBean) {
            AddParticipantsBean bean = (AddParticipantsBean) object;
            if (bean != null)
                Utils.sendUpdatedParticipantsBroadCast(mActivity,true,bean.participants,mSetupId);
        }
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
                JSONArray participantsJArray = Utils.participantsPhoneNumberValidation(mParentEmailLL, mActivity);
                if (Constants.IS_EMPTY_EMAIL) {
                    Constants.IS_EMPTY_EMAIL = false;
                    addParticipants(mSetupId, participantsJArray);
                }
                break;
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
