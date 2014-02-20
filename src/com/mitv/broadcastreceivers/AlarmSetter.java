package com.mitv.broadcastreceivers;

import java.text.ParseException;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.millicom.mitv.models.dto.TVBroadcastWithChannelInfoDTO;
import com.millicom.mitv.models.dto.TVChannelDTO;
import com.mitv.model.OldNotificationDbItem;
import com.mitv.model.OldProgram;
import com.mitv.model.OldSeason;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationService;
import com.mitv.utilities.DateUtilities;

public class AlarmSetter extends BroadcastReceiver {

	private static final String TAG = "AlarmSetter";
	
	//private static final int PERIOD=5000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// set the alarms on the phone reboot
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			scheduleAlarms(context);
		}
	}

	static void scheduleAlarms(Context context) {
		// get the list of alarms
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		List<OldNotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		
		for(int i=0; i<notificationList.size(); i++){
		
			//TODO fix this stuff using DTOs? or other approach?
			
//			OldNotificationDbItem item = notificationList.get(i);
//			
//			TVBroadcastWithChannelInfoDTO broadcast = new TVBroadcastWithChannelInfoDTO();
//			broadcast.setBeginTimeStringGmt(item.getBroadcastBeginTimeStringLocal());
//			long beginTimeMillisGmt = Long.parseLong(item.getBroadcastBeginTimeInMillisGmtAsString());
//			broadcast.setBeginTimeMillisGmt(beginTimeMillisGmt);
//			
//			long beginTimeMillisLocal = DateUtilities.convertTimeStampToLocalTime(beginTimeMillisGmt);
//			String tvDateString = "";
//			try {
//				tvDateString = DateUtilities.isoDateToTvDateString(beginTimeMillisLocal);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			broadcast.setTvDateString(tvDateString);
//			
//			
//			OldProgram program = new OldProgram();
//			program.setProgramId(item.getProgramId());
//			program.setTitle(item.getProgramTitle());
//			program.setProgramType(item.getProgramType());
//			
//			OldSeason season = new OldSeason();
//			season.setNumber(item.getProgramSeason());
//			
//			program.setSeason(season);
//			program.setEpisodeNumber(item.getProgramEpisodeNumber());
//			program.setYear(item.getProgramYear());
//			
//			broadcast.setProgram(program);
//			
//			TVChannelDTO channel = new TVChannelDTO();
//			channel.setChannelId(item.getChannelId());
//			channel.setName(item.getChannelName());
//			channel.setAllImageUrls(item.getChannelLogoUrl());
//			
//			broadcast.setChannel(channel);
//			
//			NotificationService.setAlarm(context, broadcast, channel, item.getNotificationId());
		}
	}
}
