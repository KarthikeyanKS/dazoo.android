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
		int notificationId = intent.getIntExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, 0);
		Broadcast broadcast = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_BROADCAST);
		Channel channel = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_CHANNEL);
		
		Log.d(TAG,"Notification id: " + String.valueOf(notificationId));
		
		Log.d(TAG,"What notification knows:" + broadcast.getBeginTime() + " channel " + channel.getChannelId());
		Log.d(TAG,"Program: " + broadcast.getProgram().getProgramId());
		
		NotificationService.showNotification(context, broadcast, channel, notificationId);
	}
}
