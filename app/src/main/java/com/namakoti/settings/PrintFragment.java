package com.namakoti.settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.TempleBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.chanting.WebViewActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anusha on 4/13/2018.
 */

public class PrintFragment extends BaseFragment implements View.OnClickListener,VolleyResponseListener,
        AdapterView.OnItemSelectedListener{

    private static final int SELECTED_GOD_NAMA_CODE = 100;
//    @BindView(R.id.totalTV)
//    TextView totalTV;
//
//    @BindView(R.id.runningTV)
//    TextView runningTV;
//
//    @BindView(R.id.printTV)
//    TextView printTV;
//
    @BindView(R.id.spinnerNamasPrinted)
    Spinner spinnerNamasPrinted;

    @BindView(R.id.spinnerTemple)
    Spinner spinnerTemple;

    @BindView(R.id.spinnerPrintType)
    Spinner spinnerPrintType;

    @BindView(R.id.spinnerHandWritten)
    Spinner spinnerHandWritten;

    @BindView(R.id.printedNameTV)
    TextView printedNameTV;

    @BindView(R.id.button_pay)
    TextView button_pay;

//    @BindView(R.id.gods_spinner)
//    TextView gods_spinner;
//    @BindView(R.id.spinnerChantingType)
//    Spinner mSpinnerChantingType;
//    @BindView(R.id.god_image)
//    NetworkImageView god_image;

    private Cursor mCursor;
//    private ArrayList<GodNamesBean> mGodList;
    private String mThemeId;
    private String mName;
//    private int mTotalCount;
//    private int mRunningCount;
//    private int mPrintCount;
    private String mNamasTableName;
    private String mFromScreen;
    private int mNamakotiID;
    private long mOnGoingLocalCount;
    private TempleBean mTempleBean;
    private String mSelectedTempleID;
    private String mSelectedPrintTypeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.print_layout, container, false);
        ButterKnife.bind(this,view);


