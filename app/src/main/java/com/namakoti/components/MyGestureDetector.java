package com.namakoti.components;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by anusha on 1/1/2018.
 */
public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private final String TAG = MyGestureDetector.class.getName();
    private final GestureSwipeListener listener;

    public MyGestureDetector(GestureSwipeListener listener){
        this.listener = listener;
    }

    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_VELOCITY_THRESHOLD = 50;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        result = onSwipeRight();
                    } else {
                        result = onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        result = onSwipeBottom();
                    } else {
                        result = onSwipeTop();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    public boolean onSwipeRight() {
        Log.i(TAG, "onSwipeRight");
        return false;
    }

    public boolean onSwipeLeft() {
        Log.i(TAG, "onSwipeLeft");
        return false;
    }

    public boolean onSwipeTop() {
        Log.i(TAG, "onSwipeTop");

        listener.countOnSwipe(false, true);
        return false;
    }

    public boolean onSwipeBottom() {
        Log.i(TAG, "onSwipeBottom");

        listener.countOnSwipe(true, false);
        return false;
    }
}
