
package com.mitv;



import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.mitv.enums.UserTutorialStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.ImageLoaderManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.UserTutorialStatus;
import com.mitv.utilities.AppDataUtils;
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
				
		if(ContentManager.sharedInstance().getCacheManager().isLoggedIn()) 
		{
			String userId = ContentManager.sharedInstance().getCacheManager().getUserId();
			
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
		
		boolean shouldFlushData = (isCurrentVersionAnUpgradeFromInstalledVersion() && isCurrentORMDatabaseVersionGreaterThenInstalledORMDatabaseVersion());
		
		boolean forceDataFlush = Constants.FORCE_CACHE_DATABASE_FLUSH;
		
		if(forceDataFlush || shouldFlushData)
		{
			ContentManager.sharedInstance().getCacheManager().clearAllPersistentCacheData();
			
			AppDataUtils.sharedInstance(this).clearAllPreferences(false);
		}

		setInstalledAppVersionToCurrentVersion();
		
		setInstalledORMDatabaseVersionToCurrentVersion();
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
	
	
	
	private int getInstalledORMDatabaseVersion()
	{
		return AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_APP_INSTALLED_ORM_DATABASE_VERSION, Constants.CACHE_DATABASE_VERSION);
	}
	
	
	
	private void setInstalledORMDatabaseVersionToCurrentVersion()
	{
		int currentORMDatabaseVersion = Constants.CACHE_DATABASE_VERSION;
		
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_INSTALLED_ORM_DATABASE_VERSION, currentORMDatabaseVersion, false);
	}
	
	
	
	private boolean isCurrentORMDatabaseVersionGreaterThenInstalledORMDatabaseVersion()
	{
		boolean isCurrentORMDatabaseVersionGreaterThenInstalledVersion;
		
		int installedORMDatabaseVersion = getInstalledORMDatabaseVersion();
		
		int currentORMDatabaseVersion = Constants.CACHE_DATABASE_VERSION;
		
		if(currentORMDatabaseVersion > installedORMDatabaseVersion)
		{
			isCurrentORMDatabaseVersionGreaterThenInstalledVersion = true;
		}
		else
		{
			isCurrentORMDatabaseVersionGreaterThenInstalledVersion = false;
		}
		
		return isCurrentORMDatabaseVersionGreaterThenInstalledVersion;
	}
	
	
	
	/**
	 * This method checks if user has seen the tutorial or
	 * if the user has seen the tutorial && not opened the
	 * app for at least two weeks, then we will show the
	 * tutorial again to the user.
	 * 
	 * @return
	 */
	public boolean hasUserSeenTutorial()
	{
		boolean hasSeenTutorial = true;
		
		UserTutorialStatus userTutorialStatus = ContentManager.sharedInstance().getCacheManager().getUserTutorialStatus();
		
		UserTutorialStatusEnum status = userTutorialStatus.getUserTutorialStatus();
		
		switch (status) 
		{
			case NEVER_SEEN_TUTORIAL:
			{
				hasSeenTutorial = false; 
			
				ContentManager.sharedInstance().getCacheManager().setUserHasSeenTutorialOnce();
				
				break;
			}
		
			/* The user has seen the tutorial once, we now need to check if the user has open the app the last two weeks */
			case SEEN_ONCE: {
				
				/* 
				 * TRUE: If app has been open in last two weeks, tutorial will NOT show.
				 * FALSE: if app has not been open in last two weeks, tutorial will show.
				 */
				boolean openLastTwoWeeks = ContentManager.sharedInstance().checkIfUserOpenedAppLastTwoWeeks();
				
				if (!openLastTwoWeeks) {
					hasSeenTutorial = false;
					
					ContentManager.sharedInstance().getCacheManager().setUserHasSeenTutorialTwice();
				}
				
				break;
			}
			
			case NEVER_SHOW_AGAIN: {
				/* Do nothing, we just return false */
				break;
			}
			
			default:
				break;
		}
			
		
		
		if (!hasSeenTutorial) 
		{
			setIsViewingTutorial(true);
		}
		
		return hasSeenTutorial;
	}
	
	
	
	/**
	 * Splashscreen activity uses this pref if onResumed in the middle of the tutorial.
	 * 
	 * @param isViewingTutorial
	 */
	public void setIsViewingTutorial(boolean isViewingTutorial)
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, isViewingTutorial, true);
	}
	
	
	
	public boolean getIsViewingTutorial()
	{
		boolean isViewingTutorial = AppDataUtils.sharedInstance(this).getPreference(Constants.SHARED_PREFERENCES_IS_VIEWING_TUTORIAL, false);
		
		return isViewingTutorial;
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