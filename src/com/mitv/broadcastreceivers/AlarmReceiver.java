
package com.mitv.broadcastreceivers;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.ui.helpers.NotificationHelper;



public class AlarmReceiver 
	extends BroadcastReceiver 
{
	private static final String TAG = AlarmReceiver.class.getName();

	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(Constants.INTENT_NOTIFICATION)) 
		{
			int notificationId = intent.getIntExtra(Constants.INTENT_ALARM_EXTRA_NOTIFICIATION_ID, 0);
			
			long broadcastBeginTimeMillis = intent.getLongExtra(Constants.INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS, 0);
			
			String broadcastName = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_BROADCAST_NAME);
			String channelId = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_CHANNELID);
			String channelName = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_CHANNEL_NAME);
			String channelLogo = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL);
			String dateDate = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_DATE_DATE);
			String broadcastHourAndMinuteRepresentation = intent.getStringExtra(Constants.INTENT_ALARM_EXTRA_BROADCAST_HOUR_AND_MINUTE_TIME);
								
			Log.d(TAG,"AlarmReceiver: broadcastBeginTimeMillis: " + String.valueOf(broadcastBeginTimeMillis));
			Log.d(TAG,"AlarmReceiver: channelId: "+ channelId);
			Log.d(TAG,"AlarmReceiver: channelName: " + channelName);
			Log.d(TAG,"AlarmReceiver: channelLogo: " + channelLogo);
			Log.d(TAG,"AlarmReceiver: broadcastName: " +  broadcastName);
			Log.d(TAG,"AlarmReceiver: date: " + dateDate);
			Log.d(TAG,"AlarmReceiver: broadcast time: " + broadcastHourAndMinuteRepresentation);
			
			Log.d(TAG,"AlarmReceiver: Notification id: " + String.valueOf(notificationId));
			
			NotificationHelper.showNotification(context, broadcastBeginTimeMillis, broadcastHourAndMinuteRepresentation, broadcastName, channelId, channelName, channelLogo, dateDate, notificationId);
		}
	}	
}
