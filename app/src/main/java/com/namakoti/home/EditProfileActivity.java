package com.namakoti.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.EditProfileDetailsBean;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.utils.Constants;
import com.namakoti.utils.PermissionListener;
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
 * Created by anusha on 12/8/2017.
 */

public class EditProfileActivity extends BaseActivity implements VolleyResponseListener,View.OnClickListener,
        AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener, PermissionListener{

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

    private EditProfileActivity mActivity;
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
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ButterKnife.bind(this);
        mActivity = this;
        mActivity.setToolbarWithBack(getString(R.string.edit_profile));

        mImageLoader = VolleyUtil.getInstance().imageLoader(this,com.intuit.sdp.R.dimen._65sdp);
        url = UserInfoBean.getInstance().getProfileImage(this);
        if (! TextUtils.isEmpty(url))
            mProfileImage.setImageUrl(Constants.GOD_IMAGE_BASE_URL+ url, mImageLoader);

        setListeners();
        editProfileRequest();
    }

    private void setListeners() {
        mSpinnerSalutation.setOnItemSelectedListener(this);
        mSpinnerDate.setOnItemSelectedListener(this);
        mSpinnerMonth.setOnItemSelectedListener(this);
        mSpinnerYear.setOnItemSelectedListener(this);
        mSpinnerCountry.setOnItemSelectedListener(this);
        mSaveButton.setOnClickListener(this);
        mAdvancedCb.setOnCheckedChangeListener(this);
        mProfileImage.setOnClickListener(this);

    }

    private void editProfileRequest() {
        showProgress(true, mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserIdString);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.EDIT_PROFILE_DETAILS, params, ServiceMethod.EDIT_PROFILE_DETAILS, Request.Method.POST, this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_save:
                performValidation();
                break;
            case R.id.imageView_userProfile:
                selectImage(REQUEST_CAMERA,SELECT_FILE, this);
                break;
        }
    }

    private void performValidation() {
        String firstname = mFirstNameEt.getText().toString();
        String lastname = mLastNameEt.getText().toString();
        String gothram = mGothramEt.getText().toString();
        String address1 = mAddress1Et.getText().toString();
        String address2 = mAddress2Et.getText().toString();
        String pincode = mPostalCodeEt.getText().toString();
        String city = mCityEt.getText().toString();
        String state = mStateEt.getText().toString();
        String email = mEmailEt.getText().toString();
        String phone = mMobileEt.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
            mFirstNameEt.setError(getString(R.string.error_firstName));
            mFirstNameEt.requestFocus();
        }
        else if (TextUtils.isEmpty(lastname)) {
            mLastNameEt.setError(getString(R.string.error_lastname));
            mLastNameEt.requestFocus();
        }
        else if (mSelectedDate == null || mSelectedDate.equalsIgnoreCase(getString(R.string.select))) {
            Utils.showToast(mActivity,"Please select date");
        }
        else if (mSelectedMonth == null || mSelectedMonth.equalsIgnoreCase(getString(R.string.select))) {
            Utils.showToast(mActivity,"Please select month");
        }
        else if (mSelectedYear == null || mSelectedYear.equalsIgnoreCase(getString(R.string.select))) {
            Utils.showToast(mActivity,"Please select year");
        }
        else if (TextUtils.isEmpty(gothram)) {
            mGothramEt.setError(getString(R.string.error_gothram));
            mGothramEt.requestFocus();
        }

        else if (TextUtils.isEmpty(address1)) {
            mAddress1Et.setError(getString(R.string.error_address1));
            mAddress1Et.requestFocus();
        }
        else if (TextUtils.isEmpty(address2)) {
            mAddress2Et.setError(getString(R.string.error_address2));
            mAddress2Et.requestFocus();
        }
        else if (TextUtils.isEmpty(pincode)) {
            mPostalCodeEt.setError(getString(R.string.error_pincode));
            mPostalCodeEt.requestFocus();
        }
        else if (TextUtils.isEmpty(city)) {
            mCityEt.setError(getString(R.string.error_city));
            mCityEt.requestFocus();
        }
        else if (TextUtils.isEmpty(state)) {
            mStateEt.setError(getString(R.string.error_state));
            mStateEt.requestFocus();
        }

        else if (TextUtils.isEmpty(email)) {
            mEmailEt.setError(getString(R.string.error_email));
            mEmailEt.requestFocus();
        }else if (!Utils.emailValidator(mEmailEt.getText().toString())) {
            mEmailEt.setError(getString(R.string.error_valid_email));
            mEmailEt.requestFocus();
        }
        else if (TextUtils.isEmpty(phone)) {
            mMobileEt.setError(getString(R.string.error_phone));
            mMobileEt.requestFocus();
        }
        else if (phone != null && phone.length() > 0 && !mPhonePattern.matcher(phone).matches()){
            mMobileEt.setError(getString(R.string.error_valid_phone));
            mMobileEt.requestFocus();
        }
        else if (phone.length() < 10){
            mMobileEt.setError(getString(R.string.error_valid_phone));
            mMobileEt.requestFocus();
        }
        else {
            saveProfileRequest();
        }
    }


    private void saveProfileRequest() {
        showProgress(true, mActivity);
        String urlString = (url != null) ? url:"";
        checkNull(mProfileBean.title);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserIdString);
        params.put("title", mProfileBean.allsalute.get(mSelectedSalute).code);
        params.put("last_name", mLastNameEt.getText().toString());
        params.put("gender", checkGender());
        params.put("date_of_birth", mSelectedYear+"-" + Utils.monthStringToInt(mSelectedMonth)+"-" +mSelectedDate);
        params.put("gothram", mGothramEt.getText().toString());
        params.put("address2", mAddress2Et.getText().toString());
        params.put("city", mCityEt.getText().toString());
        params.put("state", mStateEt.getText().toString());
        params.put("country", mProfileBean.allcountries.get(mSelecteCountry).cc);
        params.put("email", mEmailEt.getText().toString());
        params.put("address1", mAddress1Et.getText().toString());

        params.put("first_name",mFirstNameEt.getText().toString());
        params.put("mobile", mMobileEt.getText().toString());
        params.put("zip_code", mPostalCodeEt.getText().toString());
        params.put("selfchant", (mAdvancedCb.isChecked())? mSelfChantCb.isChecked() + "" : ""+false);
        params.put("otherchant", (mAdvancedCb.isChecked())? mSelfChantOthersCb.isChecked() + "" : ""+false);
        params.put("attendinperson", (mAdvancedCb.isChecked())? mAttendingPersonCb.isChecked() + "" : ""+false);
        params.put("socialissue", (mAdvancedCb.isChecked())? mChantForSocialCb.isChecked() + "" : ""+false);
        params.put("file",(finalBitmap != null) ? getStringImage(finalBitmap) :urlString);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.UPDATE_PROFILE_DETAILS, params, ServiceMethod.UPDATE_PROFILE, Request.Method.POST, this);
    }

    private String checkGender() {
        String gender = "";
        if (mMaleRb.isChecked()){
            gender = "M";
        }else if (mFemaleRb.isChecked()){
            gender = "F";
        }

        return gender;
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object != null && object instanceof EditProfileDetailsBean) {
            mProfileBean = (EditProfileDetailsBean) object;
            updateUI(mProfileBean );
        }
        else if (object != null && serviceMethod == ServiceMethod.UPDATE_PROFILE) {
            ErrorBean bean = (ErrorBean) object;
            Utils.showToast(mActivity,bean.message);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void updateUI(EditProfileDetailsBean bean) {
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
        mMonthsList = Utils.getMonthList(mActivity);
        mDateList = Utils.getDateList(mActivity);
        mYearList = Utils.getYearList(mActivity);
        if (bean.allsalute != null && bean.allsalute.size() > 0){
            mSaluteList = Utils.getSalutationList(bean.allsalute);
            Utils.setSpinnerAdapter(mActivity,mSaluteList, mSpinnerSalutation);
        }
        if (bean.allcountries != null && bean.allcountries.size() > 0){
            mCountryList = Utils.getCountryList(bean.allcountries);
            Utils.setSpinnerAdapter(mActivity,mCountryList, mSpinnerCountry);
        }
        Utils.setSpinnerAdapter(mActivity,mMonthsList, mSpinnerMonth);
        Utils.setSpinnerAdapter(mActivity,mDateList, mSpinnerDate);
        Utils.setSpinnerAdapter(mActivity,mYearList, mSpinnerYear);

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
            case R.id.spinnerSalutation:
                mSelectedSalute = mSpinnerSalutation.getSelectedItemPosition();

                break;
            case R.id.spinnerDate:
                if (! mDateList.get(position).equalsIgnoreCase(getString(R.string.select))){
                    mSelectedDate = mDateList.get(position);
                }
                break;
            case R.id.spinnerMonth:
                if (! mMonthsList.get(position).equalsIgnoreCase(getString(R.string.select))){
                    mSelectedMonth = mMonthsList.get(position);
                }break;
            case R.id.spinnerYear:
                if (! mYearList.get(position).equalsIgnoreCase(getString(R.string.select))){
                    mSelectedYear = mYearList.get(position);
                }
                break;
            case R.id.spinnerCountry:
                mSelecteCountry =mSpinnerCountry.getSelectedItemPosition();
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()){
            case R.id.advancedOption:
                if (checked){
                    mAdvancedLL.setVisibility(View.VISIBLE);
                }
                else{
                    mAdvancedLL.setVisibility(View.GONE);
                    mSelfChantCb.setChecked(false);
                    mSelfChantOthersCb.setChecked(false);
                    mAttendingPersonCb.setChecked(false);
                    mChantForSocialCb.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bm = null;
                if (requestCode == SELECT_FILE){
                    bm = onSelectFromGalleryResult(data);
                }
                else if (requestCode == REQUEST_CAMERA){
                    bm = onCaptureImageResult(data);
                }
                finalBitmap = bm;
                mProfileImage.setImageBitmap(bm);

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onPermissionAccepted(int code) {
        if(code == Constants.REQUEST_CAMERA_PERMISSION){
            cameraIntent(REQUEST_CAMERA, this);
        }else if(code == Constants.REQUEST_GALLERY_PERMISSION){
            galleryIntent(SELECT_FILE, this);
        }
    }

    @Override
    public void onPermissionDenied(int code) {
        if(code == Constants.REQUEST_CAMERA_PERMISSION){

            Utils.showToast(this, "This permission is required to capture the picture");

        }else if(code == Constants.REQUEST_GALLERY_PERMISSION){

            Utils.showToast(this, "This permission is required to select a picture");

        }else if(code == Constants.REQUEST_MULTIPLE_PERMISSION){
            Utils.showToast(this, "Please accept all permissions to proceed");
        }
    }
}
