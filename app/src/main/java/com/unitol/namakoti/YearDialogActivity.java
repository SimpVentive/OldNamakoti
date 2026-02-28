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

import com.unitol.namakoti.adapters.DatesAdapter;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class YearDialogActivity extends Activity {

    protected static final String TAG = "YearDialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salutation_activity);
        ListView countryList = (ListView) findViewById(R.id.listview_countries);
        final TextView cancel_btn = (TextView) findViewById(R.id.cancel_btn);
        final TextView mainTitle = (TextView) findViewById(R.id.mainTitle);
        mainTitle.setText("Select the year");

//		String allSalutes = getIntent().getExtras().getString(ConstantsManager.SALUTION_JSON_4_ADAPTER);

        try {
			/*JSONArray allSalutesArray = new JSONArray(allSalutes);
			
			ArrayList<JSONObject> salutionList = new ArrayList<JSONObject>();
			for (int i = 0; i < allSalutesArray.length(); i++) {
				JSONObject saute = (JSONObject) allSalutesArray.get(i);
				salutionList.add(saute);
			}*/

            final Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            int firstYear = currentYear - 110;
            List<String> years = new ArrayList<String>();
            for (int i = firstYear; i < currentYear; i++) {
                years.add("" + i);
            }

            DatesAdapter adapterYears = new DatesAdapter(YearDialogActivity.this, years);
            countryList.setAdapter(adapterYears);
        } catch (Exception e) {
            // TODO: handle exception
        }

        countryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
                String jsonObject = (String) viewLV.getTag();
                Log.i(TAG, "jsonObject: " + jsonObject);
                Intent intent = new Intent();
                intent.putExtra(ConstantsManager.YEAR_JSON_4_ADAPTER, jsonObject);

                setResult(ConstantsManager.YEAR_CODE, intent);

                finish();//finishing activity  
            }
        });

        final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(YearDialogActivity.this);

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
