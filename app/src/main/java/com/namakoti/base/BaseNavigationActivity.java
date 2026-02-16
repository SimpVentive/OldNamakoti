package com.namakoti.base;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.google.firebase.messaging.FirebaseMessaging;
import com.namakoti.R;
import com.namakoti.active.ActiveChantsFragment;
import com.namakoti.beans.ErrorBean;
import com.namakoti.chanting.SelfChantingFragment;
import com.namakoti.gcm.NotificationUtils;
import com.namakoti.home.HomeFragment;
import com.namakoti.home.ProfileFragment;
import com.namakoti.karmic.KarmicChantingFragment;
import com.namakoti.login.LogOutActivity;
import com.namakoti.settings.PaymentFragment;
import com.namakoti.settings.PrintFragment;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseNavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, VolleyResponseListener {

    public static final String PUSH_GKC_REQUEST = "PUSH_GKC_REQUEST";
    private BaseNavigationActivity mActivity;
    private String TAG = BaseNavigationActivity.class.getName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_navigation_activity);
        updateUI();
    }

    private void updateUI() {
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.apptoolbar);
//        setSupportActionBar(toolbar);
        setToolbarTitle(toolbar, "");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mActivity.setCustomActionbarTitle(getString(R.string.self_chanting));

        if (getIntent() != null && getIntent().hasExtra(Constants.PUSH_TITLE_KEY)) {
            String title = getIntent().getStringExtra(Constants.PUSH_TITLE_KEY);
            String msg = getIntent().getStringExtra(Constants.PUSH_MESSAGE_KEY);
            Utils.showAlertDialog(mActivity, title, "" + msg, null, null, false, true);
            if (!title.equalsIgnoreCase("Namakoti App Wishes")) {
                loadKarmic();
                mActivity.setCustomActionbarTitle(getString(R.string.karmic_chanting));
            } else {
                mActivity.setCustomActionbarTitle(getString(R.string.self_chanting));
                loadSelf();
            }
        } else {
            mActivity.setCustomActionbarTitle(getString(R.string.self_chanting));
            loadSelf();
        }

        //Send token to server after successful login.
//        sendRegistrationToServer(NotificationUtils.getRegisteredDeviceToken(mActivity));
        registrationBroadcastReceiver();
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        updateUI();
    }*/

    private void registrationBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
                        displayFirebaseRegId();
                    } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                        String title = intent.getStringExtra(Constants.PUSH_TITLE_KEY);
                        if (title != null && !title.equalsIgnoreCase("Namakoti App Wishes")) {
                            loadKarmic();
                            mActivity.setCustomActionbarTitle(getString(R.string.karmic_chanting));
//                            Utils.showToast(mActivity, "receiver" );
                        }
                    }
                }
            }
        };
        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences and displays on the screen
    private void displayFirebaseRegId() {
//        String regId = Utils.getStringFromSP(mActivity,Constants.FCM_DEVICE_TOKEN_KEY);
        String regId = NotificationUtils.getRegisteredDeviceToken(mActivity);
        Log.e(TAG, "Firebase reg id: " + regId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerReceiver() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION_ACTION), Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION_ACTION));
            }
            // register GCM registration complete receiver
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.REGISTRATION_COMPLETE));
            // register new push message receiver by doing this, the activity will be notified each time a new message arrives
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));
            // clear the notification area when the app is opened
            NotificationUtils.clearNotifications(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(mRegistrationBroadcastReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    /**
     * This methodis used to send the device token to server
     *
     * @param token
     */
    public void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        if (TextUtils.isEmpty(token)) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserIdString);
        params.put("token", token);

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.PUSH_TOKEN_URL, params, ServiceMethod.PUSH_TOKEN, Request.Method.POST, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExit();
        }
//        else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        } else*/
        if (id == android.R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_home) {
            setCustomActionbarTitle(getString(R.string.home));
//            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof HomeFragment)) {
//                loadHome();
//            }
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else */
        if (id == R.id.nav_self) {
            setCustomActionbarTitle(getString(R.string.self_chanting));
            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof SelfChantingFragment)) {

                loadSelf();
            }
        } else if (id == R.id.nav_karmic) {
            setCustomActionbarTitle(getString(R.string.karmic_chanting));
            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof KarmicChantingFragment)) {
                loadKarmic();
            }
        } else if (id == R.id.nav_profile) {
            setCustomActionbarTitle(getString(R.string.profile));
            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof ProfileFragment)) {
                loadProfile();
            }
        }
        /*else if (id == R.id.nav_print) {
            setCustomActionbarTitle(getString(R.string.print_delivery));
            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof PrintFragment)) {
                loadPrint();
            }
        } else if (id == R.id.nav_pay) {
            setCustomActionbarTitle(getString(R.string.payment));
            if (getCurrentFrag(this) == null || !(getCurrentFrag(this) instanceof PaymentFragment)) {
                loadPayment();
            }
        }*/
        else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LogOutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPayment() {
        Log.e("selected", "active");
        PaymentFragment payFragment = new PaymentFragment();
        replaceFragment(R.id.fragment_container, payFragment, false);
    }

    private void loadPrint() {
        Log.e("selected", "active");
        PrintFragment printFragment = new PrintFragment();
        replaceFragment(R.id.fragment_container, printFragment, false);
    }

    private void loadActive() {
        Log.e("selected", "active");
        ActiveChantsFragment activeFragment = new ActiveChantsFragment();
        replaceFragment(R.id.fragment_container, activeFragment, false);
    }

    private void loadProfile() {
        Log.e("selected", "profile");
        ProfileFragment profileFragment = new ProfileFragment();
        replaceFragment(R.id.fragment_container, profileFragment, false);
    }

    private void loadKarmic() {
        Log.e("selected", "karmic");
        KarmicChantingFragment karmicFragment = new KarmicChantingFragment();
        removeFragment();
        replaceFragment(R.id.fragment_container, karmicFragment, false);
    }


    private void loadSelf() {
        Log.e("selected", "selfFragment");
        SelfChantingFragment selfFragment = new SelfChantingFragment();
        removeFragment();
        replaceFragment(R.id.fragment_container, selfFragment, false);
    }

    private void showExit() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_nk_logo_small)
                .setTitle(getString(R.string.app_name))
                .setMessage("Do you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void loadHome() {
        Log.e("selected", "home");
        HomeFragment homeFragment = new HomeFragment();
        replaceFragment(R.id.fragment_container, homeFragment, false);
    }

    protected void setToolbarTitle(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object instanceof ErrorBean) {
            ErrorBean bean = (ErrorBean) object;
//            Utils.showToast(mActivity,bean.message);
        }
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
}
