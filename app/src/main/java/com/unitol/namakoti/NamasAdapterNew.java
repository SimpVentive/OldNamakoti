package com.unitol.namakoti;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NamasAdapterNew extends BaseAdapter{
	
	private static final String TAG = "NamasAdapter";
	private List<JSONObject> languagesWithIDList;
	private Activity mActivity;

	public NamasAdapterNew(Activity mActivity, List<JSONObject> languagesWithIDList) {
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
//		Log.i(TAG, "getView: "+arg0);
		
		JSONObject namaObject = languagesWithIDList.get(arg0);
		
		try {
			String namaOfGod = namaObject.getString("sub_theme_name");
			namaOfGod = ""+Html.fromHtml(namaOfGod);
			language.setText(namaOfGod);
			
			v.setTag(namaObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return v;
	}

}
