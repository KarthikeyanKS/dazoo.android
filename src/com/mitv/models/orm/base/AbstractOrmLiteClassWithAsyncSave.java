package com.mitv.models.orm.base;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;



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
	
	
	
	protected void saveListElementsInAsyncTask(final List<AbstractOrmLiteClassWithAsyncSave<T>> elementList)
	{
		Thread t = new Thread(new Runnable() 
		{
			public void run() 
			{
				try
				{
					for(AbstractOrmLiteClass<T> element : elementList)
					{
						element.save();
					}
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