package com.namakoti.chanting.chantforcause;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.CauseBean;
import com.namakoti.beans.ChantsBean;
import com.namakoti.beans.LanguagesBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.chanting.self.SelectGodActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 12/14/2017.
 */

public class CreateChantForCauseActivity extends BaseActivity implements VolleyResponseListener,View.OnClickListener,
        View.OnTouchListener,AdapterView.OnItemSelectedListener{

    @BindView(R.id.spinnerCause)
    Spinner mSpinnerCause;

    @BindView(R.id.textView_godName_cc)
    TextView mGodNameTV;

    @BindView(R.id.selecgod_rl)
    RelativeLayout mSelectGodRL;

    @BindView(R.id.spinnerLang)
    Spinner mSpinnerLang;

    @BindView(R.id.spinnerNamas)
    Spinner mSpinnerNamas;

    @BindView(R.id.editText_noOfChants_cc)
    EditText mCountChantET;

    @BindView(R.id.exact_cause_etv)
    EditText mExactCauseEtv;

    @BindView(R.id.editText_personName_cc)
    EditText mPersonET;

    @BindView(R.id.button_startChanting_cc)
    Button mStartChanButton;

    private static final int SELECT_GOD = 1;
    private String mSelectedThemeId;
    private String mSelectedGod;
    private String mSelectedLangCode;
    private List<CauseBean> mCauseList;
    private List<LanguagesBean> mLanguageList;
    private List<ChantsBean> mChantsList;
    private String mSelectedChnatsSubThemeid;
    private String mSelectedCauseId;
    private CreateChantForCauseActivity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chanting_for_cause_new);
        ButterKnife.bind(this);
        mActivity = this;

        setListeners();
        mActivity.setToolbarWithBack(getString(R.string.new_chant_for_cause));
        getCauses();

