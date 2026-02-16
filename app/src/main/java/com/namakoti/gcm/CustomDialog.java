package com.namakoti.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;

import butterknife.BindView;

public class CustomDialog extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.viewTv)
    TextView mViewTv;
    @BindView(R.id.dismissTv)
    TextView mDismissTv;
    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_push_dialog);

        getBundle();
        mViewTv.setOnClickListener(this);
        mDismissTv.setOnClickListener(this);
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null){
            String msg = intent.getStringExtra("message");
            String title = intent.getStringExtra("title");
            mMessage.setText(msg);
            mTitle.setText(title);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dismissTv:
                finish();
                break;
            case R.id.viewTv:
                finish();
                break;
        }
    }

}
