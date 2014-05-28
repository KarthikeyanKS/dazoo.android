
package com.mitv.models;



import android.util.Log;

import com.mitv.enums.NotificationType;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;



public class Notification 
{
	private static final String TAG = Notification.class.getName();
	
	
	private Integer notificationID;
	private NotificationType notificationType;
	private Long competitionId;
	private Long eventId;
	private String programId;
	private String channelId;
	private String beginTime;
	private long beginTimeMillis;
	private String endTime;
	
	
	public Notification(){}
	
	
	
	public Notification(final NotificationORM ormData)
	{
		this.notificationID = ormData.getNotificationID();
		this.notificationType = ormData.getNotificationType();
		this.competitionId = ormData.getCompetitionId();
		this.eventId = ormData.getEventId();
		this.programId = ormData.getProgramId();
		this.channelId = ormData.getChannelId();
		this.beginTimeMillis = ormData.getBeginTimeMillis();
		this.beginTime = ormData.getBeginTime();
		this.endTime = ormData.getEndTime();
	}
	
	
	
	public Notification(
			final Integer notificationID,
			final TVBroadcastWithChannelInfo broadcast)
	{
		this.notificationID = notificationID;
		this.notificationType = NotificationType.TV_BROADCAST; 
		this.competitionId = Long.valueOf(0);
		this.eventId = Long.valueOf(0);
		
		this.programId = broadcast.getProgram().getProgramId();
		this.channelId = broadcast.getChannel().getChannelId().getChannelId();
		
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
	}
	
	
	
	public Notification(
			final Integer notificationID,
			final Long competitionID,
			final EventBroadcast broadcast)
	{
		this.notificationID = notificationID;
		this.notificationType = NotificationType.COMPETITION_EVENT; 
		this.competitionId = competitionID;
		
		try
		{
			this.eventId = Long.parseLong(broadcast.getEventBroadcastId());
		}
		catch(NumberFormatException nfex)
		{
			this.eventId = Long.valueOf(0);
			
			Log.w(TAG, "Pasing of eventId failed");
		}
		
		this.programId = broadcast.getProgramId();
		this.channelId = broadcast.getChannelId();
		
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
	}


	
	public static String getTag() {
		return TAG;
	}


	public Integer getNotificationID() {
		return notificationID;
	}


	public NotificationType getNotificationType() {
		return notificationType;
	}


	public Long getEventId() {
		return eventId;
	}


	public String getProgramId() {
		return programId;
	}


	public String getChannelId() {
		return channelId;
	}


	public String getBeginTime() {
		return beginTime;
	}


	public long getBeginTimeMillis() {
		return beginTimeMillis;
	}


	public String getEndTime() {
		return endTime;
	}



	public Long getCompetitionId() {
		return competitionId;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((notificationID == null) ? 0 : notificationID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Notification other = (Notification) obj;
		
		if (notificationID == null) 
		{
			if (other.getNotificationID() != null)
				return false;
		} 
		else if (!notificationID.equals(other.getNotificationID()))
			return false;
		
		return true;
	}
}
