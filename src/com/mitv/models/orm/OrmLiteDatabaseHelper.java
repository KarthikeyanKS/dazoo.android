
package com.mitv.models.orm;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;



public class OrmLiteDatabaseHelper 
	extends OrmLiteSqliteOpenHelper
{
	private static final String LOG = OrmLiteDatabaseHelper.class.getSimpleName();

	
	
	public interface Upgrader 
	{
		InputStream getUpgradeStream(int oldVersion, int newVersion);
	}

	private Upgrader upgrader = null;

	protected static final String DATABASE_NAME = "aclol.db";
	protected static final int DATABASE_VERSION = 1;

	protected ConnectionSource connectionSource;

	
	
	private static OrmLiteDatabaseHelper instance;

	
	
	protected OrmLiteDatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		instance = this;
	}

	
	
	protected OrmLiteDatabaseHelper(
			Context context, 
			String databaseName,
			int databaseVersion, 
			Upgrader upgrader) 
	{
		super(context, databaseName, null, databaseVersion);
		
		this.upgrader = upgrader;
		
		instance = this;
	}

	
	
	protected static OrmLiteDatabaseHelper getInstance() 
	{
		return instance;
	}

	
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try 
		{
			Log.i(LOG, "onCreate");
		} 
		catch (Exception ex) 
		{
			Log.e(LOG, "onCreate failed", ex);
			
			throw new RuntimeException(ex);
		}
	}

	
	
	@Override
	public void onUpgrade(
			SQLiteDatabase db, 
			ConnectionSource connectionSource,
			int oldVersion, 
			int newVersion) 
	{
		try 
		{
			Log.i(LOG, "onUpgrade");
			
			if (upgrader != null)
			{
				InputStream isUpgradeStream = upgrader.getUpgradeStream(oldVersion, newVersion);
				
				BufferedReader r = new BufferedReader(new InputStreamReader(isUpgradeStream));
				
				String sqlLine;
				
				int lineNumber = 1;
				
				while ((sqlLine = r.readLine()) != null) 
				{
					String alterSql = sqlLine.trim();
					
					if (alterSql.length() > 0) 
					{
						String sql = sqlLine.trim();
						
						try 
						{
							db.execSQL(sql);
							Log.d(LOG, "Ran Upgrade Sql Statement : " + sql);
						} 
						catch (Exception ex) 
						{
							Log.d(LOG, "Failed To Execute Upgrade Sql Statement At Line : "+lineNumber);
						}
					}
					
					lineNumber++;
				}
				
				isUpgradeStream.close();
			}
			
			onCreate(db, connectionSource);
		} 
		catch (Exception ex) 
		{
			Log.e(LOG, "onUpgrade failed", ex);
			
			throw new RuntimeException(ex);
		}
	}

	
	
	@Override
	public void close()
	{
		super.close();
	}
}