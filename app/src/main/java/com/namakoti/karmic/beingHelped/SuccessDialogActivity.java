package com.namakoti.karmic.beingHelped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class SuccessDialogActivity extends BaseActivity implements View.OnClickListener {


    public static final String TIME_KEY = "TIME_KEY";
    @BindView(R.id.dateTV)
    TextView mDateTV;

    @BindView(R.id.doneTV)
    TextView mDoneTV;

    private SuccessDialogActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_dialog);
        ButterKnife.bind(this);
        mActivity = this;
        getBundle();
        mDoneTV.setOnClickListener(this);

    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            String time = intent.getStringExtra(TIME_KEY);
//            String sDate = CalendarUtils.getDateFormattedString(Constants.DATE_TIME_FORMAT, time, "hh:mm a, dd MMMM yyyy ");
            mDateTV.setText(time);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doneTV:
                Intent intent = new Intent();
                intent.setAction(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION);
                sendBroadcast(intent);
                finish();
                break;
        }
    }
}
