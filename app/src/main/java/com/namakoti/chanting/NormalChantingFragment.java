package com.namakoti.chanting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.adapters.MyListCursorAdapter;
import com.namakoti.adapters.RecyclerViewClickListener;
import com.namakoti.base.BaseFragment;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.chanting.self.ChantingDetailsActivity;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.listeners.RecyclerItemClickListener;
import com.namakoti.utils.Constants;
import com.namakoti.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NormalChantingFragment} factory method to
 * create an instance of this fragment.
 */
public class NormalChantingFragment extends BaseFragment implements RecyclerViewClickListener{

    private static final int CHANT_DETAILS_CODE = 101;

    private Cursor mCursor;
    private MyListCursorAdapter adapter;

    @BindView(R.id.no_details)
    TextView mNoResultsTv;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ArrayList<GodNamesBean> mGodList;

    public NormalChantingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActivityCompat.invalidateOptionsMenu(getActivity());

        View view = inflater.inflate(R.layout.fragment_normal_chanting, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(manager);

        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase,mBaseActivity.mUserIdLong,DatabaseHelper.NORMAL_NAMAS_TABLE_NAME);
        Cursor mCursorGod = DatabaseHelper.getGodImages(mBaseActivity.mDatabase);

        mGodList = mBaseActivity.getGodListFromDb(mCursorGod);

        if (mCursor != null) {
            if (mCursor.getCount() != 0 ){
                adapter = new MyListCursorAdapter(getActivity(),this,mCursor, mGodList);
                mRecyclerView.setAdapter(adapter);
            }
            setRecyclerViewVisibility(mCursor.getCount());
        }
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        setBundleForChantDetails(position);
                    }
                })
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {
        setBundleForChantDetails(position);
    }

    private void setBundleForChantDetails(int position) {
        if (mCursor.moveToPosition(position)) {
            Intent intent = Utils.setBundleForChantDetails(mBaseActivity, mCursor, Constants.KEY_SELF_CHANT, true);
            startActivityForResult(intent, CHANT_DETAILS_CODE);
        }
    }

    public void updateData() {
        mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase,mBaseActivity.mUserIdLong,DatabaseHelper.NORMAL_NAMAS_TABLE_NAME);
        if (mCursor != null) {
            if (mCursor.getCount() != 0){
                MyListCursorAdapter adapter = new MyListCursorAdapter(getActivity(),this, mCursor, mGodList);
                mRecyclerView.setAdapter(adapter);
            }
            setRecyclerViewVisibility(mCursor.getCount());
        }
    }

    private void setRecyclerViewVisibility(int count) {
        if (count != 0){
            mNoResultsTv.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mNoResultsTv.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_self_chanting_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_chants:
                startActivityForResult(new Intent(mBaseActivity, CreateNormalChantingActivity.class), CREATE_CHANT_RESULT_CODE);break;
            default:
                break;
        }
        return true;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANT_DETAILS_CODE){
            if (resultCode == RESULT_OK){
                boolean isSyncUpted = data.getBooleanExtra(ChantingDetailsActivity.EXTRA_IS_SYNC,false);
                if (isSyncUpted){
                    mCursor = DatabaseHelper.getNamasListCursor(mBaseActivity.mDatabase,mBaseActivity.mUserIdLong,DatabaseHelper.NORMAL_NAMAS_TABLE_NAME);
                    if (mCursor != null) {
//                        if (adapter != null){
//                            adapter.updateList(mCursor);
//                        }
                        MyListCursorAdapter adapter = new MyListCursorAdapter(getActivity(),this, mCursor, mGodList);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        }
    }
}
