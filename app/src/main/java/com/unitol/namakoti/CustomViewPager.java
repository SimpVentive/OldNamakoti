package com.unitol.namakoti;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

   private boolean isPagingEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isPagingEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
        if (isPagingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	
        if (isPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    //disable view pager scrolling
    public void setPagingEnabled(boolean b) {
        isPagingEnabled = b;
    }
}