

package com.mitv.models.objects.mitvapi;



import java.util.Calendar;

import com.mitv.enums.NotificationTypeEnum;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.orm.NotificationORM;
import com.mitv.utilities.DateUtils;



public class Notification 
{
	/*
	 * This field is only set after the notification is scheduled
	 */
	private Integer notificationId;
	
	private NotificationTypeEnum notificationType;
	
	private String programId;
	private String channelId;
	
	private Long broadcastBeginTimeInMilliseconds;
	private String broadcastTitle;
	private String broadcastChannelName;
	
	/* Only used if the notificationType is COMPETITION_EVENT */
	private Long competitionId;
	private Long eventId;
	
	
	protected Calendar beginTimeCalendarGMT;
	protected Calendar beginTimeCalendarLocal;
	
	
	
	public Notification(){}
	
	
	public Notification(TVBroadcastWithChannelInfo broadcast)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.TV_BROADCAST;
		
		this.programId = broadcast.getProgram().getProgramId();
		this.channelId = broadcast.getChannel().getChannelId().getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		this.broadcastTitle = broadcast.getTitle();
		this.broadcastChannelName = broadcast.getChannel().getName();
		
		this.competitionId = Long.valueOf(-1);
		this.eventId = Long.valueOf(-1);
	}
	
	
	
	public Notification(
			Event event,
			EventBroadcast broadcast,
			TVChannel channel)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.COMPETITION_EVENT;
		
		this.programId = broadcast.getProgramId();
		this.channelId = broadcast.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		this.broadcastTitle = event.getTitle();
		this.broadcastChannelName = channel.getName();
		
		this.competitionId = event.getCompetitionId();
		this.eventId = event.getEventId();
	}
	
	
	
	public Notification(NotificationORM ormData)
	{
		this.notificationId = ormData.getNotificationId();
		this.notificationType = ormData.getNotificationType();
		
		this.programId = ormData.getProgramId();
		this.channelId = ormData.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = ormData.getBeginTimeInMilliseconds();
		this.broadcastTitle = ormData.getBroadcastTitle();
		this.broadcastChannelName = ormData.getBroadcastChannelName();
		
		this.competitionId = ormData.getCompetitionId();
		this.eventId = ormData.getEventId();
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT()
	{
		if(beginTimeCalendarGMT == null)
		{	
			beginTimeCalendarGMT = DateUtils.getNowWithGMTTimeZone();
			
			beginTimeCalendarGMT.setTimeInMillis(broadcastBeginTimeInMilliseconds);
		}
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarLocal()
	{
		if(beginTimeCalendarLocal == null)
		{	
			beginTimeCalendarLocal = DateUtils.getNowWithGMTTimeZone();
			
			beginTimeCalendarLocal.setTimeInMillis(broadcastBeginTimeInMilliseconds);
			
			beginTimeCalendarLocal = DateUtils.setTimeZoneAndOffsetToLocal(beginTimeCalendarLocal);
		}
		
		return beginTimeCalendarLocal;
	}
	
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	
	
	public boolean isTheSameDayAs(Notification other)
	{
		Calendar beginTime1 = this.getBeginTimeCalendarGMT();
		Calendar beginTime2 = other.getBeginTimeCalendarGMT();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}



	public Integer getNotificationId() {
		return notificationId;
	}



	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}



	public NotificationTypeEnum getNotificationType() {
		return notificationType;
	}



	public Long getCompetitionId() {
		return competitionId;
	}



	public Long getEventId() {
		return eventId;
	}



	public String getProgramId() {
		return programId;
	}



	public Long getBeginTimeInMilliseconds() {
		return broadcastBeginTimeInMilliseconds;
	}



	public String getChannelId() {
		return channelId;
	}


	public String getBroadcastTitle() {
		return broadcastTitle;
	}


	public String getBroadcastChannelName() {
		return broadcastChannelName;
	}
	
}
