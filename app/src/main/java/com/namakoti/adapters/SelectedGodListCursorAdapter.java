package com.namakoti.adapters;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.database.DatabaseHelper;

/**
 * Created by anusha on 12/31/2017.
 */

public class SelectedGodListCursorAdapter extends RecyclerViewCursorAdapter<SelectedGodListCursorAdapter.ViewHolder> {
    private final BaseActivity mActivity;
    private Cursor mCursor;


    public SelectedGodListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        this.mCursor = cursor;
        this.mActivity = (BaseActivity) context;
    }

    public void updateList(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View view;
        public TextView godNameTv;

        public ImageView god_image;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            godNameTv = view.findViewById(R.id.godNameTv);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        mCursor = cursor;
        viewHolder.godNameTv.setText(mActivity.fromHtml(mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_SUB_THEME_NAME))));
//        Drawable godDrawable = mActivity.getGodImage(mCursor.getString(mCursor.getColumnIndex( DatabaseHelper.TABLE_NAMAS.COL_THEME_NAME)),mActivity);
//        viewHolder.god_image.setImageDrawable(godDrawable);
    }
}
