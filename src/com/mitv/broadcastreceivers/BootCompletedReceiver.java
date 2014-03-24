
package com.mitv.broadcastreceivers;



import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.FileUtils;
import com.mitv.utilities.GenericUtils;



public class BootCompletedReceiver 
	extends BroadcastReceiver 
{
	@SuppressWarnings("unused")
	private static final String TAG = BootCompletedReceiver.class.getName();


	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			/* Will only be false first time app boots */
	        boolean startedOnceBeforeSharedPrefs = SecondScreenApplication.sharedInstance().isAppPreinstalled();
	              
	        File file = FileUtils.getFile(Constants.APP_WAS_PREINSTALLED_FILE_NAME);
	        
	        /* Will only be false first time app boots */
	        boolean startedOnceBeforeExternalStorage = FileUtils.fileExists(file);
	        
	        /* Write file to external storage */
	        if(Constants.IS_PREINSTALLED_VERSION) {
	        	SecondScreenApplication.sharedInstance().setAppAsPreinstalled();
	        	FileUtils.saveFile(file);
	        }
        	
        	/* If this was the first time the app started, using Google Analytics send "Preinstalled user booted device" */
        	if(!startedOnceBeforeSharedPrefs && !startedOnceBeforeExternalStorage) 
        	{
        		/* Will only get here first time app boots */
	        	String hardCodedTrackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
	        	
	        	GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
	    		
	        	Tracker tracker = googleAnalyticsInstance.getTracker(hardCodedTrackingId);
	    		
	    		String gaValue = Constants.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT;

	    		tracker.send(MapBuilder
	    				  .createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_EVENT_KEY_ACTION_FIRST_BOOT, gaValue, null) 	// Set any additional fields for this hit.
	    				  .build()                                   			// Build and return the Map to the send method.
	    				);
        	}
	    }		
	}	
}