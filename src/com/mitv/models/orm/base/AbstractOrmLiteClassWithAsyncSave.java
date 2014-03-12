package com.mitv.models.orm.base;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

import android.util.Log;



public abstract class AbstractOrmLiteClassWithAsyncSave<T>
	extends AbstractOrmLiteClass<T> 
{
	private static final String TAG = AbstractOrmLiteClassWithAsyncSave.class.getName();
	
	
	@DatabaseField(columnName = "modifydate")
	protected Date modifydate;
	
	
	
	public void saveInAsyncTask()
	{
		Thread t = new Thread(new Runnable() 
		{
			public void run() 
			{
				try
				{
					save();
				}
				catch(SQLException sqlex)
				{
					Log.w(TAG, sqlex.getMessage(), sqlex);
				}
			}
		});

		t.start();
	}
	
	
	
	@Override
	protected void onBeforeSave() 
	{
		this.modifydate = new Date();
	}
	
	
	
	@Override
	protected void onAfterSave() 
	{}
}