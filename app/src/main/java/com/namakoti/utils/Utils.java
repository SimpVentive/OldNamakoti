package com.namakoti.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.EditProfileDetailsBean;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.chanting.self.NewChantDetailsActivity;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Seshagiri on 10/16/2016.
 */

public class Utils {

    private static RelativeLayout mParticipantRL;
    private static CircularNetworkImageView mPerson1Iv;
    private static CircularNetworkImageView mPerson2Iv;
    private static CircularNetworkImageView mPerson3Iv;
    private static CircularNetworkImageView mPerson4Iv;
    private static CircularNetworkImageView mPerson5Iv;
    private static TextView mAddCountIv;

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static String getIMEINumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId() + 10;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Save string value from SharedPreference for the given key
     */
    public static void saveStringInSP(Context _activity, String key, String value) {
        SharedPreferences preferences = _activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Save string value from SharedPreference for the given key
     */
    public static void deleteStringInSP(Context _activity, String key) {
        SharedPreferences preferences = _activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Retrieve string value from SharedPreference for the given key
     */
    public static String getStringFromSP(Context _activity, String key) {
        try {
            SharedPreferences preferences = _activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            return preferences.getString(key, null);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Retrieve string value from SharedPreference for the given key
     */
    public static String getStringFromSP(Context context, String key, String defaultValue) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            return preferences.getString(key, defaultValue);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Retrieve boolean value from SharedPreference for the given key
     */
    public static boolean getBooleanFromSP(Context context, String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    /**
     * Retrieve integer value from SharedPreference for the given key
     */
    public static int getIntFromSP(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    /**
     * Save string value from SharedPreference for the given key
     */
    /*
     * public static void saveStringInSP(Activity _activity,String key, String
	 * value){ SharedPreferences preferences =
	 * _activity.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
	 * android.content.Context.MODE_PRIVATE); SharedPreferences.Editor editor =
	 * preferences.edit(); editor.putString(key, value); editor.commit(); }
	 */

    /**
     * Retrieve boolean value from SharedPreference for the given key
     */
    public static void saveBooleanInSP(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Retrieve boolean value from SharedPreference for the given key
     */
    public static void saveIntInSP(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static void writeLogs(String message) {
        if (isExternalStorageWritable()) {

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/EzRider");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logs" + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            try {
                logFile.createNewFile();

                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(message);
                buf.newLine();
                buf.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * This will create dialog with OK, cancel buttons and returns the dialog object
     *
     * @param activity       -- required
     * @param msgTitle       -- required
     * @param msg            -- required
     * @param okListener     -- optional, default -- closes dialog
     * @param cancelListener -- optional, default -- closes dialog
     * @param showCancelBtn  -- required
     * @param showDialog     -- default false
     * @return
     */
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog showAlertDialog(Activity activity, String msgTitle, String msg, DialogInterface
            .OnClickListener okListener, DialogInterface.OnClickListener cancelListener, boolean showCancelBtn, boolean
                                                      showDialog, String okText, String cancelText) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setTitle(msgTitle);

        if (TextUtils.isEmpty(okText)) {
            okText = "OK";
        }

        if (TextUtils.isEmpty(cancelText)) {
            cancelText = "Cancel";
        }

        if (okListener != null) {
            dialogBuilder.setPositiveButton(okText, okListener);
        } else {
            dialogBuilder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (showCancelBtn) {
            if (cancelListener != null) {
                dialogBuilder.setNegativeButton(cancelText, cancelListener);
            } else {
                dialogBuilder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }

        AlertDialog dialog = dialogBuilder.create();
        if (showDialog)
            dialog.show();

        return dialog;
    }

    /**
     * This will create dialog with OK, cancel buttons and returns the dialog object
     *
     * @param activity       -- required
     * @param msgTitle       -- required
     * @param msg            -- required
     * @param okListener     -- optional, default -- closes dialog
     * @param cancelListener -- optional, default -- closes dialog
     * @param showCancelBtn  -- required
     * @param showDialog     -- default false
     * @return
     */
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog showAlertDialog(Activity activity, String msgTitle, String msg, DialogInterface
            .OnClickListener okListener, DialogInterface.OnClickListener cancelListener, boolean showCancelBtn, boolean
                                                      showDialog) {

        return showAlertDialog(activity, msgTitle, msg, okListener, cancelListener, showCancelBtn, showDialog, null, null);

    }

    public static void saveLongInSP(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    /**
     * Retrieve long value from SharedPreference for the given key
     */
    public static long getLongFromSP(Context context, String key) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            return preferences.getLong(key, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Send mail for the given mail address.
     */
    public static void sendEMail(Context context, String emailId, String subject, String body, Uri attachmentUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        if (subject != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (body != null)
            intent.putExtra(Intent.EXTRA_TEXT, body);
        if (emailId != null)
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        if (attachmentUri != null)
            intent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
        context.startActivity(Intent.createChooser(intent, "Send email via..."));
    }

    /**
     * This method used to save final bitmap into android file storage system
     *
     * @param finalBitmap - bitmap
     */
    public static void saveImagePictureToFileStorage(Bitmap finalBitmap) {
        if (finalBitmap != null) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/profilePictures");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fileName = "Image-" + n + ".jpg";
            File file = new File(myDir, fileName);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static boolean checkGPSEnabledOrNot(Context mContext) {
        boolean isGpsEnabled;
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGpsEnabled = false;
        } else {
            isGpsEnabled = true;
        }
        return isGpsEnabled;
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void setSpinnerAdapter(Context context, ArrayList<String> list, Spinner spinner) {
        if (list != null && list.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
        }
    }

    public static ArrayList<String> getMonthList(Context context) {
        // Create a DateFormatSymbols instance
        DateFormatSymbols dfs = new DateFormatSymbols();

        // DateFormatSymbols instance has a method by name
        // getMonths() which returns back an array of
        // months name
        String[] arrayOfMonthsNames = dfs.getMonths();

        ArrayList<String> months = new ArrayList<String>();
        for (int i = 0; i < arrayOfMonthsNames.length + 1; i++) {
            if (i == 0)
                months.add(context.getString(R.string.select));
            else
                months.add(arrayOfMonthsNames[i - 1]);
        }
        return months;
    }

    public static ArrayList<String> getSalutationList(ArrayList<EditProfileDetailsBean.CodeValues> allsalute) {

        ArrayList<String> salutation = new ArrayList<String>();
        for (int i = 0; i < allsalute.size(); i++) {
            salutation.add(allsalute.get(i).name);
        }
        return salutation;
    }

    public static ArrayList<String> getCountryList(ArrayList<EditProfileDetailsBean.AllCountriesList> allsalute) {

        ArrayList<String> salutation = new ArrayList<String>();
        for (int i = 0; i < allsalute.size(); i++) {
            salutation.add(allsalute.get(i).cname);
        }
        return salutation;
    }

    public static ArrayList<String> getDateList(Context context) {
        ArrayList<String> date = new ArrayList<String>();
        date.add(context.getString(R.string.select));
        for (int i = 1; i < 32; i++) {
            date.add(String.format("%02d", i));
        }
        return date;
    }

    public static ArrayList<String> getYearList(Context context) {
        ArrayList<String> yearList = new ArrayList<String>();
        Calendar now = Calendar.getInstance(Locale.getDefault());
        int year = now.get(Calendar.YEAR);

        yearList.add(context.getString(R.string.select));

        for (int i = 1908; i <= year; i++) {
            yearList.add(i + "");
        }
        return yearList;
    }

    public static String convertDateToStringObject(Date originalDate, String formattedString) {
        DateFormat dateFormat = new SimpleDateFormat(formattedString);
        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(originalDate);
        return strDate;
    }

    public static int monthStringToInt(String month) {
        int January = Integer.valueOf(Calendar.JANUARY);
        int feb = Integer.valueOf(Calendar.FEBRUARY);
        int march = Integer.valueOf(Calendar.MARCH);
        int april = Integer.valueOf(Calendar.APRIL);
        int may = Integer.valueOf(Calendar.MAY);
        int june = Integer.valueOf(Calendar.JUNE);
        int july = Integer.valueOf(Calendar.JULY);
        int august = Integer.valueOf(Calendar.AUGUST);
        int sep = Integer.valueOf(Calendar.SEPTEMBER);
        int octt = Integer.valueOf(Calendar.OCTOBER);
        int nov = Integer.valueOf(Calendar.NOVEMBER);
        int dec = Integer.valueOf(Calendar.DECEMBER);

        int monthInt = 0;
        if (month.equals("January")) {
            monthInt = January;
        } else if (month.equals("February")) {
            monthInt = feb;
        } else if (month.equals("March")) {
            monthInt = march;
        } else if (month.equals("April")) {
            monthInt = april;
        } else if (month.equals("May")) {
            monthInt = may;
        } else if (month.equals("June")) {
            monthInt = june;
        } else if (month.equals("July")) {
            monthInt = july;
        } else if (month.equals("August")) {
            monthInt = august;
        } else if (month.equals("September")) {
            monthInt = sep;
        } else if (month.equals("October")) {
            monthInt = octt;
        } else if (month.equals("November")) {
            monthInt = nov;
        } else if (month.equals("December")) {
            monthInt = dec;
        }
        return monthInt + 1;
    }

    public static void setUserImageVisibility(BaseActivity mContext, ArrayList<String> list, RelativeLayout participantRL, CircularNetworkImageView person1Iv, CircularNetworkImageView person2Iv, CircularNetworkImageView person3Iv, CircularNetworkImageView person4Iv, CircularNetworkImageView person5Iv, TextView addCountIv) {
        mParticipantRL = participantRL;
        mPerson1Iv = person1Iv;
        mPerson2Iv = person2Iv;
        mPerson3Iv = person3Iv;
        mPerson4Iv = person4Iv;
        mPerson5Iv = person5Iv;
        mAddCountIv = addCountIv;
        ImageLoader mIimageLoader = VolleyUtil.getInstance().imageLoader(mContext, com.intuit.sdp.R.dimen._40sdp);


        if (list != null && list.size() > 0) {
            mParticipantRL.setVisibility(View.VISIBLE);
            if (list.size() == 1) {
                mPerson1Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(0), mIimageLoader);
                setImageVisibility(true, false, false, false, false, false);
            } else if (list.size() == 2) {
                mPerson1Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(0), mIimageLoader);
                mPerson2Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(1), mIimageLoader);
                setImageVisibility(true, true, false, false, false, false);
            } else if (list.size() == 3) {
                mPerson1Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(0), mIimageLoader);
                mPerson2Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(1), mIimageLoader);
                mPerson3Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(2), mIimageLoader);
                setImageVisibility(true, true, true, false, false, false);
            } else if (list.size() == 4) {
                mPerson1Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(0), mIimageLoader);
                mPerson2Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(1), mIimageLoader);
                mPerson3Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(2), mIimageLoader);
                mPerson4Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(3), mIimageLoader);

                setImageVisibility(true, true, true, true, false, false);
            } else if (list.size() == 5 || list.size() > 5) {
                mPerson1Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(0), mIimageLoader);
                mPerson2Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(1), mIimageLoader);
                mPerson3Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(2), mIimageLoader);
                mPerson4Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(3), mIimageLoader);
                mPerson5Iv.setImageUrl(Constants.GOD_IMAGE_BASE_URL + list.get(4), mIimageLoader);

                if (list.size() > 5) {
                    mAddCountIv.setText("" + (list.size() - 5));
                    setImageVisibility(true, true, true, true, true, true);
                } else {
                    setImageVisibility(true, true, true, true, true, false);
                }
            }

        } else {
            mParticipantRL.setVisibility(View.GONE);
        }

    }

    private static void setImageVisibility(boolean first, boolean second, boolean third, boolean fourth, boolean fifth, boolean count) {
        if (first)
            mPerson1Iv.setVisibility(View.VISIBLE);
        else
            mPerson1Iv.setVisibility(View.INVISIBLE);
        if (second)
            mPerson2Iv.setVisibility(View.VISIBLE);
        else
            mPerson2Iv.setVisibility(View.INVISIBLE);
        if (third)
            mPerson3Iv.setVisibility(View.VISIBLE);
        else
            mPerson3Iv.setVisibility(View.INVISIBLE);
        if (fourth)
            mPerson4Iv.setVisibility(View.VISIBLE);
        else
            mPerson4Iv.setVisibility(View.INVISIBLE);
        if (fifth)
            mPerson5Iv.setVisibility(View.VISIBLE);
        else
            mPerson5Iv.setVisibility(View.INVISIBLE);

        if (count) {
            mAddCountIv.setVisibility(View.VISIBLE);
        } else
            mAddCountIv.setVisibility(View.INVISIBLE);
    }

    public static JSONArray participantsEmailValidation(LinearLayout mParentEmailLL, Context activity) {
        JSONArray jArray = new JSONArray();
        for (int i = 0; i < mParentEmailLL.getChildCount(); i++) {
            View mView = mParentEmailLL.getChildAt(i);
            EditText name = (EditText) mView.findViewById(R.id.addNameEtv);
            String nameStr = name.getText().toString();
            EditText email = (EditText) mView.findViewById(R.id.addEmailEtv);
            String emailStr = email.getText().toString();
            if (TextUtils.isEmpty(nameStr)) {
                name.setError(activity.getString(R.string.error_enter_person_name));
                name.requestFocus();
            } else if (TextUtils.isEmpty(emailStr)) {
                email.setError(activity.getString(R.string.error_email));
                email.requestFocus();
            } else if (!Utils.emailValidator(emailStr)) {
                email.setError(activity.getString(R.string.error_valid_email));
                email.requestFocus();
            } else {
                if (i == mParentEmailLL.getChildCount() - 1)
                    Constants.IS_EMPTY_EMAIL = true;

                JSONObject jObject = new JSONObject();
                try {
                    jObject.put("name", nameStr);
                    jObject.put("email", emailStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jArray.put(jObject);
            }

        }
        return jArray;
    }


    public static JSONArray participantsPhoneNumberValidation(LinearLayout mParentEmailLL, Context activity) {
        JSONArray jArray = new JSONArray();
        for (int i = 0; i < mParentEmailLL.getChildCount(); i++) {
            View mView = mParentEmailLL.getChildAt(i);
            EditText name = (EditText) mView.findViewById(R.id.addNameEtv);
            String nameStr = name.getText().toString();
            EditText phone = (EditText) mView.findViewById(R.id.addEmailEtv);
            phone.setInputType(InputType.TYPE_CLASS_NUMBER);
            String phoneStr = phone.getText().toString();
            phone.setHint(activity.getString(R.string.phone));
            if (TextUtils.isEmpty(nameStr)) {
                name.setError(activity.getString(R.string.error_enter_person_name));
                name.requestFocus();
            } else if (TextUtils.isEmpty(phoneStr)) {
                phone.setError(activity.getString(R.string.error_phone));
                phone.requestFocus();
            }
            else if (phoneStr != null && phoneStr.length() > 0 && !((BaseActivity) activity).mPhonePattern.matcher(phoneStr).matches()){
                phone.setError(activity.getString(R.string.error_valid_phone));
                phone.requestFocus();
            }
            else if (phone.length() < 10) {
                phone.setError(activity.getString(R.string.error_phone_valid_phn));
                phone.requestFocus();
            } else {
                if (i == mParentEmailLL.getChildCount() - 1)
                    Constants.IS_EMPTY_EMAIL = true;

                JSONObject jObject = new JSONObject();
                try {
                    jObject.put("name", nameStr);
                    jObject.put("mobile", phoneStr.trim().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jArray.put(jObject);
            }

        }
        return jArray;
    }

    public static ArrayList<String> getChantTypeList() {
        ArrayList<String> type = new ArrayList<String>();
        for (int i = 0; i < Constants.sChantType.length; i++) {
            type.add(Constants.sChantType[i]);
        }
        return type;
    }

    public static Intent setBundleForChantDetails(BaseActivity mActivity, Cursor mCursor, String typeOfClass, boolean movedtoPos) {
        /*int totalCount = 0;
        Cursor data = DatabaseHelper.getTotalCount(typeOfClass, UserInfoBean.getInstance().getUserId(mActivity));
        // calc list total count but not used now
        if (data != null && data.getCount() > 0){
            // move cursor to before 1st, just in case
            data.moveToPosition(-1);
            if (data.moveToFirst()){
                do{
                    int total = data.getInt(data.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_TOTAL_COUNT));
                    int running = data.getInt(data.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_RUNNING_COUNT));
                    if (total == running)
                        totalCount = totalCount + total;
                    else if (total < running)
                        totalCount = totalCount + running;

                }while(data.moveToNext());
            }
            data.close();
        }*/

        Intent intent = null;
        if (mCursor != null) {
            if (!movedtoPos)
                mCursor.moveToFirst();
            intent = new Intent(mActivity, /*ChantingDetailsActivity*/NewChantDetailsActivity.class);
            Bundle bundle = new Bundle();
            long no_count = 0;
            if(typeOfClass.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || typeOfClass.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)){
                no_count = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NO_TOTAL_COUNT));
            }
            SaveChantsBean bean = new SaveChantsBean(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_PRINT_USERNAME)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_MUSIC)),
                    no_count,
                    mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_TOTAL_COUNT)),
                    mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_PRINTED_COUNT)),
                    mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_RUNNING_COUNT)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_PRINTING_SERVICE)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME)),
                    mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_THEME_ID)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_SUB_THEME_ID)),
                    mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_LANGUAGE_ID)));
            bundle.putSerializable(NewChantDetailsActivity.BUNDLE_NAMAS_BEAN_KEY, bean);
