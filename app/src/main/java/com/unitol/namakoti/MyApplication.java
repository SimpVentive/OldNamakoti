package com.unitol.namakoti;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.unitol.namakoti.util.Debug;

public class MyApplication extends Application {

    private static MyApplication instance = null;
    public static SharedPreferences mPref;
    public static Editor mEditor;
    private static final String TAG = "SendLog";

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate =========");

        FirebaseApp.initializeApp(getApplicationContext());
        if (Debug.DEBUG_FLAG) {
            FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(false);
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
        } else {
            FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(true);
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        }


        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mPref.edit();

        // Setup handler for uncaught exceptions.
		/*Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException (Thread thread, Throwable e)
			{
				handleUncaughtException (thread, e);
			}
		});*/

    }

    public void handleUncaughtException(Thread thread, @NonNull Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        Intent intent = new Intent();
        intent.setAction("com.unitol.namakoti.SendLog"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);

        System.exit(1); // kill off the crashed app
    }


}
