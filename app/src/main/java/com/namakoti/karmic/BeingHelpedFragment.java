package com.namakoti.karmic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.adapters.KarmicSelfAdapter;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.listeners.EndlessRecyclerViewScrollListener;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class BeingHelpedFragment extends BaseFragment implements VolleyResponseListener {

//    private static final int BEING_HELPED_RESULT_CODE = 100;
    private static final int CHANT_DETAILS_CODE = 101;

    @BindView(R.id.no_details)
    TextView mNoResultsTv;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private int mPage = 1;
    private KarmicSelfAdapter adapter;
    private ArrayList<KarmicSelfBean.SelfGkcDetails> mList;
    private Cursor mCursor;
    private EndlessRecyclerViewScrollListener scrollListener;
    public BroadcastReceiver brd_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleParticipantBroadcast(intent);
        }
    };

    public BeingHelpedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActivityCompat.invalidateOptionsMenu(getActivity());
        View view = inflater.inflate(R.layout.fragment_normal_chanting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void addParticipants(String setupId) {
        showProgress(true);
        Map<String, String> params = JsonObjectUtils.getInstance(mBaseActivity).appParticipants(mBaseActivity, null, setupId);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, Constants.ADD_PARTICIPANTS_URL, params, ServiceMethod.ADD_PARTICIPANTS, Request.Method.POST, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(manager);

        getKarmicSelfList(mPage);
    }

    private void getKarmicSelfList(int page) {
        showProgress(true);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mBaseActivity.mUserIdString);
        params.put("page", "" + page);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, Constants.KARMIC_SELF_URL, params, ServiceMethod.KARMIC_SELF, Request.Method.POST, this);

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
        if (object != null && object instanceof KarmicSelfBean) {
            KarmicSelfBean bean = (KarmicSelfBean) object;
            if(bean != null){
                insertDataIntoDb(bean);
                if (adapter == null)
                    updateUI(bean);
                else {
                /*if (isUpdateNewChant){
                    isUpdateNewChant = false;
                    if (bean != null && bean.selfgkc != null && bean.selfgkc.size() > 0)
                        mList.add(mList.size(), bean.selfgkc.get(bean.selfgkc.size()-1));
                }
                else*/
                    if (mList != null)
                        mList.addAll(bean.selfgkc);
                    adapter.updateList(mList);
                }
            }
        }
    }

    private void updateUI(KarmicSelfBean bean) {
        if (bean.selfgkc != null && bean.selfgkc.size() > 0) {
            mList = bean.selfgkc;
//            insertDataIntoDb(bean);
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoResultsTv.setVisibility(View.GONE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
            mRecyclerView.setLayoutManager(mLayoutManager);
            if (adapter == null) {
                adapter = new KarmicSelfAdapter(mList, mBaseActivity, this);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setNestedScrollingEnabled(false);
            } else {
                adapter.updateList(bean.selfgkc);
            }
            scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    getKarmicSelfList(page + 1);
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoResultsTv.setVisibility(View.VISIBLE);
        }
    }

    private void insertDataIntoDb(KarmicSelfBean bean) {
        mBaseActivity.insertSelfKarmic(bean, null);
        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase, mBaseActivity.mUserIdLong, DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME);
        /*if (mCursor != null) {
            if (mCursor.getCount() != 0) {
                Toast.makeText(getActivity(), "" + mCursor.getCount(), Toast.LENGTH_LONG).show();
            }
        }*/
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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if (menu != null)
//            menu.clear();
//        inflater.inflate(R.menu.menu_karmic_add, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_karmic:
//                getActivity().registerReceiver(brd_receiver,new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION));
//                startActivity(new Intent(mBaseActivity, NewBeingHelpedActivity.class)/*, BEING_HELPED_RESULT_CODE*/);
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == BEING_HELPED_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                getKarmicSelfList(mPage);
            }
        } else*/ if (requestCode == CHANT_DETAILS_CODE) {
            if (resultCode == RESULT_OK) {
                updateUiFromActivityResult(data);
            }
        }
    }

    public void updateUiFromActivityResult(Intent data) {
        boolean isSyncUpted = data.getBooleanExtra(ChantingDetailsActivity.EXTRA_IS_SYNC, false);
        if (isSyncUpted) {
            mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase, mBaseActivity.mUserIdLong, DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME);
            if (mCursor != null) {
                adapter = new KarmicSelfAdapter(mList, mBaseActivity, this);
                mRecyclerView.setAdapter(adapter);
            }
        }
    }

    private void handleParticipantBroadcast(Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(Constants.ADD_PARTICIPANT_INTENT_ACTION)) {
                ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra(Constants.PARTICNTS_LIST_KEY);
                String gkcID = intent.getStringExtra(Constants.PARTICNTS_GKC_SETUP_KEY);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < mList.size(); i++) {
                        KarmicSelfBean.SelfGkcDetails bean = mList.get(i);
                        if (bean.gkc_setup_id.equalsIgnoreCase(gkcID)) {
                            bean.participants = list;
                            mList.set(i, bean);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

            } else if (intent.getAction().equalsIgnoreCase(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION)) {
//                adapter = null;
                mPage = 1;
                int oldSize = 0;
                if (mList != null && mList.size() > 0){
                    oldSize = mList.size();
                    mList.clear();
                    adapter.notifyItemRangeRemoved(0, oldSize);
                }
                getKarmicSelfList(mPage);
            }
        }
    }
}
