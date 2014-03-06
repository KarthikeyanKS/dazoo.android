
package com.mitv.models.sql;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mitv.Constants;



public class NotificationSQLDatabaseHelper 
	extends SQLiteOpenHelper 
{
	private static final String	DATABASE_CREATE	= 
			"CREATE TABLE " + Constants.NOTIFICATION_DB_TABLE_NOTIFICATIONS + 
			"(" + Constants.NOTIFICATION_DB_COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY, " + 
			Constants.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME_IN_MILISECONDS + " INTEGER, " +
			Constants.NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_BROADCAST_END_TIME + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_BROADCAST_TYPE + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_SHARE_URL + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_CHANNEL_ID + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_CHANNEL_NAME + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_SMALL + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_MEDIUM + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_LARGE + " TEXT NOT NULL, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_ID + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_TITLE + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_TYPE + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_SHORT + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_LONG + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_TAGS + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_CREDITS + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_SEASON + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_YEAR + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_GENRE + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_SERIES_ID + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_SERIES_NAME + " TEXT, " +			
			Constants.NOTIFICATION_DB_COLUMN_SPORT_TYPE_ID + " TEXT, " +
			Constants.NOTIFICATION_DB_COLUMN_SPORT_TYPE_NAME + " TEXT " + ");";

	
	public NotificationSQLDatabaseHelper(Context context) 
	{
		super(context, Constants.NOTIFICATION_DATABASE_NAME, null, Constants.NOTIFICATION_DATABASE_VERSION);
	}

	
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
	}

	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(NotificationSQLDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old notification data.");
		
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS ");
		sb.append(Constants.NOTIFICATION_DB_TABLE_NOTIFICATIONS);
		
		db.execSQL(sb.toString());
		
		onCreate(db);
	}
}