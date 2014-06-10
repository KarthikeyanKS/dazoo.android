
package com.mitv.ui.helpers;



import java.util.List;
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
import android.util.Log;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.enums.NotificationTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.utilities.DateUtils;



public class NotificationHelper
{
	private static final String	TAG	= NotificationHelper.class.getName();
	
	
	
	public static void scheduleNotifications(final Context context) 
	{
		List<com.mitv.models.objects.mitvapi.Notification> notifications = ContentManager.sharedInstance().getFromCacheNotifications();
		
		for(com.mitv.models.objects.mitvapi.Notification element : notifications)
		{
			NotificationHelper.scheduleNotification(context, element);
		}
	}
		
	
	
	public static void scheduleNotification(
			final Context context,
			final com.mitv.models.objects.mitvapi.Notification notification) 
	{
		Random random = new Random();
		
		int notificationId = random.nextInt(Integer.MAX_VALUE);
		
		notification.setNotificationId(notificationId);
		
		Intent intent = getAlarmIntent(notification);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		long millisecondsBeforeNotification = (Constants.NOTIFY_MINUTES_BEFORE_THE_BROADCAST * DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
		long alarmTimeInMillis = notification.getBeginTimeInMilliseconds();

		alarmTimeInMillis = alarmTimeInMillis - millisecondsBeforeNotification;
		
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);

		ContentManager.sharedInstance().addToCacheNotifications(notification);
	}
	
	
	
	private static Intent getAlarmIntent(final com.mitv.models.objects.mitvapi.Notification notification) 
	{
		int notificationId = notification.getNotificationId();
		
		Intent intent = new Intent(Constants.INTENT_NOTIFICATION);
		intent.putExtra(Constants.INTENT_NOTIFICATION_EXTRA_NOTIFICATION_ID, notificationId);
		
		return intent;
	}

	
	
	public static void showNotification(
			final Context context, 
			final int notificationID)
	{	
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		com.mitv.models.objects.mitvapi.Notification notification = ContentManager.sharedInstance().getFromCacheNotificationWithId(notificationID);
		
		if(notification != null)
		{
			Intent intent;
			
			StringBuilder notificationTitleSB = new StringBuilder();
			StringBuilder notificationTextSB = new StringBuilder();
			
			NotificationTypeEnum notificationType = notification.getNotificationType();
			
			switch (notificationType) 
			{
				case TV_BROADCAST:
				{
					intent = new Intent(context, BroadcastPageActivity.class);
					intent.putExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, notification.getBeginTimeInMilliseconds());
					intent.putExtra(Constants.INTENT_EXTRA_CHANNEL_ID, notification.getChannelId());
					intent.putExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);
					intent.putExtra(Constants.INTENT_EXTRA_IS_FROM_NOTIFICATION, true);
					
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
					notificationTitleSB.append(notification.getBroadcastTitle());
					
					String broadcastHourAndMinuteRepresentation = notification.getBeginTimeHourAndMinuteLocalAsString();
					
					String channelName = notification.getBroadcastChannelName();
					
					notificationTextSB.append(broadcastHourAndMinuteRepresentation)
					.append(" ")
					.append(channelName);
					
					break;
				}
				
				case COMPETITION_EVENT:
				{
					intent = new Intent(context, EventPageActivity.class);

					intent.putExtra(Constants.INTENT_COMPETITION_ID, notification.getCompetitionId());
					
					intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, notification.getEventId());
					
					/* WARNING using constant here to get the competition.getDisplayName() */
					intent.putExtra(Constants.INTENT_COMPETITION_NAME, Constants.FIFA_EVENT_PAGE_HEADER);

					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
					notificationTitleSB.append(notification.getBroadcastTitle());
					
					String broadcastHourAndMinuteRepresentation = notification.getBeginTimeHourAndMinuteLocalAsString();
					
					String channelName = notification.getBroadcastChannelName();
					
					notificationTextSB.append(broadcastHourAndMinuteRepresentation)
					.append(" ")
					.append(channelName);
					
					break;
				}
				
				default:
				{
					intent = null;
					Log.w(TAG, "Null intent - default notification type");
					break;
				}
			}
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_ONE_SHOT);
			
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mitv_notification_large_icon);
		
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.mitv_notification_small_icon)
			.setLargeIcon(bitmap)
			.setContentTitle(notificationTitleSB)
			.setContentText(notificationTextSB)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.setWhen(System.currentTimeMillis())
			.setDefaults(Notification.DEFAULT_ALL); // default sound, vibration and light
			
			notificationManager.notify(notificationID, notificationBuilder.build());
	
			Log.d(TAG, "Notification with ID " + notificationID + " will be removed");
			
			ContentManager.sharedInstance().removeFromCacheNotificationWithID(notificationID);
		}
	}

	
	
	public static void removeNotification(
			final Context context, 
			final int notificationId) 
	{
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(notificationId);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent(Constants.INTENT_NOTIFICATION);
		
		PendingIntent sender = PendingIntent.getBroadcast(context, notificationId, intent, 0);
		
		alarmManager.cancel(sender);

		ContentManager.sharedInstance().removeFromCacheNotificationWithID(notificationId);
	}
}