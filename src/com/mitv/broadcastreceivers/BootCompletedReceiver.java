package com.mitv.broadcastreceivers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.AppConfigurationManager;

public class BootCompletedReceiver extends BroadcastReceiver {


	private static final String TAG = "BootCompletedReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


        	/* Write to Shared Preferences */
	        boolean startedOnceBeforeSharedPrefs = SecondScreenApplication.sharedInstance().getWasPreinstalled();
	        if(!startedOnceBeforeSharedPrefs) {
	        	SecondScreenApplication.sharedInstance().setWasPreinstalled();
	        }
	        
	        /* Write file to external storage */
	        boolean startedOnceBeforeExternalStorage = wasPreinstalledFileExists();
        	saveWasPreinstalledFile();
        	
        	/* IF this was the first time the app started, using Google Analytics send "Preinstalled user booted device" */
        	if(startedOnceBeforeSharedPrefs && startedOnceBeforeExternalStorage) {
	        	String hardCodedTrackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
	        	GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
	    		Tracker tracker = googleAnalyticsInstance.getTracker(hardCodedTrackingId);
	    		
	    		
	    		String gaValue = Consts.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT;
	    		gaValue = AppConfigurationManager.replaceDashWithEnDash(gaValue);
	    		
	    		tracker.send(MapBuilder
	    				  .createEvent(Consts.GA_EVENT_KEY_SYSTEM_EVENT, "OnBoot", gaValue, null) 	// Set any additional fields for this hit.
	    				  .build()                                   			// Build and return the Map to the send method.
	    				);
        	}
	    }		
	}
	
//	private static File appWasPreinstalledFileInternalStorage() {
//		File file = null;
//		
//		ContextWrapper cw = new ContextWrapper(SecondScreenApplication.getInstance().getApplicationContext());
//		File directory = cw.getDir("media", Context.MOD);
//	}
	
	private static File appWasPreinstalledFile() {
		File file = null;

		if (isExternalStorageReadable()) {
			String root = Environment.getExternalStorageDirectory().toString();
			try {
				Locale locale = SecondScreenApplication.getCurrentLocale();
				if (locale == null) {
					locale = Locale.getDefault();
				}

				String filePath = String.format(locale, "%s/Android/data/", root);

				File myDir = new File(filePath);
				myDir.mkdirs();

				String fname = Consts.APP_WAS_PREINSTALLED_FILE_NAME;
				file = new File(myDir, fname);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;
	}
	
	private static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean wasPreinstalledFileExists() {
		boolean wasPreinstalledFileExists = false;

		if(isExternalStorageReadable()) {
			File file = appWasPreinstalledFile();

			if (file != null) {
				wasPreinstalledFileExists = file.exists();
			}
		}
		return wasPreinstalledFileExists;
	}
	
	private static void saveWasPreinstalledFile() {
		File file = appWasPreinstalledFile();
		if (file != null) {
			if (!wasPreinstalledFileExists()) {
				if (isExternalStorageWritable()) {
					try {
						FileOutputStream os = new FileOutputStream(file, true);
						OutputStreamWriter out = new OutputStreamWriter(os);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}