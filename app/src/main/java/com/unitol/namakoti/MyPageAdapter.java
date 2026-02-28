package com.unitol.namakoti;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import java.util.List;

public class MyPageAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	private List<Fragment> fragments;

	protected final int[] ICONS = { R.drawable.slidericon, R.drawable.slidericon, R.drawable.slidericon };
	private int mCount = ICONS.length;

	public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {

		return this.fragments.get(position);

	}

	@Override
	public int getCount() {

		return mCount;

	}

	@Override
	public int getIconResId(int index) {
		return ICONS[index % ICONS.length];
	}

}
