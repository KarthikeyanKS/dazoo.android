
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
	private Long beginTimeInMilliseconds;
	
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
		this.beginTimeInMilliseconds = notification.getBeginTimeInMilliseconds();
		this.competitionId = notification.getCompetitionId();
		this.eventId = notification.getEventId();
		this.programId = notification.getProgramId();
		this.channelId = notification.getChannelId();
	}
	
	
	
	public static void createAndSaveInAsyncTask(List<Notification> notifications)
	{
		List<AbstractOrmLiteClassWithAsyncSave<NotificationORM>> ormData = new ArrayList<AbstractOrmLiteClassWithAsyncSave<NotificationORM>>(notifications.size());
		
		for(Notification notification : notifications)
		{
			NotificationORM elementORM = new NotificationORM(notification);

			ormData.add(elementORM);
		}

		new NotificationORM().saveListElementsInAsyncTask(ormData);
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
		return beginTimeInMilliseconds;
	}



	public String getChannelId() {
		return channelId;
	}
}
