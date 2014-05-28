
package com.mitv.models;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.NotificationType;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class NotificationORM 
	extends AbstractOrmLiteClassWithAsyncSave<NotificationORM> 
{
	private static final String TAG = NotificationORM.class.getName();
	
	
	@DatabaseField(id=true)
	private Integer notificationID;
	
	@DatabaseField()
	private NotificationType notificationType;
	
	@DatabaseField()
	protected Long competitionId;
	
	@DatabaseField()
	protected Long eventId;
	
	@DatabaseField()
	protected String programId;
	
	@DatabaseField()
	protected String channelId;
	
	@DatabaseField()
	protected String beginTime;
	
	@DatabaseField()
	protected long beginTimeMillis;
	
	@DatabaseField()
	protected String endTime;
	
	
	
	public NotificationORM(){}
	
	
	
	public NotificationORM(Notification notification)
	{
		this.notificationID = notification.getNotificationID();
		this.notificationType = notification.getNotificationType();
		this.competitionId = notification.getCompetitionId();
		this.eventId = notification.getEventId();
		this.programId = notification.getProgramId();
		this.channelId = notification.getChannelId();
		this.beginTimeMillis = notification.getBeginTimeMillis();
		this.beginTime = notification.getBeginTime();
		this.endTime = notification.getEndTime();
	}
	
	
	
	public static void createAndSaveInAsyncTask(List<Notification> notifications)
	{
		List<AbstractOrmLiteClassWithAsyncSave<NotificationORM>> ormData = new ArrayList<AbstractOrmLiteClassWithAsyncSave<NotificationORM>>(notifications.size());
		
		for(Notification object : notifications)
		{
			NotificationORM orm = new NotificationORM(object);

			ormData.add(orm);
		}

		new NotificationORM().saveListElementsInAsyncTask(ormData);
	}
	
	
	
	public static Notification getNotificationByID(long id) 
	{
		Notification data;
		
		NotificationORM ormData = new NotificationORM().getNotificationORM(id);
		
		if(ormData != null)
		{
			data = new Notification(ormData);
			
			return data;
		}
		else
		{
			return null;
		}
	}
	
	
	public static List<Notification> getNotifications()
	{
		List<Notification> dataList;
		
		List<NotificationORM> ormData = new NotificationORM().getAllNotificationsORM();
		
		if(ormData != null)
		{
			dataList = new ArrayList<Notification>(ormData.size());
			
			for(NotificationORM orm : ormData)
			{
				Notification dataObject = new Notification(orm);
				
				dataList.add(dataObject);
			}

			return dataList;
		}
		else
		{
			return null;
		}
	}
	


	private NotificationORM getNotificationORM(long id) 
	{
		NotificationORM data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			Dao<NotificationORM, Long> dao = (Dao<NotificationORM, Long>) getDao();
			
			data = dao.queryForId(id);
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
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
}
