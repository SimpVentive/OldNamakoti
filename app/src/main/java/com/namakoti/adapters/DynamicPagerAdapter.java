package com.namakoti.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by anusha on 3/3/2018.
 */
public class DynamicPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<WeakReference<Fragment>> registeredFragments = new ArrayList<WeakReference<Fragment>>();
    private ArrayList<String> tabTitleAL = new ArrayList<>();

    public DynamicPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        WeakReference<Fragment> weakReference = registeredFragments.get(position);
        return weakReference.get();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleAL.get(position);
    }

    @Override
    public int getCount() {
        return registeredFragments.size();
    }


    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    public void add(Fragment f,String name,int pos){
        registeredFragments.add(pos,new WeakReference<Fragment>(f));
        tabTitleAL.add(pos,name);
        notifyDataSetChanged();
    }

    public void remove(int position){
        registeredFragments.remove(position);
        tabTitleAL.remove(position);
        notifyDataSetChanged();
    }
}

