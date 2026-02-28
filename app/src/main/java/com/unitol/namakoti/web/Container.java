package com.unitol.namakoti.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.CustomViewPager;
import com.unitol.namakoti.EnternamaFragment;
import com.unitol.namakoti.HelpFragment;
import com.unitol.namakoti.MainActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.MyPageAdapter;
import com.unitol.namakoti.OnFragmentTransactionListenerPhone;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SetNamaFragment;
import com.unitol.namakoti.ui.EditProfileFragmentUrlConnection;
import com.unitol.namakoti.ui.HomeFragment;
import com.unitol.namakoti.ui.PaymentFragment;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Container extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, OnFragmentTransactionListenerPhone {

    private final static String TAG = "Container";
    private CustomViewPager mViewPager;
    public SharedPreferences mPref;
    private boolean isLogin = false;
    private static Container instance;
    private LinearLayout ll_chanting, ll_settings, ll_help, ll_share, ll_logout, ll_delete_account;
    private HashMap<String, Stack<Fragment>> mStacks;
    private String mCurrentTab;

    private ImageView back;
    private TextView mainTitle;
    private Button btn_menu;
    private ScrollView rlmenuview;
    private ImageView logoOnTopView;
    private ImageView rightImageView;
    private AudioManager audioManager;
    private List<Fragment> fragments;
    private Animation anim, outanim;
    private MyPageAdapter pageAdapter;
    public final static int ENTER_NAMA_TAB = 0;
    public final static int SETTINGS_TAB = 1;
    public final static int HELP_TAB = 2;
    public final static int UPGRADE_TAB = 3;
    public final static int HOME_TAB_ID = 4;
    private int lastChangedTab;
    private LinearLayout viewpagerLayout;
    //	private IconPageIndicator mIndicator

    public static Container getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(this);
        int height = metrics.heightPixels;
        int h = (int) (height * 0.01f);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        isLogin = mPref.getBoolean(getString(R.string.pref_login_flag_key), false);

        setContentView(R.layout.dashboard);


        initUI();

        hideOutSideMenu(getWindow().getDecorView().findViewById(android.R.id.content));

        viewpagerLayout = (LinearLayout) findViewById(R.id.viewpager_layout);

        pageAdapter = new MyPageAdapter(this.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);

        mStacks = new HashMap<String, Stack<Fragment>>();

        mStacks.put(ConstantsManager.TAB_HOME, new Stack<Fragment>());
        mStacks.put(ConstantsManager.TAB_2, new Stack<Fragment>());
        mStacks.put(ConstantsManager.TAB_3, new Stack<Fragment>());
        mStacks.put(ConstantsManager.TAB_4, new Stack<Fragment>());
        mStacks.put(ConstantsManager.TAB_5, new Stack<Fragment>());

        instance = this;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        DisplayMetrics dm = NamaKotiUtils.getDisplayMetrics(this);

        int width = dm.widthPixels;

        mainTitle = (TextView) findViewById(R.id.mainTitle);

        final RelativeLayout.LayoutParams paramsOfContent = new RelativeLayout.LayoutParams((int) (width * 0.58f), ViewPager.LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams paramsOfContent2 = new RelativeLayout.LayoutParams((int) (width * 0.22f), ViewPager.LayoutParams.WRAP_CONTENT);

        paramsOfContent.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainTitle.setLayoutParams(paramsOfContent);

        paramsOfContent2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        logoOnTopView = (ImageView) findViewById(R.id.logo);
        rightImageView = (ImageView) findViewById(R.id.right_image_view);
        rightImageView.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean(ConstantsManager.UPGRADED_SUCCESFUL)) {
                Log.i(TAG, "UPGRADED_SUCCESFUL");
                //setCurrentTab(SETTINGS_TAB);
                pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), true, true, null);
            } else if (b.getBoolean(ConstantsManager.PRINT_SUCCESFUL)) {
                Log.i(TAG, "PRINT_SUCCESFUL");
                //setCurrentTab(SETTINGS_TAB);
                pushFragments(ConstantsManager.TAB_3, new PaymentFragment(), true, true, null);
            }
        }
        doInitialization();

        String userName = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_name_key), "");
        if (TextUtils.isEmpty(userName)) {
            ProfileAsync profileAsync = new ProfileAsync(this);
            profileAsync.execute();
        }
    }


    private void doInitialization() {
        // Default to first page
        String userNamas = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_namas_key), "");
        JSONArray array;
        try {
            if (!TextUtils.isEmpty(userNamas)) {
                array = new JSONArray(userNamas);
                Log.i(TAG, "namasArray size: " + array.length());
                if (array.length() > 0) {
                    manageFragments(ConstantsManager.TAB_2);
                }
            } else {
                MyApplication.mEditor.putBoolean(getString(R.string.pref_is_from_initialise_tabs_key), true).commit();
                manageFragments(ConstantsManager.TAB_3);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction() != null) {
            intent.getAction();
        }
    }


    /*
     * To add fragment to a tab. tag -> Tab identifier fragment -> Fragment to
     * show, in tab identified by tag shouldAnimate -> should animate
     * transaction. false when we switch tabs, or adding first fragment to a tab
     * true when when we are pushing more fragment into navigation stack.
     * shouldAdd -> Should add to fragment navigation stack (mStacks.get(tag)).
     * false when we are switching tabs (except for the first time) true in all
     * other cases.
     */
    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd, Bundle b) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment whichFrag = manager.findFragmentById(R.id.realtabcontent);
        if (checkForSameFragmentTransaction(whichFrag, fragment, b)) {
            return;
        }

        if (shouldAdd) {
            mStacks.get(tag).push(fragment);
        }

        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(b);

        if (shouldAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    /**
     * It changes the header view like back btn, title header, right img and its visibility
     */
    public void onFragmentTabChange(int leftViewVisibility, int titleImageID, int rightImageID, String tagOfRightImg, int rightImageVisibility) {

        logoOnTopView.setImageResource(titleImageID);
        rightImageView.setImageResource(rightImageID);

        if (rightImageVisibility == View.VISIBLE) {
            rightImageView.setVisibility(View.VISIBLE);
            rightImageView.setTag(tagOfRightImg);
        } else if (rightImageVisibility == View.INVISIBLE) {
            rightImageView.setVisibility(View.INVISIBLE);
        } else if (rightImageVisibility == View.GONE) {
            rightImageView.setVisibility(View.GONE);
        }

    }

    private void initUI() {
        ll_chanting = (LinearLayout) findViewById(R.id.ll_chanting);
        ll_help = (LinearLayout) findViewById(R.id.ll_help);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_delete_account = (LinearLayout) findViewById(R.id.ll_delete_account);
        ll_settings = (LinearLayout) findViewById(R.id.ll_settings);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        back = (ImageView) findViewById(R.id.back);
        rlmenuview = (ScrollView) findViewById(R.id.anim_ll1);
        btn_menu = (Button) findViewById(R.id.btn_close_menu);

        ll_settings.setOnClickListener(this);
        ll_chanting.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        ll_delete_account.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        back.setOnClickListener(this);
        btn_menu.setOnClickListener(this);

        int isUpgraded = MyApplication.mPref.getInt(getResources().getString(R.string.pref_user_upgrade_key), 0);
        if (isUpgraded != 0) {
//            ll_share.setVisibility(View.GONE);
        }


        anim = AnimationUtils.loadAnimation(Container.this, R.anim.slide_in_right_animation1);
        outanim = AnimationUtils.loadAnimation(Container.this, R.anim.slide_out);


    }

    /**
     * Here, It is the case, when you are trying to do Fragment transaction with
     * the same class(SignInFragmentPhone.java). Suppose, if you are in sign-up
     * view and trying to do login by pressing any other tabs like chat,
     * message, phone, InApp, It will try to do fragment transaction with same
     * class (SignInFragmentPhone.java) NOTE: we are using same class
     * (SignInFragmentPhone.java) for both SignUp and SignIn
     */
    private boolean checkForSameFragmentTransaction(Fragment whichFrag, Fragment fragment, Bundle b) {
        if (fragment == whichFrag) {
            return true;
        }
        return false;
    }


    public void popFragments() {
        /*
         * Select the second last fragment in current tab's stack.. which will
         * be shown after the fragment transaction given below
         */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

        /* pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();

        /*
         * We have the target fragment in hand.. Just show it.. Show a standard
         * navigation animation
         */
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    @Override
    public void onClick(@NonNull View v) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        switch (v.getId()) {
            case R.id.ll_chanting:
                manageFragments(ConstantsManager.TAB_2);
                break;
            case R.id.ll_share:
                lastChangedTab = UPGRADE_TAB;
                manageFragments(ConstantsManager.TAB_5);
                break;
            case R.id.ll_help:
                manageFragments(ConstantsManager.TAB_4);
                break;
            case R.id.ll_settings:
                manageFragments(ConstantsManager.TAB_3);
                break;
            case R.id.ll_logout:
                NamaKotiUtils.showSignOutDialog(Container.this, getResources().getString(R.string.signout), getResources().getString(R.string.signout_dlg_msg), lastChangedTab);
                break;
            case R.id.ll_delete_account:
                NamaKotiUtils.showDeleteAccountDialog(Container.this, getResources().getString(R.string.delete_account), getResources().getString(R.string.delete_account_dlg_msg), lastChangedTab);
                break;
            case R.id.right_image_view:
                String tag = "";
                tag = (String) v.getTag();

                if (tag.equals("sync")) {
                    if (!haveNetworkConnection()) {
                        NamaKotiUtils.enableWifiSettings(Container.this);
                        Log.i(TAG, "We do not have the network connection");
                    } else {
                        EnternamaFragment intstance = EnternamaFragment.getInstance();
                        intstance.syncCount(intstance.user_namakoti_id);
                    }

                } else if (tag.equals("viewProfile")) {

                    if (!haveNetworkConnection()) {
                        NamaKotiUtils.enableWifiSettings(Container.this);
                        Log.i(TAG, "We do not have the network connection");
                        return;
                    }

                    Log.i(TAG, "We have the network connection");
                    pushFragments(ConstantsManager.TAB_3, new EditProfileFragmentUrlConnection(), false, (mStacks.get(ConstantsManager.TAB_3).size() == 0) ? true : false, null);
                }

                break;

            case R.id.back:
                v = rlmenuview;
                v.startAnimation(anim);
                rlmenuview.setVisibility(View.GONE);
                v.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_close_menu:
                v = rlmenuview;
                v.startAnimation(outanim);
                v.setVisibility(View.GONE);
                break;
        }

    }

    public void hideMenu() {
        if (rlmenuview != null) {
            //rlmenuview.startAnimation(outanim);
            rlmenuview.setVisibility(View.GONE);
        }
    }


    public void manageFragments(@NonNull String fragmentId) {
        mCurrentTab = fragmentId;
        if (fragmentId.equalsIgnoreCase(ConstantsManager.TAB_HOME)) {
            hideMenu();
            NamaKotiUtils.showSignOutDialog(Container.this, getResources().getString(R.string.signout), getResources().getString(R.string.signout_dlg_msg), lastChangedTab);
        } else if (fragmentId.equalsIgnoreCase(ConstantsManager.TAB_2)) {
            hideMenu();
            lastChangedTab = ENTER_NAMA_TAB;
            pushFragments(ConstantsManager.TAB_2, new EnternamaFragment(), false, (mStacks.get(ConstantsManager.TAB_2).size() == 0) ? true : false, null);
        } else if (fragmentId.equalsIgnoreCase(ConstantsManager.TAB_3)) {
            hideMenu();
            lastChangedTab = SETTINGS_TAB;
            pushFragments(ConstantsManager.TAB_3, new HomeFragment(), false, (mStacks.get(ConstantsManager.TAB_3).size() == 0) ? true : false, null);

        } else if (fragmentId.equalsIgnoreCase(ConstantsManager.TAB_4)) {
            hideMenu();
            lastChangedTab = HELP_TAB;
            pushFragments(ConstantsManager.TAB_4, new HelpFragment(), false, (mStacks.get(ConstantsManager.TAB_4).size() == 0) ? true : false, null);
        } else if (fragmentId.equalsIgnoreCase(ConstantsManager.TAB_5)) {
            hideMenu();
            /*int rcInt = MyApplication.mPref.getInt(getResources().getString(R.string.pref_present_running_count_key), 0);
            if (rcInt >= 10000) {
                pushFragments(ConstantsManager.TAB_5, new UpgradePopUpFragment(), false, (mStacks.get(ConstantsManager.TAB_5).size() == 0) ? true : false, null);
            } else {
                pushFragments(ConstantsManager.TAB_5, new UpgradeFragment(), false, (mStacks.get(ConstantsManager.TAB_5).size() == 0) ? true : false, null);
            }*/

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_SUBJECT, "Namakoti");
                String sAux = "";//
                // "\nNamakoti allows to chant nama of gods & print those as book to submit in temple \n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.unitol.namakoti";
//                sAux = sAux + "https://www.namakoti.com/";
//                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share Namakoti"));
            } catch (Exception e) { //e.toString();
            }
        }

    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_SETTLING:

                mViewPager.setPagingEnabled(false); // if we want disable the page
                // scrolling it set to false

                break;
            case ViewPager.SCROLL_STATE_IDLE:
                // Toast.makeText(this, "onPageScrollStateChanged",
                // Toast.LENGTH_SHORT).show();

                mViewPager.setPagingEnabled(false); // if we want disable the page
                // scrolling it set to false

                break;
            case ViewPager.SCROLL_STATE_DRAGGING:

                break;
        }
    }

    @Override
    public void onFragmentTransactionPhone(JSONObject response, String whichView, String title, String phoneNO) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // This method is being called for each tab(While moving to one tab to
        // another)

        Log.i(TAG, "nresume");

        System.gc();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @SuppressLint("StringFormatInvalid")
    public boolean isLogin() {
        if (mPref != null) {
            return mPref.getBoolean(getResources().getString(R.string.pref_login_flag_key, ""), false);
        }
        return false;
    }

    // App SignOut
    public void signOut() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing out...");
        // new LogoutAsyncTask(progressDialog).execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " onDestroy() ");
        MyApplication.mEditor.putBoolean(getString(R.string.pref_create_nama_key), false).commit();
        mStacks = null;
        System.gc();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, " onConfigurationChanged ");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");
    }


    @Override
    public void onBackPressed() {

        try {
            if (null != rlmenuview && rlmenuview.getVisibility() == View.VISIBLE) {
                rlmenuview.startAnimation(outanim);
                rlmenuview.setVisibility(View.GONE);
            }

            if (((BaseFragment) mStacks.get(mCurrentTab).lastElement()).onBackPressed() == false) {

                if (mStacks.get(mCurrentTab).size() == 1) {
                    Log.i(TAG, "Call finish");
                    super.onBackPressed(); // or call finish..
                    Intent in = new Intent(Container.this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
//					This code is written when no nama is set, The fragment SetNamaFragment.java is loaded twice. I am checking here, if it is loaded twice, i managed to call popFragments() twice
                    if (mStacks.get(mCurrentTab).size() > 0 && mStacks.get(mCurrentTab).lastElement() instanceof SetNamaFragment) {
                        popFragments();
                        if (mStacks.get(mCurrentTab).size() > 0 && mStacks.get(mCurrentTab).lastElement() instanceof SetNamaFragment) {
                            popFragments();
                        }
                    } else {
                        popFragments();
                    }
                }

            } else {
                // do nothing.. fragment already handled back button press.
                Log.i(TAG, "Else condition");
            }

        } catch (Exception e) {
            Log.i(TAG, "Error >>>>> " + e.toString());
//			finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: " + resultCode);
        /*
         * if(mStacks.get(mCurrentTab).size() == 0){ return; }
         *
         * Now current fragment on screen gets onActivityResult callback..
         * mStacks.get(mCurrentTab).lastElement().onActivityResult(requestCode,
         * resultCode, data);
         */

        if (requestCode == WebConstants.LOGGED_IN) {
            if (resultCode == Activity.RESULT_OK) {

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public void hideOutSideMenu(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (view != rlmenuview) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if (rlmenuview.getVisibility() == View.VISIBLE) {
                        rlmenuview.startAnimation(outanim);
                        rlmenuview.setVisibility(View.GONE);
                    }
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        else if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                hideOutSideMenu(innerView);
            }
        }
    }


}

