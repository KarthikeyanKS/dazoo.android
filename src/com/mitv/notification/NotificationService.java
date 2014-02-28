
package com.mitv.notification;


import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.Consts;
import com.mitv.R;



public class NotificationService
{
	@SuppressWarnings("unused")
	private static final String	TAG	= NotificationService.class.getName();
	
		
	public static void setAlarm(Context context, TVBroadcastWithChannelInfo broadcast, int notificationId)
	{
		// Call alarm manager to set the notification at the certain time
		Intent intent = getAlarmIntent(notificationId, broadcast);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar = broadcast.getBroadcastBeginTimeForNotification();

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
	
	
	
	public static void setAlarm(Context context, TVBroadcastWithChannelInfo broadcast) 
	{
		Random random = new Random();
		
		int notificationId = random.nextInt(Integer.MAX_VALUE);
		
		// Call alarm manager to set the notification at the certain time
		Intent intent = getAlarmIntent(notificationId, broadcast);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar = broadcast.getBroadcastBeginTimeForNotification();

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

		NotificationDataSource notificationDataSource = new NotificationDataSource(context);

		NotificationSQLElement dbNotification = new NotificationSQLElement(notificationId, broadcast);
		
		notificationDataSource.addNotification(dbNotification);
	}
	
	
	
	private static Intent getAlarmIntent(int notificationId, TVBroadcastWithChannelInfo broadcast) 
	{
		Intent intent = new Intent(Consts.INTENT_NOTIFICATION);

		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS, broadcast.getBeginTimeMillis());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNELID, broadcast.getChannel().getChannelId().getChannelId());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_NOTIFICIATION_ID, notificationId);
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_NAME, broadcast.getChannel().getName());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL, broadcast.getChannel().getImageUrl());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_NAME, broadcast.getProgram().getTitle());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_HOUR_AND_MINUTE_TIME, broadcast.getBeginTimeHourAndMinuteLocalAsString());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_DATE_DATE, broadcast.getBeginTimeDateRepresentation());
		
		return intent;
	}

	
	
	public static void showNotification(
			Context context, 
			long broadcastBeginTimeMillis, 
			String broadcastHourAndMinuteRepresentation, 
			String broadcastName, 
			String channelId, 
			String channelName, 
			String channelLogoUrl, 
			String dateDate, 
			int notificationId)
	{	
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent broadcastPageIntent = new Intent(context, BroadcastPageActivity.class);
		
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastBeginTimeMillis);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, channelId);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);
		broadcastPageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mitv_notification_large_icon);

		NotificationCompat.Builder notificationBuilder;

		notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.mitv_notification_small_icon)
		.setLargeIcon(largeIcon)
		.setContentTitle(broadcastName)
		.setContentText(broadcastHourAndMinuteRepresentation + " " + channelName)
		.setContentIntent(PendingIntent.getActivity(context, 1, broadcastPageIntent, PendingIntent.FLAG_UPDATE_CURRENT))
		.setAutoCancel(true)
		.setWhen(System.currentTimeMillis())
		.setDefaults(Notification.DEFAULT_ALL); // default sound, vibration, light
		notificationManager.notify(notificationId, notificationBuilder.build());

		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		
		notificationDataSource.removeNotification(notificationId);
	}

	
	
	public static void removeNotification(Context context, int notificationId) 
	{
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(notificationId);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent(Consts.INTENT_NOTIFICATION);
		
		PendingIntent sender = PendingIntent.getBroadcast(context, notificationId, intent, 0);
		
		alarmManager.cancel(sender);

		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		
		notificationDataSource.removeNotification(notificationId);
	}
}