package com.unitol.namakoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.unitol.namakoti.adapters.CountryAdapter;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountryDialogActivity extends Activity {

    protected static final String TAG = "CountryDialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salutation_activity);
        ListView countryList = (ListView) findViewById(R.id.listview_countries);
        final TextView cancel_btn = (TextView) findViewById(R.id.cancel_btn);
        final TextView mainTitle = (TextView) findViewById(R.id.mainTitle);
        mainTitle.setText("Select the country");

        String allCountries = getIntent().getExtras().getString(ConstantsManager.COUNTRY_JSON_4_ADAPTER);


        List<JSONObject> allCountriesList = new ArrayList<JSONObject>();

        try {
            JSONArray allCountriesArray = new JSONArray(allCountries);
            for (int i = 0; i < allCountriesArray.length(); i++) {
                allCountriesList.add((JSONObject) allCountriesArray.get(i));
            }
        } catch (Exception e) {

        }

        CountryAdapter adapterAllCountries = new CountryAdapter(CountryDialogActivity.this, allCountriesList);
        countryList.setAdapter(adapterAllCountries);

        countryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
                JSONObject jsonObject = (JSONObject) viewLV.getTag();
                Log.i(TAG, "jsonObject: " + jsonObject);
                Intent intent = new Intent();
                intent.putExtra(ConstantsManager.COUNTRY_JSON_4_ADAPTER, jsonObject.toString());

                setResult(ConstantsManager.COUNTRY_CODE, intent);

                finish();//finishing activity  
            }
        });

        final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(CountryDialogActivity.this);

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
		
		/*ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);  
				finish();
			}
		});*/
    }

    /**
     * parse the usernamas and returns list of jsonobjects
     */
    private List<JSONObject> parseUserNamas(String userNamas) {
        //"namas":[{"nama_total_count":"0","nama_running_count":"0","sub_theme_name":"Om Sai Ram","user_namakoti_id":"36","user_theme_id":"3","user_sub_theme_id":"4","user_language_id":"1"}]}
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
            if (!TextUtils.isEmpty(userNamas)) {
                JSONArray namasArray = new JSONArray(userNamas);
                Log.i(TAG, "namasArray size: " + namasArray.length());
                for (int i = 0; i < namasArray.length(); i++) {
                    JSONObject object = (JSONObject) namasArray.get(i);
                    jsonObjects.add(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

}
