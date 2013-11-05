package com.millicom.secondscreen.notification;

import java.util.ArrayList;
import java.util.List;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.NotificationDbItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotificationDataSource {

	private static final String			TAG			= "NotificationDataSource";

	private SQLiteDatabase				database;
	private NotificationDatabaseHelper	dbHelper;
	private String[]					allColumns	= { Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_URL, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID,
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TITLE, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TYPE, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SEASON, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE,
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_YEAR, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TAG, Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, Consts.NOTIFICATION_DB_COLUMN_CHANNEL_NAME,
			Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIME, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS };

	public NotificationDataSource(Context context) {
		dbHelper = new NotificationDatabaseHelper(context);
	}

	public void addNotification(NotificationDbItem notification) {
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
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, notification.getChannelId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_NAME, notification.getChannelName());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIME, notification.getBroadcastBeginTime());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS, notification.getBroadcastTimeInMillis());
		long rowId = database.insert(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, null, values);
		database.close();
	}

	public NotificationDbItem getNotification(String channelId, long beginTimeMillis) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String query = "SELECT * FROM " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS + " WHERE " + Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS + " = " + beginTimeMillis + " AND "
				+ Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID + " = " + "'" + channelId + "'";

		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
			return setCursorValues(cursor);
		} else {
			return null;
		}
	}

	public List<NotificationDbItem> getAllNotifications() {
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

		return notificationList;
	}

	public void deleteNotification(int notificationId) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String selectQuery = "SELECT * FROM notifications WHERE notification_id = " + notificationId;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor != null) cursor.moveToFirst();
		int deleteSucceed = database.delete(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + " = " + notificationId, null);
		database.close();
	}

	public NotificationDbItem setCursorValues(Cursor cursor) {
		NotificationDbItem notification = new NotificationDbItem();
		if (cursor.getCount() > 0) {
			if (!cursor.isNull(0)) {
				notification.setNotificationId(cursor.getInt(0));
				notification.setBroadcstUrl(cursor.getString(1));
				notification.setProgramId(cursor.getString(2));
				notification.setProgramTitle(cursor.getString(3));

				String programType = cursor.getString(4);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramSeason(cursor.getString(5));
					notification.setProgramEpisodeNumber(cursor.getInt(6));
				} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					notification.setProgramType(programType);
					notification.setProgramYear(cursor.getInt(7));
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					notification.setProgramType(programType);
				}
				notification.setProgramTag(cursor.getString(8));
				notification.setChannelId(cursor.getString(9));
				notification.setChannelName(cursor.getString(10));
				notification.setBroadcastBeginTime(cursor.getString(11));
				notification.setBroadcastBeginTimeMillis(cursor.getString(12));
			}
		}
		return notification;
	}
}
