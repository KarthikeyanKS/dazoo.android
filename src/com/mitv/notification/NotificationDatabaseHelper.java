
package com.mitv.notification;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mitv.Consts;



public class NotificationDatabaseHelper 
	extends SQLiteOpenHelper 
{
	private static final String	DATABASE_CREATE	= 
			"CREATE TABLE " + Consts.NOTIFICATION_DB_TABLE_NOTIFICATIONS + 
			"(" + Consts.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY, " + 
			Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME_IN_MILISECONDS + " INTEGER, " +
			Consts.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_BROADCAST_END_TIME + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_BROADCAST_TYPE + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_SHARE_URL + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_ID + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_NAME + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_SMALL + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_MEDIUM + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_LARGE + " TEXT NOT NULL, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_ID + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TITLE + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TYPE + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_SHORT + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_LONG + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_TAGS + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_CREDITS + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_SEASON + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_YEAR + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_GENRE + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_SERIES_ID + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_SERIES_NAME + " TEXT, " +			
			Consts.NOTIFICATION_DB_COLUMN_SPORT_TYPE_ID + " TEXT, " +
			Consts.NOTIFICATION_DB_COLUMN_SPORT_TYPE_NAME + " TEXT " + ");";

	
	public NotificationDatabaseHelper(Context context) 
	{
		super(context, Consts.NOTIFICATION_DATABASE_NAME, null, Consts.NOTIFICATION_DATABASE_VERSION);
	}

	
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
	}

	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(NotificationDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");
		
		db.execSQL("DROP TABLE IF EXISTS " + Consts.NOTIFICATION_DATABASE_NAME);
		
		onCreate(db);
	}
}