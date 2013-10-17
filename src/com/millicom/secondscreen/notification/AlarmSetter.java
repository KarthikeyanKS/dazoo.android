package com.millicom.secondscreen.notification;

import java.util.List;

import com.millicom.secondscreen.content.model.NotificationDbItem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class AlarmSetter extends BroadcastReceiver {

	//private static final int PERIOD=5000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// set the alarms on the phone reboot
		scheduleAlarms(context);
	}

	static void scheduleAlarms(Context context) {
		// get the list of alarms
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		
		for(int i=0; i<notificationList.size(); i++){
			
			//NotificationService.setAlarm(context, broadcast, channel);
		}
	}
}