//        Cursor mCursorGod = DatabaseHelper.getGodImages(mBaseActivity.mDatabase);
//        mGodList = mBaseActivity.getGodListFromDb(mCursorGod);
//        ViewTreeObserver observer = gods_spinner.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    gods_spinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                } else {
//                    gods_spinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//            }
//        });
//        Utils.setSpinnerAdapter(mBaseActivity, Utils.getChantTypeList(),mSpinnerChantingType);
        setListener();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getPrint();
    }

    private void getPrint() {
        showProgress(true);
        String url = String.format(Constants.TEMPLE_PRINTS_URL,""+UserInfoBean.getInstance().getUserId(getActivity()) ,""+mThemeId);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, url, null, ServiceMethod.TEMPLE_PRINTS, Request.Method.GET, this);
    }

    private void setListener() {
//        mSpinnerChantingType.setOnItemSelectedListener(this);
//        gods_spinner.setOnClickListener(this);
        button_pay.setOnClickListener(this);
    }

    /*private void setGodAdapter(int pos) {

        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase,mUserIdString,mNamasTableName);
        if (mCursor != null) {
            int count = mCursor.getCount();
            if (count != 0) {
                if (mCursor.moveToPosition(0)) {
                    gods_spinner.setText(mBaseActivity.fromHtml(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME))));
                    mThemeId = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_THEME_ID));
                    mTotalCount = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_TOTAL_COUNT));
                    mPrintCount = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_PRINTED_COUNT));
                    mRunningCount = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_RUNNING_COUNT));
                    mName = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_PRINT_USERNAME));
                    String nameGod = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME));
                    getPrint();
                    mBaseActivity.setImageLoader(mGodList, god_image, nameGod);
                }
            }
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_GOD_NAMA_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = data;
                Bundle bundle = intent.getExtras();
                SaveChantsBean mSelectedBean = (SaveChantsBean) bundle.getSerializable(SelectChantGodActivity.SELECTED_NAMAS_BEAN_KEY);
//                mThemeId = Integer.parseInt(mSelectedBean.getUser_theme_id());
//                gods_spinner.setText(mBaseActivity.fromHtml(mSelectedBean.getSub_theme_name()));
//                mTotalCount = Integer.parseInt(mSelectedBean.getUser_theme_id());
//                mPrintCount = Integer.parseInt(mSelectedBean.getUser_theme_id());
//                mRunningCount =  Integer.parseInt(mSelectedBean.getUser_theme_id());
                mName = mSelectedBean.getPrint_username();
                getPrint();
//                mBaseActivity.setImageLoader(mGodList, god_image, mSelectedBean.getTheme_name());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gods_spinner:
                Intent intent = new Intent(mBaseActivity, SelectChantGodActivity.class);
                intent.putExtra(SelectChantGodActivity.BUNDLE_IS_FROM_CAUSE, mFromScreen);
                startActivityForResult(intent, SELECTED_GOD_NAMA_CODE);
                break;
            case R.id.button_pay:
//                if (mOnGoingLocalCount < 10000){
//                    Utils.showToast(mBaseActivity, "You should have minimum of 10000 chants(On Going) for printing");
//                }else{
                    UserInfoBean userBean = UserInfoBean.getInstance();
                String amount = "200";
                String printcat="no";
                    String url = Constants.PRINT_CHANT_URL+"?user_id="+userBean.getUserId(getActivity())
                            +"&username="+userBean.getUserName(mBaseActivity)+"&password="+userBean.getPassword(mBaseActivity)
                            +"&user_namakoti_id="+mNamakotiID+"&name="+printedNameTV.getText().toString()
                            +"&templename="+mSelectedTempleID+"&printingcount="+10000
                            +"&printtype="+mSelectedPrintTypeID+"&amount="+amount+"&printcat="+printcat+"&file="+"";


                    Intent i = new Intent(mBaseContext, WebViewActivity.class);
                    i.putExtra(Constants.WEBVIEW_URL, url);
                    i.putExtra(Constants.WEBVIEW_HDR_IMG, R.drawable.print_and_delivery_hdr);
                    startActivity(i);
//                }
        }
    }
    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        if (object != null && serviceMethod == ServiceMethod.TEMPLE_PRINTS) {
            TempleBean bean = (TempleBean) object;
            updateUI(bean);
        }
    }

    private void updateUI(TempleBean bean) {
        mTempleBean = bean;
        printedNameTV.setText(mName);
//        totalTV.setText(""+mTotalCount);
//        runningTV.setText(""+mRunningCount);
//        printTV.setText(""+mPrintCount);
        if (bean != null){
            if (bean.temples != null && bean.temples.size() > 0) {
                Utils.setSpinnerAdapter(mBaseContext, getTemple(bean.temples),spinnerTemple);
                mSelectedTempleID= bean.temples.get(0).temple_id;
            }
            if (bean.print_type != null && bean.print_type.size() > 0) {
                Utils.setSpinnerAdapter(mBaseContext, getPrintType(bean.print_type),spinnerPrintType);
                mSelectedPrintTypeID = bean.print_type.get(0).printing_id;
            }
            if (bean.count_number != null && bean.count_number.size() > 0) {
                Utils.setSpinnerAdapter(mBaseContext, getCount(bean.count_number),spinnerNamasPrinted);
            }
            Utils.setSpinnerAdapter(mBaseContext, getHandChantList(),spinnerHandWritten);
        }
    }

    private ArrayList<String> getHandChantList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Yes");
        list.add("No");
        return list;
    }

    private ArrayList<String> getTemple(ArrayList<TempleBean.Temples> temples) {

        ArrayList<String> listTemple = new ArrayList<>();
        for (int i = 0; i < temples.size(); i++) {
            listTemple.add(temples.get(i).temple_name);
        }
        return listTemple;
    }

    private ArrayList<String> getPrintType(ArrayList<TempleBean.PrintType> list) {
        ArrayList<String> causes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            causes.add(list.get(i).prinitng_name);
        }
        return causes;
    }
    private ArrayList<String> getCount(ArrayList<TempleBean.CountNumber> list) {

        ArrayList<String> listCount = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listCount.add(list.get(i).no_count);
        }
        return listCount;
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        mBaseActivity.handleErrorMessage(object);
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spinnerTemple:
                if (mTempleBean.temples != null && mTempleBean.temples.size() > 0){
                    if (position != 0) {
                        mSelectedTempleID = mTempleBean.temples.get(position - 1).temple_id;
                    }
                }
                break;
            case R.id.spinnerCause:
                if (mTempleBean.print_type != null && mTempleBean.print_type.size() > 0){
                    if (position != 0) {
                        mSelectedPrintTypeID = mTempleBean.print_type.get(position - 1).printing_id;
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void updateUI(int namakotiId, SaveChantsBean bean, String fromScreen, long localCount) {
        mThemeId = bean.getUser_theme_id();
        mNamakotiID = namakotiId;
        mName = bean.getPrint_username();
        mOnGoingLocalCount = localCount;
        if (fromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)){
            mNamasTableName = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
            mFromScreen = Constants.KEY_SELF_CHANT;
        }
        else if (fromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)){
            mNamasTableName = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
            mFromScreen = Constants.KEY_CHANT_FOR_CAUSE;
        }
        if (isAdded()){
            getPrint();
        }
    }
}
