package com.millicom.secondscreen.notification;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.R;

public class DazooNotification extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		
		int toSet = intent.getIntExtra(Consts.INTENT_EXTRA_NOTIFICATION_TO_SET,0);
		
		Broadcast broadcast = intent.getExtras().getParcelable(Consts.INTENT_EXTRA_BROADCAST);
		NotificationService.setNotification(getApplicationContext(), broadcast);
	}

}
