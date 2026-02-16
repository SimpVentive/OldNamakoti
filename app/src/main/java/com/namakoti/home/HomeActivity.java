package com.namakoti.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.messaging.FirebaseMessaging;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.chanting.ChantingSelectionActivity;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.gcm.NotificationUtils;
import com.namakoti.login.LoginActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.namakoti.R.id.logout;

public class HomeActivity extends BaseActivity implements View.OnClickListener, VolleyResponseListener {

    @BindView(R.id.selfChantingLL)
    LinearLayout self_chanting;

    @BindView(R.id.karmicChantingLL)
    LinearLayout karmic_chanting;

    @BindView(R.id.activeChantsLL)
    LinearLayout active_chants;

    @BindView(R.id.settingsLL)
    LinearLayout settings;

    CircularNetworkImageView mProfileImg;
    private ImageLoader mIimageLoader;

    private String TAG = HomeActivity.class.getName();
   // private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProfileImg = (CircularNetworkImageView) toolbar.findViewById(R.id.profileImg);

        setToolbarTitle(getString(R.string.title_home));
        getSupportActionBar().setLogo(R.drawable.ic_nk_logo_small);

        mIimageLoader = VolleyUtil.getInstance().imageLoader(this, com.intuit.sdp.R.dimen._40sdp);
        String url = UserInfoBean.getInstance().getProfileImage(this);
        mProfileImg.setImageUrl(Constants.GOD_IMAGE_BASE_URL + url, mIimageLoader);
        mProfileImg.setVisibility(View.VISIBLE);
        mProfileImg.setOnClickListener(this);

        //implement on click events
        self_chanting.setOnClickListener(this);
        karmic_chanting.setOnClickListener(this);
        active_chants.setOnClickListener(this);
        settings.setOnClickListener(this);

        //Send token to server after successful login.
//        sendRegistrationToServer(NotificationUtils.getRegisteredDeviceToken(this),this);
//        registrationBroadcastReceiver();
    }

/*    private void registrationBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction() != null && intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction() != null && intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//                    txtMessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();
    }*/

    // Fetches reg id from shared preferences and displays on the screen
    private void displayFirebaseRegId() {
//        String regId = Utils.getStringFromSP(mActivity,Constants.FCM_DEVICE_TOKEN_KEY);
        String regId = NotificationUtils.getRegisteredDeviceToken(this);
        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Firebase Reg Id is not received yet!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* registerBroadcast();*/
    }

   /* private void registerBroadcast() {
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }*/

    @Override
    protected void onPause() {
       /* LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);*/
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.selfChantingLL:
                intent = new Intent(HomeActivity.this, ChantingSelectionActivity.class);
                intent.putExtra("chant type", "self");
                startActivity(intent);
                break;

            case R.id.karmicChantingLL:
                intent = new Intent(HomeActivity.this, ChantingSelectionActivity.class);
                intent.putExtra("chant type", "karmic");
                startActivity(intent);
                break;

            case R.id.activeChantsLL:
                intent = new Intent(HomeActivity.this, ChantingSelectionActivity.class);
                intent.putExtra("chant type", "active");
                startActivity(intent);
                break;

            case R.id.settingsLL:
                intent = new Intent(HomeActivity.this, ChantingSelectionActivity.class);
                intent.putExtra("chant type", "settings");
                startActivity(intent);
                break;
            case R.id.profileImg:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case logout:
                logout();
                break;
        }
        return false;
    }

    private void logout() {
        finish();
        UserInfoBean.getInstance().deleteUserInfo(this);
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {

    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {

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
