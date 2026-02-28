package com.unitol.namakoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LanguagesDialogActivity extends Activity{

	protected static final String TAG = "LanguagesDialogActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salutation_activity);
		ListView countryList =  (ListView) findViewById(R.id.listview_countries);
		TextView cancel_btn =  (TextView) findViewById(R.id.cancel_btn);
		TextView mainTitle =  (TextView) findViewById(R.id.mainTitle);
		mainTitle.setText("Select language");

		String noOfLanguages = MyApplication.mPref.getString(getString(R.string.pref_number_of_languages_key), null);
		
		LanugaugeAdapter adapter = new LanugaugeAdapter(LanguagesDialogActivity.this, setMyAdapter(noOfLanguages));
		countryList.setAdapter(adapter);
		
		countryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
				String jsonObject = (String) viewLV.getTag();
				Log.i(TAG, "jsonObject: "+jsonObject);
                Intent intent=new Intent();  
                intent.putExtra(ConstantsManager.LANGUAGES, jsonObject.toString());  
                  
                setResult(ConstantsManager.LANGUAGE_CODE,intent);  
                  
                finish();//finishing activity  
			}
		});
		
		final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
		
		ViewTreeObserver observer = main_layout.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(LanguagesDialogActivity.this);

				int width = metrics.widthPixels;
				int height = metrics.heightPixels;
				main_layout.getLayoutParams().height = (int) ((0.6f) * height);
				main_layout.getLayoutParams().width = (int) ((0.8f) * width);
				main_layout.requestLayout();

                main_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
		});
		
		cancel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);  
				finish();
			}
		});
	}
	
	private List<String> setMyAdapter(String noOfLanguages) {
		List<String> languagesWithIDList = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(noOfLanguages);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String language_id = jsonObject.getString("language_id");
				String language_name = jsonObject.getString("language_name");
				String languagesWithID = language_id+ConstantsManager.SPLIT_KEY+language_name;
				languagesWithIDList.add(languagesWithID);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return languagesWithIDList;
	}
	
}
