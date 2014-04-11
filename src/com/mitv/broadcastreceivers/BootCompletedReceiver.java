
package com.mitv.broadcastreceivers;



import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mitv.Constants;
import com.mitv.GATrackingManager;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.FileUtils;



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
	        if(Constants.IS_PREINSTALLED_VERSION) 
	        {
	        	SecondScreenApplication.sharedInstance().setAppAsPreinstalled();
	        	
	        	FileUtils.saveFile(file);
	        }
        	
        	/* If this was the first time the app started, using Google Analytics send "Preinstalled user booted device" */
        	if(!startedOnceBeforeSharedPrefs && !startedOnceBeforeExternalStorage && Constants.IS_PREINSTALLED_VERSION) 
        	{
        		/* Will only get here first time app boots */
	        	GATrackingManager.sharedInstance().sendFirstBootEvent();
        	}
	    }		
	}	
}