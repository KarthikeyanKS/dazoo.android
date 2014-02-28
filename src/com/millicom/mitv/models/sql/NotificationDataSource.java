
package com.millicom.mitv.models.sql;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class NotificationDataSource 
{
	private static final String	TAG	= NotificationDataSource.class.getName();

	
	
	private NotificationSQLDatabaseHelper dbHelper;


	
	public NotificationDataSource(Context context) 
	{
		dbHelper = new NotificationSQLDatabaseHelper(context);
	}

	
	
	public void addNotification(NotificationSQLElement notification)
	{
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, notification.getNotificationId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME_IN_MILISECONDS, notification.getBroadcastBeginTime());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME, notification.getBroadcastBeginTime());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_END_TIME, notification.getBroadcastEndTime());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_TYPE, notification.getBroadcastType());
		values.put(Consts.NOTIFICATION_DB_COLUMN_SHARE_URL, notification.getShareUrl());
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, notification.getChannelId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_NAME, notification.getChannelName());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_SMALL, notification.getChannelLogoSmall());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_MEDIUM, notification.getChannelLogoMedium());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_LARGE, notification.getChannelLogoLarge());
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID, notification.getProgramId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TITLE, notification.getProgramTitle());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TYPE, notification.getProgramType());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_SHORT, notification.getSynopsisShort());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_LONG, notification.getSynopsisLong());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TAGS, notification.getProgramTags());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_CREDITS, notification.getProgramCredits());
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SEASON, notification.getProgramSeason());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE, notification.getProgramEpisodeNumber());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_YEAR, notification.getProgramYear());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_GENRE, notification.getProgramGenre());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY, notification.getProgramCategory());
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_SERIES_ID, notification.getSeriesId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_SERIES_NAME, notification.getSeriesName());
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_SPORT_TYPE_ID, notification.getSportTypeId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_SPORT_TYPE_NAME, notification.getSportTypeName());
		
		database.insert(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, null, values);

		database.close();
	}
	
	
	
	public boolean removeNotification(final int notificationId) 
	{
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
		StringBuilder selectQuerySB = new StringBuilder();
		selectQuerySB.append("SELECT * FROM ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS);
		selectQuerySB.append(" WHERE ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID);
		selectQuerySB.append(" = ");
		selectQuerySB.append(notificationId);
		
		Cursor cursor = database.rawQuery(selectQuerySB.toString(), null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		}
		else
		{
			Log.d(TAG,"CURSOR IS EMPTY");
		}
		
		int deleteSucceed = database.delete(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + " = " + notificationId, null);
		
		cursor.close();
		
		database.close();
		
		return (deleteSucceed == 1);
	}

	
	
	public NotificationSQLElement getNotification(
			final TVChannelId tvChannelId, 
			final String beginTime) 
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String tvChannelIdString = tvChannelId.getChannelId();
		
		StringBuilder selectQuerySB = new StringBuilder();
		selectQuerySB.append("SELECT * FROM ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS);
		selectQuerySB.append(" WHERE ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME);
		selectQuerySB.append(" = ");
		selectQuerySB.append("'");
		selectQuerySB.append(beginTime);
		selectQuerySB.append("'");
		selectQuerySB.append(" AND ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID);
		selectQuerySB.append(" = ");
		selectQuerySB.append("'");
		selectQuerySB.append(tvChannelIdString);
		selectQuerySB.append("'");
		
		Cursor cursor = database.rawQuery(selectQuerySB.toString(), null);
		
		if (cursor != null)
		{
			cursor.moveToFirst();
			database.close();
			
			return setCursorValues(cursor);
		} 
		else
		{
			database.close();
			
			return null;
		}
	}
	
	
	
	public int getNotificationCount()
	{
		StringBuilder selectQuerySB = new StringBuilder();
		selectQuerySB.append("SELECT * FROM ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS);
		
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		Cursor cursor = database.rawQuery(selectQuerySB.toString(), null);
		
		int count = cursor.getCount();
		
		cursor.close();
		
		database.close();
		
		return count;
	}

	
	
	public List<NotificationSQLElement> getAllNotifications()
	{
		List<NotificationSQLElement> notifications = new ArrayList<NotificationSQLElement>();
		
		StringBuilder selectQuerySB = new StringBuilder();
		selectQuerySB.append("SELECT * FROM ");
		selectQuerySB.append(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS);

		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		Cursor cursor = database.rawQuery(selectQuerySB.toString(), null);
		
		if (cursor.moveToFirst())
		{
			do 
			{
				NotificationSQLElement notification = setCursorValues(cursor);
				
				notifications.add(notification);
			} 
			while (cursor.moveToNext());
		}
		
		cursor.close();
		
		database.close();

		return notifications;
	}

	

	private NotificationSQLElement setCursorValues(Cursor cursor) 
	{
		NotificationSQLElement notification = new NotificationSQLElement(cursor);
				
		return notification;
	}
}