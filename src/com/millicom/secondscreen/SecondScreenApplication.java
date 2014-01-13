package com.millicom.secondscreen;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.millicom.secondscreen.manager.AppConfigurationManager;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.manager.DazooCore.AppConfigurationCallback;
import com.millicom.secondscreen.utilities.BootCompletedReceiver;
import com.millicom.secondscreen.utilities.DeviceUtilities;
import com.millicom.secondscreen.utilities.ObscuredSharedPreferences;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.testflightapp.lib.TestFlight;

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
	private static Editor					editor;

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
	}
	
	private void setupGoogleAnalytics() {
		String trackingId = AppConfigurationManager.getInstance().getGoogleAnalyticsTrackingId();
		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(this);
		Tracker tracker = googleAnalyticsInstance.getTracker(trackingId);
		
		String appVersion = Consts.GA_APP_VERSION_NOT_SET;
		String wasPreinstalled = BootCompletedReceiver.wasPreinstalled() ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String deviceId = DeviceUtilities.getDeviceId();
		
		PackageInfo pinfo;
		try {
			pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion = "" + pinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		tracker.set(Consts.GA_KEY_APP_VERSION, appVersion);
		tracker.set(Consts.GA_KEY_DEVICE_ID, deviceId);
		tracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED, wasPreinstalled);
		
		double sampleRateDecimal = AppConfigurationManager.getInstance().getGoogleAnalyticsSampleRate();
		double sampleRateAsPercentage = sampleRateDecimal * 100.0d;
		String sampleRateAsString = String.valueOf(sampleRateAsPercentage);
		
		tracker.set(Fields.SAMPLE_RATE, sampleRateAsString);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		
		/* Fetch and update app configuration */
		DazooCore.getAppConfiguration(new AppConfigurationCallback() {
			@Override
			public void onAppConfigurationResult() {
				/* Initialize Google Analytics */
				boolean googleAnalyticsEnabled = AppConfigurationManager.getInstance().isGoogleAnalyticsEnabled();
				if(googleAnalyticsEnabled) {
					setupGoogleAnalytics();
				}
			}
		});
						
		// sSharedPreferences = getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);
		sSharedPreferences = new ObscuredSharedPreferences(this, this.getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE));

		// re-initialize hour preference at every app start
		setSelectedHour(6);
		setIsOnStartAgain(false);
				
		calculateSizes();
		
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(displayImageOptions)
		.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
        .memoryCacheSize(2 * 1024 * 1024)
		.discCacheSize(50 * 1024 * 1024)
	    .discCacheFileCount(100)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs()
		.build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * Store user account access token
	 */
	public void setAccessToken(String token) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_ACCESS_TOKEN, token);
		editor.commit();
	}

	/**
	 * Retrieve user account access token
	 */
	public String getAccessToken() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_ACCESS_TOKEN, "");
	}

	/**
	 * Store user email
	 */
	public void setUserEmail(String email) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_EMAIL, email);
		editor.commit();
	}

	/**
	 * Get user email
	 */
	public String getUserEmail() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_EMAIL, "");
	}

	/**
	 * Store user password
	 */
	public void setUserPassword(String password) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_PASSWORD, password);
		editor.commit();
	}

	/**
	 * Get user password
	 */
	public String getUserPassword() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_PASSWORD, "");
	}

	/**
	 * Store user first name
	 */
	public void setUserFirstName(String firstName) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_FIRST_NAME, firstName);
		editor.commit();
	}

	/**
	 * Get user first name
	 */
	public String getUserFirstName() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_FIRST_NAME, "");
	}

	/**
	 * Store user last name
	 */
	public void setUserLastName(String lastName) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_LAST_NAME, lastName);
		editor.commit();
	}

	/**
	 * Get user last name
	 */
	public String getUserLastName() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_LAST_NAME, "");
	}

	/**
	 * Store user id
	 */
	public void setUserId(String id) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_USER_ID, id);
		editor.commit();
	}

	/**
	 * Get user id
	 */
	public String getUserId() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_USER_ID, "");
	}

	/**
	 * Set user existing flag
	 */
	public void setUserExistringFlag(boolean flag) {
		editor = sSharedPreferences.edit();
		editor.putBoolean(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_EXISTING_FLAG, flag);
		editor.commit();
	}

	/**
	 * Get user existing flag
	 */
	public boolean getUserExistringFlag() {
		return sSharedPreferences.getBoolean(Consts.MILLICOM_SECONDSCREEN_USER_ACCOUNT_EXISTING_FLAG, false);
	}
	
	/**
	 * Set user avatar url
	 */
	public void setUserAvatarUrl(String url){
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONSCREEN_USER_ACCOUNT_AVATAR_URL, url);
		editor.commit();
	}
	
	/**
	 * Get user avatar url
	 */
	public String getUserAvatarUrl(){
		return sSharedPreferences.getString(Consts.MILLICOM_SECONSCREEN_USER_ACCOUNT_AVATAR_URL, "");
	}
	
	
	/**
	 * Update the selected hour
	 */
	public void setSelectedHour(int hour) {
		editor = sSharedPreferences.edit();
		editor.putInt(Consts.MILLICOM_SECONDSCREEN_TV_GUIDE_HOUR, hour);
		editor.commit();
	}

	/**
	 * Get the selected hour
	 */
	public int getSelectedHour() {
		return sSharedPreferences.getInt(Consts.MILLICOM_SECONDSCREEN_TV_GUIDE_HOUR, 6);
	}

	/**
	 * Get if are back to the start page
	 */
	public boolean getIsOnStartAgain() {
		return sSharedPreferences.getBoolean(Consts.MILLICOM_SECONDSCREEN_HOMEPAGE_AGAIN, false);
	}

	/**
	 * Set if are back to the start page
	 */
	public void setIsOnStartAgain(boolean isHomePage) {
		editor = sSharedPreferences.edit();
		editor.putBoolean(Consts.MILLICOM_SECONDSCREEN_HOMEPAGE_AGAIN, isHomePage);
		editor.commit();
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

			Log.d(TAG, "DELETE ACTIVITY");
			if (a != null) a.finish();
		}
	}
}
