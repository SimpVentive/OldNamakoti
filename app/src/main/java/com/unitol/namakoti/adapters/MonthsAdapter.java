package com.unitol.namakoti.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unitol.namakoti.R;

public class MonthsAdapter extends BaseAdapter{
	
	private static final String TAG = "MonthsAdapter";
	private String[] languagesWithIDList;
	private Activity mActivity;

	public MonthsAdapter(Activity mActivity, String[] arrayMonths) {
		this.languagesWithIDList = arrayMonths;
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return languagesWithIDList.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return languagesWithIDList[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View v = inflater.inflate(R.layout.spinner_row_item, parent, false);
		TextView language = (TextView)v.findViewById(R.id.language);
		
		String lan = languagesWithIDList[arg0];
//		Log.i(TAG, "lan: "+lan);
		String[] lang = lan.split("-:-");
		language.setText(lang[0]);
		v.setTag(lan);
		
		return v;
	}

}
