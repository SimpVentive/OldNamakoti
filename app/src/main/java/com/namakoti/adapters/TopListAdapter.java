package com.namakoti.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.SyncChantKarmicBean;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 09-12-2017.
 */
public class TopListAdapter extends RecyclerView.Adapter<TopListAdapter.MyViewHolder> {
    private ArrayList<SyncChantKarmicBean.TopListDetailsBean> mTopList;
    private final BaseActivity mContext;
    private MyViewHolder mHolder;


    public TopListAdapter(ArrayList<SyncChantKarmicBean.TopListDetailsBean> toplist, BaseActivity activity) {
        mTopList = toplist;
        mContext = activity;
    }

    public void updateList(ArrayList<SyncChantKarmicBean.TopListDetailsBean> toplist) {
        this.mTopList = toplist;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar mPrintProgressBar;
        public TextView mNameTv;
        public TextView mPrintRunningCountTv, mPrintTotalCountTV;
        public MyViewHolder(View itemView) {
            super(itemView);

            mNameTv =itemView.findViewById(R.id.nameTV);
            mPrintProgressBar =itemView.findViewById(R.id.printProgressBar);
            mPrintRunningCountTv =itemView.findViewById(R.id.printRunningCountTv);
            mPrintTotalCountTV =itemView.findViewById(R.id.printTotalCountTV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mHolder = holder;
        final SyncChantKarmicBean.TopListDetailsBean details = mTopList.get(position);

        holder.mNameTv.setText(details.name);
        setProgressValue(holder.mPrintProgressBar,holder.mPrintRunningCountTv, holder.mPrintTotalCountTV,details.orginal_chants_count,details.chants_count_will_do);

    }
    private void setProgressValue(@NonNull ProgressBar progressBar, @NonNull TextView runningCountTv, TextView totalCountTV, int runningCount, int totalCount) {
        runningCountTv.setTextColor(ResourcesCompat.getColor(mContext.getResources(), android.R.color.white, null));
        progressBar.setMax(totalCount);
        if (runningCount == 0 || runningCount == totalCount) {
            //make the progress bar visible
            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setMax(totalCount);
            progressBar.setSecondaryProgress(totalCount);
            if (runningCount == 0){
                runningCountTv.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.chant_gray, null));
                progressBar.setProgress(0);
            }else if (runningCount == totalCount){
                progressBar.setProgress(totalCount);
            }

        }else if ( runningCount< progressBar.getMax() ) {
            progressBar.setSecondaryProgress(runningCount);
        }else {
            progressBar.setProgress(0);
            progressBar.setSecondaryProgress(totalCount);
        }
        LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        backgroundDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.light_pink), PorterDuff.Mode.SRC_IN);
        progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        runningCountTv.setText(""+runningCount);
        totalCountTV.setText(""+totalCount);
    }

    @Override
    public int getItemCount() {
        return mTopList.size();
    }
}
