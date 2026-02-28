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

import com.unitol.namakoti.adapters.MonthsAdapter;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MonthDialogActivity extends Activity {

    protected static final String TAG = "MonthDialogActivity";
    String[] arrayMonths = {
            "January-:-01",
            "February-:-02",
            "March-:-03",
            "April-:-04",
            "May-:-05",
            "June-:-06",
            "July-:-07",
            "August-:-08",
            "September-:-09",
            "October-:-10",
            "November-:-11",
            "December-:-12"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salutation_activity);
        ListView countryList = (ListView) findViewById(R.id.listview_countries);
        final TextView cancel_btn = (TextView) findViewById(R.id.cancel_btn);
        final TextView mainTitle = (TextView) findViewById(R.id.mainTitle);
        mainTitle.setText("Select the month");

//		String allSalutes = getIntent().getExtras().getString(ConstantsManager.SALUTION_JSON_4_ADAPTER);

        try {
			/*JSONArray allSalutesArray = new JSONArray(allSalutes);
			
			ArrayList<JSONObject> salutionList = new ArrayList<JSONObject>();
			for (int i = 0; i < allSalutesArray.length(); i++) {
				JSONObject saute = (JSONObject) allSalutesArray.get(i);
				salutionList.add(saute);
			}*/

//			JSONObject obj = new JSONObject(salutionList);

            MonthsAdapter adapterMonths = new MonthsAdapter(MonthDialogActivity.this, arrayMonths);
            countryList.setAdapter(adapterMonths);
        } catch (Exception e) {
            // TODO: handle exception
        }

        countryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View viewLV, int arg2, long arg3) {
                String jsonObject = (String) viewLV.getTag();
                Log.i(TAG, "jsonObject: " + jsonObject);
                Intent intent = new Intent();
                intent.putExtra(ConstantsManager.MONTH_JSON_4_ADAPTER, jsonObject);

                setResult(ConstantsManager.MONTH_CODE, intent);

                finish();//finishing activity  
            }
        });

        final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = NamaKotiUtils.getDisplayMetrics(MonthDialogActivity.this);

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
