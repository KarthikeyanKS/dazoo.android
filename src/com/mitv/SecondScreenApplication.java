
package com.mitv;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.mitv.utilities.AppDataUtils;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;



public class SecondScreenApplication 
	extends Application 
{
	private static final String TAG = SecondScreenApplication.class.getName();

	
	
	private static SecondScreenApplication sharedInstance;

	
	private ContentManager contentManager;
	
	
	
	/* Do not remove. A public constructor is required by the Application class */
	public SecondScreenApplication() 
	{}

	
	
	public static SecondScreenApplication sharedInstance() 
	{
		if (sharedInstance == null) 
		{
			sharedInstance = new SecondScreenApplication();
		}

		return sharedInstance;
	}
	
	
	
	public static boolean isContentManagerNull() {
		SecondScreenApplication app = sharedInstance;
		if(app != null) {
			return (app.contentManager == null);
		} else {
			return true;
		}
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();

		sharedInstance = this;
		
		if(isAppRestarting()) {
			Log.e(TAG, "AppIsRestarging was true, setting it false");
			setAppIsRestarting(false);
		}
		
		/* Initial call to AppDataUtils, in order to initialize the SharedPreferences object */
		AppDataUtils.sharedInstance(this);
		
		/* Initial call to ImageLoaderManager, in order to configure the image loader objects */
		ImageLoaderManager.sharedInstance(this);

		contentManager = getContentManager();

		boolean enableStrictMode = Constants.ENABLE_STRICT_MODE;

		if (enableStrictMode) 
		{
			StrictMode.ThreadPolicy threadPolicy;
			
			/* The following policies are not available prior to API Level 11 (HONEYCOMB):
			 * 
			 *  detectCustomSlowCalls()
			 *  penaltyDeathOnNetwork()
			 *  penaltyFlashScreen()
			 *  permitCustomSlowCalls()
			 *  
			 **/
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			{
				threadPolicy = new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.penaltyFlashScreen()
				.build();
			}
			else
			{
				threadPolicy = new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build();
			}

			StrictMode.setThreadPolicy(threadPolicy);

			StrictMode.VmPolicy vmPolicy;
			
			/* The following policies are not available prior to API Level 11 (HONEYCOMB):
			 * 
			 *  detectActivityLeaks()
			 *  detectFileUriExposure()
			 *  detectLeakedClosableObjects()
			 *  detectLeakedRegistrationObjects()
			 *  
			 **/
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			{
				vmPolicy = new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				//.penaltyLog()
				.build();
			}
			else
			{
				vmPolicy = new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				//.penaltyLog()
				.build();
			}
			
			StrictMode.setVmPolicy(vmPolicy);
		}
		
		if(Constants.FORCE_CACHE_DATABASE_FLUSH || isCurrentVersionAnUpgradeFromInstalledVersion())
		{
			ContentManager.clearAllPersistentCacheData();
			
			AppDataUtils.sharedInstance(this).clearAllPreferences(false);
		}

		setInstalledAppVersionToCurrentVersion();
	}
	
	
	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		
		Log.e(TAG, "Running low on memory.");
	}
	

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	
	
	public static boolean isApplicationSystemApp() 
	{
		ApplicationInfo applicationInfo = GenericUtils.getApplicationInfo();
		
		if(applicationInfo != null)
		{
			String appLocation = applicationInfo.publicSourceDir;
			
			// OR String appLocation = applicationInfo.sourceDir;
			// Both returns the same
			// if package is pre-installed then output will be /system/app/application_name.apk
			// if package is installed by user then output will be /data/app/application_name.apk

			// Check if package is system app
			if (appLocation != null && appLocation.startsWith(Constants.SYSTEM_APP_PATH)) 
			{
				return true;
			}
		}

		return false;
	}

	
	
	public static boolean isApplicationSystemAppUsingFlag()
	{
		ApplicationInfo applicationInfo = GenericUtils.getApplicationInfo();
		
		if(applicationInfo != null)
		{
			// FLAG_SYSTEM is only set to system applications,
			// This should work even if application is installed in external storage

			// Check if package is system app
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) 
			{
				return true;
			}
		}

		return false;
	}
	
	
	public ImageLoaderManager getImageLoaderManager()
	{
		return ImageLoaderManager.sharedInstance(this); 
	}
	
	
	public void setAppAsPreinstalled() 
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_WAS_PREINSTALLED, true, false);
	}

	
	public boolean isAppPreinstalled() 
	{
		return AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_WAS_PREINSTALLED, false);
	}
	
	
	public static void setAppIsRestarting(boolean value) 
	{
		AppDataUtils.sharedInstance(sharedInstance).setPreference(Constants.SHARED_PREFERENCES_APP_IS_RESTARTING, value, true);
	}

	
	public static boolean isAppRestarting() 
	{
		return AppDataUtils.sharedInstance(sharedInstance).getPreference(Constants.SHARED_PREFERENCES_APP_IS_RESTARTING, false);
	}
	
	private String getCurrentAppVersion()
	{
		return GenericUtils.getCurrentAppVersion();
	}
	
	
	private String getInstalledAppVersion()
	{
		return AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_INSTALLED_VERSION, "");
	}
	
	
	private void setInstalledAppVersionToCurrentVersion()
	{
		String currentVersion = getCurrentAppVersion();
		
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_INSTALLED_VERSION, currentVersion, false);
	}
	
	
	private boolean isCurrentVersionAnUpgradeFromInstalledVersion()
	{
		boolean isCurrentVersionAnUpgradeFromInstalledVersion;
		
		String installedAppVersion = getInstalledAppVersion();
		
		String currentAppVersion = getCurrentAppVersion();
		
		if(installedAppVersion.isEmpty() || currentAppVersion.isEmpty())
		{
			return false;
		}
		else
		{
			isCurrentVersionAnUpgradeFromInstalledVersion = (installedAppVersion.equalsIgnoreCase(currentAppVersion) == false);
		}
		
		return isCurrentVersionAnUpgradeFromInstalledVersion;
	}
	
	
	/**
	 * This method checks if user has seen the tutorial or
	 * if the user has seen the tutorial && not opened the
	 * app for at least two weeks, then we will show the
	 * tutorial again to the user.
	 * 
	 * @return
	 */
	public boolean hasUserSeenTutorial() {
		
		boolean hasUserSeenTutorial = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_USER_HAS_SEEN_TUTORIAL, false);
		boolean neverShowTutorialAgain = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_TUTORIAL_SHOULD_NEVER_START_AGAIN, false);
		
		String lastOpenApp = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_DATE_LAST_OPEN_APP, "");
		Calendar now = DateUtils.getNow();
		
		if (hasUserSeenTutorial) {
			
			if (!neverShowTutorialAgain) {
				
				if (!lastOpenApp.isEmpty() && !lastOpenApp.equals("")) {
					
					/* Get calendar from the string lastOpenApp */
					Calendar cal = getDateUserLastOpenApp(lastOpenApp);
					
					/* 
					 * TRUE: If app has been open in last two weeks, tutorial will NOT show.
					 * FALSE: if app has not been open in last two weeks, tutorial will show.
					 */
					boolean openLastTwoWeeks = checkIfUserOpenedAppLastTwoWeeks(now, cal);
					
					/* Sets user is viewing tutorial */
					if (!openLastTwoWeeks) {
						setIsViewingTutorial(true);
					}
					
					return openLastTwoWeeks;
				}
			}
		}
		
		if (!hasUserSeenTutorial) {
			setIsViewingTutorial(true);
		}
		
		return hasUserSeenTutorial;
	}
	
	
	
	/* No handling when new year, just returns true, which means that the tutorial will not show. */
	private boolean checkIfUserOpenedAppLastTwoWeeks(Calendar now, Calendar lastTime) {
		
		if (lastTime.before(now)) {
			int a = now.get(Calendar.DAY_OF_YEAR);
			int b = lastTime.get(Calendar.DAY_OF_YEAR);
			
			int difference = a-b;
			
			if (difference > 13) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
	private Calendar getDateUserLastOpenApp(String lastOpenApp) {
		Calendar cal = Calendar.getInstance();
		
	    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
		
		try {
			cal.setTime(sdf.parse(lastOpenApp));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return cal;
	}
	
	
	
	public void setIsViewingTutorial(boolean isViewingTutorial) {
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, isViewingTutorial, false);
	}
	
	
	
	public boolean getIsViewingTutorial() {
		boolean isViewingTutorial = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, false);
		
		return isViewingTutorial;
	}
	
	
	
	public void setUserSeenTutorial() {
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_USER_HAS_SEEN_TUTORIAL, true, false);
	}
	
	
	
	public void setDateUserLastOpenedApp(String date) {
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_DATE_LAST_OPEN_APP, date, false);
	}
	
	
	
	public void setTutorialToNeverShowAgain() {
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_TUTORIAL_SHOULD_NEVER_START_AGAIN, true, false);
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, false, false);
	}

	
	
	public ContentManager getContentManager() 
	{
		if(contentManager == null) {
			contentManager = new ContentManager();
		}
		return contentManager;
	}
}