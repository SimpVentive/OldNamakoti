package com.namakoti.settings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 4/8/2018.
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.paymentTV)
    TextView mPaymentTV;

    @BindView(R.id.printTV)
    TextView mPrintTV;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.settings, container, false);
        ButterKnife.bind(this,view);

        mPaymentTV.setOnClickListener(this);
        mPrintTV.setOnClickListener(this);

        return view;
    }

    private void setToolBar(String title) {
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        ((BaseActivity)getActivity()).setSupportActionBar(toolbar);
        setToolbarTitle(title);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setToolBar(getString(R.string.settings));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.paymentTV:
//                addFragment(R.id.settingsLL, new PaymentFragment(),true);
                break;

            case R.id.printTV:
                break;
        }
    }
}
