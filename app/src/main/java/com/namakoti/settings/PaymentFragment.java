package com.namakoti.settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.adapters.PaymentAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class PaymentFragment extends BaseFragment implements VolleyResponseListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener{
    private View view;
    private View footerView;
    private int SELECTED_GOD_NAMA_CODE = 100;
    private BaseActivity mActivity;

    @BindView(R.id.gods_spinner)
    TextView gods_spinner;

    @BindView(R.id.payment_and_delivery_listview)
    ListView mPaymentListview;

    @BindView(R.id.no_details)
    TextView no_details;

    @BindView(R.id.god_image)
    NetworkImageView god_image;

    @BindView(R.id.spinnerChantingType)
    Spinner mSpinnerChantingType;

    private SaveChantsBean mSelectedBean;
    private Cursor mCursor;
    private int mPage = 1;
    private int userNamakotiId;
    private int pages;
    private String TAG = PaymentFragment.class.getName();
    private int increaseTag = 1;
    private List<JSONObject> transactionsList;
    private ArrayList<GodNamesBean> mGodList;
    private String mNamasTableName;
    private String mFromScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.pament_details, container, false);
        ButterKnife.bind(this, view);
        mActivity = (BaseActivity) getActivity();
        transactionsList = new ArrayList<JSONObject>();
        setFooter();

        Cursor mCursorGod = DatabaseHelper.getGodImages(mBaseActivity.mDatabase);
        mGodList = mBaseActivity.getGodListFromDb(mCursorGod);

        ViewTreeObserver observer = gods_spinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    gods_spinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    gods_spinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        Utils.setSpinnerAdapter(mActivity, Utils.getChantTypeList(),mSpinnerChantingType);
        setListener();
        return view;
    }

    private void setListener() {
        mSpinnerChantingType.setOnItemSelectedListener(this);
        gods_spinner.setOnClickListener(this);
        mPaymentListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                JSONObject namaObject = (JSONObject) view.getTag();
                Intent i = new Intent(mActivity, PaymentDialogActivity.class);
                i.putExtra(Constants.PAYMENT_JSON, namaObject.toString());
                startActivity(i);
            }
        });
    }

    private void setGodAdapter(int pos) {
        if (pos == 0){
            mNamasTableName = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
            mFromScreen = Constants.KEY_SELF_CHANT;
        }
        else if (pos == 1){
            mNamasTableName = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
            mFromScreen = Constants.KEY_CHANT_FOR_CAUSE;
        }
        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase,mBaseActivity.mUserIdLong,mNamasTableName);

        if (mCursor != null) {
            int count = mCursor.getCount();
            if (count != 0) {
                if (mCursor.moveToPosition(0)) {
                    gods_spinner.setText(mActivity.fromHtml(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME))));
                    userNamakotiId = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID));
                    String nameGod = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME));
                    payment();
                    footerView.setTag(increaseTag);
                    mActivity.setImageLoader(mGodList, god_image, nameGod);
                }
            } else {
                no_details.setVisibility(View.VISIBLE);
            }
        }
    }

    private void toolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mBaseActivity.setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.payment));
        mBaseActivity.getSupportActionBar().setLogo(R.drawable.back_ic);
        View logoView = getToolbarLogoIcon(toolbar);
        /*logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "click", Toast.LENGTH_LONG).show();
                popFragment(TAG);
            }
        });*/
        // add back arrow to toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
        }
        return false;
    }

    private void setFooter() {
        footerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        mPaymentListview.addFooterView(footerView);

        footerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Log.i(TAG, "footerView");
                int tag = (Integer) footerView.getTag();

                if (tag < pages) {
                    increaseTag = tag + 1;
                    footerView.setTag(increaseTag);
                    payment();
                } else {
                    footerView.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "Transactions are completed", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void totalTransactions(int p, int visibility) {
        pages = p;
        footerView.setVisibility(visibility);
        if (p <= 1) {
            footerView.setVisibility(View.GONE);
        }
    }

    private void payment() {
        transactionsList.clear();
        showProgress(true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mBaseActivity.mUserIdString);
        params.put("user_namakoti_id", "" + userNamakotiId);
        params.put("page", "" + mPage);


        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, Constants.TRANSACTIONS, params, ServiceMethod.TRANSACTIONS, Request.Method.POST, this);

    }

    private void setToolBar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.payment));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);

        if (serviceMethod == ServiceMethod.TRANSACTIONS) {
            if (object != null) {
                String isSuccess = (String) object;
                String total = "";
                try {
                    JSONObject jsonObject = new JSONObject(isSuccess);
                    total = jsonObject.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                TransactionBean bean = (TransactionBean)object;
//                if (bean != null){
                int visibility = View.VISIBLE;
                if (total.equals("0")) {
                    onTransactions(false);
                } else {
                    int totalCount = Integer.parseInt(total);
                    float modulus = totalCount % 5;//gives reminder
                    int pages = totalCount / 5;
                    if (increaseTag > pages) {
                        visibility = View.GONE;
                    }

                    if (modulus > 0) {
                        pages = pages + 1;
                        Log.i(TAG, "Not multiple of 5, modulus: " + modulus + "pages : " + pages);
                    }
                    totalTransactions(pages, visibility);
                    onTransactions(true);
                    PaymentAdapter paymentAdapter = new PaymentAdapter(mActivity, getJsonObjects(transactionsList, isSuccess), "");
                    mPaymentListview.setAdapter(paymentAdapter);
                }
//                }

            } else {
                Utils.showAlertDialog(mActivity, "Error", " " + getString(R.string.unable_response), null, null, false, true);
            }
        }
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        mBaseActivity.handleErrorMessage(object);
    }

    private List<JSONObject> getJsonObjects(List<JSONObject> jsonObjects, String response) {
        try {
            JSONObject resObj = new JSONObject(response);

            // total param is given in response, in case there is a issue with this,
            // using iterator of keys
            int total = 0;
            if (resObj.has("total")) {
                try {
                    total = Integer.parseInt(resObj.getString("total"));
                } catch (Exception e) {
                }
            }
            if (total > 0) {
                int i = 0;
                while (i < total) {
                    JSONObject object = resObj.getJSONObject("" + i);
                    jsonObjects.add(object);
                    i++;
                }
            }else{
                Iterator<String> keys = resObj.keys();
                while (keys.hasNext()) {
                    if (resObj.get(keys.next()) instanceof JSONObject) {
                        JSONObject object = resObj.getJSONObject(keys.next());
                        jsonObjects.add(object);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

    /**
     * Transaction is done or not
     */
    public void onTransactions(boolean done) {
        if (done) {
            no_details.setVisibility(View.GONE);
            mPaymentListview.setVisibility(View.VISIBLE);
        } else {
            no_details.setVisibility(View.VISIBLE);
            mPaymentListview.setVisibility(View.GONE);
        }
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
            case R.id.gods_spinner:
                Intent intent = new Intent(mActivity, SelectChantGodActivity.class);
                intent.putExtra(SelectChantGodActivity.BUNDLE_IS_FROM_CAUSE, mFromScreen);
                startActivityForResult(intent, SELECTED_GOD_NAMA_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_GOD_NAMA_CODE) {
            if (resultCode == RESULT_OK) {

                Intent intent = data;
                Bundle bundle = intent.getExtras();
                mSelectedBean = (SaveChantsBean) bundle.getSerializable(SelectChantGodActivity.SELECTED_NAMAS_BEAN_KEY);
                gods_spinner.setText(mActivity.fromHtml(mSelectedBean.getSub_theme_name()));
                userNamakotiId = mSelectedBean.getUser_namakoti_id();
                payment();
                mActivity.setImageLoader(mGodList, god_image, mSelectedBean.getTheme_name());
            }
        }
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()){
            case R.id.spinnerChantingType:
                String type = mSpinnerChantingType.getSelectedItem().toString();
                if (position == 0){
                    setGodAdapter(0);
                }
                else if (position == 1){
                    setGodAdapter(1);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
