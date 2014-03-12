
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
	        boolean startedOnceBeforeSharedPrefs = SecondScreenApplication.sharedInstance().isAppPreinstalled();
	      
	        if(!startedOnceBeforeSharedPrefs) 
	        {
	        	SecondScreenApplication.sharedInstance().setAppAsPreinstalled();
	        }
	        
	        File file = FileUtils.getFile(Constants.APP_WAS_PREINSTALLED_FILE_NAME);
	        
	        /* Write file to external storage */
	        boolean startedOnceBeforeExternalStorage = FileUtils.fileExists(file);
        	
	        FileUtils.saveFile(file);
        	
        	/* IF this was the first time the app started, using Google Analytics send "Preinstalled user booted device" */
        	if(startedOnceBeforeSharedPrefs && startedOnceBeforeExternalStorage) 
        	{
	        	String hardCodedTrackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
	        	
	        	GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
	    		
	        	Tracker tracker = googleAnalyticsInstance.getTracker(hardCodedTrackingId);
	    		
	    		String gaValue = Constants.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT;
	    		
	    		gaValue = GenericUtils.replaceDashWithEnDash(gaValue);
	    		
	    		tracker.send(MapBuilder
	    				  .createEvent(Constants.GA_EVENT_KEY_SYSTEM_EVENT, "OnBoot", gaValue, null) 	// Set any additional fields for this hit.
	    				  .build()                                   			// Build and return the Map to the send method.
	    				);
        	}
	    }		
	}	
}