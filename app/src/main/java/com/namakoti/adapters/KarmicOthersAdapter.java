package com.namakoti.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.components.CircularNetworkImageView;
import com.namakoti.karmic.HelpingOthersFragment;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.karmic.helpingOthers.OthersDetailsActivity;
import com.namakoti.karmic.helpingOthers.RequestAcceptActivity;
import com.namakoti.utils.CalendarUtils;
import com.namakoti.utils.Constants;
import com.namakoti.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 09-12-2017.
 */
public class KarmicOthersAdapter extends RecyclerView.Adapter<KarmicOthersAdapter.MyViewHolder> {
    private final BaseActivity mContext;
    private final HelpingOthersFragment mInstance;
    private ArrayList<KarmicOthersBean.RequestGkcDetails> mOthersList;

    public KarmicOthersAdapter(ArrayList<KarmicOthersBean.RequestGkcDetails> requestgkc, BaseActivity activity, HelpingOthersFragment fragment) {
        this.mOthersList = requestgkc;
        this.mContext = activity;
        this.mInstance = fragment;
    }

    public void updateList(ArrayList<KarmicOthersBean.RequestGkcDetails> selfgkc) {
        this.mOthersList = selfgkc;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.karmic_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final KarmicOthersBean.RequestGkcDetails details = mOthersList.get(position);

        holder.mGodNameTv.setText(mContext.fromHtml(details.sub_theme_name));
        holder.mDateTV.setText("" + details.created_date);
        holder.mGodCountTv.setText(details.no_of_chants);


        holder.mParticipantRL.setTag(R.id.acceptBtn, details);
        holder.mDescriptionLL.setTag(R.id.acceptBtn, details);
        holder.mDeclineBtn.setTag(R.id.acceptBtn, details);
        holder.mAcceptBtn.setTag(R.id.acceptBtn, details);
        final Date now = new Date();
        if (details.reqstatus == null || TextUtils.isEmpty(details.reqstatus)) {
            holder.mAcceptLL.setVisibility(View.VISIBLE);
            Date endDate = CalendarUtils.ConvertStingToDate(details.end_datetime, Constants.DATE_TIME_FORMAT);
            //enddate < current date then gray out buttons
            if (now.after(endDate) || now.equals(endDate)) {
                setEnable(false, 0.3f, holder.mDeclineBtn, holder.mAcceptBtn);
            } else {
                setEnable(true, 1f, holder.mDeclineBtn, holder.mAcceptBtn);
            }
        } else
            holder.mAcceptLL.setVisibility(View.GONE);

        if (details.description != null)
            holder.mDescriptionTv.setText(details.description);
        else
            holder.mDescriptionTv.setText("");

        Utils.setUserImageVisibility(mContext, details.participants, holder.mParticipantRL, holder.mPerson1Iv,
                holder.mPerson2Iv, holder.mPerson3Iv, holder.mPerson4Iv, holder.mPerson5Iv, holder.mAddCountIv);
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
                KarmicOthersBean.RequestGkcDetails bean = (KarmicOthersBean.RequestGkcDetails) view.getTag(R.id.acceptBtn);
                Intent intent = Utils.getParticipantsListIntent(mContext, bean.start_datetime, bean.participant_add,
                        bean.end_datetime, bean.gkc_setup_id, bean.created_by);
//                mInstance.startActivityForResult(intent, mInstance.ADD_PARTICIPANT_RESULT_CODE);
                mInstance.startActivity(intent);
            }
        });

        holder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callRequestDialog(view, "accept");
            }
        });

        holder.mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                    } else {
                        mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callRequestDialog(view, "decline");
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
                KarmicOthersBean.RequestGkcDetails bean = (KarmicOthersBean.RequestGkcDetails) view.getTag(R.id.acceptBtn);
                Date selectedDate = CalendarUtils.ConvertStingtoDate(bean.end_datetime, Constants.DATE_TIME_FORMAT);

                if (bean.status.equalsIgnoreCase(Constants.GKCD)) {
                    Toast.makeText(mContext, "You cannot access to the details page", Toast.LENGTH_LONG).show();
                }
//                else if (bean.participant_id != null && !TextUtils.isEmpty(bean.participant_id))
//                    mInstance.setBundleForChantDetails(bean);
                else {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                        } else {
                            mContext.registerReceiver(mInstance.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(mContext, OthersDetailsActivity.class);
                    intent.putExtra(OthersDetailsActivity.OTHERS_BEAN_KEY, bean);
                    intent.putExtra(OthersDetailsActivity.PAGE_COUNT_KEY, mInstance.mPage);
                    intent.putExtra(OthersDetailsActivity.IS_FROM_PUSH_KEY, false);
                    intent.putExtra(ParticipantsListActivity.CREATED_BY_KEY, bean.created_by);
                    mInstance.startActivity(intent);
                }

            }
        });
    }

    /**
     * set buttons disable(enddate crossed in current date) and enable
     *
     * @param enable
     * @param value
     * @param mDeclineBtn
     * @param mAcceptBtn
     */
    private void setEnable(boolean enable, float value, Button mDeclineBtn, Button mAcceptBtn) {
        mAcceptBtn.setAlpha(value);
        mDeclineBtn.setAlpha(value);
        mAcceptBtn.setEnabled(enable);
        mDeclineBtn.setEnabled(enable);
    }

    private void callRequestDialog(View view, String type) {
        KarmicOthersBean.RequestGkcDetails bean = (KarmicOthersBean.RequestGkcDetails) view.getTag(R.id.acceptBtn);
        Intent intent = new Intent(mContext, RequestAcceptActivity.class);
        intent.putExtra(OthersDetailsActivity.OTHERS_BEAN_KEY, bean);
        intent.putExtra(OthersDetailsActivity.REQUEST_TYPE_KEY, type);
        mInstance.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mOthersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout mGodNameRL;
        private final LinearLayout mAcceptLL;
        private final Button mDeclineBtn;
        private final Button mAcceptBtn;
        private final RelativeLayout mParticipantRL;
        private final LinearLayout mDescriptionLL;
        public TextView mDateTV;
        public CircularNetworkImageView mPerson1Iv, mPerson2Iv, mPerson3Iv, mPerson4Iv, mPerson5Iv, mRightArrowIV;
        public TextView mGodNameTv, mGodCountTv, mDescriptionTv, mAddCountIv;

        public MyViewHolder(View itemView) {
            super(itemView);
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

            mGodNameRL = itemView.findViewById(R.id.godNameRL);
            mAcceptLL = itemView.findViewById(R.id.acceptLL);
            mDeclineBtn = itemView.findViewById(R.id.declineBtn);
            mAcceptBtn = itemView.findViewById(R.id.acceptBtn);
//            mGodNameRL.setVisibility(View.GONE);
        }
    }
}
