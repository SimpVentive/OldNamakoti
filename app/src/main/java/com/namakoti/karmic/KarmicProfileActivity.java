package com.namakoti.karmic;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.SyncChantKarmicBean;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.utils.Constants;
import com.namakoti.utils.VolleyUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 9/9/2018.
 */

public class KarmicProfileActivity extends BaseActivity implements View.OnClickListener{

    public static String SYNC_BEAN_KEY = "SYNC_BEAN_KEY";
    @BindView(R.id.userProfileIV)
    CircularNetworkImageView mUserProfileIV;

    @BindView(R.id.nameTV)
    TextView nameTV;

    @BindView(R.id.cityTV)
    TextView cityTV;

    @BindView(R.id.descriptionTv)
    TextView descriptionTv;

    @BindView(R.id.requestRaisedTv)
    TextView requestRaisedTv;

    @BindView(R.id.requestParticipatedTv)
    TextView requestParticipatedTv;
    private SyncChantKarmicBean mSyncChantBean;
    private ImageLoader mIimageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.karmic_profile_details);
        ButterKnife.bind(this);
        mIimageLoader = VolleyUtil.getInstance().imageLoader(this, com.intuit.sdp.R.dimen._65sdp);

        getBundle();
        toolbar();
    }
    private void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.profile));
        // add back arrow to toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mSyncChantBean = (SyncChantKarmicBean) getIntent().getSerializableExtra(SYNC_BEAN_KEY);
                if (mSyncChantBean != null){
                    if (mSyncChantBean.profile != null){
                        nameTV.setText(mSyncChantBean.profile.name);
                        cityTV.setText(mSyncChantBean.profile.city);
                    }
                    if (mSyncChantBean.karmicchantdetails != null){
                        descriptionTv.setText(mSyncChantBean.karmicchantdetails.description);
                        mUserProfileIV.setImageUrl(Constants.GOD_IMAGE_BASE_URL + mSyncChantBean.karmicchantdetails.photo, mIimageLoader);

                    }
                    if (mSyncChantBean.askinghelp != null){
                        requestRaisedTv.setText(""+mSyncChantBean.askinghelp.askinghelping);
                    }
                    if (mSyncChantBean.helpingoters != null){
                        requestParticipatedTv.setText(""+mSyncChantBean.helpingoters.helpingoters);
                    }
                }
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
