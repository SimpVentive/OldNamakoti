package com.namakoti.karmic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.namakoti.R;
import com.namakoti.base.BaseFragment;
import com.namakoti.karmic.beingHelped.NewBeingHelpedActivity;
import com.namakoti.utils.Constants;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KarmicChantingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_self_chanting, container, false);
        ButterKnife.bind(this, view);

        mTabs.addTab(mTabs.newTab().setText(getString(R.string.being_helped)));
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.helping_others)));
        mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        final PagerAdapter adapter = new PagerAdapter
                (mBaseActivity.getSupportFragmentManager(), mTabs.getTabCount());
        mViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mViewpager.setAdapter(adapter);
        mTabs.addOnTabSelectedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    BeingHelpedFragment tab1 = new BeingHelpedFragment();
                    return tab1;
                case 1:
                    HelpingOthersFragment tab2 = new HelpingOthersFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_karmic_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_karmic) {
            BeingHelpedFragment frag1 = (BeingHelpedFragment) Objects.requireNonNull(mViewpager.getAdapter()).instantiateItem(mViewpager, mViewpager.getCurrentItem());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requireActivity().registerReceiver(frag1.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                } else {
                    requireActivity().registerReceiver(frag1.brd_receiver, new IntentFilter(Constants.CREATE_KARMIC_CHANT_INTENT_ACTION));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(mBaseActivity, NewBeingHelpedActivity.class)/*, BEING_HELPED_RESULT_CODE*/);
        }
        return true;
    }

}
