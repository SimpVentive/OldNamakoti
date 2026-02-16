package com.namakoti.karmic.beingHelped;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.adapters.ParticipantsAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.listeners.RecyclerItemClickListener;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class ParticipantsListActivity extends BaseActivity implements VolleyResponseListener {

    public static final String GKC_SETUP_ID_KEY = "GKC_SETUP_ID_KEY";
    public static final String COUNT_DAYS_LEFT_KEY = "COUNT_DAYS_LEFT_KEY";
    public static final String ADD_PARTICIPANT_KEY = "ADD_PARTICIPANT_KEY";
    public static final String PARTICIPANTS_LIST_KEY = "PARTICIPANTS_LIST_KEY";
    public static final String PARTICIPANTS_ID_KEY = "PARTICIPANTS_ID_KEY";
    private static final int CANCEL_REQ_RESULT_CODE = 101;
    public static String SHOW_CANCEL_BTN_KEY = "SHOW_CANCEL_BTN_KEY";
    public static String CREATED_BY_KEY = "CREATED_BY_KEY";
    @BindView(R.id.no_details)
    TextView mNoResultsTv;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String mGkcSetupId;
    private int mPage = 1;
    private ParticipantsListActivity mActivity;
    BroadcastReceiver brd_receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ADD_PARTICIPANT_INTENT_ACTION)) {
                if (intent != null) {
                    getParticipantsList();
                }
            }
        }
    };
    private ParticipantsAdapter mAdapter;
    private ArrayList<ParticipantsBean.ParticipantsItemBean> mParticipantsList;
    private String mDaysLeft;
    private boolean mAddParticipants;
    private boolean isFromActivityResult;
    private boolean isShowCancelBtn;
    private String mCreatedBy;
    private boolean isFromCancelledReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participants_layout);
        ButterKnife.bind(this);
        mActivity = this;
        getBundle();
        setToolbarWithBack(getString(R.string.participants));
        getParticipantsList();

    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mGkcSetupId = intent.getStringExtra(GKC_SETUP_ID_KEY);
            mDaysLeft = intent.getStringExtra(COUNT_DAYS_LEFT_KEY);
            mAddParticipants = intent.getBooleanExtra(ADD_PARTICIPANT_KEY, false);
            isShowCancelBtn = intent.getBooleanExtra(SHOW_CANCEL_BTN_KEY, false);
            mCreatedBy = intent.getStringExtra(CREATED_BY_KEY);
        }
    }

    private void getParticipantsList() {
        showProgress(true, mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put("gkc_setup_id", mGkcSetupId);
        params.put("page", "" + mPage);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.GET_PARTICIPANTS_URL, params, ServiceMethod.GET_PARTICIPANTS, Request.Method.POST, this);

    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);

        if (object != null && object instanceof ParticipantsBean) {
            ParticipantsBean bean = (ParticipantsBean) object;
            updateUI(bean);
            if (isFromCancelledReq) {
                if (bean != null)
                    Utils.sendUpdatedParticipantsBroadCast(mActivity, false, bean.participants, mGkcSetupId);
            }
        }
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
                intent.putExtra(GKC_SETUP_ID_KEY, mGkcSetupId);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CANCEL_REQ_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                isFromCancelledReq = true;
                getParticipantsList();
            }
        }
    }

    private void updateUI(ParticipantsBean bean) {
        if (bean.participants != null && bean.participants.size() > 0) {
            mParticipantsList = bean.participants;
            if (mAdapter == null) {
                mAdapter = new ParticipantsAdapter(mParticipantsList, mActivity, this);
                DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
                itemDecorator.setDrawable(ContextCompat.getDrawable(mActivity, R.drawable.divider_gray));
                mRecyclerView.addItemDecoration(itemDecorator);

                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setNestedScrollingEnabled(false);
            } else {
                mAdapter.updateList(bean.participants);
            }
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (Integer.parseInt(mDaysLeft) < 0) {
                                mDaysLeft = "0";
                            }
                            Intent intent = new Intent(mActivity, ParticipantsDetailsActivity.class);
                            intent.putExtra(ParticipantsDetailsActivity.PARTICIPANTS_BEAN_KEY, mParticipantsList.get(position));
                            intent.putExtra(ParticipantsListActivity.GKC_SETUP_ID_KEY, mGkcSetupId);
                            intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, mCreatedBy);
                            intent.putExtra(COUNT_DAYS_LEFT_KEY, mDaysLeft);
                            intent.putExtra(SHOW_CANCEL_BTN_KEY, isShowCancelBtn);
                            startActivityForResult(intent, CANCEL_REQ_RESULT_CODE);
                        }
                    })
            );
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAddParticipants)
            getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;

    }

}
