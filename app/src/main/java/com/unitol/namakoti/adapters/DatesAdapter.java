package com.unitol.namakoti.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unitol.namakoti.R;

import java.util.List;

public class DatesAdapter extends BaseAdapter{
	
	private List<String> languagesWithIDList;
	private Activity mActivity;

	public DatesAdapter(Activity mActivity, List<String> languagesWithIDList) {
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
		View v = inflater.inflate(R.layout.spinner_row_item, parent, false);
		TextView language = (TextView)v.findViewById(R.id.language);
		
		String lan = languagesWithIDList.get(arg0);
//		String[] lang = lan.split("-:-");
		language.setText(lan);
		v.setTag(lan);
		
		return v;
	}

}
