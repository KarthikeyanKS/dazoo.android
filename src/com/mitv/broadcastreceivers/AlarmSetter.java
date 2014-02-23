
package com.mitv.broadcastreceivers;



import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.TVSeriesSeason;
import com.mitv.model.NotificationDbItem;
import com.mitv.notification.NotificationDataSource;
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
		
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		
		for(int i=0; i<notificationList.size(); i++)
		{
			NotificationDbItem item = notificationList.get(i);
			
			long beginTimeMillisGmt = Long.parseLong(item.getBroadcastBeginTimeInMillisGmtAsString());
			
			Calendar beginTimeCalendar = Calendar.getInstance();
			beginTimeCalendar.setTimeInMillis(beginTimeMillisGmt);
			
			// TODO - fetch this?
			Calendar endTimeCalendar = null;
						
			TVBroadcastWithChannelInfo broadcast = new TVBroadcastWithChannelInfo();
			
			broadcast.setBeginTimeCalendar(beginTimeCalendar);
			broadcast.setEndTimeCalendar(endTimeCalendar);
						
			TVProgram program = new TVProgram();
			program.setProgramId(item.getProgramId());
			program.setTitle(item.getProgramTitle());
			
			ProgramTypeEnum progarmType = ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(item.getProgramType());
			
			program.setProgramType(progarmType);
			
			TVSeriesSeason season = new TVSeriesSeason();
			season.setNumber(Integer.parseInt(item.getProgramSeason()));
			
			program.setSeason(season);
			program.setEpisodeNumber(item.getProgramEpisodeNumber());
			program.setYear(item.getProgramYear());
			
			broadcast.setProgram(program);
			
			TVChannel tvChannel = new TVChannel();
			tvChannel.setChannelId(item.getChannelId());
			tvChannel.setName(item.getChannelName());
			tvChannel.setAllImageUrls(item.getChannelLogoUrl());
			
			broadcast.setChannel(tvChannel);
			
			NotificationService.setAlarm(context, broadcast, item.getNotificationId());
		}
	}
}
