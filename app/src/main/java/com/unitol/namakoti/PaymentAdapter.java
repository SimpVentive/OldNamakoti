package com.unitol.namakoti;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaymentAdapter extends BaseAdapter{
	
	private static final String TAG = "PaymentAdapter";
	private List<JSONObject> languagesWithIDList;
	private Activity mActivity;

	public PaymentAdapter(Activity mActivity, List<JSONObject> languagesWithIDList) {
		this.languagesWithIDList = languagesWithIDList;
		this.mActivity = mActivity;
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
		View v = inflater.inflate(R.layout.payment_row_item, parent, false);
		TextView payment = (TextView)v.findViewById(R.id.payment);
		
		JSONObject namaObject = languagesWithIDList.get(arg0);
		
		try {
			String status = namaObject.getString("transaction_status");
			status = status.equals("null") ?  "Failed" : status;
			
			payment.setText(namaObject.getString("transaction_id")+" ["+status+"]");
			Log.i(TAG, "======"+namaObject.getString("transaction_id")+" ["+namaObject.getString("transaction_status")+"]");
			v.setTag(namaObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return v;
	}

}
