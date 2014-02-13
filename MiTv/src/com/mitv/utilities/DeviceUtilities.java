package com.mitv.utilities;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.mitv.SecondScreenApplication;

public class DeviceUtilities {
	
	//TODO change this to a pseudo unique own generated ID instead: http://stackoverflow.com/a/17625641 ??!
	public static String getDeviceId() {
		String deviceId = null;
		
		Context context = SecondScreenApplication.getInstance().getApplicationContext();
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		
		return deviceId;
	}
	
}
