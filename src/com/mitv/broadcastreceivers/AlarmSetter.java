
package com.mitv.broadcastreceivers;



import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationSQLElement;
import com.mitv.notification.NotificationService;



public class AlarmSetter 
	extends BroadcastReceiver 
{
	@SuppressWarnings("unused")
	private static final String TAG = AlarmSetter.class.getName();
	

	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// set the alarms on the phone reboot
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			scheduleAlarms(context);
		}
	}

	
	
	private static void scheduleAlarms(Context context) 
	{
		// Get the list of alarms
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		
		List<NotificationSQLElement> notificationList = notificationDataSource.getAllNotifications();
		
		for(int i=0; i<notificationList.size(); i++)
		{
			NotificationSQLElement item = notificationList.get(i);
			
			TVBroadcastWithChannelInfo broadcast = new TVBroadcastWithChannelInfo(item);
			
			NotificationService.setAlarm(context, broadcast, item.getNotificationId());
		}
	}
}
