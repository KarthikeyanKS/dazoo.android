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
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, Consts.NOTIFICATION_DB_COLUMN_BEGIN_TIME_MILLIS };

	public NotificationDataSource(Context context) {
		dbHelper = new NotificationDatabaseHelper(context);
	}

	public void addNotification(NotificationDbItem notification) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, notification.getNotificationId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID, notification.getProgramId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BROADCAST_URL, notification.getBroadcastUrl());
		values.put(Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, notification.getChannelId());
		values.put(Consts.NOTIFICATION_DB_COLUMN_BEGIN_TIME_MILLIS, notification.getTimeInMillis());
		long rowId = database.insert(Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS, null, values);
		database.close();
	}

	public NotificationDbItem getNotification(String channelId, long beginTimeMillis) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();

		String query = "SELECT * FROM " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS + " WHERE " + Consts.NOTIFICATION_DB_COLUMN_BEGIN_TIME_MILLIS + " = " + beginTimeMillis + " AND "
				+ Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID + " = " + "'" + channelId + "'";

		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();

			NotificationDbItem notification = new NotificationDbItem();
			notification.setNotificationId(cursor.getInt(0));
			notification.setBroadcstUrl(cursor.getString(1));
			notification.setProgramId(cursor.getString(2));
			notification.setChannelId(cursor.getString(3));
			notification.setTimeInMillis(cursor.getString(4));
			return notification;
		} else {
			return null;
		}
	}

	public List<NotificationDbItem> getAllNotifications() {
		List<NotificationDbItem> notificationList = new ArrayList<NotificationDbItem>();
		String selectQuery = "SELECT * FROM " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS;

		SQLiteDatabase database = dbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				NotificationDbItem notification = new NotificationDbItem();
				notification.setNotificationId(cursor.getInt(0));
				notification.setProgramId(cursor.getString(1));
				notification.setBroadcstUrl(cursor.getString(2));
				notification.setChannelId(cursor.getString(3));
				notification.setTimeInMillis(cursor.getString(4));
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
}
