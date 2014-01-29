package com.mitv.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

public class HardwareUtilities {
		
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity activity) {
		int screenWidth = 0;
		if (Build.VERSION.SDK_INT >= 11) {
	        Point size = new Point();
	        try {
	        	activity.getWindowManager().getDefaultDisplay().getRealSize(size);
	            screenWidth = size.x;
	        } catch (NoSuchMethodError e) {
	            Log.i("HardwareUtilities", "Error it can't work");
	        }

	    } else {
	        DisplayMetrics metrics = new DisplayMetrics();
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        screenWidth = metrics.widthPixels;
	    }
		return screenWidth;
	}
	
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity activity) {
		int screenHeight = 0;
		if (Build.VERSION.SDK_INT >= 11) {
	        Point size = new Point();
	        try {
	        	activity.getWindowManager().getDefaultDisplay().getRealSize(size);
	            screenHeight = size.y;
	        } catch (NoSuchMethodError e) {
	            Log.i("HardwareUtilities", "Error it can't work");
	        }

	    } else {
	        DisplayMetrics metrics = new DisplayMetrics();
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        screenHeight = metrics.heightPixels;
	    }
		return screenHeight;
	}
}
