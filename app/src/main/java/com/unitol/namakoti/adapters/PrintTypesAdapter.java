package com.unitol.namakoti.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unitol.namakoti.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PrintTypesAdapter extends BaseAdapter{
	
	private static final String TAG = "SalutationAdapter";
	private List<JSONObject> languagesWithIDList;
	private Activity mActivity;

	public PrintTypesAdapter(Activity mActivity, List<JSONObject> languagesWithIDList) {
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
//		Log.i(TAG, "getView: "+arg0);
//		[{"temple_id":"1","temple_name":"Bhadrachalam (Ramalayam)"}],
		
		JSONObject namaObject = languagesWithIDList.get(arg0);
		
		try {
			language.setText(namaObject.getString("prinitng_name"));
			v.setTag(namaObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return v;
	}

}