//        mSelectedGod = "Goddess Lakshmi";
//        mSelectedThemeId = "7";
//        mGodNameTV.setText(mSelectedGod);
    }

    private void setListeners() {
        mSelectGodRL.setOnClickListener(this);
        mStartChanButton.setOnClickListener(this);

        mSpinnerLang.setOnItemSelectedListener(this);
        mSpinnerNamas.setOnItemSelectedListener(this);
        mSpinnerCause.setOnItemSelectedListener(this);

        mSpinnerLang.setOnTouchListener(this);
        mSpinnerNamas.setOnTouchListener(this);
        mSpinnerCause.setOnTouchListener(this);
    }

    private void getLanguages() {
        showProgress(true, mActivity);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.GET_LAUNGUAGES, null, ServiceMethod.GET_LANGUAGES, Request.Method.GET, this);
    }

    private void getCauses() {
        showProgress(true, mActivity);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CAUSE_OPTIONS, null, ServiceMethod.CAUSE_OPTIONS, Request.Method.GET, this);
    }
    private void getChatsList() {
        showProgress(true, mActivity);

        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).getChantListParams(mSelectedLangCode,mSelectedThemeId);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CAUSE_CHANT_LIST, params, ServiceMethod.GET_CHANTS, Request.Method.POST, this);
    }

    private void saveChant() {
        showProgress(true, mActivity);
        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).
                getSaveChantCauseParams(mActivity, mSelectedCauseId,mSelectedLangCode, mSelectedThemeId,
                        mSelectedChnatsSubThemeid, mCountChantET.getText().toString(),mExactCauseEtv.getText().toString(),mPersonET.getText().toString());

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CAUSE_SAVE_CHANT_LIST, params, ServiceMethod.SAVE_CHANT,
                        Request.Method.POST, this);
    }


    private void performValidation() {
        if (mSpinnerCause.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))){
            Utils.showAlertDialog(mActivity,"Error","Please select Cause",null,null,false,true);
        }else if (mGodNameTV.getText().toString().equalsIgnoreCase(getString(R.string.select))){
            Utils.showAlertDialog(mActivity,"Error","Please select God",null,null,false,true);
        }else if (mSpinnerLang.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))){
            Utils.showAlertDialog(mActivity,"Error","Please select Language",null,null,false,true);
        }
        else if (TextUtils.isEmpty(mExactCauseEtv.getText().toString())){
            mExactCauseEtv.requestFocus();
            mExactCauseEtv.setError("Please enter exact cause");
        }
        else if (TextUtils.isEmpty(mCountChantET.getText().toString())){
            mCountChantET.requestFocus();
            mCountChantET.setError(getString(R.string.error_chant_count));
        }
        else if ((mCountChantET.getText().toString() != null) && Long.parseLong(mCountChantET.getText().toString())==0){
            mCountChantET.requestFocus();
            mCountChantET.setError(getString(R.string.error_valid_chant));
        }
        else if (TextUtils.isEmpty(mPersonET.getText().toString())){
            mPersonET.requestFocus();
            mPersonET.setError(getString(R.string.error_enter_person_name));
        }
        else if ((mPersonET.getText().toString() != null) && (mPersonET.getText().toString().length() > 0)
                && (mPersonET.getText().toString().length() < 4)){
            mPersonET.requestFocus();
            mPersonET.setError(getString(R.string.error_person_name_min));
        }
        else{
            saveChant();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.selecgod_rl:
                if (mSpinnerCause != null && mSpinnerCause.getSelectedItem() != null){
                    if (mSpinnerCause.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))){
                        Utils.showAlertDialog(mActivity,"Error","Please select Cause",null,null,false,true);
                        mSelectedThemeId = null;
                        mGodNameTV.setText(getString(R.string.select));
                    }else{
                        Intent intent = new Intent(mActivity, SelectGodActivity.class);
                        intent.putExtra(SelectGodActivity.EXTRA_URL_KEY, Constants.CAUSE_GODS_NAMES);
                        intent.putExtra(SelectGodActivity.EXTRA_CAUSE_KEY, mSelectedCauseId);
                        startActivityForResult(intent, SELECT_GOD);
                    }
                }
                break;
            case R.id.button_startChanting_cc:
                performValidation();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_GOD){
            if (resultCode == RESULT_OK){
                mSelectedThemeId = data.getStringExtra(SelectGodActivity.EXTRA_SELECTED_GOD_THEMEID);
                mSelectedGod = data.getStringExtra(SelectGodActivity.EXTRA_SELECTED_GOD_THEME_NAME);
                if (mSelectedLangCode != null && ! mGodNameTV.getText().toString().equalsIgnoreCase(mSelectedGod)){
                    getChatsList();
                }
                mGodNameTV.setText(mSelectedGod);

            }
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if(object != null && object instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) object;
            if (!list.isEmpty() && list.get(0) instanceof CauseBean) {
                mCauseList = (List<CauseBean>) object;
                if (mCauseList != null && mCauseList.size() > 0) {
                    Utils.setSpinnerAdapter(mActivity, getCauseList(mCauseList),mSpinnerCause);
                    getLanguages();
                }
            }
            else if (!list.isEmpty() && list.get(0) instanceof LanguagesBean) {
                mLanguageList = (List<LanguagesBean> ) object;
                if(mLanguageList != null && mLanguageList.size() >0) {
                    Utils.setSpinnerAdapter(mActivity, getLanguageList(mLanguageList), mSpinnerLang);
                }
            }
            else if (!list.isEmpty() && list.get(0) instanceof ChantsBean) {
                mChantsList = (List<ChantsBean> ) object;
                if(mChantsList != null && mChantsList.size() >0) {
                    Utils.setSpinnerAdapter(mActivity, getChantList(mChantsList), mSpinnerNamas);
                }
            }
            else if (!list.isEmpty() && list.get(0) instanceof SaveChantsBean) {
                List<SaveChantsBean> chantBean = (List<SaveChantsBean> ) object;
                if (chantBean != null && chantBean.size()>0){
                    insetDataIntoDb(chantBean);
                }
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
    private void insetDataIntoDb(List<SaveChantsBean> chantBean) {
        long result = -1;
        for (int i = 0; i< chantBean.size(); i++){
            SaveChantsBean savedBean = chantBean.get(i);
            ContentValues cv = DatabaseHelper.getInstance().getSavedNamasQuery(savedBean,mUserIdLong);
            result = mDatabase.update(DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME,cv,
                    DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID+ "=" +savedBean.getUser_namakoti_id(), null);

            if (result != -1 && result == 0) {
                mDatabase.insert(DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME,"firstName", cv);

                ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(mUserIdLong,
                        ""+savedBean.getUser_namakoti_id(),savedBean.getNama_total_count(),
                        Constants.KEY_CHANT_FOR_CAUSE,savedBean.getNo_chants());
                ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(mUserIdLong,
                        ""+savedBean.getUser_namakoti_id(),savedBean.getNama_total_count(),
                        savedBean.getNama_running_count(),Constants.KEY_CHANT_FOR_CAUSE,savedBean.getNo_chants());
                updateLocalCountTable(mDatabase,localCv,savedBean.getUser_namakoti_id(), DatabaseHelper.CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME,mUserIdLong);
                updateServerCountTable(mDatabase,serverCv,savedBean.getUser_namakoti_id(), DatabaseHelper.CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME,mUserIdLong);
            }
        }
    }

    private ArrayList<String> getCauseList(List<CauseBean> causeList) {
        ArrayList<String> causes = new ArrayList<>();
        causes.add(getString(R.string.select));
        for (int i = 0; i < causeList.size(); i++) {
            causes.add(causeList.get(i).getName());
        }
        return causes;
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()){
            case R.id.spinnerLang:
                if (mLanguageList != null && mLanguageList.size() > 0){
                    String language = mSpinnerLang.getSelectedItem().toString();
                    if (position != 0 && mLanguageList != null && mLanguageList.size() > 0){
                        mSelectedLangCode = mLanguageList.get(position-1).getLanguage_id();
                        if (mSelectedLangCode != null && mSelectedThemeId != null)
                            getChatsList();
                    }
                }
                break;
            case R.id.spinnerNamas:
                if (mChantsList != null && mChantsList.size() > 0) {
                    String namas = mSpinnerNamas.getSelectedItem().toString();
                    if (mChantsList != null && mChantsList.size() > 0) {
                        mSelectedChnatsSubThemeid = mChantsList.get(position).getSub_theme_id();
                    }
                }
                break;
            case R.id.spinnerCause:
                if (mCauseList != null && mCauseList.size() > 0) {
                    String cause = mSpinnerCause.getSelectedItem().toString();
                    if (mCauseList != null && mCauseList.size() > 0) {
                        if (position != 0){
                            mSelectedCauseId = mCauseList.get(position-1).getId();
                        }else{
                            mSelectedThemeId = null;
                            mGodNameTV.setText(getString(R.string.select));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideSoftKeyboard(mActivity,mCountChantET);
        return  false;
    }
}
