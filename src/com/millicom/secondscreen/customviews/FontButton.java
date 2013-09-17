package com.millicom.secondscreen.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import com.millicom.secondscreen.R;

public class FontButton extends Button {

	private static final String TAG = "FontButton";

	public FontButton(Context context) {
		super(context);
	}

	public FontButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public FontButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.FontButton);
		String customFont = a.getString(R.styleable.FontButton_fontbutton);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String asset) {

		/*
		 * Resource for the bug: http://code.google.com/p/android/issues/detail?id=5038
		 */
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
			return false;
		}

		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
			setTypeface(tf);
		} catch (Exception e) {
			Log.e(TAG, "Could not get typeface: " + e.getMessage());
			return false;
		}

		return true;
	}
}