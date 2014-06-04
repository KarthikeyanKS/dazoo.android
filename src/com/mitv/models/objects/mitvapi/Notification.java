

package com.mitv.models.objects.mitvapi;



import com.mitv.enums.NotificationTypeEnum;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.orm.NotificationORM;



public class Notification 
{
	/*
	 * This field is only set after the notification is scheduled
	 */
	private Integer notificationId;
	
	private NotificationTypeEnum notificationType;
	private Long beginTimeInMilliseconds;
	
	private String programId;
	private String channelId;
	
	/* Only used if the notificationType is COMPETITION_EVENT */
	private Long competitionId;
	private Long eventId;
	
	
	
	public Notification(){}
	
	
	public Notification(TVBroadcastWithChannelInfo broadcast)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.TV_BROADCAST;
		this.beginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		
		this.programId = broadcast.getProgram().getProgramId();
		this.channelId = broadcast.getChannel().getChannelId().getChannelId();
		
		this.competitionId = Long.valueOf(-1);
		this.eventId = Long.valueOf(-1);
	}
	
	
	
	public Notification(
			Event event,
			EventBroadcast broadcast)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.COMPETITION_EVENT;
		this.beginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		
		this.programId = broadcast.getProgramId();
		this.channelId = broadcast.getChannelId();
		
		this.competitionId = event.getCompetitionId();
		this.eventId = event.getEventId();
	}
	
	
	
	public Notification(NotificationORM ormData)
	{
		this.notificationId = ormData.getNotificationId();
		this.notificationType = ormData.getNotificationType();
		this.beginTimeInMilliseconds = ormData.getBeginTimeInMilliseconds();
		this.competitionId = ormData.getCompetitionId();
		this.eventId = ormData.getEventId();
		this.programId = ormData.getProgramId();
		this.channelId = ormData.getChannelId();
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
		return beginTimeInMilliseconds;
	}



	public String getChannelId() {
		return channelId;
	}
	
}
