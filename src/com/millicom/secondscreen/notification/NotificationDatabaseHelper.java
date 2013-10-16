package com.millicom.secondscreen.notification;

import com.millicom.secondscreen.Consts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

	// database creation sql statement
	private static final String	DATABASE_CREATE				= "create table " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS + "(" + Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + "integer primary key" + Consts.NOTIFICATION_DB_COLUMN_BROADCAST_URL + "text"
			+ Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID + "text not null" + Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID + "text not null" + Consts.NOTIFICATION_DB_COLUMN_BEGIN_TIME_MILLIS + "long" + ");";

	public NotificationDatabaseHelper(Context context) {
		super(context, Consts.NOTIFICATION_DATABASE_NAME, null, Consts.NOTIFICATION_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(NotificationDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Consts.NOTIFICATION_DATABASE_NAME);
		onCreate(db);
	}

}
