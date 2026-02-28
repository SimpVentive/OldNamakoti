package com.unitol.namakoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.web.CountryISDCodesForSignUpAsync;

import org.json.JSONObject;

public class CoutryCodeActivity extends Activity{

	protected static final String TAG = "CoutryCodeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.countries_codes);
		
		ListView countryList =  (ListView) findViewById(R.id.listview_countries);

		CountryISDCodesForSignUpAsync async = new CountryISDCodesForSignUpAsync(this, countryList);
		async.execute();
		
		countryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
				JSONObject jsonObject = (JSONObject) viewLV.getTag();
				Log.i(TAG, "jsonObject: "+jsonObject);
                Intent intent=new Intent();  
                intent.putExtra(ConstantsManager.COUNTRY_JSON, jsonObject.toString());  
                  
                setResult(ConstantsManager.COUNTRY_CODE,intent);  
                  
                finish();//finishing activity  
			}
		});
		
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);  
		super.onBackPressed();
	}
}
