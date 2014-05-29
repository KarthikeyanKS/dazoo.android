
package com.mitv;



import java.io.File;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.mitv.managers.ContentManager;
import com.mitv.managers.ImageLoaderManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.utilities.AppDataUtils;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.FileUtils;
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
	
	
	
	public static boolean isContentManagerNull() 
	{
		SecondScreenApplication app = sharedInstance;
		
		if(app != null) 
		{
			return (app.contentManager == null);
		} 
		else
		{
			return true;
		}
	}
	
	
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate() 
	{
		super.onCreate();

		sharedInstance = this;
		
		if(isAppRestarting()) 
		{
			Log.e(TAG, "AppIsRestarging was true, setting it false");
			
			setAppIsRestarting(false);
		}
		
		if(ContentManager.sharedInstance().isLoggedIn()) 
		{
			String userId = ContentManager.sharedInstance().getFromCacheUserId();
			
			TrackingGAManager.sharedInstance().setUserIdOnTracker(userId);
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
		
		Log.w(TAG, "Running low on memory.");
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
		
		/* Checking if user has seen tutorial before from file, needed to not show the tutorial every time the app updates to a new version */
		File fileOnce = FileUtils.getFile(Constants.USER_HAS_SEEN_TUTORIAL__ONCE_FILE_NAME);
		File fileTwice = FileUtils.getFile(Constants.USER_HAS_SEEN_TUTORIAL_TWICE_FILE_NAME);
		
		boolean hasUserSeenTutorialOnceFromFile = FileUtils.fileExists(fileOnce);
		boolean hasUserSeenTutorialTwiceFromFile = FileUtils.fileExists(fileTwice);
		
		boolean hasUserSeenTutorial = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_USER_HAS_SEEN_TUTORIAL, false);
		boolean neverShowTutorialAgain = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_TUTORIAL_SHOULD_NEVER_START_AGAIN, false);
		
		String lastOpenAppAsString = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_DATE_LAST_OPEN_APP, "");
		
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		if (hasUserSeenTutorial && hasUserSeenTutorialOnceFromFile)
		{	
			if (!neverShowTutorialAgain && !hasUserSeenTutorialTwiceFromFile)
			{	
				if (lastOpenAppAsString != null && 
					lastOpenAppAsString.isEmpty() == false)
				{
					/* Get calendar from the string lastOpenApp */
					Calendar cal = DateUtils.convertISO8601StringToCalendar(lastOpenAppAsString);
					
					/* 
					 * TRUE: If app has been open in last two weeks, tutorial will NOT show.
					 * FALSE: if app has not been open in last two weeks, tutorial will show.
					 */
					boolean openLastTwoWeeks = checkIfUserOpenedAppLastTwoWeeks(now, cal);
					
					/* Sets user is viewing tutorial */
					if (!openLastTwoWeeks)
					{
						setTutorialToNeverShowAgain();
						
						setIsViewingTutorial(true);
						
						FileUtils.saveFile(fileTwice);
					}
					
					return openLastTwoWeeks;
				}
			}
		}
		
		if (!hasUserSeenTutorial && !hasUserSeenTutorialOnceFromFile) 
		{
			setIsViewingTutorial(true);
			
			FileUtils.saveFile(fileOnce);
		}
		
		return hasUserSeenTutorial;
	}
	
	
	
	/* No handling when new year, just returns true, which means that the tutorial will not show. */
	private boolean checkIfUserOpenedAppLastTwoWeeks(Calendar now, Calendar lastTime)
	{	
		if (lastTime.before(now))
		{
			int a = now.get(Calendar.DAY_OF_YEAR);
			int b = lastTime.get(Calendar.DAY_OF_YEAR);
			
			int difference = a-b;
			
			if (difference > 13) 
			{
				return false;
			}
		}

		return true;
	}
	

	
	
	public void setIsViewingTutorial(boolean isViewingTutorial)
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, isViewingTutorial, true);
	}
	
	
	
	public boolean getIsViewingTutorial()
	{
		boolean isViewingTutorial = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, false);
		
		return isViewingTutorial;
	}
	
	
	
	public void setUserSeenTutorial() 
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_USER_HAS_SEEN_TUTORIAL, true, false);
	}
	
	
	
	public void setDateUserLastOpenedApp(Calendar calendar) 
	{
		String calendarRepresentationAsString = DateUtils.convertFromCalendarToISO8601String(calendar);
		
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_DATE_LAST_OPEN_APP, calendarRepresentationAsString, false);
	}
	
	
	
	public void setTutorialToNeverShowAgain() 
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_TUTORIAL_SHOULD_NEVER_START_AGAIN, true, true);
	}

	
	
	public ContentManager getContentManager() 
	{
		if(contentManager == null) 
		{
			contentManager = new ContentManager();
		}
		
		return contentManager;
	}
}