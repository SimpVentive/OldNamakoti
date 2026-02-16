package com.namakoti.adapters;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by anusha on 12/31/2017.
 */

public class MyListCursorAdapter extends RecyclerViewCursorAdapter<MyListCursorAdapter.ViewHolder> {
    private static RecyclerViewClickListener itemListener;
    private final BaseActivity mActivity;
    private final ArrayList<GodNamesBean> mGodList;
    private Cursor mCursor;


    public MyListCursorAdapter(Context context, RecyclerViewClickListener itemListener, Cursor cursor, ArrayList<GodNamesBean> godList){
        super(context,cursor);
        this.itemListener = itemListener;
        this.mCursor = cursor;
        this.mActivity = (BaseActivity) context;
        this.mGodList = godList;
    }

    public void updateList(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
        private final View view;
        public TextView god_name;
        public TextView target_count;
        public TextView ongoing_count;
        public TextView date;
        public NetworkImageView god_image;

//        public ImageView god_image;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            god_image = (NetworkImageView) view.findViewById(R.id.nc_god_image);
            god_name = view.findViewById(R.id.nc_god_name);
            target_count = view.findViewById(R.id.nc_target_count);
            ongoing_count = view.findViewById(R.id.nc_on_going_count);
            date = view.findViewById(R.id.nc_date);
//            view.setOnClickListener(this);
        }
        /*@Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }*/
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chanting_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        mCursor = cursor;
        viewHolder.god_name.setText(mActivity.fromHtml(mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME))));
        viewHolder.target_count.setText(mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_NO_TOTAL_COUNT)));
        viewHolder.ongoing_count.setText(mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_NAMA_RUNNING_COUNT)));
        String godName = mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME));

        mActivity.setImageLoader(mGodList,viewHolder.god_image,godName);

    }
}
