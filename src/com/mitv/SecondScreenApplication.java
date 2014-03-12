
package com.mitv;



import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import com.mitv.utilities.AppDataUtils;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;



public class SecondScreenApplication 
	extends Application 
{
	private static final String TAG = SecondScreenApplication.class.getName();

	
	private static SecondScreenApplication instance;

	
	
	public SecondScreenApplication() {}

	
	
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
			// this will work even if application is installed in external storage

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

		instance = this;

		// Used in views where we don't want to reset the view itself
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(displayImageOptions)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileCount(100)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		
		ImageLoader.getInstance().init(config);

		L.disableLogging();
		
		boolean enableStrictMode = Constants.ENABLE_STRICT_MODE;

		if (enableStrictMode) 
		{
			 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
             .detectDiskReads()
             .detectDiskWrites()
             .detectNetwork()
             .penaltyLog()
             .penaltyFlashScreen()
             .build());
     
			 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
             .detectLeakedSqlLiteObjects()
             .detectLeakedClosableObjects()
             .penaltyLog()
             .build());
		}
		
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
	}
	

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	
	
	public void setWasPreinstalled() 
	{
		AppDataUtils.setPreference(Constants.SHARED_PREFERENCES_APP_WAS_PREINSTALLED, true);
	}

	
	
	public boolean getWasPreinstalled() 
	{
		return AppDataUtils.getPreference(Constants.SHARED_PREFERENCES_APP_WAS_PREINSTALLED, false);
	}
}
