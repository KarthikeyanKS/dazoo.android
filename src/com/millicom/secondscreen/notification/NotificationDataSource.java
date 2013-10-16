package com.millicom.secondscreen.notification;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.NotificationDbItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NotificationDataSource {
	
	private SQLiteDatabase database;
	private NotificationDatabaseHelper dbHelper;
	private String[] allColumns = {Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID, Consts.NOTIFICATION_DB_COLUMN_BROADCAST_URL, Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID,
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID, Consts.NOTIFICATION_DB_COLUMN_BEGIN_TIME_MILLIS};
	
	public NotificationDataSource(Context context){
		dbHelper = new NotificationDatabaseHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}

	public void close(){
		dbHelper.close();
	}
	
	//public NotificationDbItem createNotification(NotificationDbItem notification){
	//	ContentValues values = new ContentValues();
	//	
	//	
	//}
}
