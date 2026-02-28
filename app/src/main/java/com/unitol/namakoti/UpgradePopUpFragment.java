package com.unitol.namakoti;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.web.SyncChantAsync;

import org.json.JSONArray;
import org.json.JSONException;

public class UpgradePopUpFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = "UpgradePopUpFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			Log.i(TAG, "container: null");
			return null;
		}
		
		Log.i(TAG, "onCreateView: ");
		View v = inflater.inflate(R.layout.upgrade_popup, container, false);
		mActivity.onFragmentTabChange(View.VISIBLE , R.drawable.upgrade_hdr, 0, "", View.GONE);
		final RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioGroup1);
		RadioButton upgrade = (RadioButton) v.findViewById(R.id.upgrade);
		RadioButton upgrade_and_print = (RadioButton) v.findViewById(R.id.upgrade_and_print);
		RadioButton upgrade_and_chanting = (RadioButton) v.findViewById(R.id.upgrade_and_chanting);
		View view_below_to_upgrade = v.findViewById(R.id.view_below_to_upgrade);
//      rg.setOnCheckedChangeListener(new OnRadioCheckedChangeListener());

		// set the custom dialog components - text, image and button
//		RadioButton upgrade_and_print = (RadioButton) v.findViewById(R.id.upgrade_and_print);
//		RadioButton upgrade_and_chanting = (RadioButton) v.findViewById(R.id.upgrade_and_chanting);
		
		String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
		Log.i(TAG, "userNamasAvailable: "+userNamasAvailable);
		
		try {
			JSONArray array = new JSONArray(userNamasAvailable);
			if (array.length() == 0) { //When no nama set
				upgrade_and_print.setVisibility(View.GONE);
				upgrade_and_chanting.setVisibility(View.GONE);
				view_below_to_upgrade.setVisibility(View.GONE);
				upgrade.setChecked(true);
			} else {
				upgrade.setVisibility(View.GONE);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			Log.e(TAG, "catch: "+e1.toString());
		}

		Button dialogButton = (Button) v.findViewById(R.id.pay_btn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");	
//		    	String pwd = MyApplication.mPref.getString(getString(R.string.pref_pwd_key), "");	
//				String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");
//				String url = "";
				
				if (!mActivity.haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(mActivity);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
				Log.i(TAG, "We have the network connection");
				
				switch (rg.getCheckedRadioButtonId()) {
				case R.id.upgrade:

					mActivity.pushFragments(ConstantsManager.TAB_5, new UpgradeFragment(), false, true, null);
					
					break;
				case R.id.upgrade_and_print:
//					mActivity.pushFragments(ConstantsManager.TAB_5, new UpgradeFragment(), false, true, null);
					/*String imageFile = (String) hand_written_img.getTag();

					url = WebConstants.PRINT_CHANT_URL+"?user_id="+userID+"&username="+logInUserName+"&password="+pwd
					+"&user_namakoti_id="+user_namakoti_id+"&name="+nameOfPerson+"&templename="+temple_id+"&printingcount="+10000
					+"&printtype="+printing_id+"&amount="+amount+"&printcat="+printcat+"&file="+"";*/
					//mActivity.setCurrentTab(MainActivityPhone.SETTINGS_TAB);
			        MyApplication.mEditor.putString(getResources().getString(R.string.hand_written_image_namakoti), "").commit();
					SyncChantAsync syncChantAsync = new SyncChantAsync(mActivity, null, false, true);
					syncChantAsync.execute(null, null, null);
					
					break;
				case R.id.upgrade_and_chanting:
					mActivity.pushFragments(ConstantsManager.TAB_5, new UpgradeFragment(), false, true, null);
//					url = WebConstants.UPGRADE_URL+"?user_id="+userID+"&username="+logInUserName+"&password="+pwd;
					break;

				default:
					break;
				}
//				Log.i(TAG, "url: "+url);

//				Intent i = new Intent(mActivity, WebViewActivity.class);
//				i.putExtra(ConstantsManager.WEBVIEW_URL, url);
//				startActivity(i);
				
			}
		});
		
		return v;
	}

	
	
	@Override
	public void onClick(View v) {
		
	}

}
