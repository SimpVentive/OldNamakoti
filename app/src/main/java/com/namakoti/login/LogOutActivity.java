package com.namakoti.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.UserInfoBean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogOutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.cancel_button)
    Button mCancel;

    @BindView(R.id.ok_button)
    Button mDone;

    private LogOutActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        ButterKnife.bind(this);
        mActivity = this;

        mCancel.setOnClickListener(this);
        mDone.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                finish();
                break;
            case R.id.ok_button:
                logout();
                break;
        }
    }


    private void logout() {
        finish();
        UserInfoBean.getInstance().deleteUserInfo(this);
        navigateToLogin();
    }

    protected void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
