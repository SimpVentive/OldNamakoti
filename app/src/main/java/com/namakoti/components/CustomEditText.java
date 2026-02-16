package com.namakoti.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.namakoti.R;


/**
 * @author hpadagala
 * This is the CustomEditText class . This is used in all xml files in place of EditText to set the custom font 
 * */
public class CustomEditText extends EditText {
	
	public static final String TAG = "CustomEditText";
	
	public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTahomaTypeface(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTahomaTypeface(context, attrs);
    }

    /**
	 * This method is used for giving the 'Tahome' typeface to the text provided
	 * on the {@link EditText}.
	 * 
	 * @param context
	 *            {@link Context} reference in which the {@link EditText} is
	 *            used.
	 * @param attrs
	 *            {@link AttributeSet}
	 */
	public void setTahomaTypeface(Context context, AttributeSet attrs) {
		// Typeface.createFromAsset doesn't work in the layout editor.
		// Skipping...
		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		String boldStyle = styledAttrs.getString(R.styleable.CustomTextView_textStyle);
		Typeface typeface_bold;

		if (boldStyle != null && boldStyle.equals("light")) {
			typeface_bold = Typeface.createFromAsset(context.getAssets(), "font/Bariol_Light.ttf");
		}else if (boldStyle != null && boldStyle.equals("regular")) {
			typeface_bold = Typeface.createFromAsset(context.getAssets(), "font/Bariol_Regular.ttf");
		}
		else if (boldStyle != null && boldStyle.equals("bold")) {
			typeface_bold = Typeface.createFromAsset(context.getAssets(), "font/Bariol_Bold.ttf");
		}
		else if (boldStyle != null && boldStyle.equals("thin")) {
			typeface_bold = Typeface.createFromAsset(context.getAssets(), "font/Bariol_Thin.ttf");
		}
		else{
			typeface_bold = Typeface.createFromAsset(context.getAssets(), "font/Bariol_Regular.ttf");
		}

		setTypeface(typeface_bold);

		styledAttrs.recycle();
	}
	
	

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		invalidate();
		super.onWindowFocusChanged(hasWindowFocus);
	}

}
