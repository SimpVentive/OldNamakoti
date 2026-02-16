package com.namakoti.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.EditProfileDetailsBean;
import com.namakoti.components.CircularNetworkImageView;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by anusha on 6/10/2018.
 */

public class ProfileFragment extends BaseFragment implements VolleyResponseListener{


    private static final int REQUEST_CODE_EDIT_PROFILE = 100;
    @BindView(R.id.button_save)
    public Button mSaveButton;

    @BindView(R.id.advancedOption)
    CheckBox mAdvancedCb;

    @BindView(R.id.advancedLL)
    LinearLayout mAdvancedLL;

    @BindView(R.id.checkbox_selfChanting)
    CheckBox mSelfChantCb;

    @BindView(R.id.checkbox_chanting_others)
    CheckBox mSelfChantOthersCb;

    @BindView(R.id.checkbox_attending_person)
    CheckBox mAttendingPersonCb;

    @BindView(R.id.checkbox_social_cause)
    CheckBox mChantForSocialCb;

    @BindView(R.id.firstNameEt)
    public EditText mFirstNameEt;

    @BindView(R.id.lastNameEt)
    public EditText mLastNameEt;

    @BindView(R.id.maleRb)
    public RadioButton mMaleRb;

    @BindView(R.id.femaleRb)
    public RadioButton mFemaleRb;

    @BindView(R.id.spinnerDate)
    public Spinner mSpinnerDate;

    @BindView(R.id.spinnerMonth)
    public Spinner mSpinnerMonth;

    @BindView(R.id.spinnerYear)
    public Spinner mSpinnerYear;

    @BindView(R.id.spinnerSalutation)
    public Spinner mSpinnerSalutation;

    @BindView(R.id.gothramEt)
    public EditText mGothramEt;

    @BindView(R.id.address1Et)
    public EditText mAddress1Et;

    @BindView(R.id.address2Et)
    public EditText mAddress2Et;

    @BindView(R.id.postalCodeEt)
    public EditText mPostalCodeEt;

    @BindView(R.id.cityEt)
    public EditText mCityEt;

    @BindView(R.id.stateEt)
    public EditText mStateEt;

    @BindView(R.id.spinnerCountry)
    public Spinner mSpinnerCountry;

    @BindView(R.id.emailEt)
    public EditText mEmailEt;

    @BindView(R.id.mobileEt)
    public EditText mMobileEt;

    @BindView(R.id.imageView_userProfile)
    public CircularNetworkImageView mProfileImage;

