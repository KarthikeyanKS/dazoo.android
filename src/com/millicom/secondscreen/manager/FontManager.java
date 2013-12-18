package com.millicom.secondscreen.manager;

import java.util.HashMap;

import com.millicom.secondscreen.SecondScreenApplication;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class FontManager {

	private static final String TAG = "FontManager";
	private static FontManager selfInstance;
	private HashMap<String, Typeface> fontMap;

	public FontManager() {
		this.fontMap = new HashMap<String, Typeface>();
	}

	public static FontManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new FontManager();
		}
		return selfInstance;
	}

	public static Typeface getTypefaceStatic(Context ctx, String asset) {
		return getInstance().getTypeface(ctx, asset);
	}
	
	public Typeface getTypeface(Context ctx, String asset) {
		Typeface tf = fontMap.get(asset);

		if (tf == null) {
			try {
				tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
				fontMap.put(asset, tf);
			} catch (Exception e) {
				Log.e(TAG, "Could not get typeface: " + e.getMessage());
			}
		}

		return tf;
	}

}
