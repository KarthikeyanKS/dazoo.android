package com.millicom.secondscreen.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.millicom.secondscreen.SecondScreenApplication;

public class BootCompletedReceiver extends BroadcastReceiver {


	private static final String TAG = "BootCompletedReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

	        boolean startedOnceBefore = SecondScreenApplication.getInstance().getWasPreinstalled();
	        if(!startedOnceBefore) {
	        	SecondScreenApplication.getInstance().setWasPreinstalled();
	        }
	    }
		
	}
}