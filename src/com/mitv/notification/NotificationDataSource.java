
package com.mitv.notification;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_URL, notification.getBroadcastUrl());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID, notification.getProgramId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TITLE, notification.getProgramTitle());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TYPE, notification.getProgramType());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SEASON, notification.getProgramSeason());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE, notification.getProgramEpisodeNumber());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_YEAR, notification.getProgramYear());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TAG, notification.getProgramTag());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_GENRE, notification.getProgramGenre());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, notification.getChannelId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_NAME, notification.getChannelName());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_URL	, notification.getChannelLogoUrl());
//		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIME, notification.getBroadcastBeginTimeStringLocal());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS, notification.getBroadcastBeginTimeInMillisGmtAsString());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY, notification.getProgramCategory());
		
		long rowId = database.insert(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, null, values);
		
		Log.d(TAG,"ROW IS INSERTED: " + String.valueOf(rowId));
		
		database.close();
	}

	
	
	public NotificationDbItem getNotification(String channelId, long beginTimeMillis) 
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String query = String.format(SecondScreenApplication.getCurrentLocale(), "SELECT * FROM %s WHERE %s = %s AND %s = '%s'", Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS, beginTimeMillis, Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, channelId);

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
			if (!cursor.isNull(0)) {
				notification.setNotificationId(cursor.getInt(0));
				notification.setBroadcstUrl(cursor.getString(1));
				notification.setProgramId(cursor.getString(2));
				notification.setProgramTitle(cursor.getString(3));

				String programType = cursor.getString(4);
				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramSeason(cursor.getString(5));
					notification.setProgramEpisodeNumber(cursor.getInt(6));
				} else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramYear(cursor.getInt(7));
					notification.setProgramGenre(cursor.getString(9));
				} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramCategory(cursor.getString(14));
				} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramCategory(cursor.getString(14));
					notification.setProgramGenre(cursor.getString(9));
				}
				
				notification.setProgramTag(cursor.getString(8));
				notification.setChannelId(cursor.getString(10));
				notification.setChannelName(cursor.getString(11));
				notification.setChannelLogoUrl(cursor.getString(12));
//				notification.setBroadcastBeginTimeStringLocal(cursor.getString(13));
				notification.setBroadcastBeginTimeMillisGmtAsString(cursor.getString(13));
			}
		}
		return notification;
	}
}
