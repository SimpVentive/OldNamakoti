package com.unitol.namakoti;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.unitol.namakoti.web.Container;

public class BaseFragment extends Fragment {
	public Container mActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivity =	(Container) this.getActivity();
	}
	
	public boolean onBackPressed(){
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		
	}
}
