
package com.mitv;



import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;



public class FontManager 
{
	private static final String TAG = FontManager.class.getName();
	
	
	private static FontManager instance;
	private HashMap<String, Typeface> fontMap;
	
	
	
	public static final String FONT_MEDIUM 		= "roboto_mitv_medium.ttf";
	public static final String FONT_BOLD 		= "roboto_mitv_bold.ttf";
	public static final String FONT_LIGHT 		= "roboto_mitv_light.ttf";
	public static final String FONT_REGULAR 	= "roboto_mitv_regular.ttf";

	
	
	private FontManager() 
	{
		this.fontMap = new HashMap<String, Typeface>();
	}

	
	
	public static FontManager getInstance() 
	{
		if (instance == null) 
		{
			instance = new FontManager();
		}
		
		return instance;
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
	
	
	
	public Typeface getTypeface(Context ctx, String asset) 
	{
		Typeface tf = fontMap.get(asset);

		if (tf == null) 
		{
			try 
			{
				tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
				
				fontMap.put(asset, tf);
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "Could not get typeface: " + e.getMessage());
			}
		}

		return tf;
	}
}