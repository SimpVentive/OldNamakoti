package com.namakoti.karmic.beingHelped;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.CauseBean;
import com.namakoti.beans.ChantsBean;
import com.namakoti.beans.CreateBeingHelpBean;
import com.namakoti.beans.LanguagesBean;
import com.namakoti.beans.RequestCauseForBean;
import com.namakoti.chanting.self.SelectGodActivity;
import com.namakoti.utils.CalendarUtils;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 12/14/2017.
 */

public class NewBeingHelpedActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener,
        View.OnTouchListener, AdapterView.OnItemSelectedListener {


    private static final int SELECT_GOD = 1;
    private static final int FINISH_ACTIVITY_CODE = 2;
    protected static String BUNDLE_SAVED_VALUES = "BUNDLE_KEY";
    @BindView(R.id.textView_godName_cc)
    TextView mGodNameTV;
    @BindView(R.id.selecgod_rl)
    RelativeLayout mSelectGodRL;
    @BindView(R.id.spinnerRequestFor)
    Spinner mSpinnerRequestFor;
    @BindView(R.id.spinnerCause)
    Spinner mSpinnerCause;
    @BindView(R.id.spinnerLang)
    Spinner mSpinnerLang;
    @BindView(R.id.namamEtv)
    EditText mNamamEtv;
    @BindView(R.id.spinnerNamas)
    Spinner mSpinnerNamas;
    @BindView(R.id.editText_noOfChants_cc)
    EditText mCountChantET;
    @BindView(R.id.startDateRL)
    RelativeLayout mStartDateRL;
    @BindView(R.id.endtDateRL)
    RelativeLayout mEndtDateRL;
    @BindView(R.id.startTimeRL)
    RelativeLayout mStartTimeRL;
    @BindView(R.id.endtTimeRL)
    RelativeLayout mEndtTimeRL;
    @BindView(R.id.startDateTv)
    TextView mStartDateTv;
    @BindView(R.id.endDateTv)
    TextView mEndDateTv;
    @BindView(R.id.startDateIv)
    ImageView mStartDateIv;
    @BindView(R.id.endDateIv)
    ImageView mEndDateIv;
    @BindView(R.id.startTimeTv)
    TextView mStartTimeTv;
    @BindView(R.id.endtTimeTv)
    TextView mEndTimeTv;
    @BindView(R.id.startTimeIv)
    ImageView mStartTimeIv;
    @BindView(R.id.endtTimeIv)
    ImageView mEndTimeIv;
    @BindView(R.id.nirgunaRg)
    RadioGroup mNirgunaRg;
    @BindView(R.id.nirgunaRb)
    RadioButton mNirgunaRb;
    @BindView(R.id.sagunaRb)
    RadioButton mSagunaRb;
    @BindView(R.id.addParticipantsRg)
    RadioGroup mAddParticipantsRg;
    @BindView(R.id.addYesRb)
    RadioButton mAddYesRb;
    @BindView(R.id.addNoRb)
    RadioButton mAddNoRb;
    @BindView(R.id.chantsDoYouWantToDoEt)
    EditText mChantsDoYouWantToDoEt;
    @BindView(R.id.noOfPrayersEtv)
    EditText mNoOfPrayersEtv;
    @BindView(R.id.chantsPerPersonEtv)
    EditText mChantsPerPersonEtv;
    @BindView(R.id.button_invite)
    Button mInviteButton;
    private String mSelectedRequestCauseId;
    private String mSelectedGod;
    private String mSelectedLangCode;
    private String mSelectedCauseId;
    private String mSelectedChnatsSubThemeid;
    private String mSelectedThemeId;
    private List<CauseBean> mCauseList;
    private List<LanguagesBean> mLanguageList;
    private List<RequestCauseForBean> mRequestForCauseList;
    private NewBeingHelpedActivity mActivity;
    private boolean isStarDateValidated;
    private boolean isEndDateValidated;
    private List<ChantsBean> mChantsList;
    private int mStartHr;
    private int mStartMin;
    private int mEndHr;
    private int mEndMin;
    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            mStartHr = hourOfDay;
            mStartMin = minutes;
            String selectedTime = CalendarUtils.updateTime(mStartHr, mStartMin);
            mStartTimeTv.setText(selectedTime);
        }
    };
    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            mEndHr = hourOfDay;
            mEndMin = minutes;
            String selectedTime = CalendarUtils.updateTime(mEndHr, mEndMin);
            mEndTimeTv.setText(selectedTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_being_helped_chant);
        ButterKnife.bind(this);
        mActivity = this;

        setListeners();
        String time = CalendarUtils.getCurrentTime(Constants.TIME_FORMAT);
        String[] timeArray = time.split(":");
        if (timeArray != null && timeArray.length > 0) {
            mStartHr = Integer.parseInt(timeArray[0]);
            mEndHr = mStartHr;
            mStartMin = Integer.parseInt(timeArray[1]);
            mEndMin = mStartMin;
        }
        String selectedTime = CalendarUtils.updateTime(mStartHr, mStartMin);
        mStartTimeTv.setText(selectedTime);
        mEndTimeTv.setText(selectedTime);
        mActivity.setToolbarWithBack(getString(R.string.new_chant_for_cause));
        getCauseForList();
    }

    private void setListeners() {
        mSelectGodRL.setOnClickListener(this);
        mInviteButton.setOnClickListener(this);
        mStartDateRL.setOnClickListener(this);
        mEndtDateRL.setOnClickListener(this);
        mEndtTimeRL.setOnClickListener(this);
        mStartTimeRL.setOnClickListener(this);

        mSpinnerLang.setOnItemSelectedListener(this);
        mSpinnerCause.setOnItemSelectedListener(this);
        mSpinnerRequestFor.setOnItemSelectedListener(this);
        mSpinnerNamas.setOnItemSelectedListener(this);

        mSpinnerLang.setOnTouchListener(this);
        mSpinnerCause.setOnTouchListener(this);
        mSpinnerNamas.setOnTouchListener(this);
        mSpinnerNamas.setOnTouchListener(this);
    }

    private void getLanguages() {
        showProgress(true, mActivity);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.GET_LAUNGUAGES, null, ServiceMethod.GET_LANGUAGES, Request.Method.GET, this);
    }

    private void getCauseForList() {
        showProgress(true, mActivity);
        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.GET_KARMIC_CAUSE_FOR_URL, null, ServiceMethod.KARMIC_CAUSE_FOR, Request.Method.GET, this);
    }

    private void getCauses() {
        showProgress(true, mActivity);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CAUSE_OPTIONS, null, ServiceMethod.CAUSE_OPTIONS, Request.Method.GET, this);
    }

    private void getChatsList() {
        showProgress(true, mActivity);

        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).getChantListParams(mSelectedLangCode, mSelectedThemeId);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CAUSE_CHANT_LIST, params, ServiceMethod.GET_CHANTS, Request.Method.POST, this);
    }

    private void performValidation() {
        String chant = mCountChantET.getText().toString();
        String selfChant = mChantsDoYouWantToDoEt.getText().toString();
        String perPerson = mChantsPerPersonEtv.getText().toString();
        String countPart = mNoOfPrayersEtv.getText().toString();
        if (mSpinnerRequestFor.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select Request for Cause", null, null, false, true);
        } else if (mSpinnerCause.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select Cause", null, null, false, true);
        } else if (mGodNameTV.getText().toString().equalsIgnoreCase(getString(R.string.select))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select God", null, null, false, true);
        } else if (mSpinnerLang.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select Language", null, null, false, true);
        }
//        else if (TextUtils.isEmpty(mNamamEtv.getText().toString())){
//            mNamamEtv.requestFocus();
//            mNamamEtv.setError(getString(R.string.error_namam));
//        }
        else if (TextUtils.isEmpty(chant)) {
            mCountChantET.requestFocus();
            mCountChantET.setError(getString(R.string.error_chant_count));
        }
        else if ((chant != null) && Long.parseLong(chant) == 0){
            mCountChantET.requestFocus();
            mCountChantET.setError(getString(R.string.error_valid_chant));
        }
        else if (TextUtils.isEmpty(selfChant)) {
            mChantsDoYouWantToDoEt.requestFocus();
            mChantsDoYouWantToDoEt.setError(getString(R.string.error_self_chant_no));
        }
        else if (selfChant != null && selfChant.length() > 0 && chant != null && chant.length() > 0
                && (Long.parseLong(chant) < Long.parseLong(selfChant))) {
            mChantsDoYouWantToDoEt.requestFocus();
            mChantsDoYouWantToDoEt.setError(getString(R.string.error_chant_self_person_valid));
        }
        else if (mStartDateTv == null || mStartDateTv.getText().toString().equalsIgnoreCase(getString(R.string.start_date))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select StartDate", null, null, false, true);
        } else if (mEndDateTv != null && !startDateValidator()) {
            Toast.makeText(mActivity, "Start date cannot be less than current date", Toast.LENGTH_LONG).show();
        } else if (mEndDateTv.getText() == null || mEndDateTv.getText().toString().equalsIgnoreCase(getString(R.string.end_date))) {
            Utils.showAlertDialog(mActivity, "Error", "Please select EndDate", null, null, false, true);
        } else if (mEndDateTv != null && !endtDateValidator()) {
            Toast.makeText(mActivity, "End date must be greater than or equal to the start date.", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(countPart)) {
            mNoOfPrayersEtv.requestFocus();
            mNoOfPrayersEtv.setError(getString(R.string.error_no_of_people));
        }
        else if (Long.parseLong(countPart) == 0) {
            mNoOfPrayersEtv.requestFocus();
            mNoOfPrayersEtv.setError("Please enter valid prayers count");
        }
        else if (TextUtils.isEmpty(perPerson)) {
            mChantsPerPersonEtv.requestFocus();
            mChantsPerPersonEtv.setError(getString(R.string.error_people_count_no));
        }
        else if (Long.parseLong(perPerson) == 0) {
            mChantsPerPersonEtv.requestFocus();
            mChantsPerPersonEtv.setError("Please enter valid prayers chant count");
        }
        else if (perPerson != null && perPerson.length() > 0 && chant != null && chant.length() > 0
                && (Long.parseLong(chant) < (Long.parseLong(selfChant)+
                (Long.parseLong(perPerson) * Long.parseLong(countPart))))) {
            mChantsPerPersonEtv.requestFocus();
            mChantsPerPersonEtv.setError(getString(R.string.error_chant_per_person_valid));
        }
        /*else if (mStartDateTv != null && ! isStarDateValidated ){
            Date startDate = CalendarUtils.ConvertStingtoDate(mStartDateTv.getText().toString(), Constants.DATE_FORMAT);
            if(now.after(startDate) ) {
                if (! nowStr.equals(mStartDateTv.getText().toString()))
                    Toast.makeText(mActivity,"Start date cannot be less than current date",Toast.LENGTH_LONG).show();
                else
                    isStarDateValidated = true;
            } else
                isStarDateValidated = true;
        } else if (mEndDateTv != null && !isEndDateValidated) {
            if (mEndDateTv != null || mEndDateTv.getText().toString().equalsIgnoreCase(getString(R.string.start_date))) {
                Date endDate = CalendarUtils.ConvertStingtoDate(mEndDateTv.getText().toString(), Constants.DATE_FORMAT);
                Date startDate = CalendarUtils.ConvertStingtoDate(mStartDateTv.getText().toString(), Constants.DATE_FORMAT);
                if (startDate.after(endDate)) {
                    if (!mStartDateTv.getText().toString().equals(mEndDateTv.getText().toString()))
                        Toast.makeText(mActivity, "End date must be greater than or equal to the start date.", Toast.LENGTH_LONG).show();
                    else
                        isEndDateValidated = true;
                } else
                    isEndDateValidated = true;
            }
        } */ else {
            callInviteScreen();
        }
    }

    private boolean endtDateValidator() {
        if (mEndDateTv != null || mEndDateTv.getText().toString().equalsIgnoreCase(getString(R.string.start_date))) {
            Date endDate = CalendarUtils.ConvertStingtoDate(mEndDateTv.getText().toString(), Constants.DATE_FORMAT);
            Date startDate = CalendarUtils.ConvertStingtoDate(mStartDateTv.getText().toString(), Constants.DATE_FORMAT);
            if (startDate.after(endDate)) {
                if (!mStartDateTv.getText().toString().equals(mEndDateTv.getText().toString()))
                    return false;
                else
                    return true;
            } else
                return true;
        }
        return false;
    }

    private boolean startDateValidator() {
        if (mStartDateTv != null || mStartDateTv.getText().toString().equalsIgnoreCase(getString(R.string.start_date))) {
            Date now = CalendarUtils.calendarToDate(Calendar.getInstance());
            String nowStr = CalendarUtils.convertDateToStringObject(now, Constants.DATE_FORMAT);
            Date startDate = CalendarUtils.ConvertStingtoDate(mStartDateTv.getText().toString(), Constants.DATE_FORMAT);
            if (now.after(startDate)) {
                if (!nowStr.equals(mStartDateTv.getText().toString()))
                    return false;
                else
                    return true;
            } else
                return true;
        }
        return false;
    }

    private void callInviteScreen() {
        CreateBeingHelpBean bean = new CreateBeingHelpBean();
        bean.setRequestId(mSelectedRequestCauseId);
        bean.setCauseId(mSelectedCauseId);
        bean.setGodId(mSelectedThemeId);
        bean.setLanguageId(mSelectedLangCode);
        bean.setNamam(mSelectedChnatsSubThemeid);
        bean.setNoOfChants(mCountChantET.getText().toString());
        bean.setSelfChantNo(mChantsDoYouWantToDoEt.getText().toString());
        bean.setNoOfPeopleHelp(""+Integer.parseInt(mNoOfPrayersEtv.getText().toString()));
        bean.setPeopleChantNo(mChantsPerPersonEtv.getText().toString());
        bean.setStartDate(mStartDateTv.getText().toString());
        bean.setEndDate(mEndDateTv.getText().toString());
        bean.setStartTime(mStartHr + ":" + mStartMin + ":00");
        bean.setEndTime(mEndHr + ":" + mEndMin + ":00");
        bean.setPujaType((mNirgunaRb.isChecked()) ? "NIRGUNA" : "SAGUNA");
        bean.setAddParticipantsType((mAddYesRb.isChecked()) ? "YES" : "NO");
        Intent intent = new Intent(mActivity, InviteParticipantsActivity.class);
        intent.putExtra(BUNDLE_SAVED_VALUES, bean);
        startActivityForResult(intent,FINISH_ACTIVITY_CODE);
    }

    private boolean checkDateValidation() {
        Date startDate = CalendarUtils.ConvertStingtoDate(mStartDateTv.getText().toString(), Constants.DATE_FORMAT);
        Date now = new Date();
        if (mStartDateTv == null) {
            Utils.showAlertDialog(mActivity, "Error", "Please select StartDate", null, null, false, true);
        } else if (now.after(startDate) || now.equals(startDate)) {
            Toast.makeText(mActivity, "after today", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity, "Selected date is not in pill card range", Toast.LENGTH_LONG).show();
            return false;
        }

        /*else if (mEndDateTv != null){
            Utils.showAlertDialog(mActivity,"Error","Please select EndDate",null,null,false,true);
        }
        else if (now.after(selectedDate) || now.equals(selectedDate)) {
            if (selectedDate.after(pillDate) || selectedDate.equals(pillDate)) {

            }
        }*/
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selecgod_rl:
                if(mSpinnerCause != null && mSpinnerCause.getSelectedItem() != null){
                    if (mSpinnerCause.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select))) {
                        Utils.showAlertDialog(mActivity, "Error", "Please select Cause", null, null, false, true);
                        mSelectedThemeId = null;
                        mGodNameTV.setText(getString(R.string.select));
                    } else {
                        Intent intent = new Intent(mActivity, SelectGodActivity.class);
                        intent.putExtra(SelectGodActivity.EXTRA_URL_KEY, Constants.CAUSE_GODS_NAMES);
                        intent.putExtra(SelectGodActivity.EXTRA_CAUSE_KEY, mSelectedCauseId);
                        startActivityForResult(intent, SELECT_GOD);
                    }
                }
                break;
            case R.id.button_invite:
                performValidation();
                break;
            case R.id.startDateRL:
                showStartDatePicker();
                break;
            case R.id.endtDateRL:
                showEndDatePicker();
                break;
            case R.id.endtTimeRL:
                new TimePickerDialog(mActivity, endTimeListener, mEndHr, mEndMin, false).show();
                break;
            case R.id.startTimeRL:
                new TimePickerDialog(mActivity, startTimeListener, mStartHr, mStartMin, false).show();
                break;
        }
    }

    private void showStartDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mStartDateTv.setText(CalendarUtils.getStringFromCalendar("yyyy-MM-dd", calendar));
                        isStarDateValidated = false;
                    }
                }, year, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mEndDateTv.setText(CalendarUtils.getStringFromCalendar("yyyy-MM-dd", calendar));
                        isEndDateValidated = false;
                    }
                }, year, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_GOD) {
            if (resultCode == RESULT_OK) {
                mSelectedThemeId = data.getStringExtra(SelectGodActivity.EXTRA_SELECTED_GOD_THEMEID);
                mSelectedGod = data.getStringExtra(SelectGodActivity.EXTRA_SELECTED_GOD_THEME_NAME);
                if (mSelectedLangCode != null && !mGodNameTV.getText().toString().equalsIgnoreCase(mSelectedGod)) {
                    getChatsList();
                }
                mGodNameTV.setText(mSelectedGod);

            }
        }
        if (requestCode == FINISH_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object != null && object instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) object;
            if (!list.isEmpty() && list.get(0) instanceof RequestCauseForBean) {
                mRequestForCauseList = (List<RequestCauseForBean>) object;
                if (mRequestForCauseList != null && mRequestForCauseList.size() > 0) {
                    Utils.setSpinnerAdapter(mActivity, getRequestCauseForList(mRequestForCauseList), mSpinnerRequestFor);
                    getCauses();
                }
            } else if (!list.isEmpty() && list.get(0) instanceof CauseBean) {
                mCauseList = (List<CauseBean>) object;
                if (mCauseList != null && mCauseList.size() > 0) {
                    Utils.setSpinnerAdapter(mActivity, getCauseList(mCauseList), mSpinnerCause);
                    getLanguages();
                }
            } else if (!list.isEmpty() && list.get(0) instanceof LanguagesBean) {
                mLanguageList = (List<LanguagesBean>) object;
                if (mLanguageList != null && mLanguageList.size() > 0) {
                    Utils.setSpinnerAdapter(mActivity, getLanguageList(mLanguageList), mSpinnerLang);
                }
            } else if (!list.isEmpty() && list.get(0) instanceof ChantsBean) {
                mChantsList = (List<ChantsBean>) object;
                if (mChantsList != null && mChantsList.size() > 0) {
                    Utils.setSpinnerAdapter(mActivity, getChantList(mChantsList), mSpinnerNamas);
                }
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

    private ArrayList<String> getRequestCauseForList(List<RequestCauseForBean> causeList) {
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
        switch (adapterView.getId()) {
            case R.id.spinnerLang:
                if (mLanguageList != null && mLanguageList.size() > 0){
                    String language = mSpinnerLang.getSelectedItem().toString();
                    if (position != 0) {
                        mSelectedLangCode = mLanguageList.get(position - 1).getLanguage_id();
                        if (mSelectedLangCode != null && mSelectedThemeId != null)
                            getChatsList();
                    }
                }
                break;
            case R.id.spinnerCause:
                if (mCauseList != null && mCauseList.size() > 0){
                    String cause = mSpinnerCause.getSelectedItem().toString();
                    if (position != 0) {
                        mSelectedCauseId = mCauseList.get(position - 1).getId();
                    } else {
                        mSelectedThemeId = null;
                        mGodNameTV.setText(getString(R.string.select));
                    }
                }
                break;
            case R.id.spinnerRequestFor:
                if (mRequestForCauseList != null && mRequestForCauseList.size() > 0) {
                    String name = mSpinnerRequestFor.getSelectedItem().toString();
                    if (position != 0) {
                        mSelectedRequestCauseId = mRequestForCauseList.get(position - 1).getId();
                    }
                }
                break;
            case R.id.spinnerNamas:
                if (mChantsList != null && mChantsList.size() > 0) {
                    String namas = mSpinnerNamas.getSelectedItem().toString();
                    mSelectedChnatsSubThemeid = mChantsList.get(position).getSub_theme_id();
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
        hideSoftKeyboard(mActivity, mCountChantET);
        return false;
    }
}
