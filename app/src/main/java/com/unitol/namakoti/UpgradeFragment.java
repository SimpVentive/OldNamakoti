package com.unitol.namakoti;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.WebViewActivity;

public class UpgradeFragment extends BaseFragment implements OnClickListener {

    protected static final String TAG = "UpgradeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.upgrade, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.upgrade_hdr, 0, "", View.GONE);
        TextView fullNameTv = (TextView) v.findViewById(R.id.username_upgrade);
        TextView amount_upgrade = (TextView) v.findViewById(R.id.amount_upgrade);
        String upgradeuseramt = MyApplication.mPref.getString(getResources().getString(R.string.pref_upgrade_amount_key), "");
        amount_upgrade.setText(upgradeuseramt);

//		MainActivityPhone.startSetUpInAppHelper(mActivity.getApplicationContext());

        String fullName = MyApplication.mPref.getString(getString(R.string.pref_fullname_key), "");
        fullNameTv.setText(fullName);

        Button payButton = (Button) v.findViewById(R.id.pay_id);
        payButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

//				http://118.67.249.179/namakoti/webservice/mobileupgrade?user_id=19&username=srikanthmath0@gmail.com&password=Asdasd11
                String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");
                String pwd = MyApplication.mPref.getString(getString(R.string.pref_pwd_key), "");
                String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");

                Intent i = new Intent(mActivity, WebViewActivity.class);
                String url = WebConstants.UPGRADE_URL + "?user_id=" + userID + "&username=" + logInUserName + "&password=" + pwd;
                Debug.e(TAG, "url:" + url);
                i.putExtra(ConstantsManager.WEBVIEW_URL, url);
                i.putExtra(ConstantsManager.WEBVIEW_HDR_IMG, R.drawable.upgrade_hdr);
                startActivity(i);

				/*ReCharge reCharge = null;
				if (NamaKotiUtils.isNetworkAvailable(mActivity)) {
					reCharge = mActivity.getRechargeinstance();
					Log.i(TAG, "recharge 6 for RECHARGE_CHAT1...");
					reCharge.rechargeNow(BillingConstants.UPGRADE, MainActivityPhone.mHelper);
//					reCharge.rechargeNow(BillingConstants.RECHARGE_CHAT1, mHelper);
//					UpGradeAsync async = new UpGradeAsync(getActivity());
//					async.execute();
				}*/

            }
        });

        return v;
    }


    @Override
    public void onClick(View v) {

    }

}