//            bundle.putInt(NewChantDetailsActivity.KEY_TOTAL_COUNT, totalCount);
//            if (typeOfClass.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS) || typeOfClass.equalsIgnoreCase(Constants.KEY_KARMIC_BEING))
                bundle.putString(NewChantDetailsActivity.GKC_SETUPID, mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_GKC_SETUP_ID)));
            bundle.putString(NewChantDetailsActivity.BUNDLE_IS_FROM_CAUSE, typeOfClass);
            intent.putExtras(bundle);
        }


        return intent;
    }

    public static void setBundleForChantDetails() {
    }

    public static Intent getParticipantsListIntent(BaseActivity activity, String start_datetime, String participant_add,
                                                   String end_datetime, String gkc_setup_id, String createdBy) {
        Date now = new Date();
        Intent intent = null;
        Date selectedDate = CalendarUtils.ConvertStingtoDate(start_datetime, Constants.DATE_TIME_FORMAT);
        Date endDate = CalendarUtils.ConvertStingToDate(end_datetime, Constants.DATE_TIME_FORMAT);
        boolean addParticipant = false;
        // if startdate< currentdate then show add participants
        if (now.before(selectedDate) && participant_add.equalsIgnoreCase("YES")) {
            addParticipant = true;
        }
        boolean isShowCancel = true;
        //enddate < current date then dont show cancel and send reminder btns
        if (now.after(endDate) || now.equals(endDate))
            isShowCancel = false;
        int diff = CalendarUtils.calculateDifference(end_datetime);
        intent = new Intent(activity, ParticipantsListActivity.class);
        intent.putExtra(ParticipantsListActivity.GKC_SETUP_ID_KEY, gkc_setup_id);
        intent.putExtra(ParticipantsListActivity.COUNT_DAYS_LEFT_KEY, "" + diff);
        intent.putExtra(ParticipantsListActivity.ADD_PARTICIPANT_KEY, addParticipant);
        intent.putExtra(ParticipantsListActivity.SHOW_CANCEL_BTN_KEY, isShowCancel);
        intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, createdBy);

        return intent;
    }

    public static int dpToPx(Context activity, int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void sendUpdatedParticipantsBroadCast(Activity activity, boolean isAdded, ArrayList<ParticipantsBean.ParticipantsItemBean> list, String setupId) {
        if (list != null && list.size() > 0) {
            ArrayList<String> participants = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                participants.add(list.get(i).userphoto);
            }

            Intent intent = new Intent();
            intent.setAction(Constants.ADD_PARTICIPANT_INTENT_ACTION);
            intent.putExtra(Constants.PARTICNTS_LIST_KEY, participants);
            intent.putExtra(Constants.PARTICNTS_GKC_SETUP_KEY, setupId);
            activity.sendBroadcast(intent);
            if (isAdded){
                Utils.showToast(activity, "Successfully participants added.");
                activity.finish();
            }
        }
    }
    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static int convertLongToInt(long no_chants) {
        Long obj = new Long(no_chants);
        int i = obj.intValue();
        return i;
    }
}
