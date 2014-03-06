
package com.mitv.broadcastreceivers;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mitv.ui.helpers.NotificationHelper;



public class AlarmSetter 
	extends BroadcastReceiver 
{
	@SuppressWarnings("unused")
	private static final String TAG = AlarmSetter.class.getName();
	

	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// Set the alarms when the phone reboots
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			NotificationHelper.scheduleAlarms(context);
		}
	}
}