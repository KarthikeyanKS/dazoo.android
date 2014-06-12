
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.NotificationTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.Notification;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class NotificationORM
	extends AbstractOrmLiteClassWithAsyncSave<NotificationORM>
{
	private static final String TAG = NotificationORM.class.getName();
	
	
	
	@DatabaseField(id = true)
	private Integer notificationId;
	
	@DatabaseField()
	private NotificationTypeEnum notificationType;
	
	@DatabaseField()
	private Long broadcastBeginTimeInMilliseconds;
	
	@DatabaseField()
	private String broadcastTitle;
	
	@DatabaseField()
	private String broadcastChannelName;
	
	@DatabaseField()
	private String broadcastChannelLogo;
	
	@DatabaseField()
	private ProgramTypeEnum broadcastProgramType;
	
	@DatabaseField()
	private String broadcastProgramDetails;
	
	@DatabaseField()
	private Long competitionId;
	
	@DatabaseField()
	private Long eventId;
	
	@DatabaseField()
	private String programId;
	
	@DatabaseField()
	private String channelId;
	
	
	
	private NotificationORM(){}
	
	
	
	private NotificationORM(Notification notification)
	{
		this.notificationId = notification.getNotificationId();
		this.notificationType = notification.getNotificationType();
		
		this.programId = notification.getProgramId();
		this.channelId = notification.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = notification.getBeginTimeInMilliseconds();
		this.broadcastTitle = notification.getBroadcastTitle();
		this.broadcastChannelName = notification.getBroadcastChannelName();
		this.broadcastChannelLogo = notification.getBroadcastChannelLogo();
		this.broadcastProgramType = notification.getBroadcastProgramType();
		this.broadcastProgramDetails = notification.getBroadcastProgramDetails();
		
		this.competitionId = notification.getCompetitionId();
		this.eventId = notification.getEventId();
	}
	
	
	
	public static void remove(int notificationId)
	{
		NotificationORM elementORM = new NotificationORM();
		
		try
		{
			String notificationIdAsString = Integer.valueOf(notificationId).toString();
			
			elementORM.deleteById("notificationId", notificationIdAsString);
		}
		catch(SQLException sqlex)
		{
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
	}
	
	
	
	public static void add(Notification notification)
	{
		NotificationORM elementORM = new NotificationORM(notification);
		
		try
		{
			elementORM.save();
		}
		catch(SQLException sqlex)
		{
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
	}
		
	
	
	public static List<Notification> getNotifications()
	{
		List<Notification> data;
		
		List<NotificationORM> ormData = new NotificationORM().getAllNotificationsORM();
		
		if(ormData != null)
		{
			data = new ArrayList<Notification>(ormData.size());
			
			for(NotificationORM ormElement : ormData)
			{
				Notification element = new Notification(ormElement);
				
				data.add(element);
			}

			return data;
		}
		else
		{
			return null;
		}
	}
	
	
	
	public static Notification getNotificationById(int notificationId)
	{
		Notification data = null;
		
		NotificationORM ormData = new NotificationORM().getNotificationORMById(notificationId);
		
		if(ormData != null)
		{
			data = new Notification(ormData);
		}
		
		return data;
	}
	
	
	
	private List<NotificationORM> getAllNotificationsORM()
	{
		List<NotificationORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<NotificationORM, Integer> queryBuilder = (QueryBuilder<NotificationORM, Integer>) getDao().queryBuilder();
			
			data = (List<NotificationORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		return data;
	}
	
	
	
	
	private NotificationORM getNotificationORMById(int notificationId)
	{
		NotificationORM data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			Dao<NotificationORM, Integer> dao = (Dao<NotificationORM, Integer>) getDao();
			
			data = dao.queryForId(notificationId);
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		return data;
	}



	public Integer getNotificationId() {
		return notificationId;
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



	public ProgramTypeEnum getBroadcastProgramType() {
		return broadcastProgramType;
	}



	public String getBroadcastProgramDetails() {
		return broadcastProgramDetails;
	}



	public String getBroadcastChannelLogo() {
		return broadcastChannelLogo;
	}
}
