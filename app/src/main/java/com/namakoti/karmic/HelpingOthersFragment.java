package com.namakoti.karmic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.adapters.KarmicOthersAdapter;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.listeners.EndlessRecyclerViewScrollListener;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anusha on 3/3/2018.
 */

public class HelpingOthersFragment extends BaseFragment implements VolleyResponseListener {

//    public final int REQUEST_CODE_ACCEPT = 100;
    public int mPage = 1;
//    public int ADD_PARTICIPANT_RESULT_CODE = 102;
    @BindView(R.id.no_details)
    TextView mNoResultsTv;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private int CHANT_DETAILS_CODE = 101;
    private KarmicOthersAdapter adapter;
    private Cursor mCursor;
    private ArrayList<KarmicOthersBean.RequestGkcDetails> mList;
    private EndlessRecyclerViewScrollListener scrollListener;

    public BroadcastReceiver brd_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receiveBroadcast(intent);
        }
    };
    private boolean isUpdateNewChant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_normal_chanting, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(manager);
        getHelpingOthersList();
    }

    private void getHelpingOthersList() {
        showProgress(true);
        String url = String.format(Constants.KARMIC_OTHERS_URL, "" + UserInfoBean.getInstance().getUserName(mBaseActivity), "" + mPage);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, url, null, ServiceMethod.KARMIC_OTHERS, Request.Method.GET, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        if (object != null && object instanceof KarmicOthersBean) {
            KarmicOthersBean bean = (KarmicOthersBean) object;
            if (bean != null){
                insertDataIntoDb(bean);
                if (adapter == null)
                    updateUI(bean);
                else {
                    if (mList != null)
                        mList.addAll(bean.requestgkc);
                    adapter.updateList(mList);
                }
            }
        }
    }

    private void updateUI(KarmicOthersBean bean) {
        if (bean.requestgkc != null && bean.requestgkc.size() > 0) {
            mList = bean.requestgkc;
//            insertDataIntoDb(bean);
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoResultsTv.setVisibility(View.GONE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
            mRecyclerView.setLayoutManager(mLayoutManager);
            if (adapter == null) {
                adapter = new KarmicOthersAdapter(bean.requestgkc, mBaseActivity, this);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setNestedScrollingEnabled(false);
            } else {
                adapter.updateList(bean.requestgkc);
            }
            scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    mPage = page + 1;
                    getHelpingOthersList();
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoResultsTv.setVisibility(View.VISIBLE);
        }
    }

    private void insertDataIntoDb(KarmicOthersBean bean) {
        mBaseActivity.insertHelpingOthersKarmic(bean, null);
        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase, mBaseActivity.mUserIdLong, DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME);

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

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_CODE_ACCEPT) {
            if (resultCode == RESULT_OK) {
                getHelpingOthersList();
            }
        } else*/ if (requestCode == CHANT_DETAILS_CODE) {
            if (resultCode == RESULT_OK) {
                boolean isSyncUpted = data.getBooleanExtra(ChantingDetailsActivity.EXTRA_IS_SYNC, false);
                if (isSyncUpted) {
                    mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase, mBaseActivity.mUserIdLong, DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME);
                    if (mCursor != null) {
                        adapter = new KarmicOthersAdapter(mList, mBaseActivity, this);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        }
    }

    private void receiveBroadcast(Intent intent) {
        if (intent.getAction().equals(Constants.ADD_PARTICIPANT_INTENT_ACTION)) {
            if (intent != null) {
                ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra(Constants.PARTICNTS_LIST_KEY);
                String gkcID = intent.getStringExtra(Constants.PARTICNTS_GKC_SETUP_KEY);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < mList.size(); i++) {
                        KarmicOthersBean.RequestGkcDetails bean = mList.get(i);
                        if (bean.gkc_setup_id.equalsIgnoreCase(gkcID)) {
                            bean.participants = list;
                            mList.set(i, bean);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
        else if (intent.getAction().equalsIgnoreCase(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION)) {
            isUpdateNewChant = true;
//            adapter = null;
            mPage = 1;
            int oldSize = 0;
            if (mList != null && mList.size() > 0){
                oldSize = mList.size();
                mList.clear();
                adapter.notifyItemRangeRemoved(0, oldSize);
            }
            getHelpingOthersList();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
