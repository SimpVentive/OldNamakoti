package com.namakoti.karmic.helpingOthers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseNavigationActivity;
import com.namakoti.beans.AddParticipantsBean;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.SubmitRequestBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.karmic.beingHelped.AddParticipantsActivity;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.utils.CalendarUtils;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class OthersDetailsActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        VolleyResponseListener {

    public static final String OTHERS_BEAN_KEY = "OTHERS_BEAN_KEY";
    public static final String REQUEST_TYPE_KEY = "REQUEST_TYPE_KEY";
    public static final String PAGE_COUNT_KEY = "PAGE_COUNT_KEY";
    public static final String IS_FROM_PUSH_KEY = "IS_FROM_PUSH_KEY";
    private static final int CHANT_DETAILS_CODE = 100;

    @BindView(R.id.namamTV)
    TextView mNamamTV;

    @BindView(R.id.volumeIV)
    ImageView mVolumeIV;

    @BindView(R.id.chantsRequiredTV)
    TextView mChantsRequiredTV;

    @BindView(R.id.totalChantRequestedTV)
    TextView mTotalChantRequestedTV;

    @BindView(R.id.requestCauseTv)
    TextView mRequestCauseTv;

    @BindView(R.id.descriptionTv)
    TextView mDescriptionTv;

    @BindView(R.id.includeGodNameLL)
    LinearLayout mIncludeGodNameLL;

    @BindView(R.id.includeLanguageLL)
    LinearLayout mIncludeLanguageLL;

    @BindView(R.id.includeStartDateLL)
    LinearLayout mIncludeStartDateLL;

    @BindView(R.id.includeEndDateLL)
    LinearLayout mIncludeEndDateLL;

    @BindView(R.id.includeChantingTypeLL)
    LinearLayout mIncludeChantingTypeLL;

    @BindView(R.id.includeLocationLL)
    LinearLayout mIncludeLocationLL;

    @BindView(R.id.participantsCountLL)
    LinearLayout mIncludeParticipantsLL;

    @BindView(R.id.startChantingButton)
    Button mStartChantingButton;

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

    @BindView(R.id.acceptLL)
    LinearLayout mAcceptLL;

    private OthersDetailsActivity mActivity;
    private KarmicOthersBean.RequestGkcDetails mOthersDetailsBean;
    private TextView mStartHeaderTV;
    private TextView mStartDateTV;
    private TextView mStartTimeTV;
    private TextView mEndHeaderTV;
    private TextView mEndDateTV;
    private TextView mEndTimeTV;
    private TextView mChantingTV;
    private TextView mAddress1TV;
    private TextView mAddress2TV;
    private TextView mGodNameTV;
    private TextView mLanguageTV;
    private ImageView mRightArrowIV;
    private CircularNetworkImageView mPerson1Iv;
    private CircularNetworkImageView mPerson2Iv;
    private CircularNetworkImageView mPerson3Iv;
    private CircularNetworkImageView mPerson4Iv;
    private CircularNetworkImageView mPerson5Iv;
    private TextView mAddCountTV;
    private RelativeLayout mParticipantRL;
    BroadcastReceiver brd_receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ADD_PARTICIPANT_INTENT_ACTION)) {
                if (intent != null) {
                    ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra(Constants.PARTICNTS_LIST_KEY);
                    if (list != null && list.size() > 0) {
                        mIncludeParticipantsLL.setVisibility(View.VISIBLE);
                        mOthersDetailsBean.participants = list;
                        Utils.setUserImageVisibility(mActivity, mOthersDetailsBean.participants, mParticipantRL, mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mAddCountTV);
                    }
                }
            }
        }
    };
    private ImageView mAddIV;
    private EditText mAddNameEtv;
    private EditText mAddEmailEtv;
    private ImageView mPreviousImageView;
    private String mRadioButtonType;
    private JSONArray mParticipantsJArray;
    private int mPage;
    private boolean addParticipant;
    private boolean mIsFromPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.karmic_others_details);
        ButterKnife.bind(this);
        mActivity = this;
        mAddNameEtv = mIncludeEmailLL.findViewById(R.id.addNameEtv);
        mAddEmailEtv = mIncludeEmailLL.findViewById(R.id.addEmailEtv);
        mAddIV = mIncludeEmailLL.findViewById(R.id.addIV);
        mPreviousImageView = mAddIV;

        initUI();
        setListeners();
        mRadioButtonType = Constants.I_ACCEPT;
        getBundle();
        setToolbarWithBack(getString(R.string.helping_others_details));
    }

    private void setListeners() {
        mStartChantingButton.setOnClickListener(this);
        mVolumeIV.setOnClickListener(this);
        mRequestRG.setOnCheckedChangeListener(this);
        mParticipantRL.setOnClickListener(this);
        mAddIV.setOnClickListener(this);
    }

    private void initUI() {

        mGodNameTV = mIncludeGodNameLL.findViewById(R.id.headerValueTV);
        mLanguageTV = mIncludeLanguageLL.findViewById(R.id.headerValueTV);

        mStartHeaderTV = mIncludeStartDateLL.findViewById(R.id.headerValueTV);
        ((LinearLayout) mIncludeStartDateLL.findViewById(R.id.headerValueDateLL)).setVisibility(View.VISIBLE);
        mStartDateTV = mIncludeStartDateLL.findViewById(R.id.headerDateTV);
        mStartTimeTV = mIncludeStartDateLL.findViewById(R.id.headerTimeTV);

        mEndHeaderTV = mIncludeEndDateLL.findViewById(R.id.headerValueTV);
        ((LinearLayout) mIncludeEndDateLL.findViewById(R.id.headerValueDateLL)).setVisibility(View.VISIBLE);
        mEndDateTV = mIncludeEndDateLL.findViewById(R.id.headerDateTV);
        mEndTimeTV = mIncludeEndDateLL.findViewById(R.id.headerTimeTV);

        mChantingTV = mIncludeChantingTypeLL.findViewById(R.id.headerValueTV);

        ((TextView) mIncludeLocationLL.findViewById(R.id.headerTimeTV)).setVisibility(View.GONE);
        ((LinearLayout) mIncludeLocationLL.findViewById(R.id.headerValueDateLL)).setVisibility(View.VISIBLE);
        mAddress1TV = mIncludeLocationLL.findViewById(R.id.headerValueTV);
        mAddress2TV = mIncludeLocationLL.findViewById(R.id.headerDateTV);

        mPerson1Iv = mIncludeParticipantsLL.findViewById(R.id.person1Iv);
        mPerson2Iv = mIncludeParticipantsLL.findViewById(R.id.person2Iv);
        mPerson3Iv = mIncludeParticipantsLL.findViewById(R.id.person3Iv);
        mPerson4Iv = mIncludeParticipantsLL.findViewById(R.id.person4Iv);
        mPerson5Iv = mIncludeParticipantsLL.findViewById(R.id.person5Iv);
        mAddCountTV = mIncludeParticipantsLL.findViewById(R.id.addCountTV);
        mRightArrowIV = mIncludeParticipantsLL.findViewById(R.id.rightArrowIV);
        mParticipantRL = mIncludeParticipantsLL.findViewById(R.id.participantRL);


        ((TextView) mIncludeGodNameLL.findViewById(R.id.headerTitleTV)).setText(getString(R.string.god));
        ((TextView) mIncludeLanguageLL.findViewById(R.id.headerTitleTV)).setText(getString(R.string.language));
        ((TextView) mIncludeStartDateLL.findViewById(R.id.headerTitleTV)).setText(getString(R.string.date_time));
        ((TextView) mIncludeEndDateLL.findViewById(R.id.headerTitleTV)).setVisibility(View.INVISIBLE);
        ((TextView) mIncludeChantingTypeLL.findViewById(R.id.headerTitleTV)).setText(getString(R.string.chanting_type));
        ((TextView) mIncludeLocationLL.findViewById(R.id.headerTitleTV)).setText(getString(R.string.location));
        mStartHeaderTV.setText(getString(R.string.start_time));
        mEndHeaderTV.setText(getString(R.string.end_time));

    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mOthersDetailsBean = (KarmicOthersBean.RequestGkcDetails) intent.getSerializableExtra(OTHERS_BEAN_KEY);
            mPage = intent.getIntExtra(PAGE_COUNT_KEY, 0);
            mIsFromPush = intent.getBooleanExtra(IS_FROM_PUSH_KEY, false);
            if (mIsFromPush) {
                String message = intent.getStringExtra("message");
                String title = intent.getStringExtra("title");
                Utils.showAlertDialog(mActivity, title, "" + message, null, null, false, true);
            }
        }
        updateUI();
    }

    private void updateUI() {
        mNamamTV.setText(fromHtml(mOthersDetailsBean.sub_theme_name));
        if (mOthersDetailsBean.music != null)
            mVolumeIV.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.chanting_volume_ic));
        mRequestCauseTv.setText(mOthersDetailsBean.requestfor);
        if (mOthersDetailsBean.description != null)
            mDescriptionTv.setText(mOthersDetailsBean.description);
        mGodNameTV.setText(mOthersDetailsBean.theme_name);
        mLanguageTV.setText(mOthersDetailsBean.lang);
        String sDate = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mOthersDetailsBean.start_datetime, Constants.SELF_DATE_FORMAT);
        String sTime = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mOthersDetailsBean.start_datetime, Constants.SELF_TIME_FORMAT);
        String eDate = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mOthersDetailsBean.end_datetime, Constants.SELF_DATE_FORMAT);
        String eTime = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mOthersDetailsBean.end_datetime, Constants.SELF_TIME_FORMAT);

        mStartDateTV.setText(sDate);
        mStartTimeTV.setText(sTime);
        mEndDateTV.setText(eDate);
        mEndTimeTV.setText(eTime);
        mChantingTV.setText(mOthersDetailsBean.puja_type);

        mTotalChantRequestedTV.setText("" + mOthersDetailsBean.no_of_chants);
        mChantsRequiredTV.setText("" + mOthersDetailsBean.participant_chant_no);
        Date endDate = CalendarUtils.ConvertStingToDate(mOthersDetailsBean.end_datetime, Constants.DATE_TIME_FORMAT);
        Date startDate = CalendarUtils.ConvertStingtoDate(mOthersDetailsBean.start_datetime, Constants.DATE_TIME_FORMAT);

        Date now = new Date();

        // Todo : If request is not accepted and start date is passed the current time then need to update based on condition
        if (mOthersDetailsBean.status != null && mOthersDetailsBean.status.equalsIgnoreCase(Constants.GKCDP))
            mStartChantingButton.setVisibility(View.GONE);
        else if (now.after(endDate) || now.equals(endDate))
            mStartChantingButton.setVisibility(View.GONE);
        else
            mStartChantingButton.setVisibility(View.VISIBLE);

        if (mOthersDetailsBean.status != null) {
            if (mOthersDetailsBean.status.equalsIgnoreCase(Constants.GKCA))
                setRadioButtonEnable(true, false, false, false);
            else if (mOthersDetailsBean.status.equalsIgnoreCase(Constants.GKCP))
                setRadioButtonEnable(false, true, false, false);
            else if (mOthersDetailsBean.status.equalsIgnoreCase(Constants.GKCD))
                setRadioButtonEnable(false, false, true, false);
            else if (mOthersDetailsBean.status.equalsIgnoreCase(Constants.GKCDP)) {
                setRadioButtonEnable(false, false, false, true);
            }
        }

        if (mOthersDetailsBean.participants == null && mOthersDetailsBean.participants.size() == 0) {
            mIncludeParticipantsLL.setVisibility(View.GONE);
            // if startdate< currentdate then show add participants
            if (now.before(startDate) && mOthersDetailsBean.participant_add.equalsIgnoreCase("YES")) {
                addParticipant = true;
            }
        } else {
            mIncludeParticipantsLL.setVisibility(View.VISIBLE);
            Utils.setUserImageVisibility(mActivity, mOthersDetailsBean.participants, mParticipantRL, mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mAddCountTV);
        }

        if (mOthersDetailsBean.reqstatus == null || TextUtils.isEmpty(mOthersDetailsBean.reqstatus)) {
            mStartChantingButton.setText(getString(R.string.continue_text));
            if (now.after(endDate) || now.equals(endDate)) {
                mAcceptLL.setVisibility(View.GONE);
            } else
                mAcceptLL.setVisibility(View.VISIBLE);
        } else {
            mStartChantingButton.setText(getString(R.string.start_chanting));
            mAcceptLL.setVisibility(View.GONE);
        }


    }

    private void setRadioButtonEnable(boolean accept, boolean doChant, boolean not, boolean knowOf) {
        mAcceptRb.setChecked(false);
        mDoChantRb.setChecked(false);
        mNotAbleToHelpRb.setChecked(false);
        mKnowOfRb.setChecked(false);
        if (accept)
            mAcceptRb.setChecked(true);
        else if (doChant)
            mDoChantRb.setChecked(true);
        else if (not)
            mNotAbleToHelpRb.setChecked(true);
        else if (knowOf)
            mKnowOfRb.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.volumeIV:
                break;
            case R.id.remainderButton:
                Utils.showAlertDialog(this, "", "Remainder has sent to Participants", null, null, false, true);
                break;
            case R.id.startChantingButton:
                if (mStartChantingButton.getText().toString().equalsIgnoreCase(getString(R.string.start_chanting))) {
                    if (mOthersDetailsBean.participant_id != null && !TextUtils.isEmpty(mOthersDetailsBean.participant_id)) {
                        Cursor mCursor = DatabaseHelper.getKarmicOthersNamasCursor(mDatabase, mOthersDetailsBean.mobile, mOthersDetailsBean.participant_id, DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME);
                        Intent intent = Utils.setBundleForChantDetails(mActivity, mCursor, Constants.KEY_KARMIC_OTHERS, false);
                        if (getIntent() != null)
                            intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, getIntent().getStringExtra(ParticipantsListActivity.CREATED_BY_KEY));

                        startActivityForResult(intent, CHANT_DETAILS_CODE);
                    }
                } else {
                    acceptRequest();
                }
                break;
            case R.id.participantRL:

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        registerReceiver(brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        registerReceiver(brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = Utils.getParticipantsListIntent(mActivity, mOthersDetailsBean.start_datetime,
                        mOthersDetailsBean.participant_add, mOthersDetailsBean.end_datetime, mOthersDetailsBean.gkc_setup_id, mOthersDetailsBean.created_by);
                startActivity(intent);
                break;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANT_DETAILS_CODE) {
            if (resultCode == RESULT_OK) {
                boolean isSyncUpted = data.getBooleanExtra(ChantingDetailsActivity.EXTRA_IS_SYNC, false);
                if (isSyncUpted) {
//                    mCursor = DatabaseHelper.getNamasListCursor(mDatabase,mUserIdString,DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME);
//                    if (mCursor != null) {
//                        adapter = new KarmicOthersAdapter(mList,mBaseActivity,this);
//                        mRecyclerView.setAdapter(adapter);
//                    }
                }
            }
        }
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
                sendIntentFinish(true);
            }
        } else if (object != null && object instanceof AddParticipantsBean) {
            AddParticipantsBean bean = (AddParticipantsBean) object;

            if (bean.participants.size() > 0) {
                Utils.showToast(mActivity, "successfully participants added.");
            }
            sendIntentFinish(false);
        } else if (object != null && object instanceof KarmicOthersBean) {
            KarmicOthersBean bean = (KarmicOthersBean) object;
            if (bean != null && bean.requestgkc != null) {
                for (int i = 0; i < bean.requestgkc.size(); i++) {
                    if (bean.requestgkc.get(i).participant_id.equalsIgnoreCase(mOthersDetailsBean.participant_id)) {
                        mOthersDetailsBean = bean.requestgkc.get(i);
                        updateUI();
                    }
                }
            }
        }
    }

    public void sendIntentFinish(boolean isChantUpdated) {
        Intent intent = new Intent();
        intent.setAction(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION);
        sendBroadcast(intent);

        finish();
    }

    private void getHelpingOthersList() {
        showProgress(true, mActivity);
        String url = String.format(Constants.KARMIC_OTHERS_URL, "" + UserInfoBean.getInstance().getUserName(mActivity), "" + mPage);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, url, null, ServiceMethod.KARMIC_OTHERS, Request.Method.GET, this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (addParticipant)
            getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsFromPush) {
                    Intent intent = new Intent(this, BaseNavigationActivity.class);
                    intent.putExtra(BaseNavigationActivity.PUSH_GKC_REQUEST, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else
                    finish();
                break;
            case R.id.add_participant:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        registerReceiver(brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        registerReceiver(brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mActivity, AddParticipantsActivity.class);
                intent.putExtra(ParticipantsListActivity.GKC_SETUP_ID_KEY, mOthersDetailsBean.gkc_setup_id);
                startActivity(intent);
                break;
        }
        return false;
    }

}

