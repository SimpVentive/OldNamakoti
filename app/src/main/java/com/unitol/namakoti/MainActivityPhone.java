package com.unitol.namakoti;

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
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.LayoutParams;

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

public class MainActivityPhone extends FragmentActivity implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener,
        OnFragmentTransactionListenerPhone, OnClickListener {

    private final static String TAG = "MainActivityPhone";
    public final static int ENTER_NAMA_TAB = 0;
    public final static int SETTINGS_TAB = 1;
    public final static int HELP_TAB = 2;
    public final static int UPGRADE_TAB = 3;
    public final static int HOME_TAB_ID = 4;
    private TabHost mTabHost;
    private CustomViewPager mViewPager;
    public SharedPreferences mPref;

    //	private Animation mRotateAnim;
    private boolean isLogin = false;
    private static MainActivityPhone instance;
    /* A HashMap of stacks, where we use tab identifier as keys.. */
    private HashMap<String, Stack<Fragment>> mStacks;

    /* Save current tabs identifier in this.. */
    private String mCurrentTab;

    public static MainActivityPhone getInstance() {
        return instance;
    }

    /* For header */
    private ImageView back;
    private TextView mainTitle;
    // private static HomeFragmentTab tab;
    // private MainActivityPhone activityPhone;
    private ImageView logoOnTopView;
    private ImageView rightImageView;
    // private ImageView callerTopView;
    // private ImageView plusIcon;
    // private ImageView endCall;
    // private LinearLayout dialerNote;
    // private Chronometer callTimer;
    // private TextView nameOrNumber;
    // private ImageView speaker;
    private AudioManager audioManager;

    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    //	private IconPageIndicator mIndicator;
    private LinearLayout viewpagerLayout;
    private FrameLayout tabContent;

    private int lastChangedTab;

    class TabFactory implements TabContentFactory {

        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, " onCreate ");
        super.onCreate(savedInstanceState);

        // Thread.currentThread().setUncaughtExceptionHandler(new
        // MyUncaughtExceptionHandler());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(this);
        int height = metrics.heightPixels;
        int h = (int) (height * 0.01f);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        isLogin = mPref.getBoolean(getString(R.string.pref_login_flag_key), false);

        setContentView(R.layout.main_phone);

        viewpagerLayout = (LinearLayout) findViewById(R.id.viewpager_layout);

        tabContent = (FrameLayout) findViewById(R.id.realtabcontent);


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

//		mRotateAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_and_scale);
//		NamaKotiUtils.addAnimationFrameCount(mRotateAnim);


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        DisplayMetrics dm = NamaKotiUtils.getDisplayMetrics(this);

        int width = dm.widthPixels;
        back = (ImageView) findViewById(R.id.back);

        mainTitle = (TextView) findViewById(R.id.mainTitle);

        final RelativeLayout.LayoutParams paramsOfContent = new RelativeLayout.LayoutParams((int) (width * 0.58f), LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams paramsOfContent2 = new RelativeLayout.LayoutParams((int) (width * 0.22f), LayoutParams.WRAP_CONTENT);

        paramsOfContent.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainTitle.setLayoutParams(paramsOfContent);

        paramsOfContent2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        logoOnTopView = (ImageView) findViewById(R.id.logo);
        rightImageView = (ImageView) findViewById(R.id.right_image_view);
        rightImageView.setOnClickListener(this);

        back.setOnClickListener(this);

        initialiseTabHost(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean(ConstantsManager.UPGRADED_SUCCESFUL)) {
                Log.i(TAG, "UPGRADED_SUCCESFUL");
                setCurrentTab(SETTINGS_TAB);
                pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), true, true, null);
            } else if (b.getBoolean(ConstantsManager.PRINT_SUCCESFUL)) {
                Log.i(TAG, "PRINT_SUCCESFUL");
                setCurrentTab(SETTINGS_TAB);
                pushFragments(ConstantsManager.TAB_3, new PaymentFragment(), true, true, null);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction() != null) {
            intent.getAction();
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.i(TAG, "onTabChanged: ");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        if (!tabId.equals(ConstantsManager.TAB_2)) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
////			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//
//        }

        Log.i(TAG, "onTabChanged: ");

        if (tabId.equalsIgnoreCase(ConstantsManager.TAB_3)) {
            mStacks.get(tabId).clear();
            MyApplication.mEditor.putBoolean(getString(R.string.pref_create_nama_key), false).commit();
        }
        getSupportFragmentManager().findFragmentById(R.id.realtabcontent);

        mTabHost.getCurrentTab();

		/* Set current tab.. */
        mCurrentTab = tabId;
        if (mStacks.get(tabId).size() == 0) {
            /*
             * First time this tab is selected. So add first fragment of that
			 * tab. Dont need animation, so that argument is false. We are
			 * adding a new fragment which is not present in stack. So add to
			 * stack is true.
			 */

            Log.i(TAG, "onTabChanged: " + tabId);
            if (tabId.equals(ConstantsManager.TAB_HOME)) {
                NamaKotiUtils.showSignOutDialog(MainActivityPhone.this, getResources().getString(R.string.signout), getResources().getString(R.string.signout_dlg_msg), lastChangedTab);
//				lastChangedTab = HOME_TAB_ID;
            } else if (tabId.equals(ConstantsManager.TAB_2)) {
                lastChangedTab = ENTER_NAMA_TAB;
                pushFragments(tabId, new EnternamaFragment(), false, true, null);
            } else if (tabId.equals(ConstantsManager.TAB_3)) {
                lastChangedTab = SETTINGS_TAB;
                pushFragments(tabId, new HomeFragment(), false, true, null);
            } else if (tabId.equals(ConstantsManager.TAB_4)) {
                lastChangedTab = HELP_TAB;
                pushFragments(tabId, new HelpFragment(), false, true, null);
            } else if (tabId.equals(ConstantsManager.TAB_5)) {
                lastChangedTab = UPGRADE_TAB;
                int rcInt = MyApplication.mPref.getInt(getResources().getString(R.string.pref_present_running_count_key), 0);
                if (rcInt >= 10000) {
                    pushFragments(tabId, new UpgradePopUpFragment(), false, true, null);
                } else {
                    pushFragments(tabId, new UpgradeFragment(), false, true, null);
                }
            }
        } else {
			/*
			 * We are switching tabs, and target tab is already has atleast one
			 * fragment. No need of animation, no need of stack pushing. Just
			 * show the target fragment
			 */
            pushFragments(tabId, mStacks.get(tabId).lastElement(), false, false, null);
        }

    }


    /**
     * Visibility of
     */
	/*private void onVisiBilityChangeOfViewPager(int pager, int tab) {
		viewpagerLayout.setVisibility(pager);
		tabContent.setVisibility(tab);

		if (pager == View.VISIBLE) {
			onFragmentTabChange(View.VISIBLE, getResources().getString(R.string.buy_packages), "", View.GONE);
			startSetUpInAppHelper(getApplicationContext());
		} else if (pager == View.GONE) {
			if (mHelper != null) {
				mHelper.dispose();
			}
		}
	}*/

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
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        // set the tab respect to the selected page position
        mTabHost.setCurrentTab(position);
    }

    /**
     * Initialise the Tab Host
     */
    public void initialiseTabHost(Bundle args) {
        Log.i(TAG, "initialiseTabHost");
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        // TabInfo tabInfo = null;

        AddTab(getApplicationContext(), mTabHost, getTabSpec(ConstantsManager.TAB_2, R.drawable.dialer_selector));

        AddTab(getApplicationContext(), mTabHost, getTabSpec(ConstantsManager.TAB_3, R.drawable.message_selector));

        AddTab(getApplicationContext(), mTabHost, getTabSpec(ConstantsManager.TAB_4, R.drawable.chat_selector));

        int isUpgraded = MyApplication.mPref.getInt(getResources().getString(R.string.pref_user_upgrade_key), 0);
        if (isUpgraded == 0) {
            AddTab(getApplicationContext(), mTabHost, getTabSpec(ConstantsManager.TAB_5, R.drawable.about_selector));
        }

        AddTab(getApplicationContext(), mTabHost, getTabSpec(ConstantsManager.TAB_HOME, R.drawable.home_selector));

        mTabHost.setOnTabChangedListener(this);
        // Default to first page
        String userNamas = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_namas_key), "");
        JSONArray array = null;
        try {
            array = new JSONArray(userNamas);
            Log.i(TAG, "namas array length: " + array.length());
            if (array.length() > 0) {
                onTabChanged(ConstantsManager.TAB_2); // by default mTabHost set 0 position
            } else {
                MyApplication.mEditor.putBoolean(getString(R.string.pref_is_from_initialise_tabs_key), true).commit();
                onTabChanged(ConstantsManager.TAB_3); // by default mTabHost set 0 position
                setCurrentTab(SETTINGS_TAB);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Getting the tab spec for tabhost
     *
     * @param drawable
     * @return
     */
    public TabHost.TabSpec getTabSpec(String tname, int drawable) {
        TabHost.TabSpec spec = mTabHost.newTabSpec(tname);
        View tabIndicator = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_indicator_phone, mTabHost.getTabWidget(), false);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawable);
        spec.setIndicator(tabIndicator);
        return spec;
    }

    /**
     * Add Tab content to the Tabhost
     *
     * @param context
     * @param tabHost
     * @param tabSpec
     */
    private void AddTab(Context context, TabHost tabHost, TabHost.TabSpec tabSpec) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(new TabFactory(context));
        tabHost.addTab(tabSpec);
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
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onFragmentTransactionPhone(JSONObject response,
                                           String whichView, String title, String mPhoneNo) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
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
//		new LogoutAsyncTask(progressDialog).execute();

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


    /*
     * Might be useful if we want to switch tab programmatically, from inside
     * any of the fragment.
     */
    public void setCurrentTab(int val) {
        mTabHost.setCurrentTab(val);
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
		/*if (tag.equalsIgnoreCase(ConstantsManager.TAB_3) && mStacks.get(tag).size() > 0) {
			Fragment whichFrag = mStacks.get(tag).lastElement();

			if (checkForSameFragmentTransaction(whichFrag, fragment, b)) {
				return;
			}
		}*/

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
     * Here, It is the case, when you are trying to do Fragment transaction with
     * the same class(SignInFragmentPhone.java). Suppose, if you are in sign-up
     * view and trying to do login by pressing any other tabs like chat,
     * message, phone, InApp, It will try to do fragment transaction with same
     * class (SignInFragmentPhone.java) NOTE: we are using same class
     * (SignInFragmentPhone.java) for both SignUp and SignIn
     */
    private boolean checkForSameFragmentTransaction(Fragment whichFrag,
                                                    Fragment fragment, Bundle b) {
        if (fragment == whichFrag) {
			/*if (whichFrag instanceof SignInFragmentPhone) {
				SignInFragmentPhone frag = ((SignInFragmentPhone) whichFrag);
				if (b == null && frag.loginView.getVisibility() == View.GONE) {
					Log.i(TAG, "show login view");
					frag.signUpview.setVisibility(View.GONE);
					frag.loginView.setVisibility(View.VISIBLE);
				}
				// return true;
			}*/
            return true;
        }
        return false;
    }

    public void popFragments() {
		/*
		 * Select the second last fragment in current tab's stack.. which will
		 * be shown after the fragment transaction given below
		 */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(
                mStacks.get(mCurrentTab).size() - 2);

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
    public void onBackPressed() {

//		if (mTabHost.getCurrentTab() == MainActivityPhone.UPGRADE_TAB) {
//			finish();
//		}

        try {

            if (((BaseFragment) mStacks.get(mCurrentTab).lastElement()).onBackPressed() == false) {
				/*
				 * top fragment in current tab doesn't handles back press, we
				 * can do our thing, which is
				 *
				 * if current tab has only one fragment in stack, ie first
				 * fragment is showing for this tab. finish the activity else
				 * pop to previous fragment in stack for the same tab
				 */
                if (mStacks.get(mCurrentTab).size() == 1) {
                    Log.i(TAG, "Call finish");
                    super.onBackPressed(); // or call finish..
                    Intent in = new Intent(MainActivityPhone.this, MainActivity.class);
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

    /*
     * Imagine if you wanted to get an image selected using ImagePicker intent
     * to the fragment. Ofcourse I could have created a public function in that
     * fragment, and called it from the activity. But couldn't resist myself.
     */
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

    /**
     * It changes the header view like back btn, title header, right img and its visibility
     */
    public void onFragmentTabChange(int leftViewVisibility, int titleImageID, int rightImageID, String tagOfRightImg, int rightImageVisibility) {

        if (leftViewVisibility == View.VISIBLE) {

        }

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

    public void onFragmentTabChange(int i, String title, String mPhoneNo, int mapV) {
        mainTitle.setText(title);

        if (View.VISIBLE == i) {
            back.setVisibility(View.VISIBLE);
            mainTitle.setVisibility(View.VISIBLE);
            logoOnTopView.setVisibility(View.INVISIBLE);
        } else if (View.INVISIBLE == i) {
            back.setVisibility(View.VISIBLE);
            mainTitle.setVisibility(View.INVISIBLE);
            logoOnTopView.setVisibility(View.VISIBLE);
        } else if (View.GONE == i) {
            back.setVisibility(View.GONE);
            mainTitle.setVisibility(View.GONE);
        }

    }


//


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.right_image_view:
                String tag = "";
                tag = (String) v.getTag();

                if (tag.equals("sync")) {
                    if (!haveNetworkConnection()) {
                        NamaKotiUtils.enableWifiSettings(MainActivityPhone.this);
                        Log.i(TAG, "We do not have the network connection");
                    } else {
                        EnternamaFragment intstance = EnternamaFragment.getInstance();
                        intstance.syncCount(intstance.user_namakoti_id);
                    }

                } else if (tag.equals("viewProfile")) {

                    if (!haveNetworkConnection()) {
                        NamaKotiUtils.enableWifiSettings(MainActivityPhone.this);
                        Log.i(TAG, "We do not have the network connection");
                        return;
                    }

                    Log.i(TAG, "We have the network connection");
                    pushFragments(ConstantsManager.TAB_3, new EditProfileFragmentUrlConnection(), false, true, null);
                }

                break;


            default:
                break;
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
}
