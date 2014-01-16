package com.millicom.secondscreen.manager;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class FontManager {

	private static final String TAG = "FontManager";
	private static FontManager selfInstance;
	private HashMap<String, Typeface> fontMap;
	
	public static final String FONT_MEDIUM 		= "Roboto-MiTV-Medium.ttf";
	public static final String FONT_BOLD 		= "Roboto-MiTV-Bold.ttf";
	public static final String FONT_LIGHT 		= "Roboto-MiTV-Light.ttf";
	public static final String FONT_REGULAR 	= "Roboto-MiTV-Regular.ttf";

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
	
	public static Typeface getFontBold(Context ctx) {
		return getInstance().getTypeface(ctx, FONT_BOLD);
	}
	
	public static Typeface getFontRegular(Context ctx) {
		return getInstance().getTypeface(ctx, FONT_REGULAR);
	}
	
	public static Typeface getFontLight(Context ctx) {
		return getInstance().getTypeface(ctx, FONT_LIGHT);
	}
	
	public static Typeface getFontMedium(Context ctx) {
		return getInstance().getTypeface(ctx, FONT_MEDIUM);
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
