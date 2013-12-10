package com.millicom.secondscreen.notification;

import java.util.List;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.Season;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class AlarmSetter extends BroadcastReceiver {

	private static final String TAG = "AlarmSetter";
	
	//private static final int PERIOD=5000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// set the alarms on the phone reboot
		
		scheduleAlarms(context);
	}

	static void scheduleAlarms(Context context) {
		// get the list of alarms
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		
		for(int i=0; i<notificationList.size(); i++){
		
			NotificationDbItem item = notificationList.get(i);
			
			Broadcast broadcast = new Broadcast();
			broadcast.setBeginTimeStringGmt(item.getBroadcastBeginTimeStringLocal());
			broadcast.setBeginTimeMillisGmt(Long.parseLong(item.getBroadcastBeginTimeInMillisGmtAsString()));
			
			Program program = new Program();
			program.setProgramId(item.getProgramId());
			program.setTitle(item.getProgramTitle());
			program.setProgramType(item.getProgramType());
			
			Season season = new Season();
			season.setNumber(item.getProgramSeason());
			
			program.setSeason(season);
			program.setEpisodeNumber(item.getProgramEpisodeNumber());
			program.setYear(item.getProgramYear());
			
			broadcast.setProgram(program);
			
			Channel channel = new Channel();
			channel.setChannelId(item.getChannelId());
			channel.setName(item.getChannelName());
			channel.setLogoSUrl(item.getChannelLogoUrl());
			
			broadcast.setChannel(channel);
			
			NotificationService.resetAlarm(context, broadcast, channel, item.getNotificationId());
		}
	}
}
