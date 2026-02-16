package com.namakoti.adapters;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.beans.ContactsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.karmic.beingHelped.ContactsListActivity;
import com.namakoti.utils.VolleyUtil;

import java.util.ArrayList;

/**
 * Created by admin on 03-22-2017.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private final ImageLoader mIimageLoader;
    private final ContactsListActivity mContext;
    private ArrayList<ContactsBean> mList;


    public ContactsAdapter(ArrayList<ContactsBean> list, ContactsListActivity activity) {
        this.mContext = activity;
        this.mList = UserInfoBean.getInstance().getContacts(mContext);
        mIimageLoader = VolleyUtil.getInstance().imageLoader(mContext, com.intuit.sdp.R.dimen._40sdp);

    }

    public void updateList(ArrayList<ContactsBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout mRowRL;
        private final ImageView addIV;
        public TextView mParticipantNameTV;
        public TextView mAttendingTV;
        public NetworkImageView mPersonIV;
        public MyViewHolder(View itemView) {
            super(itemView);
            mParticipantNameTV =itemView.findViewById(R.id.participantNameTV);
            mAttendingTV =itemView.findViewById(R.id.attendingTV);
            mPersonIV =itemView.findViewById(R.id.personIV);
            addIV =itemView.findViewById(R.id.arrowIV);

            mRowRL =itemView.findViewById(R.id.rowRL);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ContactsBean details = mList.get(position);

        holder.mParticipantNameTV.setText(details.getName());
        if (details.getNumber() != null){
            holder.mAttendingTV.setText(""+details.getNumber());
        }
        else
            holder.mAttendingTV.setText("");
        holder.addIV.setTag(R.id.acceptBtn, details);
        holder.mPersonIV.setImageUrl(details.getPhoto(),mIimageLoader);
        if (details.getIsSelected() == 1){
            holder.addIV.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_cross));
        }else{
            holder.addIV.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_add_orange));
        }

        holder.addIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactsBean details = (ContactsBean) view.getTag(R.id.acceptBtn);
                if (details.getIsSelected() == 1){
                    details.setIsSelected(0);
                }else{
                    details.setIsSelected(1);
                }
                UserInfoBean.getInstance().setContacts(mList,mContext);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
