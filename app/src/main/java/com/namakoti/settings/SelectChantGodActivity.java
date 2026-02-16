package com.namakoti.settings;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.namakoti.R;
import com.namakoti.adapters.SelectedGodListCursorAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.chanting.self.NewChantDetailsActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.listeners.RecyclerItemClickListener;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.VolleyResponseListener;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 2/11/2018.
 */

public class SelectChantGodActivity extends BaseActivity implements View.OnClickListener,VolleyResponseListener {


    public static String BUNDLE_IS_FROM_CAUSE = "is_from_chant_cause";
    @BindView(R.id.god_recycler)
    RecyclerView god_recycler;
    private SelectChantGodActivity mListener;
    private SelectChantGodActivity mActivity;
    private Cursor mCursor;
    private SelectedGodListCursorAdapter adapter;
    public static String SELECTED_NAMAS_BEAN_KEY = "SELECTED_NAMAS";
    private String mNamasTableName;
    private String mFromScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_god_list);
        ButterKnife.bind(this);
        mListener = this;
        mActivity = this;
        getBundle();
        initializeAdapter();
    }

    private void initializeAdapter() {
        RecyclerView.LayoutManager manager=new LinearLayoutManager(mActivity.getApplicationContext());
        god_recycler.setLayoutManager(manager);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(mActivity, R.drawable.divider_gray));
        god_recycler.addItemDecoration(itemDecorator);

        mCursor = DatabaseHelper.getNamasListCursorForGod(mActivity.mDatabase, mUserIdLong, mNamasTableName,mFromScreen);
        if (mCursor != null) {

            if (mCursor.getCount() > 0){
                adapter = new SelectedGodListCursorAdapter(mActivity,mCursor);
                god_recycler.setAdapter(adapter);
                god_recycler.addOnItemTouchListener(
                        new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                setBundleForChantDetails(position);
                            }
                        })
                );
            }else {
                finish();
            }
        }
    }

    private void getBundle() {
        if (getIntent() != null){
            mFromScreen = getIntent().getStringExtra(BUNDLE_IS_FROM_CAUSE);
            if (mFromScreen != null){
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)){
                    mNamasTableName = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
                }
                else if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)){
                    mNamasTableName = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
                }
                else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)){
                    mNamasTableName = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
                }
                else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)){
                    mNamasTableName = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {

    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {

    }

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }
    private void setBundleForChantDetails(int position) {
        if (mCursor != null && mCursor.getCount() > 0){
            if (mCursor.moveToPosition(position)) {
                Intent intent = new Intent(mActivity,ChantingDetailsActivity.class);
                Bundle bundle = new Bundle();
                SaveChantsBean bean = new SaveChantsBean(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_PRINT_USERNAME)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_MUSIC)),
                        mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NO_TOTAL_COUNT)),
                        mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_TOTAL_COUNT)),
                        mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_PRINTED_COUNT)),
                        mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_NAMA_RUNNING_COUNT)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_PRINTING_SERVICE)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME)),
                        mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_THEME_ID)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_SUB_THEME_ID)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_USER_LANGUAGE_ID)));
                bundle.putSerializable(SELECTED_NAMAS_BEAN_KEY, bean);
                intent.putExtras(bundle);
                intent = new Intent();
                intent.putExtra(NewChantDetailsActivity.GKC_SETUPID, mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TABLE_NAMAS.COL_GKC_SETUP_ID)));
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }
}
