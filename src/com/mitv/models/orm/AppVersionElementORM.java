
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mitv.models.AppVersion;
import com.mitv.models.AppVersionElement;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class AppVersionElementORM
	extends AbstractOrmLiteClassWithAsyncSave<AppVersionElementORM> 
{
	private static final String TAG = AppVersionElementORM.class.getName();
	
	
	
	@DatabaseField(id=true)
	private String name;
	
	@DatabaseField()
	private String value;
	
	@DatabaseField(canBeNull=true)
	private Date expires;
	
	
	
	private AppVersionElementORM()
	{}
	
	
	
	public AppVersionElementORM(AppVersionElement element)
	{
		this.name = element.getName();
		this.value = element.getValue();
		this.expires = element.getExpires();
	}
	
	
	
	
	public static AppVersion getAppVersion()
	{
		AppVersion data;
		
		List<AppVersionElementORM> ormData = new AppVersionElementORM().getAppVersionElementORMList();
		
		if(ormData != null)
		{
			data = new AppVersion(ormData, true);
			
			return data;
		}
		else
		{
			return null;
		}
	}
	
	
	
	private List<AppVersionElementORM> getAppVersionElementORMList()
	{
		List<AppVersionElementORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<AppVersionElementORM, Integer> queryBuilder = (QueryBuilder<AppVersionElementORM, Integer>) getDao().queryBuilder();
			
			data = (List<AppVersionElementORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		return data;
	}
	

	
	public String getName() {
		return name;
	}



	public String getValue() {
		return value;
	}



	public Date getExpires() {
		return expires;
	}
}