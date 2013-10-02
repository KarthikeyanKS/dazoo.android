package com.millicom.secondscreen;

import java.util.ArrayList;

import com.millicom.secondscreen.R;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class SecondScreenApplication extends Application {

	private static final String				TAG								= "SecondScreenApplication";

	private static SecondScreenApplication	sInstance						= null;

	public static int						sImageSizeThumbnailWidth		= 0;
	public static int						sImageSizeThumbnailHeight		= 0;

	public static int						sImageSizeLandscapeWidth		= 0;
	public static int						sImageSizeLandscapeHeight		= 0;

	public static int						sImageSizePosterWidth			= 0;
	public static int						sImageSizePosterHeight			= 0;

	public static int						sImageSizeGalleryWidth			= 0;
	public static int						sImageSizeGalleryHeight			= 0;

	public static final double				IMAGE_RATIO						= 1.78;						// Magic number
	public static final double				IMAGE_WIDTH_MULTIPLIER			= 0.35;

	public static final double				IMAGE_WIDTH_COEFFICIENT_POSTER	= 0.88;
	public static final double				IMAGE_HEIGHT_COEFFICIENT_POSTER	= 1.4;

	private final static double				POSTER_WIDTH_DIVIDER			= 2.1;
	
	private ArrayList<Activity>				mRunningActivities				= new ArrayList<Activity>();

	// SharedPreferences used to save stuffs
	private static SharedPreferences		sSharedPreferences;

	public SecondScreenApplication() {
	}

	public static SecondScreenApplication getInstance() {
		if (sInstance == null) {
			sInstance = new SecondScreenApplication();
		}

		return sInstance;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// TODO: handle configuration changes
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		sSharedPreferences = getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);

		calculateSizes();
	}
	
	/**
	 * Calculate the sizes of the image thumbnails that are used across the app.
	 */
	private void calculateSizes() {

		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int widthFullSize = display.getWidth();
		int heightFullSize = display.getHeight();

		// Checks if we are in landscape
		if (display.getWidth() > display.getHeight()) {
			widthFullSize = display.getHeight();
		}

		sImageSizeLandscapeWidth = widthFullSize;
		sImageSizeLandscapeHeight = (int) Math.ceil(widthFullSize / IMAGE_RATIO);

		sImageSizeThumbnailWidth = (int) (widthFullSize * IMAGE_WIDTH_MULTIPLIER);
		sImageSizeThumbnailHeight = (int) Math.ceil(sImageSizeThumbnailWidth / IMAGE_RATIO);

		// Make room for the extra padding
		sImageSizeGalleryWidth = widthFullSize - (int) getResources().getDimension(R.dimen.gallery_size_padding);
		sImageSizeGalleryHeight = (int) Math.ceil(sImageSizeGalleryWidth / IMAGE_RATIO);
		
		sImageSizePosterWidth = (int) Math.ceil(sImageSizeGalleryWidth / POSTER_WIDTH_DIVIDER);
		sImageSizePosterHeight = (int) Math.ceil(sImageSizePosterWidth * IMAGE_HEIGHT_COEFFICIENT_POSTER);
	}
	
	// Add a activity to list of running activities
		public ArrayList<Activity> getActivityList() {
			return mRunningActivities;
		}
		
		// Clear all running activities
		public void clearActivityBacktrace() {
			for (Activity a : mRunningActivities) {
				if (a != null) a.finish();
			}
		}
}

