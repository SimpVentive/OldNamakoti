package com.namakoti.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.utils.CalendarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaymentAdapter extends BaseAdapter {
	
	private static final String TAG = "PaymentAdapter";
	private final String mGodName;
	private List<JSONObject> languagesWithIDList;
	private Activity mActivity;

	public PaymentAdapter(Activity mActivity, List<JSONObject> languagesWithIDList, String godName) {
		this.languagesWithIDList = languagesWithIDList;
		this.mActivity = mActivity;
		this.mGodName = godName;
	}

	@Override
	public int getCount() {
		return languagesWithIDList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return languagesWithIDList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup parent) {
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View v = inflater.inflate(R.layout.new_payment_row_item, parent, false);
//		TextView payment = (TextView)v.findViewById(R.id.payment);
		Button reprint = (Button)v.findViewById(R.id.button_reprint);
		TextView godNameTv = (TextView)v.findViewById(R.id.godNameTv);
		TextView printed_count_tv = (TextView)v.findViewById(R.id.printed_count_tv);
		TextView dateTV = (TextView)v.findViewById(R.id.dateTV);
		godNameTv.setText(mGodName);
		JSONObject namaObject = languagesWithIDList.get(arg0);
		
		try {
//			String status = namaObject.getString("transaction_status");
//			status = status.equals("null") ?  "Failed" : status;
//			payment.setText(namaObject.getString("transaction_id")+" ["+status+"]");
			Log.i(TAG, "======"+namaObject.getString("transaction_id")+" ["+namaObject.getString("transaction_status")+"]");

			printed_count_tv.setText(namaObject.getString("no_of_prints"));
			dateTV.setText(CalendarUtils.getDateFormattedString("yyyy-MM-dd",namaObject.getString("created_date"),"MMM dd, yyyy"));
			v.setTag(namaObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return v;
	}

}
