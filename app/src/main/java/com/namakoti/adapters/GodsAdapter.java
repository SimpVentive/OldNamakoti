package com.namakoti.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.login.LoginActivity;
import com.namakoti.utils.Constants;

import java.util.List;

/**
 * Created by admin on 12-12-2017.
 */

public class GodsAdapter extends RecyclerView.Adapter<GodsAdapter.GodsViewHolder> {
    private String TAG = GodsAdapter.class.getSimpleName();
    private final BaseActivity context;
    private List<GodNamesBean> godsList;
    private final View.OnClickListener mListener;
    private int selectedPosition;

    public GodsAdapter(Context context, List<GodNamesBean> godsList, View.OnClickListener listener, int selectedpos) {
        this.context = (BaseActivity) context;
        this.godsList = godsList;
        this.mListener = listener;
        this.selectedPosition = selectedpos;
    }

    @Override
    public int getItemCount() {
        return godsList.size();
    }

    @Override
    public GodsAdapter.GodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_god_list_item_layout, parent, false);
        return new GodsAdapter.GodsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GodsAdapter.GodsViewHolder holder, int position) {
        GodNamesBean god = godsList.get(position);
        Log.e(TAG, "god getPhoto: " + (Constants.GOD_IMAGE_BASE_URL + god.getPhoto()));

        if (selectedPosition == position) {
            holder.mBackgroundRL.setBackgroundResource(R.drawable.bg_orange);
            holder.mBackgroundGodRL.setBackgroundResource(R.drawable.selectgod);
            holder.mGodName.setBackgroundResource(R.drawable.unselect_bg_text);
        } else {
            holder.mBackgroundRL.setBackgroundResource(R.drawable.bg_white);
            holder.mBackgroundGodRL.setBackgroundResource(R.drawable.unselectgod);
            holder.mGodName.setBackgroundResource(R.drawable.selected_bg_text);
        }

        holder.mGodName.setText(god.getTheme_name());
        holder.mGodImage.setTag(R.id.grid_image, position);
        holder.mBackgroundRL.setTag(R.id.grid_image, position);
        holder.mBackgroundGodRL.setTag(R.id.grid_image, position);

        holder.mGodImage.setOnClickListener(mListener);
        holder.mBackgroundRL.setOnClickListener(mListener);
        holder.mBackgroundGodRL.setOnClickListener(mListener);

        holder.mGodImage.setImageBitmap(null);
        holder.mGodImage.setImageResource(R.drawable.ic_defaultprofile);
        if (!TextUtils.isEmpty(god.getPhoto())) {
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(Constants.GOD_IMAGE_BASE_URL + god.getPhoto())
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.mGodImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }
                    });
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<GodNamesBean> list, int pos) {
        this.godsList = list;
        this.selectedPosition = pos;
        this.notifyDataSetChanged();
    }

    public class GodsViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mBackgroundRL;
        public RelativeLayout mBackgroundGodRL;
        public AppCompatImageView mGodImage;
        public TextView mGodName;

        public GodsViewHolder(View itemView) {
            super(itemView);
            mBackgroundRL = itemView.findViewById(R.id.bgRL);
            mBackgroundGodRL = itemView.findViewById(R.id.bgGodRL);
            mGodImage = itemView.findViewById(R.id.grid_image);
            mGodName = itemView.findViewById(R.id.grid_text);
        }
    }

}
