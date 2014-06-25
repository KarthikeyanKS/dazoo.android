
package com.mitv.models.orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class TVDateORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVDateORM>
{
	private static final String TAG = TVDateORM.class.getName();
	
	
	@DatabaseField(id = true)
	private String id;
	
	@DatabaseField()
	private String date;
	
	@DatabaseField()
	private String displayName;
	
	
	
	private TVDateORM(){}
	
	
	
	public TVDateORM(TVDate tvDate)
	{
		this.id = tvDate.getId();
		this.date = tvDate.getDateString();
		this.displayName = tvDate.getDisplayName();
	}
	
	
	
	public static void createAndSaveInAsyncTask(List<TVDate> tvDates)
	{
		List<AbstractOrmLiteClassWithAsyncSave<TVDateORM>> ormData = new ArrayList<AbstractOrmLiteClassWithAsyncSave<TVDateORM>>(tvDates.size());
		
		for(TVDate tvDate : tvDates)
		{
			TVDateORM tvDateORM = new TVDateORM(tvDate);

			ormData.add(tvDateORM);
		}

		new TVDateORM().saveListElementsInAsyncTask(ormData);
	}
		
	
	
	public static List<TVDate> getTVDates()
	{
		List<TVDate> data;
		
		List<TVDateORM> ormData = new TVDateORM().getAllTVDatesORM();
		
		if(ormData != null)
		{
			data = new ArrayList<TVDate>(ormData.size());
			
			for(TVDateORM tvDateORM : ormData)
			{
				TVDate tvDate = new TVDate(tvDateORM);
				
				data.add(tvDate);
			}

			return data;
		}
		else
		{
			return null;
		}
	}
	
	
	
	private List<TVDateORM> getAllTVDatesORM()
	{
		List<TVDateORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<TVDateORM, Integer> queryBuilder = (QueryBuilder<TVDateORM, Integer>) getDao().queryBuilder();
			
			data = (List<TVDateORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		return data;
	}



	public String getId() {
		return id;
	}



	public String getDate() {
		return date;
	}



	public String getDisplayName() {
		return displayName;
	}
}
