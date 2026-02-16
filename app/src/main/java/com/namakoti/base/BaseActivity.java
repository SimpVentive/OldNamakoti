package com.namakoti.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.beans.ChantsBean;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.beans.LanguagesBean;
import com.namakoti.beans.LoginBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.Constants;
import com.namakoti.utils.PermissionListener;
import com.namakoti.utils.PostMortemReportExceptionHandler;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private FragmentManager mFragmentManager;
    protected PostMortemReportExceptionHandler mDamageReport = new PostMortemReportExceptionHandler(this);
    public SQLiteDatabase mDatabase;
    private BaseActivity mActivity;
    private String TAG = BaseActivity.this.getClass().getName();
    private PermissionListener mPermissionListener;
    public Pattern mPhonePattern;
    private static boolean isAppInForeground;
    private final BroadcastReceiver mPushMsgBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null && intent.getAction().equals(Constants.PUSH_NOTIFICATION_ACTION)) {
                    String message = intent.getStringExtra(Constants.PUSH_MESSAGE_KEY);
                    String title = intent.getStringExtra(Constants.PUSH_TITLE_KEY);
                    Utils.showAlertDialog(mActivity, title, "" + message, null, null, false, true);
                }
            }
        }
    };
    public String mUserIdString;
    public long mUserIdLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAppInForeground = true;

        if (Constants.POSTMARTEMREPORTLOG) {
            mDamageReport.initialize();
        }
        mDatabase = MyApplication.getInstance().getDataDatabase();
        mActivity = this;
        mPhonePattern = Pattern.compile("^[6-9]\\d{9}$");
        mUserIdLong = UserInfoBean.getInstance().getUserId(mActivity);
        mUserIdString = String.valueOf(mUserIdLong);
