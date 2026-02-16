package com.namakoti.karmic.beingHelped;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.adapters.ContactsAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.ContactsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anusha on 3/22/2018.
 */

public class ContactsListActivity extends BaseActivity {


    public static final String LIST_KEY = "LIST_KEY";
    public static final String SELECTED_CONTACT_LIST = "SELECTED_CONTACT_LIST_KEY";

    @BindView(R.id.no_details)
    TextView mNoResultsTv;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String mSetupId;
    private int mPage = 1;
    private ContactsListActivity mActivity;
    private ContactsAdapter mAdapter;
    private ArrayList<ContactsBean> mContactsList;
    private String mDaysLeft;
    private ArrayList<ContactsBean> mSelectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participants_layout);
        ButterKnife.bind(this);
        mActivity = this;
        setToolbarWithBack(getString(R.string.participants));
        getBundle();
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mContactsList = (ArrayList<ContactsBean>) intent.getSerializableExtra(LIST_KEY);
            updateUI();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sendIntent();
                break;
        }
        return false;
    }

    private void sendIntent() {
        mSelectedContacts = UserInfoBean.getInstance().getContacts(mActivity);
        Intent intent = new Intent();
        intent.putExtra(SELECTED_CONTACT_LIST, mSelectedContacts);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendIntent();
    }

    private void updateUI() {
        if (mContactsList != null && mContactsList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new ContactsAdapter(mContactsList, mActivity);
                DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
                itemDecorator.setDrawable(ContextCompat.getDrawable(mActivity, R.drawable.divider_gray));
                mRecyclerView.addItemDecoration(itemDecorator);

                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setNestedScrollingEnabled(false);
            } else {
                mAdapter.updateList(mContactsList);
            }
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                        }
                    })
            );
        }
    }

}
