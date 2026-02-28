package com.unitol.namakoti.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.unitol.namakoti.R;
import com.unitol.namakoti.model.GodNamesBean;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.WebConstants;

import java.util.List;

/**
 * Created by admin on 12-12-2017.
 */

public class GodsAdapter extends RecyclerView.Adapter<GodsAdapter.GodsViewHolder> {
    private final String TAG = GodsAdapter.class.getSimpleName();
    private final Activity context;
    private List<GodNamesBean> godsList;
    private final View.OnClickListener mListener;
    private int selectedPosition;

    public GodsAdapter(Activity context, List<GodNamesBean> godsList, View.OnClickListener listener, int selectedpos) {
        this.context = context;
        this.godsList = godsList;
        this.mListener = listener;
        this.selectedPosition = selectedpos;
    }

    @Override
    public int getItemCount() {
        return godsList.size();
    }

    @Override
    public GodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_god_list_item_layout, parent, false);
        return new GodsViewHolder(itemView);
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull GodsViewHolder holder, int position) {
        GodNamesBean god = godsList.get(position);
        Log.e(TAG, "position: " + position);
        if (selectedPosition == position) {
            holder.mGodName.setBackgroundResource(R.drawable.selector_4_buttons);
            holder.mGodName.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.mGodName.setBackgroundResource(R.drawable.selector_4_unselect_buttons);
            holder.mGodName.setTextColor(context.getResources().getColor(R.color.white_unselected));
        }

        holder.mGodName.setText(god.getTheme_name());
        holder.mGodImage.setTag(R.id.grid_image, position);
        holder.mGodName.setTag(R.id.grid_image, position);

        holder.mGodImage.setOnClickListener(mListener);
        holder.mGodName.setOnClickListener(mListener);

        holder.mGodImage.setImageBitmap(null);
        holder.mGodImage.setImageResource(R.drawable.ic_defaultprofile);

        Debug.e(TAG, "url:" + WebConstants.GOD_IMAGE_BASE_URL + god.getPhoto());

        if (!TextUtils.isEmpty(god.getPhoto())) {
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(WebConstants.GOD_IMAGE_BASE_URL + god.getPhoto())
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
        Log.e(TAG, "selectedPosition: " + selectedPosition);
        this.notifyDataSetChanged();
    }

    public class GodsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mGodImage;
        public TextView mGodName;

        public GodsViewHolder(View itemView) {
            super(itemView);
            mGodImage = itemView.findViewById(R.id.grid_image);
            mGodName = itemView.findViewById(R.id.grid_text);
        }
    }

}
