package com.millicom.secondscreen.notification;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.utilities.DateUtilities;

public class NotificationService {

	private static final String	TAG	= "NotificationService";

	public static boolean setAlarm(Context context, Broadcast broadcast, Channel channel) {

		Date now = new Date();
		int notificationId = (int) now.getTime();

		// store the notificationId to remove in case of necessity

		// call alarm manager to set the notification at the certain time
		Intent intent = new Intent(context, DazooNotification.class);
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST, broadcast);
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL, channel);
		intent.putExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, notificationId);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(context, notificationId, intent, 0);

		Calendar calendar;
		try {
			calendar = DateUtilities.getTimeFifteenMinBefore(broadcast.getBeginTime());

			Log.d(TAG,
					"DATE VALUES: YEAR" + calendar.get(Calendar.YEAR) + " month: " + calendar.get(Calendar.MONTH) + " day: " + calendar.get(Calendar.DAY_OF_MONTH) + "hour: "
							+ calendar.get(Calendar.HOUR) + "minute: " + calendar.get(Calendar.MINUTE));
			alarmManager.set(AlarmManager.ELAPSED_REALTIME, calendar.getTimeInMillis(), pendingIntent);

			Log.d(TAG, "Notification time: " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR) + " " + calendar.get(Calendar.MINUTE));
			return true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean showNotification(Context context, Broadcast broadcast, Channel channel, int notificationId) {

		String broadcastUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + channel.getId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent broadcastPageIntent = new Intent();
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
		broadcastPageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		int number = ((SecondScreenApplication) context.getApplicationContext()).getNotificationNumber();

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(context.getResources().getString(R.string.app_name))
		.setContentText(
				context.getResources().getString(R.string.dazoo_notification_text_start) + " " + broadcast.getProgram().getTitle()
				+ context.getResources().getString(R.string.dazoo_notification_text_middle) + " " + channel.getName()
				+ context.getResources().getString(R.string.dazoo_notification_text_end)).setContentIntent(PendingIntent.getActivity(context, 0, broadcastPageIntent, 0))
				.setAutoCancel(true).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL) // default sound, vibration, light
				.setNumber(number);

		notificationManager.notify(notificationId, notificationBuilder.build());

		// update the number of fired notifications in the status bar
		((SecondScreenApplication) context.getApplicationContext()).setNotificationNumber(number + 1);

		return true;
	}

	public static boolean removeNotification(Context context, int notificationId) {
		// NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// notificationManager.cancel(notificationId);

		// remove alarm
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, DazooNotification.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, notificationId, intent, 0);
		alarmManager.cancel(sender);

		// remove notification from database

		return true;
	}
}
