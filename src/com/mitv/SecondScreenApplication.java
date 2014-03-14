
package com.mitv;



import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.mitv.activities.SplashScreenActivity;
import com.mitv.utilities.AppDataUtils;
import com.mitv.utilities.GenericUtils;



public class SecondScreenApplication 
	extends Application 
{
	private static final String TAG = SecondScreenApplication.class.getName();

	
	private static SecondScreenApplication instance;

	
	
	public SecondScreenApplication() 
	{}

	
	
	public static SecondScreenApplication sharedInstance() 
	{
		if (instance == null) 
		{
			instance = new SecondScreenApplication();
		}

		return instance;
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
			Log.e(TAG, e.getMessage(), e);
		}
		
		return false;
	}

	
	
	public static boolean applicationIsSystemAppUsingFlag(Context context)
	{
		String packageName = context.getPackageName();

		try 
		{
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);

			// FLAG_SYSTEM is only set to system applications,
			// This should work even if application is installed in external storage

			// Check if package is system app
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) 
			{
				return true;
			}
		}
		catch (NameNotFoundException e) 
		{
			Log.e(TAG, e.getMessage(), e);
		}

		return false;
	}

	
	
	@Override
	public void onCreate() 
	{
		super.onCreate();

		/* Initial call to AppDataUtils, in order to initialize the SharedPreferences object */
		AppDataUtils.sharedInstance(this);
		
		/* Initial call to ImageLoaderManager, in order to configure the image loader objects */
		ImageLoaderManager.sharedInstance(this);
		
		instance = this;

		boolean enableStrictMode = Constants.ENABLE_STRICT_MODE;

		if (enableStrictMode) 
		{
			StrictMode.ThreadPolicy threadPolicy;
			
			/* The following policies are not available prior to API Level 11:
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
			
			/* The following policies are not available prior to API Level 11:
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
		
		if(Constants.FORCE_DATABASE_FLUSH || isCurrentVersionAnUpgradeFromInstalledVersion())
		{
			ContentManager.clearAllPersistentCacheData();
			
			AppDataUtils.sharedInstance(this).clearAllPreferences();
		}

		setInstalledAppVersionToCurrentVersion();
	}
	

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	
	public ImageLoaderManager getImageLoaderManager()
	{
		return ImageLoaderManager.sharedInstance(this); 
	}
	
	
	public void setAppAsPreinstalled() 
	{
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_WAS_PREINSTALLED, true);
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
		
		AppDataUtils.sharedInstance(this).setPreference(Constants.SHARED_PREFERENCES_APP_INSTALLED_VERSION, currentVersion);
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

}