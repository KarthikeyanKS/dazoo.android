package com.mitv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.millicom.mitv.utilities.FileUtils;
import com.millicom.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

public class SecondScreenApplication extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = SecondScreenApplication.class.getName();

	private static String userAgent;
	private static String session;
	private static SecondScreenApplication sInstance = null;

	private static int sImageSizeThumbnailWidth = 0;
	private static int sImageSizeThumbnailHeight = 0;

	private static int sImageSizeLandscapeWidth = 0;
	private static int sImageSizeLandscapeHeight = 0;

	private static int sImageSizePosterWidth = 0;
	private static int sImageSizePosterHeight = 0;

	private static int sImageSizeGalleryWidth = 0;
	private static int sImageSizeGalleryHeight = 0;

	private static final double IMAGE_RATIO = 1.78; // Magic number
	private static final double IMAGE_WIDTH_MULTIPLIER = 0.35;
	private static final double POSTER_WIDTH_DIVIDER = 2.1;

	public static final double IMAGE_WIDTH_COEFFICIENT_POSTER = 0.88;
	public static final double IMAGE_HEIGHT_COEFFICIENT_POSTER = 1.4;

	private CheckApiVersionListener mCheckApiVersionListner;
	private AppConfigurationListener mAppConfigurationListener;
	private boolean mIsFirstStart = true;

	// SharedPreferences used to save stuff
	private static SharedPreferences sSharedPreferences;
	private static Editor editor;

	public SecondScreenApplication() {
	}

	public static SecondScreenApplication sharedInstance() {
		if (sInstance == null) {
			sInstance = new SecondScreenApplication();
		}

		return sInstance;
	}

	public static interface CheckApiVersionListener {
		public void onApiVersionChecked(boolean needsUpdate);
	}

	public static interface AppConfigurationListener {
		public void onAppConfigurationListener();
	}

	public static boolean applicationIsSystemApp(Context context) 
	{
		String packageName = context.getPackageName();

		try 
		{
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);

			String appLocation = applicationInfo.publicSourceDir;

			// OR String appLocation = applicationInfo.sourceDir;
			// Both returns the same
			// if package is pre-installed then output will be /system/app/application_name.apk
			// if package is installed by user then output will be /data/app/application_name.apk

			// Check if package is system app
			if (appLocation != null && appLocation.startsWith("/system/app/")) 
			{
				return true;
			}
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace(); // TODO Can handle as your logic
		}
		
		return false;
	}

	public static boolean applicationIsSystemAppUsingFlag(Context context) {
		String packageName = context.getPackageName();

		try {
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);

			// FLAG_SYSTEM is only set to system applications,
			// this will work even if application is installed in external storage

			// Check if package is system app
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace(); // TODO Can handle as your logic
		}

		return false;
	}

	public static Locale getCurrentLocale() {
		Locale current = sharedInstance().getApplicationContext().getResources().getConfiguration().locale;
		return current;
	}

	public int getScreenSizeMask() {
		int screenSizeMask = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSizeMask;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;

		sSharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

		calculateSizes();

		// Imageloader for views where we dont want to reset the view, ex guide channel icons.
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(displayImageOptions)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);

		// Imageloader that reset views before loading
		// DisplayImageOptions resetViewDisplayImageOptions = new DisplayImageOptions.Builder()
		// .cacheInMemory(true)
		// .cacheOnDisc(true)
		// .resetViewBeforeLoading(true)
		// .build();
		//
		// ImageLoaderConfiguration resetViewConfig = new ImageLoaderConfiguration.Builder(getApplicationContext())
		// .defaultDisplayImageOptions(resetViewDisplayImageOptions)
		// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		// .memoryCacheSize(2 * 1024 * 1024)
		// .discCacheSize(50 * 1024 * 1024)
		// .discCacheFileCount(100)
		// .tasksProcessingOrder(QueueProcessingType.LIFO)
		// .build();
		// ResetViewImageloader.getInstance().init(resetViewConfig);

		L.disableLogging();
	}

	public void setWasPreinstalled() {
		editor = sSharedPreferences.edit();

		editor.putBoolean(Consts.APP_WAS_PREINSTALLED, true);

		editor.commit();
	}

	public boolean getWasPreinstalled() {
		return sSharedPreferences.getBoolean(Consts.APP_WAS_PREINSTALLED, false);
	}


	/**
	 * Calculate the sizes of the image thumbnails that are used across the app.
	 */
	@Deprecated
	private void calculateSizes() {
		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		int widthFullSize = display.getWidth();

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

	// private void setupGoogleAnalytics()
	// {
	// GATrackingManager.getInstance();
	// }

	// public boolean checkApiVersion()
	// {
	// String apiVersion = getApiVersion();
	//
	// if (apiVersion != null && !TextUtils.isEmpty(apiVersion) && !apiVersion.equals(Consts.API_VERSION))
	// {
	// return true;
	// }
	// else
	// {
	// return false;
	// }
	// }

	// public void setCheckApiVersionListener(CheckApiVersionListener listener) {
	// mCheckApiVersionListner = listener;
	// }
	//
	// public void setAppConfigurationListener(AppConfigurationListener listener) {
	// mAppConfigurationListener = listener;
	// }
	//
	//
	//
	// public boolean isFirstStart() {
	// return mIsFirstStart;
	// }
	//
	//
	//
	// public void setisFirstStart(boolean isFirstStart)
	// {
	// mIsFirstStart = isFirstStart;
	// }

	// /**
	// * Get/Set User session from saved preferences
	// *
	// * @return
	// */
	// public static String getSession() {
	//
	// Log.i("SESSION", "Get stored session");
	// if (session == null) {
	// session = sSharedPreferences.getString(Consts.MITV_SESSION, null);
	// }
	// Log.i("SESSION", "Session: " + session);
	// return session;
	// }
	//
	// public static void setSession(String session) {
	//
	// Log.i("SESSION", "Save cookie session");
	// SecondScreenApplication.session = session;
	// if (sSharedPreferences != null) {
	// sSharedPreferences.edit().putString(Consts.MITV_SESSION, session).commit();
	// }
	// }

	// /**
	// * Get default user agent
	// *
	// * @return
	// */
	// public static String getUserAgent() {
	// if (userAgent == null) {
	// userAgent = getDefaultUserAgent();
	// }
	// return userAgent;
	// }

	// private static String getDefaultUserAgent() {
	// StringBuilder result = new StringBuilder(64);
	// result.append("Dalvik/");
	// result.append(System.getProperty("java.vm.version")); // such as 1.1.0
	// result.append(" (Linux; U; Android ");
	//
	// String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
	// result.append(version.length() > 0 ? version : "1.0");
	//
	// // add the model for the release build
	// if ("REL".equals(Build.VERSION.CODENAME)) {
	// String model = Build.MODEL;
	// if (model.length() > 0) {
	// result.append("; ");
	// result.append(model);
	// }
	// }
	// String id = Build.ID; // "MASTER" or "M4-rc20"
	// if (id.length() > 0) {
	// result.append(" Build/");
	// result.append(id);
	// }
	// result.append(")");
	// return result.toString();
	// }
	// /**
	// * Store user email
	// */
	// public void setUserEmail(String email) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_EMAIL, email);
	// editor.commit();
	// }
	//
	// /**
	// * Get user email
	// */
	// public String getUserEmail() {
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_EMAIL, "");
	// }
	//
	// /**
	// * Store user password
	// */
	// public void setUserPassword(String password) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_PASSWORD, password);
	// editor.commit();
	// }
	//
	// /**
	// * Get user password
	// */
	// public String getUserPassword() {
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_PASSWORD, "");
	// }
	//
	// /**
	// * Store user first name
	// */
	// public void setUserFirstName(String firstName) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_FIRST_NAME, firstName);
	// editor.commit();
	// }
	//
	// /**
	// * Get user first name
	// */
	// public String getUserFirstName() {
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_FIRST_NAME, "");
	// }
	//
	// /**
	// * Store user last name
	// */
	// public void setUserLastName(String lastName) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_LAST_NAME, lastName);
	// editor.commit();
	// }
	//
	// /**
	// * Get user last name
	// */
	// public String getUserLastName() {
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_LAST_NAME, "");
	// }
	//
	// /**
	// * Store user id
	// */
	// public void setUserId(String id) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_USER_ID, id);
	// editor.commit();
	// }
	//
	// /**
	// * Get user id
	// */
	// public String getUserId() {
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_USER_ID, "");
	// }
	//
	// /**
	// * Set user existing flag
	// */
	// public void setUserExistringFlag(boolean flag) {
	// editor = sSharedPreferences.edit();
	// editor.putBoolean(Consts.USER_ACCOUNT_EXISTING_FLAG, flag);
	// editor.commit();
	// }
	//
	// /**
	// * Get user existing flag
	// */
	// public boolean getUserExistingFlag() {
	// return sSharedPreferences.getBoolean(Consts.USER_ACCOUNT_EXISTING_FLAG, false);
	// }
	//
	// /**
	// * Set user avatar url
	// */
	// public void setUserAvatarUrl(String url){
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.USER_ACCOUNT_AVATAR_URL, url);
	// editor.commit();
	// }
	//
	// /**
	// * Get user avatar url
	// */
	// public String getUserAvatarUrl(){
	// return sSharedPreferences.getString(Consts.USER_ACCOUNT_AVATAR_URL, "");
	// }

	// /**
	// * Update the selected hour
	// */
	// public void setSelectedHour(int hour) {
	// editor = sSharedPreferences.edit();
	// editor.putInt(Consts.TV_GUIDE_HOUR, hour);
	// editor.commit();
	// }
	//
	// /**
	// * Get the selected hour
	// */
	// public int getSelectedHour() {
	// return sSharedPreferences.getInt(Consts.TV_GUIDE_HOUR, 6);
	// }

	// /**
	// * Get if are back to the start page
	// */
	// public boolean getIsOnStartAgain() {
	// return sSharedPreferences.getBoolean(Consts.HOMEPAGE_AGAIN, false);
	// }
	//
	// /**
	// * Set if are back to the start page
	// */
	// public void setIsOnStartAgain(boolean isHomePage) {
	// editor = sSharedPreferences.edit();
	// editor.putBoolean(Consts.HOMEPAGE_AGAIN, isHomePage);
	// editor.commit();
	// }
	//
	// /**
	// * Get api version
	// */
	// public String getApiVersion() {
	// return sSharedPreferences.getString(Consts.API_VERSION_SHARED_PREF, null);
	// }
	//
	// /**
	// * Set api version
	// */
	// public void setApiVersion(String apiVersion) {
	// editor = sSharedPreferences.edit();
	// editor.putString(Consts.API_VERSION_SHARED_PREF, apiVersion);
	// editor.commit();
	// }
}
