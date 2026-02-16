package com.namakoti.karmic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.adapters.TopListAdapter;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.SyncChantKarmicBean;
import com.namakoti.chanting.self.NewChantDetailsActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 9/9/2018.
 */

public class TopListFragment extends BaseFragment implements View.OnClickListener,VolleyResponseListener{

    public static final String CHANT_TOTAL_COUNT = "CHANT_TOTAL_COUNT";
    public static final String CHANT_PRESENT_COUNT = "CHANT_PRESENT_COUNT";
    public static final String MY_CHANT_TOTAL_COUNT = "MY_CHANT_TOTAL_COUNT";
    public static final String MY_CHANT_PRESENT_COUNT  = "MY_CHANT_PRESENT_COUNT";
    @BindView(R.id.participantsMapButton)
    Button mParticipantsMapButton;

    @BindView(R.id.topListrecyclerView)
    RecyclerView mTopListRecyclerView;

    @BindView(R.id.noResultsTv)
    TextView mNoResultsTv;

    private String mGkcSetupId;
    private int mNamakotiId;
    private TopListAdapter mAdapter;
    private SyncChantKarmicBean mSyncChantBean;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.include_toplist, container, false);
        ButterKnife.bind(this,view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        mTopListRecyclerView.setLayoutManager(manager);

        setListener();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getTopList();
    }

    private void getTopList() {
        showProgress(true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("participant_id", ""+mNamakotiId);
        params.put("gkc_setup_id", mGkcSetupId);

        VolleyUtil.getInstance().volleyStringRequest(getActivity(),Constants.GET_KARMIC_DETAILS_URL , params,
                ServiceMethod.GET_KARMIC_DETAILS_URL, Request.Method.POST, this);    }

    private void setListener() {
        mParticipantsMapButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void callGetTopList(String gkcSetupId, int namakotiId) {
        mGkcSetupId = gkcSetupId;
        mNamakotiId = namakotiId;
        if (isAdded()){
            getTopList();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.participantsMapButton:
                Intent intent = new Intent(getActivity(), KarmicProfileActivity.class);
                intent.putExtra(KarmicProfileActivity.SYNC_BEAN_KEY, mSyncChantBean);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        if (object != null && object instanceof SyncChantKarmicBean) {
            mSyncChantBean = (SyncChantKarmicBean) object;
            updateUi(mSyncChantBean, false);

        }
    }

    public void updateUi(SyncChantKarmicBean bean, boolean isFromsync) {
        if (bean != null && bean.toplist != null && bean.toplist.size() >0) {
            mNoResultsTv.setVisibility(View.GONE);
            mTopListRecyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
            mTopListRecyclerView.setLayoutManager(mLayoutManager);
            if (mAdapter == null) {
                mAdapter = new TopListAdapter(bean.toplist, mBaseActivity);
                mTopListRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.updateList(bean.toplist);
            }
        }
        else {
            mNoResultsTv.setVisibility(View.VISIBLE);
            mTopListRecyclerView.setVisibility(View.GONE);
        }
        if (bean != null && !isFromsync){
            Intent intent = new Intent();
            intent.setAction(Constants.UPDATE_CHANT_COUNT_INTENT_ACTION);
            if (bean.chantsdetails != null){
                intent.putExtra(CHANT_TOTAL_COUNT, bean.chantsdetails.totalchants);
                intent.putExtra(CHANT_PRESENT_COUNT, bean.chantsdetails.presentchants);
            }
            if (bean.mychants != null){
                intent.putExtra(MY_CHANT_TOTAL_COUNT, bean.mychants.chants_count_will_do);
                intent.putExtra(MY_CHANT_PRESENT_COUNT, bean.mychants.orginal_chants_count);
            }
            if (bean.karmicchantdetails != null){
                intent.putExtra(NewChantDetailsActivity.GKC_SETUPID, bean.karmicchantdetails.gkc_setup_id);
            }
            mBaseActivity.sendBroadcast(intent);
        }
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        mBaseActivity.handleErrorMessage(object);
    }

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

}
