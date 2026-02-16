package com.namakoti.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.namakoti.base.BaseNavigationActivity;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.karmic.helpingOthers.OthersDetailsActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by anusha on 2/13/2018.
 */
public class FcmReceiverService extends FirebaseMessagingService {

    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);
        if (refreshedToken.length() != 0) {
            if(Constants.LOG)
                Log.d("", "Refreshed token: " + refreshedToken);

            // Saving reg id to shared preferences
            Utils.saveStringInSP(getApplicationContext(), Constants.FCM_DEVICE_TOKEN_KEY, refreshedToken);
            storeRegIdInPref(refreshedToken);

            // Notify UI that registration has completed, so the progress indicator can be hidden.
            registerGCM(refreshedToken);
//        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }

    private void registerGCM(String token) {
        //Registration complete intent initially null
        Intent registrationComplete = null;

        try {

            //Displaying the token in the log so that we can copy it to send push notification
            //You can also extend the app by storing the token in to your server
            Log.w("FCM IntentService", "token:" + token);
            Utils.saveStringInSP(getApplicationContext(), Constants.FCM_DEVICE_TOKEN_KEY, token);
            //on registration complete creating intent with success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            //Putting the token to the intent
            registrationComplete.putExtra("token", token);
            // Save token in SP
            Utils.saveStringInSP(getApplicationContext(), Constants.FCM_DEVICE_TOKEN_KEY, token);
        } catch (Exception e) {
            //If any error occurred
            Log.w("GCMRegIntentService", "Registration error " + e.getMessage());
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        //Sending the broadcast that registration is completed
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
    private static final String TAG = FcmReceiverService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        Map<String, String> data = remoteMessage.getData();
        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.e(TAG, "Data Payload: " + data.toString());
            try {
                String res = data.toString();
                JSONObject json = new JSONObject(res);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }// If the app is in background, firebase itself handles the notification

    }

    private void handleDataMessage(JSONObject json) {
        if(json == null)
            return;
        if(Constants.LOG) Log.e("Push Message", "" + json.toString());
        Bundle bundle = new Bundle();
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            KarmicOthersBean.RequestGkcDetails bean = null;
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);

            String status = "";

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra(Constants.PUSH_TITLE_KEY, title);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), BaseNavigationActivity.class);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (title.equalsIgnoreCase("Namakoti App Wishes")){
                    resultIntent.putExtra("status", "downloadApp");
                }else{
                    resultIntent.putExtra("status", "gkc");
                    Gson gson = new Gson();
                    JSONObject otherdataObj = data.getJSONObject("otherdata");
                    resultIntent.putExtra("status", status);
                    bean = gson.fromJson(otherdataObj.toString(), KarmicOthersBean.RequestGkcDetails.class);
                    resultIntent.putExtra(OthersDetailsActivity.OTHERS_BEAN_KEY, bean);
                    resultIntent.putExtra(OthersDetailsActivity.PAGE_COUNT_KEY, 1);
                    resultIntent.putExtra(OthersDetailsActivity.IS_FROM_PUSH_KEY, true);
                    resultIntent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, bean.created_by);
                }
                resultIntent.putExtra(Constants.PUSH_TITLE_KEY, title);
                resultIntent.putExtra(Constants.PUSH_MESSAGE_KEY, message);

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
            sendBroadCastToActivity(message, title, status);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void sendBroadCastToActivity(String message, String title,String status) {
        Intent intentNotification = new Intent();
        intentNotification.setAction(Constants.PUSH_NOTIFICATION_ACTION);
        intentNotification.putExtra(Constants.PUSH_MESSAGE_KEY, message);
        intentNotification.putExtra(Constants.PUSH_TITLE_KEY, title);

        sendBroadcast(intentNotification);
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
