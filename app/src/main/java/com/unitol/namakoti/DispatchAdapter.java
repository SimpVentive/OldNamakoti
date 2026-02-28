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

public class DispatchAdapter extends BaseAdapter{
	
	private static final String TAG = "DispatchAdapter";
	private List<JSONObject> languagesWithIDList;
	private Activity mActivity;

	public DispatchAdapter(Activity mActivity, List<JSONObject> languagesWithIDList) {
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
		View v = inflater.inflate(R.layout.dispatch_row, parent, false);
		TextView date_dp_id = (TextView)v.findViewById(R.id.date_dp_id);
		TextView time_dp = (TextView)v.findViewById(R.id.time_dp);
		TextView status_dp = (TextView)v.findViewById(R.id.status_dp);
		TextView source_dp = (TextView)v.findViewById(R.id.source_dp);
		TextView tracking_no = (TextView)v.findViewById(R.id.tracking_no);
		
		JSONObject obj = languagesWithIDList.get(arg0);
		
		try {
			Log.i(TAG, "arg1: "+arg1);
			Log.i(TAG, "obj: "+obj);
			String statusDp = obj.getString("status");
			String sourceDp = obj.getString("NkVendors");
			
			if (!sourceDp.equals("null")) {
				JSONObject src = new JSONObject(sourceDp);
				sourceDp = src.getString("vendor_name");
			} else {
				sourceDp = "";
			}
			
			String status_dateDP = obj.getString("status_date");
			String[] dt = status_dateDP.split(" ");
			String tracking_numberDP = obj.getString("tracking_number");
			
			String date = dt[0];
			String time = dt[1];
			
			date_dp_id.setText(date);
			time_dp.setText(time);
			status_dp.setText(statusDp);
			source_dp.setText(sourceDp);
			tracking_no.setText(tracking_numberDP);
			v.setTag(obj);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "e>>> "+e.toString());
		}
		
		
		return v;
	}

}
