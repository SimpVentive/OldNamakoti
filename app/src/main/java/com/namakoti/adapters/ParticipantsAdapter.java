package com.namakoti.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.ParticipantsBean;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.VolleyUtil;

import java.util.ArrayList;

/**
 * Created by admin on 03-22-2017.
 */
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder> {
    private final ImageLoader mIimageLoader;
    private ArrayList<ParticipantsBean.ParticipantsItemBean> mList;
    private final BaseActivity mContext;
    private final ParticipantsListActivity mInstance;

    public ParticipantsAdapter(ArrayList<ParticipantsBean.ParticipantsItemBean> list, BaseActivity mBaseActivity, ParticipantsListActivity instance) {
        this.mList = list;
        this.mContext = mBaseActivity;
        this.mInstance = instance;
        mIimageLoader = VolleyUtil.getInstance().imageLoader(mContext, com.intuit.sdp.R.dimen._40sdp);
    }

    public void updateList(ArrayList<ParticipantsBean.ParticipantsItemBean> selfgkc) {
        this.mList = selfgkc;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout mRowRL;
        public TextView mParticipantNameTV;
        public TextView mAttendingTV;
        public NetworkImageView mPersonIV;
        public MyViewHolder(View itemView) {
            super(itemView);
            mParticipantNameTV =itemView.findViewById(R.id.participantNameTV);
            mAttendingTV =itemView.findViewById(R.id.attendingTV);
            mPersonIV =itemView.findViewById(R.id.personIV);
            mRowRL =itemView.findViewById(R.id.rowRL);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participants_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ParticipantsBean.ParticipantsItemBean details = mList.get(position);

        holder.mParticipantNameTV.setText(details.name);
        holder.mAttendingTV.setText(""+details.status);
        holder.mPersonIV.setImageUrl(Constants.GOD_IMAGE_BASE_URL+ details.userphoto,mIimageLoader);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
