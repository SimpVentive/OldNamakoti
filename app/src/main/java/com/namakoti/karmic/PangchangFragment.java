package com.namakoti.karmic;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.PangchangBean;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 9/9/2018.
 */

public class PangchangFragment extends BaseFragment implements View.OnClickListener,VolleyResponseListener{

    @BindView(R.id.dateTv)
    TextView mDateTv;

    @BindView(R.id.monthTv)
    TextView mMonthTv;

    @BindView(R.id.yearTv)
    TextView mYearTv;

    @BindView(R.id.tidiTv)
    TextView mTidiTv;

    @BindView(R.id.sunriceTv)
    TextView mSunriceTv;

    @BindView(R.id.sunStatTimeTv)
    TextView mSunStatTimeTv;

    @BindView(R.id.sunsetTv)
    TextView mSunsetTv;

    @BindView(R.id.sunEndTimeTv)
    TextView mSunEndTimeTv;

    @BindView(R.id.row1LL)
    LinearLayout row1LL;

    @BindView(R.id.row2LL)
    LinearLayout row2LL;

    @BindView(R.id.row3LL)
    LinearLayout row3LL;

    @BindView(R.id.row4LL)
    LinearLayout row4LL;

    @BindView(R.id.row5LL)
    LinearLayout row5LL;

    private TextView mRow1TitleTv;
    private TextView mRow2TitleTv;
    private TextView mRow3TitleTv;
    private TextView mRow4TitleTv;
    private TextView mRow5TitleTv;

    private TextView mRow1ValueTv;
    private TextView mRow2ValueTv;
    private TextView mRow3ValueTv;
    private TextView mRow4ValueTv;
    private TextView mRow5ValueTv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.panchang_layout, container, false);
        ButterKnife.bind(this,view);

        mRow1TitleTv = ((TextView)row1LL.findViewById(R.id.titleTv));
        mRow2TitleTv = (TextView)row2LL.findViewById(R.id.titleTv);
        mRow3TitleTv = (TextView)row3LL.findViewById(R.id.titleTv);
        mRow4TitleTv = (TextView)row4LL.findViewById(R.id.titleTv);
        mRow5TitleTv = (TextView)row5LL.findViewById(R.id.titleTv);

        mRow1TitleTv.setText("aksha");
        mRow2TitleTv.setText("nakshatra");
        mRow3TitleTv.setText("yoga");
        mRow4TitleTv.setText("karana");
        mRow5TitleTv.setText("rashi");

        mRow1ValueTv = (TextView)row1LL.findViewById(R.id.valueTv);
        mRow2ValueTv = (TextView)row2LL.findViewById(R.id.valueTv);
        mRow3ValueTv= (TextView)row3LL.findViewById(R.id.valueTv);
        mRow4ValueTv = (TextView)row4LL.findViewById(R.id.valueTv);
        mRow5ValueTv = (TextView)row5LL.findViewById(R.id.valueTv);

        setListener();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getPangchang();
    }

    private void getPangchang() {
        showProgress(true);

        VolleyUtil.getInstance().
                volleyStringRequest(mBaseActivity, Constants.PANGCHANG_URL, null, ServiceMethod.PANGCHANG, Request.Method.GET, this);
    }

    private void setListener() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void updateUI() {
        if (isAdded()){
            getPangchang();
        }
    }

    @Override
    public void onClick(View view) {
    }
    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        if (object != null && serviceMethod == ServiceMethod.PANGCHANG) {
            PangchangBean bean = (PangchangBean) object;
            updateUIScreen(bean);
        }
    }

    private void updateUIScreen(PangchangBean bean) {
        mDateTv.setText(bean.date);
        mMonthTv.setText(bean.month);
        mYearTv.setText(bean.year);
        mTidiTv.setText(bean.tithi);
        mSunStatTimeTv.setText(bean.sunrise);
        mSunEndTimeTv.setText(bean.sunset);
        mRow1ValueTv.setText(bean.aksha);
        mRow2ValueTv.setText(bean.nakshatra);
        mRow3ValueTv.setText(bean.yoga);
        mRow4ValueTv.setText(bean.karana);
        mRow5ValueTv.setText(bean.rashi);

    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false);
        mBaseActivity.handleErrorMessage(object);
    }

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

}
