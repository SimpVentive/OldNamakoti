package com.namakoti.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.namakoti.database.DatabaseHelper;

public class MyApplication extends Application {

	private static MyApplication mInstance = null;
	public static SharedPreferences mPref;
	public static Editor mEditor;
	private static final String TAG = "MyApplication";
	private SQLiteDatabase mNamakotiDB;
	private DatabaseHelper mDatabaseHelper;

	public static MyApplication getInstance() {
		return  mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate =========");

		FirebaseApp.initializeApp(getApplicationContext());
		FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(true);
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

		mInstance = this;
		mDatabaseHelper = new DatabaseHelper(getApplicationContext());
		mDatabaseHelper.open();
		mNamakotiDB = mDatabaseHelper.getWritableDatabase();

		mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPref.edit();


	}

	public SQLiteDatabase getDataDatabase() {
		if (!mNamakotiDB.isOpen()) {
			mNamakotiDB = mDatabaseHelper.getWritableDatabase();
		}
		return mNamakotiDB;
	}

}
