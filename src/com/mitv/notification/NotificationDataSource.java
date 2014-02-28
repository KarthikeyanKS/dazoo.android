
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
import com.mitv.model.NotificationDbItem;



public class NotificationDataSource 
{
	private static final String	TAG	= NotificationDataSource.class.getName();

	private NotificationDatabaseHelper	dbHelper;


	public NotificationDataSource(Context context) 
	{
		dbHelper = new NotificationDatabaseHelper(context);
	}

	
	
	public void addNotification(NotificationDbItem notification)
	{
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, notification.getNotificationId());
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
		
		long rowId = database.insert(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, null, values);
		
		Log.d(TAG,"ROW IS INSERTED: " + String.valueOf(rowId));
		
		database.close();
	}

	
	
	public NotificationDbItem getNotification(String channelId, long beginTimeMillis) 
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String query = String.format(SecondScreenApplication.getCurrentLocale(), "SELECT * FROM %s WHERE %s = %s AND %s = '%s'", Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME, beginTimeMillis, Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, channelId);

		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
			database.close();
			return setCursorValues(cursor);
		} else {
			database.close();
			return null;
		}
	}
	
	
	
	public int getNumberOfNotifications(){
		String selectQuery = "SELECT * FROM " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS;

		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		int count = cursor.getCount();
		cursor.close();
		database.close();
		return count;
	}

	
	
	public List<NotificationDbItem> getAllNotifications()
	{
		List<NotificationDbItem> notificationList = new ArrayList<NotificationDbItem>();
		String selectQuery = "SELECT * FROM " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS;

		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				NotificationDbItem notification = setCursorValues(cursor);
				notificationList.add(notification);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();

		return notificationList;
	}

	public void deleteNotification(int notificationId) 
	{
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String selectQuery = String.format(SecondScreenApplication.getCurrentLocale(), "SELECT * FROM %s WHERE %s = %s", Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, notificationId);
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor != null) {cursor.moveToFirst();}
		else{
			Log.d(TAG,"CURSOR IS EMPTY");
		}
		int deleteSucceed = database.delete(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + " = " + notificationId, null);
		Log.d(TAG,"Delete notification: " + String.valueOf(deleteSucceed));
		cursor.close();
		database.close();
	}
	
	

	public NotificationDbItem setCursorValues(Cursor cursor) 
	{
		NotificationDbItem notification = new NotificationDbItem();
		
		if (cursor.getCount() > 0)
		{
			if (!cursor.isNull(0))
			{
				notification.setNotificationId(cursor.getInt(0));
				
				notification.setBroadcastBeginTime(cursor.getString(1));
				
				notification.setBroadcastEndTime(cursor.getString(2));
				
				notification.setBroadcastType(cursor.getString(3));
				
				notification.setShareUrl(cursor.getString(4));
				
				notification.setChannelId(cursor.getString(5));
				
				notification.setChannelName(cursor.getString(6));
				
				notification.setChannelLogoSmall(cursor.getString(7));
				
				notification.setChannelLogoMedium(cursor.getString(8));
				
				notification.setChannelLogoLarge(cursor.getString(9));
				
				notification.setProgramId(cursor.getString(10));
				
				notification.setProgramTitle(cursor.getString(11));

				notification.setProgramType(cursor.getString(12));
				
				notification.setSynopsisShort(cursor.getString(13));
				
				notification.setSynopsisLong(cursor.getString(14));
				
				notification.setProgramTags(cursor.getString(15));
				
				notification.setProgramCredits(cursor.getString(16));
				
				
				String programTypeAsString = cursor.getString(12);
				
				ProgramTypeEnum programType = ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(programTypeAsString);
				
				switch(programType)
				{
					case TV_EPISODE:
					{
						notification.setProgramSeason(cursor.getString(17));
						notification.setProgramEpisodeNumber(cursor.getInt(18));
						notification.setSeriesId(cursor.getString(22));
						notification.setSeriesName(cursor.getString(23));
						break;
					}
					
					case SPORT:
					{
						notification.setSportTypeId(cursor.getString(24));
						notification.setSportTypeName(cursor.getString(25));
						notification.setProgramGenre(cursor.getString(20));
						break;
					}
					
					case OTHER:
					{
						notification.setProgramCategory(cursor.getString(21));
						break;
					}
					
					case MOVIE:
					{
						notification.setProgramYear(cursor.getInt(19));
						notification.setProgramGenre(cursor.getString(20));
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
