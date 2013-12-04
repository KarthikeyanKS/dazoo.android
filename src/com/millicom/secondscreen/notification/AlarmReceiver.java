package com.millicom.secondscreen.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
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
		
		Log.d(TAG,"broadcastBeginTimeMillis: " + String.valueOf(broadcastBeginTimeMillis));
		Log.d(TAG,"channelId: "+ channelId);
		Log.d(TAG,"channelName: " + channelName);
		Log.d(TAG,"channelLogo: " + channelLogo);
		Log.d(TAG," broadcastName" +  broadcastName);
		Log.d(TAG,"date: " + dateDate);
		Log.d(TAG,"broadcast time: " + broadcastTime);
		
		Log.d(TAG,"Notification id: " + String.valueOf(notificationId));
		
		NotificationService.showNotification(context, broadcastBeginTimeMillis, broadcastTime, broadcastName, channelId, channelName, channelLogo, dateDate, notificationId);
	}
}
