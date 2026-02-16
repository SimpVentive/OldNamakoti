package com.namakoti.chanting.self;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.android.volley.Request;
import com.namakoti.R;
import com.namakoti.adapters.GodsAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.ErrorBean;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectGodActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener {

    public static final String EXTRA_SELECTED_GOD_THEMEID = "theme id";
    public static final String EXTRA_SELECTED_GOD_THEME_NAME = "theme name";
    public static String EXTRA_CAUSE_KEY = "CAUSE_KEY";
    public static String EXTRA_URL_KEY = "URL_KEY";

    @BindView(R.id.gridview)
    RecyclerView gridView;

    @BindView(R.id.continue_button)
    Button mContinue;

    private View.OnClickListener mListener;
    private GodsAdapter adapter;
    private List<GodNamesBean> mGodList;
    private GodNamesBean mSelectedTheme;
    private String mUrl;
    private String mCauseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_god);
        ButterKnife.bind(this);
        mListener = this;
        mContinue.setOnClickListener(this);
        Cursor mCursorGod = DatabaseHelper.getGodImages(mDatabase);
        mGodList = getGodListFromDb(mCursorGod);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        getBundle();
        toolbar();
        getGodNames();

    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(EXTRA_URL_KEY);
            mCauseId = intent.getStringExtra(EXTRA_CAUSE_KEY);
        }
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(getString(R.string.select_god));
        // add back arrow to toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getGodNames() {
        showProgress(true, SelectGodActivity.this);

        Map<String, String> params = new HashMap<String, String>();
        if (mUrl.equalsIgnoreCase(Constants.CAUSE_GODS_NAMES)) {
            params.put("id", mCauseId);
        } else
            params.put("user_id", mUserIdString);

        VolleyUtil.getInstance().
                volleyStringRequest(this, mUrl, params, ServiceMethod.GET_GOD_NAMES, Request.Method.POST, this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, SelectGodActivity.this);
        if (object instanceof ArrayList<?> list) {
            if (!list.isEmpty() && list.get(0) instanceof GodNamesBean) {
                mGodList = (List<GodNamesBean>) object;
                if (mGodList.size() > 0) {
                    adapter = new GodsAdapter(this, mGodList, mListener, -1);
                    gridView.setAdapter(adapter);
                }
            }
        } else if (object instanceof ErrorBean error) {
            if (error.message != null)
                Utils.showAlertDialog(this, "Error", " " + error.message, null, null, false, true);
        }
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, SelectGodActivity.this);
        handleErrorMessage(object);
    }

    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public void onClick(@NonNull final View view) {
        int id = view.getId();
        if (id == R.id.continue_button) {
            if (mSelectedTheme == null) {
                Utils.showAlertDialog(this, "Error", "Please select god", null, null, false, true);
            } else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECTED_GOD_THEMEID, mSelectedTheme.getTheme_id());
                intent.putExtra(EXTRA_SELECTED_GOD_THEME_NAME, mSelectedTheme.getTheme_name());
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (id == R.id.grid_image) {
            updateRow(view);
        } else if (id == R.id.bgGodRL) {
            updateRow(view);
        } else if (id == R.id.bgRL) {
            updateRow(view);
        }
    }

    private void updateRow(@NonNull View view) {
        int pos = (int) view.getTag(R.id.grid_image);
        mSelectedTheme = mGodList.get(pos);
        adapter.updateList(mGodList, pos);
    }
}
