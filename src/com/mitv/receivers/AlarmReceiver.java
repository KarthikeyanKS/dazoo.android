package com.mitv.receivers;

import java.text.ParseException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mitv.Consts;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.notification.NotificationService;
import com.mitv.utilities.DateUtilities;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Consts.INTENT_NOTIFICATION)) {
			//int notificationId = intent.getIntExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, 0);
			//Broadcast broadcast = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_BROADCAST);
			//Channel channel = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_CHANNEL);
		
			int notificationId = intent.getIntExtra(Consts.INTENT_ALARM_EXTRA_NOTIFICIATION_ID, 0);
			long broadcastBeginTimeMillis = intent.getLongExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS, 0);
			String broadcastName = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_NAME);
			String channelId = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_CHANNELID);
			String channelName = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_NAME);
			String channelLogo = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL);
			String dateDate = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_DATE_DATE);
			String broadcastTime = intent.getStringExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_TIME);
			
			try {
				broadcastTime = DateUtilities.timeToTimeString(DateUtilities.convertTimeStampToLocalTime(DateUtilities.isoStringToLong(broadcastTime)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			Log.e(TAG,"AlarmReceiver: broadcastBeginTimeMillis: " + String.valueOf(broadcastBeginTimeMillis));
			Log.e(TAG,"AlarmReceiver: channelId: "+ channelId);
			Log.e(TAG,"AlarmReceiver: channelName: " + channelName);
			Log.e(TAG,"AlarmReceiver: channelLogo: " + channelLogo);
			Log.e(TAG,"AlarmReceiver: broadcastName: " +  broadcastName);
			Log.e(TAG,"AlarmReceiver: date: " + dateDate);
			Log.e(TAG,"AlarmReceiver: broadcast time: " + broadcastTime);
			
			Log.e(TAG,"AlarmReceiver: Notification id: " + String.valueOf(notificationId));
			
			NotificationService.showNotification(context, broadcastBeginTimeMillis, broadcastTime, broadcastName, channelId, channelName, channelLogo, dateDate, notificationId);
		}
	}
}
