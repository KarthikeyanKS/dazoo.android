package com.millicom.secondscreen.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;

public class DazooNotification extends BroadcastReceiver {
	
	private static final String TAG = "DazooNotification";

	@Override
	public void onReceive(Context context, Intent intent) {
		int notificationId = intent.getIntExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, 0);
		Broadcast broadcast = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_BROADCAST);
		Channel channel = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_CHANNEL);
		
		Toast.makeText(context, "I am a dazoo notification", Toast.LENGTH_SHORT).show();
		Log.d(TAG,"I HAVE RECEIVED A REQUEST TO SET A NOTIFICATION");
		
		NotificationService.showNotification(context, broadcast, channel, notificationId);
	}
}
