package com.millicom.secondscreen.notification;

import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;

import com.millicom.secondscreen.content.model.Broadcast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationService{

	private AlarmManager					alarmManager;
	private HashMap<Integer, Notification>	notifications;
	private int								lastId	= 0;
	private static NotificationService		instance;

	public static boolean setNotification(Context context, Broadcast broadcast) {
		String broadcastUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + broadcast.getChannel().getChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillis();
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent broadcastPageIntent = new Intent();
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
		
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(context.getResources().getString(R.string.app_name))
		.setContentText(
				context.getResources().getString(R.string.dazoo_notification_text_start) + " " + broadcast.getProgram().getTitle()
				+ context.getResources().getString(R.string.dazoo_notification_text_middle) + " " + broadcast.getChannel().getName()
				+ context.getResources().getString(R.string.dazoo_notification_text_end))
				.setContentIntent(PendingIntent.getActivity(context, 0, broadcastPageIntent, 0))
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_ALL); // default sound, vibration, light

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);

		Date now = new Date();
		long uniqueId = now.getTime();
		notificationManager.notify((int)uniqueId, notificationBuilder.build());

		return true;
	}

	public static boolean removeNotification(Context context, int notificationId) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
		
		// remove notification from storage
		
		return true;
	}
}
