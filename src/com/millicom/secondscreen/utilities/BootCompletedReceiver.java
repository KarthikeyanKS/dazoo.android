package com.millicom.secondscreen.utilities;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.w(TAG, "App started by OS when booting, means that this version of the app was preinstalled on the device => persist the info that the app was preinstalled");
    	setWasPreinstalled();
    }
    
    public static void setWasPreinstalled() {
    	SecondScreenApplication application = SecondScreenApplication.getInstance();
    	Context applicationContext = application.getApplicationContext();
    	SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putBoolean(Consts.PREFS_KEY_APP_WAS_PREINSTALLED, true);
    	editor.commit();
    }
    
    public static boolean wasPreinstalled() {
    	SecondScreenApplication application = SecondScreenApplication.getInstance();
    	Context applicationContext = application.getApplicationContext();
		SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Consts.SHARED_PREFS_MAIN_NAME, Context.MODE_PRIVATE);
		boolean wasPreinstalled = sharedPreferences.getBoolean(Consts.PREFS_KEY_APP_WAS_PREINSTALLED, false);
		
		return wasPreinstalled;
    }

}