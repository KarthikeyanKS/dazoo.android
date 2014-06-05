
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class TVTagORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVTagORM>
{
	private static final String TAG = TVTagORM.class.getName();
	
	
	
	@DatabaseField(id = true)
	private String id;
	
	@DatabaseField()
	private String displayName;


	
	private TVTagORM(){}
	
	
	
	private TVTagORM(TVTag tvTag)
	{
		this.id = tvTag.getId();
		this.displayName = tvTag.getDisplayName();
	}
	
	
	
	public static void createAndSaveInAsyncTask(List<TVTag> tvTags)
	{
		List<AbstractOrmLiteClassWithAsyncSave<TVTagORM>> ormData = new ArrayList<AbstractOrmLiteClassWithAsyncSave<TVTagORM>>(tvTags.size());
		
		for(TVTag tvTag : tvTags)
		{
			TVTagORM tvTagORM = new TVTagORM(tvTag);

			ormData.add(tvTagORM);
		}

		new TVTagORM().saveListElementsInAsyncTask(ormData);
	}
	
	
	
	public static List<TVTag> getTVTags()
	{
		List<TVTag> data;
		
		List<TVTagORM> ormData = new TVTagORM().getAllTVTagsORM();
		
		if(ormData != null)
		{
			data = new ArrayList<TVTag>(ormData.size());
			
			for(TVTagORM tvTagORM : ormData)
			{
				TVTag tvTag = new TVTag(tvTagORM);
				
				data.add(tvTag);
			}

			return data;
		}
		else
		{
			return null;
		}
	}
	
	
	
	private List<TVTagORM> getAllTVTagsORM()
	{
		List<TVTagORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<TVTagORM, Integer> queryBuilder = (QueryBuilder<TVTagORM, Integer>) getDao().queryBuilder();
			
			data = (List<TVTagORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		return data;
	}


	
	public String getId() 
	{
		return id;
	}


	
	public String getDisplayName() 
	{
		return displayName;
	}
}