package com.namakoti.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.karmic.BeingHelpedFragment;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.karmic.beingHelped.SelfDetailsActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.Utils;

import java.util.ArrayList;

/**
 * Created by admin on 09-12-2017.
 */
public class KarmicSelfAdapter extends RecyclerView.Adapter<KarmicSelfAdapter.MyViewHolder> {
    private ArrayList<KarmicSelfBean.SelfGkcDetails> mSelfList;
    private final BaseActivity mContext;
    private final BeingHelpedFragment mInstance;
    private MyViewHolder mHolder;

    public KarmicSelfAdapter(ArrayList<KarmicSelfBean.SelfGkcDetails> selfgkc, BaseActivity mBaseActivity, BeingHelpedFragment beingHelpedFragment) {
        this.mSelfList = selfgkc;
        this.mContext = mBaseActivity;
        this.mInstance = beingHelpedFragment;

    }

    public void updateList(ArrayList<KarmicSelfBean.SelfGkcDetails> selfgkc) {
        this.mSelfList = selfgkc;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mDescriptionLL;
        private RelativeLayout mGodNameRL;
        private RelativeLayout mParticipantRL;
        public TextView mDateTV;
        public CircularNetworkImageView mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mRightArrowIV;
        public TextView mGodNameTv, mGodCountTv, mDescriptionTv, mAddCountIv;

        public MyViewHolder(View itemView) {
            super(itemView);

            mGodNameRL = itemView.findViewById(R.id.godNameRL);
            mDateTV = itemView.findViewById(R.id.dateTv);
            mGodNameTv = itemView.findViewById(R.id.godNameTv);
            mGodCountTv = itemView.findViewById(R.id.godCountTv);
            mDescriptionTv = itemView.findViewById(R.id.descriptionTv);
            mPerson1Iv = itemView.findViewById(R.id.person1Iv);
            mPerson2Iv = itemView.findViewById(R.id.person2Iv);
            mPerson3Iv = itemView.findViewById(R.id.person3Iv);
            mPerson4Iv = itemView.findViewById(R.id.person4Iv);
            mPerson5Iv = itemView.findViewById(R.id.person5Iv);
            mAddCountIv = itemView.findViewById(R.id.addCountIv);
            mRightArrowIV = itemView.findViewById(R.id.rightArrowIV);
            mParticipantRL = itemView.findViewById(R.id.participantRL);
            mDescriptionLL = itemView.findViewById(R.id.descriptionLL);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.karmic_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mHolder = holder;
        final KarmicSelfBean.SelfGkcDetails details = mSelfList.get(position);

        holder.mGodNameTv.setText(mContext.fromHtml(details.sub_theme_name));
        holder.mDateTV.setText("" + details.created_date);
        holder.mGodCountTv.setText(details.no_of_chants);

        holder.mParticipantRL.setTag(R.id.acceptLL, details);
        holder.mParticipantRL.setTag(R.id.key_touch_layout, details.gkc_setup_id);
        holder.mDescriptionLL.setTag(R.id.acceptBtn, details);

        if (details.description != null)
            holder.mDescriptionTv.setText("" + details.description);
        else
            holder.mDescriptionTv.setText("");
        Utils.setUserImageVisibility(mContext, details.participants, holder.mParticipantRL, holder.mPerson1Iv, holder.mPerson2Iv, holder.mPerson3Iv, holder.mPerson4Iv, holder.mPerson5Iv, holder.mAddCountIv);


        holder.mParticipantRL.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                KarmicSelfBean.SelfGkcDetails bean = (KarmicSelfBean.SelfGkcDetails) view.getTag(R.id.acceptLL);
                Intent intent = Utils.getParticipantsListIntent(mContext, bean.start_datetime, bean.participant_add,
                        bean.end_datetime, bean.gkc_setup_id, bean.created_by);
                mInstance.startActivity(intent);

            }
        });

        holder.mDescriptionLL.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.ADD_PARTICIPANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                KarmicSelfBean.SelfGkcDetails bean = (KarmicSelfBean.SelfGkcDetails) view.getTag(R.id.acceptBtn);
                Intent intent = new Intent(mContext, SelfDetailsActivity.class);
                intent.putExtra(SelfDetailsActivity.SELF_BEAN_KEY, bean);
                intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, bean.created_by);

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSelfList.size();
    }
}
