
package com.mitv.broadcastreceivers;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mitv.Constants;
import com.mitv.ui.helpers.NotificationHelper;



public class AlarmReceiver 
	extends BroadcastReceiver 
{
	@SuppressWarnings("unused")
	private static final String TAG = AlarmReceiver.class.getName();

	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(Constants.INTENT_NOTIFICATION))
		{
			int notificationId = intent.getIntExtra(Constants.INTENT_NOTIFICATION_EXTRA_NOTIFICATION_ID, 0);

			NotificationHelper.showNotification(context, notificationId);
		}
	}	
}
