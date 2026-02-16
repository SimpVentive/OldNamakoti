package com.namakoti.settings;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.adapters.DispatchAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anusha on 2/11/2018.
 */

public class PaymentDialogActivity extends BaseActivity implements View.OnClickListener {

    protected static final String TAG = "PaymentDialogActivity";
    private LinearLayout dispatches_content;
    private LinearLayout transactions_content;
    private View transactions_layout;
    private View dispatches_layout;
    private JSONArray NkUserDispatchDetails;
//	private JSONObject obj;
//	private String statusDp;
//	private String tracking_numberDP;
//	private String sourceDp;
//	private String deliver_dateDP;
//	private String status_dateDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);

        final TextView cancel_btn =  (TextView) findViewById(R.id.cancel_btn);
        final TextView mainTitle =  (TextView) findViewById(R.id.mainTitle);

        transactions_layout = findViewById(R.id.transactions_layout);
        transactions_layout.setOnClickListener(this);
        dispatches_layout = findViewById(R.id.dispatches_layout);
        dispatches_layout.setOnClickListener(this);

        dispatches_content =  (LinearLayout) findViewById(R.id.dispatches_content);
        transactions_content =  (LinearLayout) findViewById(R.id.transactions_content);

        toolbar("Transactions and Dispatch Details");
//        mainTitle.setText("Transactions and Dispatch Details");

        String payments = getIntent().getExtras().getString(Constants.PAYMENT_JSON);

        String amount = "";
        CharSequence status = "";
        CharSequence transaction_reference = "";
        CharSequence transaction_type = "";
        CharSequence createdDate = "";
        CharSequence transactionID = "";
        String no_of_print = "";
        String temple_name = "";
        try {
            JSONObject jsonObject = new JSONObject(payments);
//			Log.i(TAG, "jsonObject: "+jsonObject);
            transactionID = jsonObject.getString("transaction_id");
            amount = jsonObject.getString("amount");
            status = jsonObject.getString("status");
            transaction_reference = jsonObject.getString("transaction_reference");
            transaction_type = jsonObject.getString("transaction_type");
            createdDate = jsonObject.getString("created_date");
            no_of_print = jsonObject.getString("no_of_prints");
            JSONObject nkTemples = jsonObject.getJSONObject("NkTemples");
            temple_name = nkTemples.getString("temple_name");

            NkUserDispatchDetails = jsonObject.getJSONArray("NkUserDispatchDetails");

            ListView dispatch_listview = (ListView) findViewById(R.id.dispatch_listview);
            List<JSONObject> disaptchesList = new ArrayList<JSONObject>();
            for (int i = 0; i < NkUserDispatchDetails.length(); i++) {
                disaptchesList.add(NkUserDispatchDetails.getJSONObject(i));
            }
            dispatch_listview.setAdapter(new DispatchAdapter(PaymentDialogActivity.this, disaptchesList));

        } catch (Exception e) {
            Log.e(TAG, ""+e.toString());
        }

        final TextView transaction_id =  (TextView) findViewById(R.id.transaction_id);
        final TextView amnt =  (TextView) findViewById(R.id.amount);
        final TextView status_tv =  (TextView) findViewById(R.id.status);
        final TextView referenceNo =  (TextView) findViewById(R.id.reference_no);
        final TextView type =  (TextView) findViewById(R.id.type);
        final TextView created_date =  (TextView) findViewById(R.id.created_date);
        final TextView noOfPrint =  (TextView) findViewById(R.id.no_of_print);
        final TextView templeName =  (TextView) findViewById(R.id.temple_name);

        transaction_id.setText(transactionID);
        amnt.setText(amount);
        status_tv.setText(status);
        referenceNo.setText(transaction_reference);
        type.setText(transaction_type);
        created_date.setText(createdDate);
        noOfPrint.setText(no_of_print);
        templeName.setText(temple_name);

        final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = getDisplayMetrics(PaymentDialogActivity.this);
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                int h = (int) (height * 0.1f);

                mainTitle.getLayoutParams().height = h;
                mainTitle.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                mainTitle.requestLayout();

                cancel_btn.getLayoutParams().height = h;
                cancel_btn.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                cancel_btn.requestLayout();

                main_layout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                main_layout.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT/*(int) ((0.95f)*width)*/;
                main_layout.requestLayout();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    main_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    main_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void toolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(title);
        // add back arrow to toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**parse the usernamas and returns list of jsonobjects*/
    private List<JSONObject> parseUserNamas(String userNamas) {
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
            JSONArray namasArray = new JSONArray(userNamas);
            Log.i(TAG, "namasArray size: "+namasArray.length());
            for (int i = 0; i < namasArray.length(); i++) {
                JSONObject object = (JSONObject) namasArray.get(i);
                jsonObjects.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.transactions_layout:
                if(transactions_content.getVisibility() == View.GONE){
                    transactions_content.setVisibility(View.VISIBLE);
                    dispatches_content.setVisibility(View.GONE);

                } else if(transactions_layout.getVisibility() == View.VISIBLE){
                    transactions_content.setVisibility(View.GONE);


                }
                break;
            case R.id.dispatches_layout:
                if(dispatches_content.getVisibility() == View.GONE){
                    dispatches_content.setVisibility(View.VISIBLE);
                    transactions_content.setVisibility(View.GONE);

                } else if(dispatches_content.getVisibility() == View.VISIBLE){
                    dispatches_content.setVisibility(View.GONE);


                }
                break;

            default:
                break;
        }
    }

    //	to get display sizes
    public static DisplayMetrics getDisplayMetrics(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

}