    private ArrayList<String> mMonthsList;
    private ArrayList<String> mSaluteList;
    private ArrayList<String> mDateList;
    private ArrayList<String> mYearList;
    private int mSelectedSalute = 0;
    private String mSelectedDate;
    private String mSelectedYear;
    private String mSelectedMonth;
    private ArrayList<String> mCountryList;
    private int mSelecteCountry = 0;
    private EditProfileDetailsBean mProfileBean;
    private ImageLoader mImageLoader;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private Bitmap finalBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.edit_profile, container, false);
        ButterKnife.bind(this,view);
        mSaveButton.setVisibility(View.GONE);

        mImageLoader = VolleyUtil.getInstance().imageLoader(mBaseActivity,com.intuit.sdp.R.dimen._65sdp);

        editProfileRequest();
        dissableViews();

        return view;
    }

    private void dissableViews() {
        mFirstNameEt.setEnabled(false);
        mLastNameEt.setEnabled(false);
        mMaleRb.setEnabled(false);
        mFemaleRb.setEnabled(false);
        mSpinnerDate.setEnabled(false);
        mSpinnerMonth.setEnabled(false);
        mSpinnerYear.setEnabled(false);
        mSpinnerSalutation.setEnabled(false);
        mGothramEt.setEnabled(false);
        mAddress1Et.setEnabled(false);
        mAddress2Et.setEnabled(false);
        mPostalCodeEt.setEnabled(false);
        mCityEt.setEnabled(false);
        mStateEt.setEnabled(false);
        mSpinnerCountry.setEnabled(false);
        mEmailEt.setEnabled(false);
        mMobileEt.setEnabled(false);
        mAdvancedCb.setEnabled(false);
        mSelfChantCb.setEnabled(false);
        mSelfChantOthersCb.setEnabled(false);
        mAttendingPersonCb.setEnabled(false);
        mChantForSocialCb.setEnabled(false);
    }

    private void editProfileRequest() {
        showProgress(true);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mBaseActivity.mUserIdString);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, Constants.EDIT_PROFILE_DETAILS, params, ServiceMethod.EDIT_PROFILE_DETAILS, Request.Method.POST, this);

    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        if (object != null && object instanceof EditProfileDetailsBean) {
            mProfileBean = (EditProfileDetailsBean) object;
            updateUI(mProfileBean );
        }
    }

    private void updateUI(EditProfileDetailsBean bean) {
//        url = UserInfoBean.getInstance().getProfileImage(mBaseActivity);
        if (! TextUtils.isEmpty(bean.userphoto))
            mProfileImage.setImageUrl(Constants.GOD_IMAGE_BASE_URL+ bean.userphoto, mImageLoader);

        mFirstNameEt.setText(bean.first_name);
        mLastNameEt.setText(checkNull(bean.last_name));
        mGothramEt.setText(bean.gothram);
        mAddress1Et.setText(checkNull(bean.address1));
        mAddress2Et.setText(checkNull(bean.address2));
        mPostalCodeEt.setText(""+bean.zip_code);
        mCityEt.setText(bean.city);
        mStateEt.setText(bean.state);
        mEmailEt.setText(bean.email);
        mMobileEt.setText(""+bean.mobile);
        mSelfChantCb.setChecked(bean.self_chant);
        mSelfChantOthersCb.setChecked(bean.other_chant);
        mAttendingPersonCb.setChecked(bean.attending_in_person);
        mChantForSocialCb.setChecked(bean.chant_for_social_issue);
        if (bean.self_chant || bean.other_chant || bean.attending_in_person || bean.chant_for_social_issue){
            mAdvancedLL.setVisibility(View.VISIBLE);
            mAdvancedCb.setChecked(true);
        }
        else{
            mAdvancedLL.setVisibility(View.GONE);
            mAdvancedCb.setChecked(false);
        }

        if (bean.gender != null  && !TextUtils.isEmpty(bean.gender)){
            if (bean.gender.equalsIgnoreCase("M")){
                mMaleRb.setChecked(true);
            }
            else {
                mFemaleRb.setChecked(true);
            }
        }
        else{
            mMaleRb.setChecked(true);
        }
        updateSpinnerUI(bean);
    }

    private void updateSpinnerUI(EditProfileDetailsBean bean) {
        mMonthsList = Utils.getMonthList(mBaseActivity);
        mDateList = Utils.getDateList(mBaseActivity);
        mYearList = Utils.getYearList(mBaseActivity);
        if (bean.allsalute != null && bean.allsalute.size() > 0){
            mSaluteList = Utils.getSalutationList(bean.allsalute);
            Utils.setSpinnerAdapter(mBaseActivity,mSaluteList, mSpinnerSalutation);
        }
        if (bean.allcountries != null && bean.allcountries.size() > 0){
            mCountryList = Utils.getCountryList(bean.allcountries);
            Utils.setSpinnerAdapter(mBaseActivity,mCountryList, mSpinnerCountry);
        }
        Utils.setSpinnerAdapter(mBaseActivity,mMonthsList, mSpinnerMonth);
        Utils.setSpinnerAdapter(mBaseActivity,mDateList, mSpinnerDate);
        Utils.setSpinnerAdapter(mBaseActivity,mYearList, mSpinnerYear);

        if (bean.title != null && !TextUtils.isEmpty(bean.title)){
            for (int i =0; i<bean.allsalute.size(); i++){
                EditProfileDetailsBean.CodeValues code = bean.allsalute.get(i);
                if (bean.title.equalsIgnoreCase(code.code)){
                    mSpinnerSalutation.setSelection(i);
                    mSelectedSalute = i;
                    break;
                }
            }
        }
        if (bean.isd_code != null && !TextUtils.isEmpty(bean.isd_code)){
            for (int i =0; i<bean.allcountries.size(); i++){
                EditProfileDetailsBean.AllCountriesList code = bean.allcountries.get(i);
                if (bean.isd_code.equalsIgnoreCase(code.cc)){
                    mSpinnerCountry.setSelection(i);
                    mSelecteCountry = i;
                    break;
                }
            }
        }
        if (bean.date_of_birth2 != null && !TextUtils.isEmpty(bean.date_of_birth2)){
            String date = null;
            String month = null;
            String year = null;
            try {
                String[] dateArray  = bean.date_of_birth2.split(" ");
                year = dateArray[0];
                month = dateArray[1];
                date = dateArray[2];
            }catch (Exception e){
                e.printStackTrace();
            }
            if (year != null && !TextUtils.isEmpty(year)){
                for (int i =0; i<mYearList.size(); i++){
                    if (year.equalsIgnoreCase(mYearList.get(i))){
                        mSpinnerYear.setSelection(i);
                        mSelectedYear = mYearList.get(i);
                        break;
                    }
                }
            }
            if (month != null && !TextUtils.isEmpty(month)){
                for (int i =0; i<mMonthsList.size(); i++){
                    if (month.equalsIgnoreCase(mMonthsList.get(i))){
                        mSpinnerMonth.setSelection(i);
                        mSelectedMonth = mMonthsList.get(i);
                        break;
                    }
                }
            }
            if (date != null && !TextUtils.isEmpty(date)){
                for (int i =0; i<mDateList.size(); i++){
                    if (date.equalsIgnoreCase(mDateList.get(i))){
                        mSpinnerDate.setSelection(i);
                        mSelectedDate = mDateList.get(i);
                        break;
                    }
                }
            }
        }
    }

    private String checkNull(String title) {
        if (title == null || title.equalsIgnoreCase("null")){
            title = "";
        }
        return title;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                startActivityForResult(new Intent(mBaseContext, EditProfileActivity.class), REQUEST_CODE_EDIT_PROFILE);
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PROFILE) {
            if (resultCode == RESULT_OK){
                editProfileRequest();
            }
        }
    }
}
