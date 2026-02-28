package com.unitol.namakoti;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LanugaugeAdapter extends BaseAdapter{
	
	private List<String> languagesWithIDList;
	private Activity mActivity;

	public LanugaugeAdapter(Activity mActivity, List<String> languagesWithIDList) {
		this.languagesWithIDList = languagesWithIDList;
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return languagesWithIDList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return languagesWithIDList.get(arg0);
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
		
		String lan = languagesWithIDList.get(arg0);
		String[] lang = lan.split("-:-");
		Log.i("Languages=========", lang[1]);
		language.setText(lang[1]);
		v.setTag(lan);
		
		return v;
	}

}
