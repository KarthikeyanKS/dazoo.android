package com.mitv.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;

public class BootCompletedReceiver extends BroadcastReceiver {


	private static final String TAG = "BootCompletedReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


        	/* Write to Shared Preferences */
	        boolean startedOnceBeforeSharedPrefs = SecondScreenApplication.getInstance().getWasPreinstalled();
	        if(!startedOnceBeforeSharedPrefs) {
	        	SecondScreenApplication.getInstance().setWasPreinstalled();
	        }
	        
	        /* Write file to external storage */
	        boolean startedOnceBeforeExternalStorage = SecondScreenApplication.getInstance().wasPreinstalledFileExists();
        	SecondScreenApplication.getInstance().saveWasPreinstalledFile();
        	
        	/* IF this was the first time the app started, using Google Analytics send "Preinstalled user booted device" */
        	if(startedOnceBeforeSharedPrefs && startedOnceBeforeExternalStorage) {
	        	String hardCodedTrackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
	        	GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
	    		Tracker tracker = googleAnalyticsInstance.getTracker(hardCodedTrackingId);
	    		
	    		
	    		String gaValue = Consts.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT;
	    		
	    		tracker.send(MapBuilder
	    				  .createEvent("SystemEvent", "OnBoot", gaValue, null) 	// Set any additional fields for this hit.
	    				  .build()                                   			// Build and return the Map to the send method.
	    				);
        	}
	    }		
	}
	
	
	
//	public static void setWasPreinstalled() {
//		SecondScreenApplication application = SecondScreenApplication.getInstance();
//		Context applicationContext = application.getApplicationContext();
//		SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = sharedPreferences.edit();
//		editor.putBoolean(Consts.PREFS_KEY_APP_WAS_PREINSTALLED, true);
//		editor.commit();
//	}
//
//	public static boolean wasPreinstalled() {
//		SecondScreenApplication application = SecondScreenApplication.getInstance();
//		Context applicationContext = application.getApplicationContext();
//		SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);
//		boolean wasPreinstalled = sharedPreferences.getBoolean(Consts.PREFS_KEY_APP_WAS_PREINSTALLED, false);
//
//		return wasPreinstalled;
//	}
}