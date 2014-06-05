
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class TVBroadcastORM
	extends AbstractOrmLiteClassWithAsyncSave<TVBroadcastORM> 
{
	private static final String TAG = TVBroadcastORM.class.getName();
	

	@DatabaseField(generatedId = true)
	protected long id;
	
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	protected TVProgramORM program;
	
	@DatabaseField()
	protected Long beginTimeMillis;
	
	@DatabaseField()
	protected String beginTime;
	
	@DatabaseField()
	protected String endTime;
	
	@DatabaseField()
	protected BroadcastTypeEnum broadcastType;
	
	@DatabaseField()
	protected String shareUrl;
	
	
	
	private TVBroadcastORM(){}
	
	
	
	public TVBroadcastORM(TVBroadcast broadcast)
	{
		this.program = new TVProgramORM(broadcast.getProgram());
		
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
		this.broadcastType = broadcast.getBroadcastType();
		this.shareUrl = broadcast.getShareUrl();
	}
	
	
	
	public static TVBroadcast getTVBroadcastByID(long id) 
	{
		TVBroadcast data;
		
		TVBroadcastORM ormData = new TVBroadcastORM().getTVBroadcastORM(id);
		
		if(ormData != null)
		{
			data = new TVBroadcast(ormData);
			
			return data;
		}
		else
		{
			return null;
		}
	}
	

	
	private TVBroadcastORM getTVBroadcastORM(long id) 
	{
		List<TVBroadcastORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<TVBroadcastORM, Integer> queryBuilder = (QueryBuilder<TVBroadcastORM, Integer>) getDao().queryBuilder();
			
			data = (List<TVBroadcastORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		if(data != null && data.isEmpty() == false)
		{
			return data.get(0);
		}
		else
		{
			return null;
		}
	}



	public TVProgramORM getProgram() {
		return program;
	}



	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}



	public String getBeginTime() {
		return beginTime;
	}



	public String getEndTime() {
		return endTime;
	}



	public BroadcastTypeEnum getBroadcastType() {
		return broadcastType;
	}



	public String getShareUrl() {
		return shareUrl;
	}
}
