package com.mitv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.R;
import com.mitv.content.SSApiVersionPage;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.manager.DazooCore;
import com.mitv.manager.DazooCore.ApiVersionCallback;
import com.mitv.manager.DazooCore.AppConfigurationCallback;
import com.mitv.utilities.DeviceUtilities;
import com.mitv.utilities.ObscuredSharedPreferences;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

//import com.testflightapp.lib.TestFlight;

public class SecondScreenApplication extends Application {

	private static final String				TAG								= "SecondScreenApplication";

	private static String userAgent;
	private static String session;
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
	/**
	 * Get/Set User session from saved preferences
	 * 
	 * @return
	 */
	public static String getSession() {
		
		Log.i("SESSION", "Get stored session");
		if (session == null) {
			session = sSharedPreferences.getString(Consts.MILLICOM_SESSION, null);
		}
		Log.i("SESSION", "Session: " + session);
		return session;
	}

	public static void setSession(String session) {
		
		Log.i("SESSION", "Save cookie session");
		SecondScreenApplication.session = session;
		if (sSharedPreferences != null) {
			sSharedPreferences.edit().putString(Consts.MILLICOM_SESSION, session).commit();
		}
	}

	/**
	 * Get default user agent
	 * 
	 * @return
	 */
	public static String getUserAgent() {
		if (userAgent == null) {
			userAgent = getDefaultUserAgent();
		}
		return userAgent;
	}

	private static String getDefaultUserAgent() {
		StringBuilder result = new StringBuilder(64);
		result.append("Dalvik/");
		result.append(System.getProperty("java.vm.version")); // such as 1.1.0
		result.append(" (Linux; U; Android ");

		String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
		result.append(version.length() > 0 ? version : "1.0");

		// add the model for the release build
		if ("REL".equals(Build.VERSION.CODENAME)) {
			String model = Build.MODEL;
			if (model.length() > 0) {
				result.append("; ");
				result.append(model);
			}
		}
		String id = Build.ID; // "MASTER" or "M4-rc20"
		if (id.length() > 0) {
			result.append(" Build/");
			result.append(id);
		}
		result.append(")");
		return result.toString();
	}
	
	public static SecondScreenApplication getInstance() {
		if (sInstance == null) {
			sInstance = new SecondScreenApplication();
		}
		return sInstance;
	}
	
	private File appWasPreinstalledFile() {
		String root = Environment.getExternalStorageDirectory().toString();
		
		String packageName = getPackageName();
		
		String filePath = String.format(getCurrentLocale(), "%s/Android/data/%s", root, packageName);
		
		File myDir = new File(filePath);
		myDir.mkdirs();

		String fname = Consts.MILLICOM_SECONDSCREEN_APP_WAS_PREINSTALLED_FILE_NAME;
		File file = new File(myDir, fname);
		
		return file;
	}
	
	public boolean wasPreinstalledFileExists() {
		File file = appWasPreinstalledFile();
		boolean wasPreinstalledFileExists = file.exists();
		
		return wasPreinstalledFileExists;
	}
	
	public void saveWasPreinstalledFile() {
		File file = appWasPreinstalledFile();
		if (!wasPreinstalledFileExists()) {
			try {
				FileOutputStream os = new FileOutputStream(file, true);
				OutputStreamWriter out = new OutputStreamWriter(os);
				out.write("app was preinstalled");
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Locale getCurrentLocale() {
		Locale current = getInstance().getApplicationContext().getResources().getConfiguration().locale;
		return current;
	}

	
	public int getScreenSizeMask() {
		int screenSizeMask = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSizeMask;
	}
	
	public boolean isConnectedToWifi() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		boolean isConnectedToWifi = false;
		if (mWifi.isConnected()) {
			isConnectedToWifi = true;
		}
		return isConnectedToWifi;
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
		String wasPreinstalledSharedPrefs = getWasPreinstalled() ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = wasPreinstalledFileExists() ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
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
		tracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS, wasPreinstalledSharedPrefs);
		tracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE, wasPreinstalledExternalStorage);
		
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
		
		/* Fetch api version */
		DazooCore.getApiVersion(new ApiVersionCallback() {
			@Override
			public void onApiVersionResult() {
				setApiVersion(SSApiVersionPage.getInstance().getApiVersionString());
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
//		.writeDebugLogs()
		.build();
		ImageLoader.getInstance().init(config);
		

		L.disableLogging();
	}
	

	
	public void setWasPreinstalled() {
		editor = sSharedPreferences.edit();
		editor.putBoolean(Consts.MILLICOM_SECONDSCREEN_APP_WAS_PREINSTALLED, true);
		editor.commit();
	}
	
	public boolean getWasPreinstalled() {
		return sSharedPreferences.getBoolean(Consts.MILLICOM_SECONDSCREEN_APP_WAS_PREINSTALLED, false);
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
	 * Get api version
	 */
	public String getApiVersion() {
		return sSharedPreferences.getString(Consts.MILLICOM_SECONDSCREEN_API_VERSION_SHARED_PREF, null);
	}
	
	/**
	 * Set api version
	 */
	public void setApiVersion(String apiVersion) {
		editor = sSharedPreferences.edit();
		editor.putString(Consts.MILLICOM_SECONDSCREEN_API_VERSION_SHARED_PREF, apiVersion);
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