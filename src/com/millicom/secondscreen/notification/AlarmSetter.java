package com.millicom.secondscreen.notification;

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
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//Intent i = new Intent(context, ScheduledService.class);
		//PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		//
		//mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);
	}
}
