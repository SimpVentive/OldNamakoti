package com.unitol.namakoti.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SetNamaFragment;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.web.SyncChantAsync;

import org.json.JSONArray;
import org.json.JSONException;

public class HomeFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = "HomeFragment";
    LinearLayout profile, setnama, change_pwd, print_details, accountdetails, logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.settings_new_look, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.settings_hdr, 0, "", View.INVISIBLE);

        String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
        boolean isFromSetNama = MyApplication.mPref.getBoolean(getString(R.string.pref_create_nama_key), false);
        boolean isFromInitializingTabs = MyApplication.mPref.getBoolean(getString(R.string.pref_is_from_initialise_tabs_key), false);
        Log.i(TAG, "isFromSetNama: " + isFromSetNama);
//		Log.i(TAG, "userNamasAvailable: onCreateView "+userNamasAvailable);

        try {
            if (!TextUtils.isEmpty(userNamasAvailable)) {
                JSONArray array = new JSONArray(userNamasAvailable);
                if (array.length() == 0 && !isFromSetNama && isFromInitializingTabs) { //When no nama set
                    mActivity.pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), true, true, null);
                    MyApplication.mEditor.putBoolean(getString(R.string.pref_is_from_initialise_tabs_key), false).commit();
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        MyApplication.mEditor.putBoolean(getString(R.string.pref_create_nama_key), false).commit();

        profile = (LinearLayout) v.findViewById(R.id.profile_ll);
        setnama = (LinearLayout) v.findViewById(R.id.setnama_ll);
        change_pwd = (LinearLayout) v.findViewById(R.id.change_pwd_ll);
        print_details = (LinearLayout) v.findViewById(R.id.print_details_ll);
        accountdetails = (LinearLayout) v.findViewById(R.id.accountdetails_ll);
        logout = (LinearLayout) v.findViewById(R.id.logout_ll);

        profile.setOnClickListener(this);
        setnama.setOnClickListener(this);
        change_pwd.setOnClickListener(this);
        print_details.setOnClickListener(this);
        accountdetails.setOnClickListener(this);
        logout.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent in = null;
        switch (v.getId()) {
            case R.id.back_image:
                break;
            case R.id.home_image:
                break;
			/*case R.id.edit_image:
			in = new Intent(mActivity, EnternamaActivity.class);
			startActivity(in);
			// finish();
			break;
		case R.id.setnama_button:
			mActivity.pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), false, true, null);
			// finish();
			break;
		case R.id.enternama_button:
			mActivity.pushFragments(ConstantsManager.TAB_3, new EnternamaFragment(), false, true, null);
			// mActivity.onFragmentTabChange(View.VISIBLE ,
			// getResources().getString(R.string.empty_description), "",
			// View.GONE);
			break;
		case R.id.publishnama_button:
			mActivity.pushFragments(ConstantsManager.TAB_3, new PublishnamaFragment(), false, true, null);
			// mActivity.onFragmentTabChange(View.VISIBLE ,
			// getResources().getString(R.string.empty_description), "",
			// View.GONE);
			// in = new Intent(mActivity,PublishnamaActivity.class);
			// startActivity(in);
			// finish();
			break;
		case R.id.profile_button:
			mActivity.pushFragments(ConstantsManager.TAB_3, new ViewProfileFragment(), false, true, null);
			// mActivity.onFragmentTabChange(View.VISIBLE ,
			// getResources().getString(R.string.empty_description), "",
			// View.GONE);
			// mActivity.onFragmentTabChange(View.VISIBLE ,
			// R.drawable.view_profile_hdr, 0, View.GONE);
			break;
		case R.id.accountdetailes_button:
			mActivity.pushFragments(ConstantsManager.TAB_3, new PaymentFragment(), false, true, null);
			// mActivity.onFragmentTabChange(View.VISIBLE ,
			// getResources().getString(R.string.empty_description), "",
			// View.GONE);
			break;
		case R.id.logout_button:
			MyApplication.mEditor.putBoolean(getResources().getString(R.string.pref_login_flag_key, ""),
					false).commit();
			Intent i = new Intent(mActivity, MainActivity.class);
			startActivity(i);
			mActivity.finish();
			break;*/

            case R.id.profile_ll:
                mActivity.pushFragments(ConstantsManager.TAB_3, new ViewProfileFragment(), false, true, null);
                break;
            case R.id.setnama_ll:

                if (!mActivity.haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(mActivity);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                mActivity.pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), false, true, null);
                break;
            case R.id.change_pwd_ll:
                NamaKotiUtils.openWebUrlLink(requireActivity(),"https://newstore.namakoti.com/");
             /*   mActivity.pushFragments(ConstantsManager.TAB_3,
                        new ChangePasswordFrag(), false, true, null);*/
                break;
            case R.id.print_details_ll:

                String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
//			Log.i(TAG, "userNamasAvailable: "+userNamasAvailable);

                try {
                    JSONArray array = new JSONArray(userNamasAvailable);
                    if (array.length() == 0) { //When no nama set
                        NamaKotiUtils.showNoNamaDialog(mActivity, getResources().getString(R.string.no_nama),
                                "No nama has been set to Print & Delivery", "Ok", "Cancel");
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                if (!mActivity.haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(mActivity);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                MyApplication.mEditor.putString(getResources().getString(R.string.hand_written_image_namakoti), "").commit();
                SyncChantAsync syncChantAsync = new SyncChantAsync(mActivity, null, false, false);
                syncChantAsync.execute(null, null, null);

//			mActivity.pushFragments(ConstantsManager.TAB_3, new PublishnamaFragment(), false, true, null);
                break;
            case R.id.accountdetails_ll:

                if (!mActivity.haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(mActivity);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                mActivity.pushFragments(ConstantsManager.TAB_3, new PaymentFragment(), false, true, null);
                break;
            case R.id.logout_ll:
			/*MyApplication.mEditor.putBoolean(
					getResources().getString(R.string.pref_login_flag_key, ""),
					false).commit();
			Intent intent = new Intent(mActivity, MainActivity.class);
			startActivity(intent);
			mActivity.finish();*/

                NamaKotiUtils.showSignOutDialog(mActivity, getResources().getString(R.string.signout), getResources().getString(R.string.signout_dlg_msg), -1);
                break;

            default:
                break;
        }
    }
}