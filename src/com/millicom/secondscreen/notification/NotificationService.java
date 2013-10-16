package com.millicom.secondscreen.notification;

import com.millicom.secondscreen.content.model.Broadcast;

import com.millicom.secondscreen.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationService {

	public static void createNotification(Activity activity, Broadcast broadcast) {
		
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity).
				setSmallIcon(R.drawable.ic_launcher).
				setContentTitle(activity.getResources().getString(R.string.app_name)).
				setContentText(activity.getResources().getString(R.string.dazoo_notification_text_start) + " " + broadcast.getProgram().getTitle() + 
						       activity.getResources().getString(R.string.dazoo_notification_text_middle) + " " + broadcast.getChannel().getName() + 
						       activity.getResources().getString(R.string.dazoo_notification_text_end)).
			    setContentIntent(PendingIntent.getActivity(activity, 0, new Intent(), 0));
		
		NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		//notificationManager.notify(id, mBuilder.build());
		
	}

	public static boolean setNotification(Broadcast broadcast){
		return true;
	}
	
	public static boolean removeNotification(Broadcast broadcast){
		return true;
	}
	
}
