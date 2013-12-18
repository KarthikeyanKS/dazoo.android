package com.millicom.secondscreen.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.manager.FontManager;

public class FontTextView extends TextView {

	private static final String TAG = "FontTextView";

	public FontTextView(Context context) {
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.FontTextView);
		String customFont = a.getString(R.styleable.FontTextView_fonttextview);
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

		if (asset != null) {
			Typeface tf = FontManager.getTypefaceStatic(ctx, asset);
			if (tf != null) {
				setTypeface(tf);
				return true;
			}
		}

		return false;
	}

}