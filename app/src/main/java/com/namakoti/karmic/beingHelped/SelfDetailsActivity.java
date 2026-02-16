package com.namakoti.karmic.beingHelped;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.CalendarUtils;
import com.namakoti.utils.Constants;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyUtil;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class SelfDetailsActivity extends BaseActivity implements View.OnClickListener {

    public static final String SELF_BEAN_KEY = "SELF_BEAN_KEY";
    private static final int CHANT_DETAILS_CODE = 100;

    @BindView(R.id.includeNumOfPeopleLL)
    LinearLayout mIncludeNumOfPeopleLL;

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

    @BindView(R.id.bottomRL)
    LinearLayout bottomRL;

    @BindView(R.id.startChantingButton)
    Button mStartChantingButton;

    @BindView(R.id.remainderButton)
    Button mRemainderButton;

    private SelfDetailsActivity mActivity;
    private KarmicSelfBean.SelfGkcDetails mSelfDetailsBean;
    private TextView mInvitedPeopleTV;
    private TextView mNotParticipateTV;
    private TextView mTotalChantsTV;
    private TextView mStartHeaderTV;
    private LinearLayout mStartDateLL;
    private TextView mStartDateTV;
    private TextView mStartTimeTV;
    private TextView mEndHeaderTV;
    private TextView mEndDateTV;
    private TextView mEndTimeTV;
    private TextView mChantingTV;
    private TextView mChantingTitleTV;
    private TextView mAddress1TV;
    private TextView mAddress2TV;
    private ImageView mRightArrowIV;
    private CircularNetworkImageView mPerson1Iv;
    private CircularNetworkImageView mPerson2Iv;
    private CircularNetworkImageView mPerson3Iv;
    private CircularNetworkImageView mPerson4Iv;
    private CircularNetworkImageView mPerson5Iv;
    private TextView mAddCountTV;
    private ImageView mVolumeIV;
    private TextView mNamamTV;
    private TextView mGodNameTV;
    private TextView mLanguageTV;
    private ImageLoader mImageLoader;
    private RelativeLayout mParticipantRL;
    BroadcastReceiver brd_receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ADD_PARTICIPANT_INTENT_ACTION)) {
                if (intent != null) {
                    ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra(Constants.PARTICNTS_LIST_KEY);
                    if (list != null && list.size() > 0) {
                        mIncludeParticipantsLL.setVisibility(View.VISIBLE);
                        mSelfDetailsBean.participants = list;
                        Utils.setUserImageVisibility(mActivity, mSelfDetailsBean.participants, mParticipantRL, mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mAddCountTV);
                    }
                }
            }
        }
    };
    private boolean addParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.karmic_being_details);
        ButterKnife.bind(this);
        mActivity = this;
        mImageLoader = VolleyUtil.getInstance().imageLoader(mActivity, com.intuit.sdp.R.dimen._40sdp);

        initUI();
        setListeners();
        getBundle();
        setToolbarWithBack(getString(R.string.being_helped_details));
    }

    private void setListeners() {
        mStartChantingButton.setOnClickListener(this);
        mRemainderButton.setOnClickListener(this);
        mVolumeIV.setOnClickListener(this);
        mParticipantRL.setOnClickListener(this);
    }

    private void initUI() {
        mInvitedPeopleTV = mIncludeNumOfPeopleLL.findViewById(R.id.invitedPeopleTV);
        mNotParticipateTV = mIncludeNumOfPeopleLL.findViewById(R.id.notParticipateTV);
        mTotalChantsTV = mIncludeNumOfPeopleLL.findViewById(R.id.totalChantsTV);
        mVolumeIV = mIncludeNumOfPeopleLL.findViewById(R.id.volumeIV);
        mNamamTV = mIncludeNumOfPeopleLL.findViewById(R.id.namamTV);

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
            mSelfDetailsBean = (KarmicSelfBean.SelfGkcDetails) intent.getSerializableExtra(SELF_BEAN_KEY);
        }

        mNamamTV.setText(fromHtml(mSelfDetailsBean.sub_theme_name));
        if (mSelfDetailsBean.music != null)
            mVolumeIV.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.chanting_volume_ic));
        mRequestCauseTv.setText(mSelfDetailsBean.requestfor);
        if (mSelfDetailsBean.description != null)
            mDescriptionTv.setText(mSelfDetailsBean.description);
        mGodNameTV.setText(mSelfDetailsBean.theme_name);
        mLanguageTV.setText(mSelfDetailsBean.lang);
        String sDate = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mSelfDetailsBean.start_datetime, Constants.SELF_DATE_FORMAT);
        String sTime = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mSelfDetailsBean.start_datetime, Constants.SELF_TIME_FORMAT);
        String eDate = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mSelfDetailsBean.end_datetime, Constants.SELF_DATE_FORMAT);
        String eTime = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, mSelfDetailsBean.end_datetime, Constants.SELF_TIME_FORMAT);

        Date startDate = CalendarUtils.ConvertStingtoDate(mSelfDetailsBean.start_datetime, Constants.DATE_TIME_FORMAT);
        Date endDate = CalendarUtils.ConvertStingToDate(mSelfDetailsBean.end_datetime, Constants.DATE_TIME_FORMAT);
        Date now = new Date();

        mStartDateTV.setText(sDate);
        mStartTimeTV.setText(sTime);
        mEndDateTV.setText(eDate);
        mEndTimeTV.setText(eTime);
        mChantingTV.setText(mSelfDetailsBean.puja_type);
        mAddCountTV.setText("" + 0);
        mInvitedPeopleTV.setText("" + mSelfDetailsBean.no_of_participants);
        mNotParticipateTV.setText("" + mSelfDetailsBean.participant_chant_no);
        mTotalChantsTV.setText("" + mSelfDetailsBean.no_of_chants);
        if (mSelfDetailsBean.participants != null && mSelfDetailsBean.participants.size() > 0) {
            mIncludeParticipantsLL.setVisibility(View.VISIBLE);
            Utils.setUserImageVisibility(mActivity, mSelfDetailsBean.participants, mParticipantRL, mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mAddCountTV);
        } else {
            mIncludeParticipantsLL.setVisibility(View.GONE);
            // if startdate< currentdate then show add participants
            if (now.before(startDate) && mSelfDetailsBean.participant_add.equalsIgnoreCase("YES")) {
                addParticipant = true;
            }
        }
        if (now.after(endDate) || now.equals(endDate))
            bottomRL.setVisibility(View.GONE);
        else
            bottomRL.setVisibility(View.VISIBLE);


    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
                intent.putExtra(ParticipantsListActivity.GKC_SETUP_ID_KEY, mSelfDetailsBean.gkc_setup_id);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (addParticipant)
            getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
                    if (mSelfDetailsBean.participant_id != null && !TextUtils.isEmpty(mSelfDetailsBean.participant_id)) {
                        Cursor mCursor = DatabaseHelper.getKarmicOthersNamasCursor(mDatabase, mSelfDetailsBean.user_id, mSelfDetailsBean.participant_id, DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME);
                        Intent intent = Utils.setBundleForChantDetails(mActivity, mCursor, Constants.KEY_KARMIC_BEING, false);
                        if (getIntent() != null)
                            intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, getIntent().getStringExtra(ParticipantsListActivity.CREATED_BY_KEY));

                        startActivityForResult(intent, CHANT_DETAILS_CODE);
                    }
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
                Intent intent = Utils.getParticipantsListIntent(mActivity, mSelfDetailsBean.start_datetime, mSelfDetailsBean.participant_add,
                        mSelfDetailsBean.end_datetime, mSelfDetailsBean.gkc_setup_id, mSelfDetailsBean.created_by);
                startActivity(intent);
                break;

        }
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
}

