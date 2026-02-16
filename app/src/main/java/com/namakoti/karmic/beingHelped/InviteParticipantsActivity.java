package com.namakoti.karmic.beingHelped;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.AddParticipantsBean;
import com.namakoti.beans.ContactsBean;
import com.namakoti.beans.CreateBeingHelpBean;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.utils.Constants;
import com.namakoti.utils.JsonObjectUtils;
import com.namakoti.utils.PermissionListener;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/8/2018.
 */

public class InviteParticipantsActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, PermissionListener {


    private static final int CONTACT_LIST = 100;
    @BindView(R.id.contactsSelectionRg)
    RadioGroup mContactsSelectionRg;
    @BindView(R.id.contactsRb)
    RadioButton mContactsRb;
    @BindView(R.id.inviteFrndsRb)
    RadioButton mInviteFrndsRb;
    @BindView(R.id.totalPeopleTV)
    TextView mTotalPeopleTV;
    @BindView(R.id.youRequestedTV)
    TextView mYouRequestedTV;
    @BindView(R.id.parentEmailLL)
    LinearLayout mParentEmailLL;
    @BindView(R.id.includeEmailLL)
    LinearLayout mIncludeEmailLL;
    @BindView(R.id.messageEtv)
    EditText mMessageEtv;
    @BindView(R.id.descriptionEtv)
    EditText mDescriptionEtv;
    @BindView(R.id.yesOrNoRg)
    RadioGroup mYesOrNoRg;
    @BindView(R.id.yesRb)
    RadioButton mYesRb;
    @BindView(R.id.noRb)
    RadioButton mNoRb;
    @BindView(R.id.button_request)
    Button mRequestButton;
    private InviteParticipantsActivity mActivity;
    private EditText mAddNameEtv;
    private EditText mAddEmailEtv;
    private ImageView mAddIV;
    private ImageView mPreviousImageView;
    private CreateBeingHelpBean mBeingHelpBean;
    private JSONArray jArray;
    private ArrayList<ContactsBean> mContactsList;
    private ArrayList<ContactsBean> mSelectedContactList;
    private String mStartTime;
    private boolean mCreatedKarmicChant;
    private KarmicSelfBean.SelfGkcDetails mSelfGkcBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_participants);
        ButterKnife.bind(this);
        mActivity = this;
        getBundle();

        mActivity.setToolbarWithBack(getString(R.string.invite_participants));
        mAddNameEtv = mIncludeEmailLL.findViewById(R.id.addNameEtv);
        mAddEmailEtv = mIncludeEmailLL.findViewById(R.id.addEmailEtv);
        mAddIV = mIncludeEmailLL.findViewById(R.id.addIV);
        mPreviousImageView = mAddIV;
        setListeners();
        if (contactPermission(this)) {
            new ContactAsnyc().execute();
        }
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mBeingHelpBean = (CreateBeingHelpBean) intent.getSerializableExtra(NewBeingHelpedActivity.BUNDLE_SAVED_VALUES);
        }
    }

    private void setListeners() {
        mRequestButton.setOnClickListener(this);
        mAddIV.setOnClickListener(this);
        mContactsSelectionRg.setOnCheckedChangeListener(this);
        mYesOrNoRg.setOnCheckedChangeListener(this);
        mTotalPeopleTV.setOnClickListener(this);
        mContactsRb.setChecked(true);
        setEmailCheckRb(mAddNameEtv);
        setEmailCheckRb(mAddEmailEtv);
    }

    private void addParticipants(String setupId) {
        showProgress(true, mActivity);
        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).appParticipants(mActivity, jArray, setupId);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.ADD_PARTICIPANTS_URL, params, ServiceMethod.ADD_PARTICIPANTS, Request.Method.POST, this);
    }

    private void createKarmicChant(String msg) {
        showProgress(true, mActivity);
        Map<String, String> params = JsonObjectUtils.getInstance(mActivity).createKarmicChantJson(mActivity,
                msg, mDescriptionEtv.getText().toString(), mYesRb.isChecked() ? "YES" : "NO", mBeingHelpBean);

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.CREATE_KARMIC_CHANT_URL, params, ServiceMethod.CREATE_KARMIC_CHANT, Request.Method.POST, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addIV:
                Drawable imageDrawable = ((ImageView) view).getDrawable();
                Drawable plus = ContextCompat.getDrawable(mActivity, R.drawable.ic_add_orange);
                Drawable.ConstantState constantStateDrawableA = imageDrawable.getConstantState();
                Drawable.ConstantState constantStateDrawableB = plus.getConstantState();

                if (constantStateDrawableA.equals(constantStateDrawableB)) {
                    mPreviousImageView = (ImageView) view;
                    mPreviousImageView.setImageResource(R.drawable.ic_cross);
                    onAddField(view);

                } else {
                    onDelete(view);
                }
                break;
            case R.id.button_request:
                if (! mCreatedKarmicChant){
                    jArray = new JSONArray();
                    String msg = mMessageEtv.getText().toString();
                    String des = mDescriptionEtv.getText().toString();
                    boolean isExceededParticipants = false;
                    if (mContactsRb.isChecked()) {
                        if (mYouRequestedTV.getText().toString().equalsIgnoreCase("0")) {
                            jArray = null;
                        } else {
                            if (mSelectedContactList != null && mSelectedContactList.size() > 0) {
                                if (Long.parseLong(mBeingHelpBean.getNoOfPeopleHelp()) < Long.parseLong("" + mSelectedContactList.size())) {
                                    isExceededParticipants = true;
                                    Utils.showToast(mActivity, "Please do not exceed Add participants count more than " + mBeingHelpBean.getNoOfPeopleHelp() + " ");
                                    return;
                                }
                                if (!isExceededParticipants) {
                                    for (int i = 0; i < mSelectedContactList.size(); i++) {
                                        String num = mSelectedContactList.get(i).getNumber().trim().toString();
                                        if (num != null && num.length() > 0) {
                                            String mobile;
                                            if (num.contains("+91")) {
                                                mobile = num.replace("+91", " ").trim();
                                            } else {
                                                mobile = num;
                                            }
                                            JSONObject jObject = new JSONObject();
                                            try {
                                                jObject.put("name", mSelectedContactList.get(i).getName());
                                                jObject.put("mobile", mobile.trim().toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            jArray.put(jObject);
                                        }
                                    }
                                }
                            }
                        }
                        if (!isExceededParticipants) {
                            if (TextUtils.isEmpty(msg)) {
                                mMessageEtv.requestFocus();
                                mMessageEtv.setError(getString(R.string.error_message));
                            } else if (TextUtils.isEmpty(des)) {
                                mDescriptionEtv.requestFocus();
                                mDescriptionEtv.setError(getString(R.string.error_description));
                            } else {
                                createKarmicChant(msg);
                            }
                        }
                    } else if (mInviteFrndsRb.isChecked()) {
//                    jArray = Utils.participantsEmailValidation(mParentEmailLL, mActivity);
                        jArray = Utils.participantsPhoneNumberValidation(mParentEmailLL, mActivity);
                        if (Long.parseLong(mBeingHelpBean.getNoOfPeopleHelp()) < Long.parseLong("" + jArray.length())) {
                            isExceededParticipants = true;
                            Utils.showToast(mActivity, "Please do not exceed Add participants count more than " + mBeingHelpBean.getNoOfPeopleHelp() + " ");
                            return;
                        }
                        if (Constants.IS_EMPTY_EMAIL && !isExceededParticipants) {
                            Constants.IS_EMPTY_EMAIL = false;
                            if (TextUtils.isEmpty(msg)) {
                                mMessageEtv.requestFocus();
                                mMessageEtv.setError(getString(R.string.error_message));
                            } else if (TextUtils.isEmpty(des)) {
                                mDescriptionEtv.requestFocus();
                                mDescriptionEtv.setError(getString(R.string.error_description));
                            } else {
                                createKarmicChant(msg);
                            }
                        }
                    }
                }else{
                    handleAddParticipants();
                }
                break;
            case R.id.totalPeopleTV:
                mContactsRb.setChecked(true);
                mInviteFrndsRb.setChecked(false);
                Intent intent = new Intent(mActivity, ContactsListActivity.class);
                intent.putExtra(ContactsListActivity.LIST_KEY, mContactsList);
                startActivityForResult(intent, CONTACT_LIST);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == Constants.REQUEST_CONTACT_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new ContactAsnyc().execute();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI() {
        if (mContactsList != null && mContactsList.size() > 0) {
            mTotalPeopleTV.setText("" + (mContactsList.size()));
            UserInfoBean.getInstance().setContacts(mContactsList, mActivity);
        } else {
            mTotalPeopleTV.setText("0");
        }
        mYouRequestedTV.setText("0");
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

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private ArrayList<ContactsBean> getContactNames() {
        ArrayList<ContactsBean> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                ContactsBean bean = new ContactsBean();
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d("TAG", name);
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    String number = null;
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        /*int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                // do something with the Home number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                // do something with the Mobile number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                // do something with the Work number here...
                                break;
                        }*/
                    }
                    phones.close();
                    if (number != null && number.length() >= 10) {
                        bean.setNumber(number);
                        bean.setName(name);
                        bean.setIsSelected(0);
                        bean.setPhoto(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
                        contacts.add(bean);
                    }
                }
            } while (cursor.moveToNext());
        }
        // Close the curosor

        cursor.close();
        return contacts;
    }

    public void getNumber(ContentResolver cr) {
        mContactsList = new ArrayList<>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            ContactsBean bean = new ContactsBean();
            bean.setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            bean.setNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            mContactsList.add(bean);
        }
        phones.close();// close cursor
    }

    public void onAddField(final View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.add_email_layout, null);
        ImageView addIv = rowView.findViewById(R.id.addIV);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        rowView.setLayoutParams(params);
        addIv.setOnClickListener(this);
        // Add the new row before the add field button.
        mParentEmailLL.addView(rowView, mParentEmailLL.getChildCount());
        if (mParentEmailLL.getChildCount() > 0) {
            for (int i = 0; i < mParentEmailLL.getChildCount(); i++) {
                View mView = mParentEmailLL.getChildAt(i);
                EditText name = (EditText) mView.findViewById(R.id.addNameEtv);
                String nameStr = name.getText().toString();
                EditText phone = (EditText) mView.findViewById(R.id.addEmailEtv);
                setEmailCheckRb(name);
                setEmailCheckRb(phone);
            }
        }
    }

    private void setEmailCheckRb(EditText name) {
        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    mInviteFrndsRb.setChecked(true);
                    mContactsRb.setChecked(false);
                }
            }
        });
    }

    public void onDelete(View v) {
        mParentEmailLL.removeView((View) v.getParent());
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);

        if (object != null && object instanceof KarmicSelfBean.SelfGkcDetails) {
            mSelfGkcBean = (KarmicSelfBean.SelfGkcDetails) object;
//            mActivity.insertSelfKarmic(null,bean);//i didn identified user namakoti id
            mCreatedKarmicChant = true;
            handleAddParticipants();
        } else if (object != null && object instanceof AddParticipantsBean) {
            showSuccessDialog();
        }

    }

    private void handleAddParticipants() {
        mStartTime = mSelfGkcBean.start_datetime;
        if (mSelfGkcBean != null){
            if (mSelfGkcBean.participant_add.equalsIgnoreCase("YES")) {
                if (jArray != null && jArray.length() > 0) {
                    addParticipants(mSelfGkcBean.gkc_setup_id);
                } else
                    showSuccessDialog();
            } else {
                Utils.showToast(mActivity, getString(R.string.error_participant_add));
                showSuccessDialog();
            }
        }
    }

    private void showSuccessDialog() {
        mCreatedKarmicChant = false;
        Intent intent = new Intent(mActivity, SuccessDialogActivity.class);
        intent.putExtra(SuccessDialogActivity.TIME_KEY, mBeingHelpBean.getStartDate()/*mStartTime*/);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        sendIntent();
    }

    private void sendIntent() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checked) {
        switch (checked) {
            case R.id.contactsRb:
                break;
            case R.id.inviteFrndsRb:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_LIST) {
            if (resultCode == RESULT_OK) {
                ArrayList<ContactsBean> list = (ArrayList<ContactsBean>) data.getSerializableExtra(ContactsListActivity.SELECTED_CONTACT_LIST);
                if (list != null && list.size() > 0) {
                    mSelectedContactList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getIsSelected() == 1) {
                            mSelectedContactList.add(list.get(i));
                        }
                    }
                    if (mSelectedContactList != null && mSelectedContactList.size() > 0) {
                        mYouRequestedTV.setText("" + (mSelectedContactList.size()));
                    } else {
                        mYouRequestedTV.setText("0");
                    }
                }
            }
        }
    }

    @Override
    public void onPermissionAccepted(int code) {
        if (code == Constants.REQUEST_CONTACT_PERMISSION) {
            new ContactAsnyc().execute();
        }
    }

    @Override
    public void onPermissionDenied(int code) {
        if (code == Constants.REQUEST_CONTACT_PERMISSION) {
            Utils.showToast(this, "This permission is required to select contacts");
        }
    }

    private class ContactAsnyc extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mContactsList = getContactNames();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            showProgress(false, mActivity);
            updateUI();
        }

        @Override
        protected void onPreExecute() {
            showProgress(true, mActivity);
        }
    }

}
