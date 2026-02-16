package com.namakoti.chanting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.namakoti.R;
import com.namakoti.adapters.DynamicPagerAdapter;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.BaseFragment;
import com.namakoti.chanting.chantforcause.CreateChantForCauseActivity;
import com.namakoti.chanting.self.CreateNormalChantingActivity;

import static android.app.Activity.RESULT_OK;


public class SelfChantingFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{
    public static final String CHANT_TYPE = "chant type";
    public static final int CREATE_CHANT_RESULT_CODE = 100;
    public static final int CREATE_CHANT_FORCAUSE_RESULT_CODE = 101;

    private OnFragmentInteractionListener mListener;
    private DynamicPagerAdapter pagerAdapter;
    private String mChantType;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public SelfChantingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChantType = getArguments().getString(CHANT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActivityCompat.invalidateOptionsMenu(getActivity());
        return inflater.inflate(R.layout.fragment_self_chanting, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChantType != null)
            setToolBar();
    }

    private void setToolBar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
        if (mChantType.equalsIgnoreCase("self"))
            setToolbarTitle(getString(R.string.self_chanting));
        else
            setToolbarTitle(getString(R.string.karmic_chanting));
        View logoView = getToolbarLogoIcon(toolbar);

       /*logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                addSelfNormalFrag();
            }
        });*/

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.normal_chanting)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.chant_for_cause)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final PagerAdapter adapter = new PagerAdapter
                (mBaseActivity.getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(this);

    }

    public void addSelfNormalFrag() {
        pagerAdapter.remove(0);
        pagerAdapter.add(new NormalChantingFragment(), getString(R.string.normal_chanting), 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.menu_self_chanting_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_CHANT_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                NormalChantingFragment frag1 = (NormalChantingFragment) viewPager.getAdapter()
                        .instantiateItem(viewPager, viewPager.getCurrentItem());
                frag1.updateData();
            }
        } else if (requestCode == CREATE_CHANT_FORCAUSE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                ChantingForCauseFragment frag = (ChantingForCauseFragment) viewPager.getAdapter()
                        .instantiateItem(viewPager, viewPager.getCurrentItem());
                frag.updateData();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_chants:
                if (viewPager.getCurrentItem() == 0) {
                    startActivityForResult(new Intent(mBaseActivity, CreateNormalChantingActivity.class), CREATE_CHANT_RESULT_CODE);
                } else if (viewPager.getCurrentItem() == 1) {
                    startActivityForResult(new Intent(mBaseActivity, CreateChantForCauseActivity.class), CREATE_CHANT_FORCAUSE_RESULT_CODE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
                    NormalChantingFragment tab1 = new NormalChantingFragment();
                    return tab1;
                case 1:
                    ChantingForCauseFragment tab2 = new ChantingForCauseFragment();
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
}
