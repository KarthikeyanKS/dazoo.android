
package com.mitv.notification;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.mitv.Consts;
import com.mitv.SecondScreenApplication;



public class NotificationDataSource 
{
	private static final String	TAG	= NotificationDataSource.class.getName();

	
	
	private NotificationDatabaseHelper dbHelper;


	
	public NotificationDataSource(Context context) 
	{
		dbHelper = new NotificationDatabaseHelper(context);
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

	
	
	public NotificationSQLElement getNotification(final String tvChannelId, final String beginTime) 
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String query = String.format(SecondScreenApplication.getCurrentLocale(), "SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'", Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME, beginTime, Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, tvChannelId);

		Cursor cursor = database.rawQuery(query, null);
		
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
		NotificationSQLElement notification = new NotificationSQLElement();
		
		if (cursor.getCount() > 0)
		{
			if (!cursor.isNull(0))
			{
				notification.setNotificationId(cursor.getInt(0));
				
				notification.setBroadcastBeginTimeInMilliseconds(cursor.getLong(1));
				
				notification.setBroadcastBeginTime(cursor.getString(2));
				
				notification.setBroadcastEndTime(cursor.getString(3));
				
				notification.setBroadcastType(cursor.getString(4));
				
				notification.setShareUrl(cursor.getString(5));
				
				notification.setChannelId(cursor.getString(6));
				
				notification.setChannelName(cursor.getString(7));
				
				notification.setChannelLogoSmall(cursor.getString(8));
				
				notification.setChannelLogoMedium(cursor.getString(9));
				
				notification.setChannelLogoLarge(cursor.getString(10));
				
				notification.setProgramId(cursor.getString(11));
				
				notification.setProgramTitle(cursor.getString(12));

				notification.setProgramType(cursor.getString(13));
				
				notification.setSynopsisShort(cursor.getString(14));
				
				notification.setSynopsisLong(cursor.getString(15));
				
				notification.setProgramTags(cursor.getString(16));
				
				notification.setProgramCredits(cursor.getString(17));
				
				
				String programTypeAsString = cursor.getString(13);
				
				ProgramTypeEnum programType = ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(programTypeAsString);
				
				switch(programType)
				{
					case TV_EPISODE:
					{
						notification.setProgramSeason(cursor.getString(18));
						notification.setProgramEpisodeNumber(cursor.getInt(19));
						notification.setSeriesId(cursor.getString(23));
						notification.setSeriesName(cursor.getString(24));
						break;
					}
					
					case SPORT:
					{
						notification.setSportTypeId(cursor.getString(25));
						notification.setSportTypeName(cursor.getString(26));
						notification.setProgramGenre(cursor.getString(21));
						break;
					}
					
					case OTHER:
					{
						notification.setProgramCategory(cursor.getString(22));
						break;
					}
					
					case MOVIE:
					{
						notification.setProgramYear(cursor.getInt(20));
						notification.setProgramGenre(cursor.getString(21));
						break;
					}
					
					case UNKNOWN:
					default:
					{
						Log.w(TAG, "Unhandled program type.");
						break;
					}
				}
			}
		}
		
		return notification;
	}
}