//        ((EzRiderApplication) getApplicationContext()).writeLogs();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        isAppInForeground = true;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(mPushMsgBroadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION_ACTION), Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(mPushMsgBroadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION_ACTION));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppInForeground = false;
        try {
            unregisterReceiver(mPushMsgBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppRunning() {
        return isAppInForeground;
    }

    /**
     * Shows the progress dialog
     */
    protected void showProgress(final boolean show, Activity activity) {
        try {
            if (mProgressDialog == null && show) {
                // First time initialize and show
                mProgressDialog = ProgressDialog.show(this, "", "Please wait...", true, false);
            } else if (mProgressDialog != null && show && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            } else if (!activity.isFinishing() && mProgressDialog != null && !show && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void openActivity(Activity fromActivity, Class toActivity) {
        Intent intent = new Intent(fromActivity, toActivity.getClass());
        startActivity(intent);
    }

    protected void setToolbarTitle(String title) {
        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

    }

    public void setToolbarWithBack(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /*public Fragment getCurrentFrag(BaseActivity baseActivity) {
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);

        return frag;
    }*/

    /**
     * Replace Fragment
     */
    public void replaceFragment(int id, @NonNull Fragment fragment, boolean addToBackStack) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        mFragmentTransaction.replace(id, fragment, fragment.getClass().getName());
        mFragmentTransaction.commitAllowingStateLoss();
    }

    public void removeFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count != 0) {
            fm.popBackStack();
            fm.executePendingTransactions();
            Fragment fragmentById = fm.findFragmentById(R.id.fragment_container);
            if (fragmentById != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragmentById).commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (Constants.POSTMARTEMREPORTLOG) {
            mDamageReport.restoreOriginalHandler();
            mDamageReport = null;
        }
        dismissProgressDialog();
        super.onDestroy();
    }

    protected void trustCertificate() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * insert local count values for all tales self cause karmic bing and karmic others
     *
     * @param mDatabase
     * @param cv
     * @param namakotiId
     * @param localCountTableName
     */
    public void updateLocalCountTable(SQLiteDatabase mDatabase, ContentValues cv, int namakotiId, String localCountTableName, long userID) {
        long result = -1;
        result = mDatabase.update(localCountTableName, cv,
                DatabaseHelper.TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + "=" + namakotiId
                        + " AND " + DatabaseHelper.TABLE_LOCAL_COUNT.COL_USER_ID + "=" + userID, null);

        if (result != -1 && result == 0) {
            mDatabase.insert(localCountTableName, "firstName", cv);
        }
    }

    /**
     * insert server count values for all tales self cause karmic bing and karmic others
     *
     * @param mDatabase
     * @param cv
     * @param namakotiId
     * @param serverCountTableName
     */
    public void updateServerCountTable(SQLiteDatabase mDatabase, ContentValues cv, int namakotiId, String serverCountTableName, long userid) {
        long result = -1;
        result = mDatabase.update(serverCountTableName, cv,
                DatabaseHelper.TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + "=" + namakotiId
                        + " AND " + DatabaseHelper.TABLE_SERVER_COUNT.COL_USER_ID + "=" + userid, null);

        if (result != -1 && result == 0) {
            mDatabase.insert(serverCountTableName, "firstName", cv);
        }
    }

    /**
     * insert self and cause namas details into db
     *
     * @param mDatabase
     * @param cv
     * @param namasBean
     * @param namasTableName
     */
    protected void updateNamasTable(SQLiteDatabase mDatabase, ContentValues cv, LoginBean.NamasDetails namasBean, String namasTableName, long mUserIdLong) {
        long result = -1;
        result = mDatabase.update(namasTableName, cv,
                DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "=" + namasBean.getUser_namakoti_id()
                        + " AND " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + "=" + mUserIdLong, null);

        if (result != -1 && result == 0) {
            mDatabase.insert(namasTableName, "firstName", cv);
        }
    }

    /**
     * insert/update karmic namas details into db
     *
     * @param mDatabase
     * @param cv
     * @param participant_id
     * @param namasTableName
     */
    public void updateKarmicSelfNamasTable(SQLiteDatabase mDatabase, ContentValues cv, String participant_id, String namasTableName, long userID) {
        long result = -1;
        int id = Integer.parseInt(participant_id);
        result = mDatabase.update(namasTableName, cv,
                DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "=" + id
                        + " AND " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + "=" + userID, null);

        if (result != -1 && result == 0) {
            mDatabase.insert(namasTableName, "firstName", cv);
        }
    }


    public void insertSelfKarmic(KarmicSelfBean bean, KarmicSelfBean.SelfGkcDetails createdBean) {
        if (createdBean != null) {
            insertBeingHelpRow(createdBean);
        } else if (bean != null && bean.selfgkc != null & bean.selfgkc.size() > 0) {
            for (int i = 0; i < bean.selfgkc.size(); i++) {
                KarmicSelfBean.SelfGkcDetails namasBean = bean.selfgkc.get(i);
                insertBeingHelpRow(namasBean);
            }
        }
    }

    public void insertHelpingOthersKarmic(KarmicOthersBean bean, KarmicOthersBean.RequestGkcDetails createdBean) {
        /*if (createdBean != null){
            insertRow(createdBean);
        }
        else*/
        if (bean != null && bean.requestgkc != null & bean.requestgkc.size() > 0) {
            for (int i = 0; i < bean.requestgkc.size(); i++) {
                KarmicOthersBean.RequestGkcDetails namasBean = bean.requestgkc.get(i);
                if (namasBean.participant_id != null && !TextUtils.isEmpty(namasBean.participant_id))
                    insertHelpingOthersRow(namasBean);
            }
        }
    }

    /**
     * insert all 3 tables info for karmic self
     *
     * @param namasBean
     */
    private void insertBeingHelpRow(KarmicSelfBean.SelfGkcDetails namasBean) {
        int id = Integer.parseInt(namasBean.participant_id);
        Cursor cursor = DatabaseHelper.getInstance().getLocalTotalCount(mDatabase, namasBean.user_id, "" + id);
        int running = 0;
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    running = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL));
                }
                cursor.close();
            }
        }
        int count;
        if (running == 0)
            count = 0;
        else
            count = running;

        ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(namasBean.user_id,
                "" + id, count, Constants.KEY_KARMIC_BEING, 0);
        ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(namasBean.user_id,
                "" + id, Integer.parseInt(namasBean.chants_count_will_do),
                Integer.parseInt(namasBean.orginal_chants_count), Constants.KEY_KARMIC_BEING, 0);

        long userID = UserInfoBean.getInstance().getUserId(mActivity);
        ContentValues cv = DatabaseHelper.getInstance().getSelfNamasQuery(namasBean, namasBean.user_id);
        updateKarmicSelfNamasTable(mDatabase, cv, namasBean.participant_id, DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME, userID);
        updateLocalCountTable(mDatabase, localCv, id, DatabaseHelper.KARMIC_SELF_LOCAL_COUNT_TABLE_NAME, userID);
        updateServerCountTable(mDatabase, serverCv, id, DatabaseHelper.KARMIC_SELF_SERVER_COUNT_TABLE_NAME, userID);
    }

    /**
     * insert all 3 tables info for karmic others
     *
     * @param namasBean
     */
    private void insertHelpingOthersRow(KarmicOthersBean.RequestGkcDetails namasBean) {
        int id = Integer.parseInt(namasBean.participant_id);
        ContentValues cv = DatabaseHelper.getInstance().getHelpingOthersCV(namasBean, namasBean.mobile);
        ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(namasBean.mobile,
                "" + id, 0, Constants.KEY_KARMIC_OTHERS, 0);
        ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(namasBean.mobile,
                "" + id, Integer.parseInt(namasBean.chants_count_will_do),
                Integer.parseInt(namasBean.orginal_chants_count), Constants.KEY_KARMIC_OTHERS, 0);

        long userID = UserInfoBean.getInstance().getUserName(mActivity);
        updateKarmicSelfNamasTable(mDatabase, cv, namasBean.participant_id, DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME, userID);
        updateLocalCountTable(mDatabase, localCv, id, DatabaseHelper.KARMIC_OTHERS_LOCAL_COUNT_TABLE_NAME, userID);
        updateServerCountTable(mDatabase, serverCv, id, DatabaseHelper.KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME, userID);
    }

    protected void selectImage(final int requestCamera, final int selectFile, final PermissionListener listener) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String userChoosenTask;

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent(requestCamera, listener);
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent(selectFile, listener);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void cameraIntent(int requestCamera, PermissionListener listener) {

        if (checkRequiredPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, listener)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            startActivityForResult(intent, requestCamera);
        }


    }

    protected void galleryIntent(int selectFile, PermissionListener listener) {

        if (checkStoragePermission(listener)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), selectFile);
        }

    }

    protected Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

    protected Bitmap onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    protected String getStringProfileImage(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = getResizedBitmap(bitmap, 500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }

        return "";
    }

    /**
     * reduces the size of the image
     *
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Drawable getGodImage(String name, Context mActivity) {
        Drawable godDrawable = null;
        if (name.equalsIgnoreCase(Constants.LORD_HANUMAN)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_RAMA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_SHIVA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_SAIBABA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_sai_baba_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_DURGA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.goddess_durga_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_GANESH)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_ganesh_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_LAKSHMI)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_VENKATESHWARA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        } else if (name.equalsIgnoreCase(Constants.LORD_AYYAPPA)) {
            godDrawable = ResourcesCompat.getDrawable(mActivity.getResources(), R.drawable.lord_shiva_small, null);
        }
        return godDrawable;
    }

    public ArrayList<String> getLanguageList(List<LanguagesBean> langList) {
        ArrayList<String> languages = new ArrayList<>();
        languages.add(getString(R.string.select));
        for (int i = 0; i < langList.size(); i++) {
            languages.add(langList.get(i).getLanguage_name());
        }
        return languages;
    }

    public ArrayList<String> getChantList(List<ChantsBean> chantList) {
        ArrayList<String> chant = new ArrayList<>();
        for (int i = 0; i < chantList.size(); i++) {
            chant.add(fromHtml(chantList.get(i).getSub_theme_name()).toString());
        }
        return chant;
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(Context context, EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void handleErrorMessage(Object object) {
        if (object != null) {
            if (object instanceof ErrorBean) {
                ErrorBean error = (ErrorBean) object;
                if (error.message != null)
                    Utils.showAlertDialog(this, "Error", " " + error.message, null, null, false, true);
            } else {
                Utils.showAlertDialog(this, "Error", " " + getString(R.string.unable_response), null, null, false, true);
            }
        } else {
            Utils.showAlertDialog(this, "Error", " " + getString(R.string.unable_response), null, null, false, true);
        }
    }

    public ArrayList<GodNamesBean> getGodListFromDb(Cursor mCursorGod) {
        ArrayList<GodNamesBean> list = new ArrayList<>();
        if (mCursorGod != null) {
            if (mCursorGod.getCount() != 0) {
                if (mCursorGod.moveToFirst()) {
                    do {
                        String photo = mCursorGod.getString(mCursorGod.getColumnIndexOrThrow(DatabaseHelper.TABLE_GOD_IMAGES.COL_PHOTO));
                        String service = mCursorGod.getString(mCursorGod.getColumnIndexOrThrow(DatabaseHelper.TABLE_GOD_IMAGES.COL_PRINTING_SERVICE));
                        String id = mCursorGod.getString(mCursorGod.getColumnIndexOrThrow(DatabaseHelper.TABLE_GOD_IMAGES.COL_THEME_ID));
                        String godName = mCursorGod.getString(mCursorGod.getColumnIndexOrThrow(DatabaseHelper.TABLE_GOD_IMAGES.COL_THEME_NAME));
                        GodNamesBean bean = new GodNamesBean(id, godName, service, photo);
                        list.add(bean);
                    } while (mCursorGod.moveToNext());
                }
                mCursorGod.close();
            }
        }
        return list;
    }

    public void setImageLoader(List<GodNamesBean> list, NetworkImageView image, String theme_name) {
        String url = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                GodNamesBean bean = list.get(i);
                if (theme_name.equalsIgnoreCase(bean.getTheme_name())) {
                    url = bean.getPhoto();
                    break;
                }
            }
        }
        ImageLoader imageLoader = VolleyUtil.getInstance().imageLoader(this, com.intuit.sdp.R.dimen._55sdp);
        image.setImageUrl(Constants.GOD_IMAGE_BASE_URL + url, imageLoader);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void storeLoginResponse(@NonNull LoginBean bean, Context context) {
        UserInfoBean.getInstance().setFullname(bean.fullname, this);
        UserInfoBean.getInstance().setFullname(bean.fullname, this);
        UserInfoBean.getInstance().setUserId(bean.user_id, this);
        UserInfoBean.getInstance().setUserName(bean.user_name, this);
        UserInfoBean.getInstance().setPassword(bean.user_password, this);
        UserInfoBean.getInstance().setUpgrade("" + bean.upgrade, this);
        UserInfoBean.getInstance().setProfileImage("" + bean.userphoto, this);
        UserInfoBean.getInstance().setUpgradeuseramt("" + bean.upgradeuseramt, this);

        insertLoginDetails(bean);
        getGodNames(context);
    }

    private void getGodNames(Context context) {
        showProgress(true, mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", String.valueOf(UserInfoBean.getInstance().getUserId(this)));

        VolleyUtil.getInstance().
                volleyStringRequest(mActivity, Constants.GET_GOD_NAMES, params, ServiceMethod.GET_GOD_NAMES, Request.Method.POST, (VolleyResponseListener) context);
    }

    /**
     * This methodis used to send the device token to server
     *
     * @param token
     */
    public void sendRegistrationToServer(final String token, Context context) {
        // sending gcm token to server
        Log.e("LoginActivity", "sendRegistrationToServer: " + token);
        if (TextUtils.isEmpty(token)) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", String.valueOf(UserInfoBean.getInstance().getUserId(this)));
        params.put("token", token);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.PUSH_TOKEN_URL, params, ServiceMethod.PUSH_TOKEN, Request.Method.POST, (VolleyResponseListener) context);
    }

    public void insertGodDetails(List<GodNamesBean> godList) {
        for (int i = 0; i < godList.size(); i++) {
            GodNamesBean godBean = godList.get(i);
            ContentValues cv = DatabaseHelper.getInstance().getInsertGodQuery(godBean);
            long result = -1;
            result = mDatabase.update(DatabaseHelper.NORMAL_GOD_IMAGES_TABLE_NAME, cv,
                    DatabaseHelper.TABLE_GOD_IMAGES.COL_THEME_ID + "=" + Integer.parseInt(godBean.getTheme_id()), null);

            if (result != -1 && result == 0) {
                mDatabase.insert(DatabaseHelper.NORMAL_GOD_IMAGES_TABLE_NAME, "firstName", cv);
            }
        }
    }

    private void insertLoginDetails(@NonNull LoginBean bean) {
        long userID = UserInfoBean.getInstance().getUserId(mActivity);
        if (bean.namas != null && bean.namas.size() > 0) {
            for (int i = 0; i < bean.namas.size(); i++) {
                LoginBean.NamasDetails namasBean = bean.namas.get(i);
                ContentValues cv = DatabaseHelper.getInstance().getNamasQuery(namasBean, bean.user_id);
                ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(bean.user_id,
                        "" + namasBean.getUser_namakoti_id(), 0, Constants.KEY_SELF_CHANT, namasBean.getNo_chants());
                ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(bean.user_id,
                        "" + namasBean.getUser_namakoti_id(), namasBean.getNama_total_count(), namasBean.getNama_running_count(),
                        Constants.KEY_SELF_CHANT, namasBean.getNo_chants());

                updateNamasTable(mDatabase, cv, namasBean, DatabaseHelper.NORMAL_NAMAS_TABLE_NAME, userID);
                updateLocalCountTable(mDatabase, localCv, namasBean.getUser_namakoti_id(), DatabaseHelper.NORMAL_LOCAL_COUNT_TABLE_NAME, userID);
                updateServerCountTable(mDatabase, serverCv, namasBean.getUser_namakoti_id(), DatabaseHelper.NORMAL_SERVER_COUNT_TABLE_NAME, userID);
            }
        }
        //chant for cause
        if (bean.causenamas != null && bean.causenamas.size() > 0) {
            for (int i = 0; i < bean.causenamas.size(); i++) {
                LoginBean.NamasDetails namasBean = bean.causenamas.get(i);
                ContentValues cv = DatabaseHelper.getInstance().getNamasQuery(namasBean, bean.user_id);
                ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(bean.user_id,
                        "" + namasBean.getUser_namakoti_id(), 0, Constants.KEY_CHANT_FOR_CAUSE, namasBean.getNo_chants());
                ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(bean.user_id,
                        "" + namasBean.getUser_namakoti_id(), namasBean.getNama_total_count(), namasBean.getNama_running_count(),
                        Constants.KEY_CHANT_FOR_CAUSE, namasBean.getNo_chants());

                updateNamasTable(mDatabase, cv, namasBean, DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME, userID);
                updateLocalCountTable(mDatabase, localCv, namasBean.getUser_namakoti_id(), DatabaseHelper.CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME, userID);
                updateServerCountTable(mDatabase, serverCv, namasBean.getUser_namakoti_id(), DatabaseHelper.CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME, userID);
            }
        }
    }

    /**
     * Method to check permission
     */
    public boolean checkCameraPermission(PermissionListener listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
            mPermissionListener = listener;
            return false;
        }

        return true;
    }

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
    }


    /**
     * Method to check permission
     */
    public boolean checkStoragePermission(PermissionListener listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestStoragePermission();
            mPermissionListener = listener;
            return false;
        }

        return true;
    }

    /**
     * Method to check permission
     */
    public boolean contactPermission(PermissionListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestContactPermission();
            mPermissionListener = listener;
            return false;
        }
        return true;
    }

    /**
     * Method to request permission for camera
     */
    private void requestContactPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_CONTACT_PERMISSION);
    }

    /**
     * Method to request permission for camera
     */
    private void requestStoragePermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CAMERA_PERMISSION);
    }


    public boolean checkRequiredPermissions(String[] permissions, PermissionListener listener) {
        mPermissionListener = listener;
        Set<String> permSet = new HashSet<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permSet.add(permission);
            }
        }

        if (permSet.size() > 0) {
            String[] reqPermission = new String[permSet.size()];
            permSet.toArray(reqPermission);
            ActivityCompat.requestPermissions(this, reqPermission, Constants.REQUEST_CAMERA_PERMISSION);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionGranted = true;

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = false;
            }
        }

        if (!isPermissionGranted) {
            if (!checkPermissionRationale(permissions)) {
                showPermissionRequiredDialog();
                // User selected the Never Ask Again Option
            }
            if (mPermissionListener != null) {
                mPermissionListener.onPermissionDenied(requestCode);
            }
        } else {
            // User accepted permissions
            if (mPermissionListener != null) {
                mPermissionListener.onPermissionAccepted(requestCode);
            }
        }
    }

    /**
     * check user select any never show again permission selection
     *
     * @param permissions
     * @return true or false
     */
    private boolean checkPermissionRationale(String permissions[]) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showPermissionRequiredDialog() {

        Utils.showAlertDialog(this, getString(R.string.permissions_required), getString(R.string.permissions_disabled), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getPackageName()));

                startActivityForResult(i, 100);
            }
        }, null, true, true, "Accept", "Cancel");
    }

    public void setCustomActionbarTitle(String title) {
        ActionBar bar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_actionbar_tilte, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(title);
        getSupportActionBar().setCustomView(viewActionBar, params);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public Fragment getCurrentFrag(@NonNull BaseActivity baseActivity) {
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_container);
    }

}