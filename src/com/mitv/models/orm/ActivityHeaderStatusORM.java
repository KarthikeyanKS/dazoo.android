
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mitv.enums.ActivityHeaderStatusEnum;
import com.mitv.models.objects.ActivityHeaderStatus;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class ActivityHeaderStatusORM 
	extends AbstractOrmLiteClassWithAsyncSave<ActivityHeaderStatusORM> 
{	
	private static final String TAG = ActivityHeaderStatusORM.class.getName();
	
	
	@DatabaseField(generatedId=true)
	private int activityHeaderId;
	
	@DatabaseField()
	private ActivityHeaderStatusEnum status;
	
	@DatabaseField()
	private String dateUserLastClickedButton;
	
	
	
	private ActivityHeaderStatusORM(){}
	
	
	
	public ActivityHeaderStatusORM(ActivityHeaderStatus activityHeaderStatus) 
	{
		this.status = activityHeaderStatus.getActivityHeaderStatus();
		
		this.dateUserLastClickedButton = activityHeaderStatus.getDateUserLastClickedButton();
	}



	public ActivityHeaderStatusEnum getActivityHeaderStatusEnum() {
		return status;
	}
	
	
	
	public String getdDateUserLastClickedButton() {
		return dateUserLastClickedButton;
	}
	
	
	
	public static ActivityHeaderStatus getActivityHeaderStatus() 
	{
		ActivityHeaderStatus ut = null;
		
		ActivityHeaderStatusORM utORM = new ActivityHeaderStatusORM().getActivityHeaderStatusORM();
		
		if(utORM != null)
		{
			ut = new ActivityHeaderStatus(utORM);
		}
		else
		{
			ut = new ActivityHeaderStatus();
		}
		
		return ut;
	}



	private ActivityHeaderStatusORM getActivityHeaderStatusORM()
	{
		List<ActivityHeaderStatusORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<ActivityHeaderStatusORM, Integer> queryBuilder = (QueryBuilder<ActivityHeaderStatusORM, Integer>) getDao().queryBuilder();
			
			data = (List<ActivityHeaderStatusORM>) queryBuilder.query();
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
}