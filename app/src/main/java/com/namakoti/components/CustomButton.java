package com.namakoti.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;


public class CustomButton extends Button {

    private Context mContext;
    private AttributeSet mAttrs;
    private int mDefStyle;

    private int textFocusColor;
    private int textUnFocusColor;
    /*private Drawable focusBackground;
	private Drawable unFocusBackground;*/

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.mContext = context;
        this.mAttrs = attrs;
        this.mDefStyle = defStyle;

        init();
    }

    public CustomButton(Context context) {
        super(context);

        this.mContext = context;

        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        this.mAttrs = attrs;

        init();
    }

    private void init() {

        setFocusable(true);
      /*  TypedArray styledAttrs = mContext.obtainStyledAttributes(mAttrs,
                R.styleable.CustomButton);

        String textFocusColor = styledAttrs
                .getString(R.styleable.CustomButton_textFocusColor);

        String textUnFocusColor = styledAttrs
                .getString(R.styleable.CustomButton_textUnFocusColor);
		

        this.textFocusColor = Color.parseColor(textFocusColor);
        this.textUnFocusColor = Color.parseColor(textUnFocusColor);

        String boldStyle = styledAttrs.getString(R.styleable.CustomButton_fontStyle);


        styledAttrs.recycle();*/

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {

        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            if (textFocusColor != 0) {
                setTextColor(textFocusColor);
            }

			/*if (focusBackground != null) {
				setBackgroundDrawable(focusBackground);
			}*/

        } else {

            if (textUnFocusColor != 0) {
                setTextColor(textUnFocusColor);
            }

			/*if (unFocusBackground != null) {
				setBackgroundDrawable(unFocusBackground);
			}*/
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (isPressed()) {
            if (textFocusColor != 0) {
                setTextColor(textFocusColor);
            }

			/*if (focusBackground != null) {
				setBackgroundDrawable(focusBackground);
			}*/
        } else {
            if (textUnFocusColor != 0) {
                setTextColor(textUnFocusColor);
            }

			/*if (unFocusBackground != null) {
				setBackgroundDrawable(unFocusBackground);
			}*/
        }
    }


}
