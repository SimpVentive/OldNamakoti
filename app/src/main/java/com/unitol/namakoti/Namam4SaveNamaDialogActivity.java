package com.unitol.namakoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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

public class Namam4SaveNamaDialogActivity extends Activity{

	protected static final String TAG = "Namam4SaveNamaDialogActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salutation_activity);
		final ListView countryList =  (ListView) findViewById(R.id.listview_countries);
		TextView cancel_btn =  (TextView) findViewById(R.id.cancel_btn);
		TextView mainTitle =  (TextView) findViewById(R.id.mainTitle);
		mainTitle.setText("Select namam");

		countryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
				String jsonObject = (String) viewLV.getTag();
				Log.i(TAG, "jsonObject: "+jsonObject);
                Intent intent=new Intent();  
                intent.putExtra(ConstantsManager.NAMAM_4_SAVE_RES, jsonObject.toString());  
                  
                setResult(ConstantsManager.NAMAM_4_SAVE,intent);  
                  
                finish();//finishing activity  
			}
		});
		
		final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
		
		ViewTreeObserver observer = main_layout.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(Namam4SaveNamaDialogActivity.this);

				String noOfLanguages = MyApplication.mPref.getString(getString(R.string.pref_chants_with_languages_key), null);

				ChantsAdapter adapter = new ChantsAdapter(Namam4SaveNamaDialogActivity.this, setMyAdapter(noOfLanguages));
				countryList.setAdapter(adapter);

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
		List<String> chantsWithIDList = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(noOfLanguages);
//			responseString: [{"sub_theme_id":"4","sub_theme_name":"Om Sai Ram"}]
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String sub_theme_id = jsonObject.getString("sub_theme_id");
				String sub_theme_name = jsonObject.getString("sub_theme_name");
//				sub_theme_name = ""+android.text.Html.fromHtml(sub_theme_name);
				sub_theme_name = ""+Html.fromHtml(sub_theme_name);
				String chantssWithID = sub_theme_id+ConstantsManager.SPLIT_KEY+sub_theme_name;
				chantsWithIDList.add(chantssWithID);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return chantsWithIDList;
	}
	
}
