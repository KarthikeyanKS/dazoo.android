package com.mitv.models.orm;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mitv.enums.UserTutorialStatusEnum;
import com.mitv.models.objects.UserTutorialStatus;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;

public class UserTutorialStatusORM extends AbstractOrmLiteClassWithAsyncSave<UserTutorialStatusORM> {
	
	private static final String TAG = UserTutorialStatusORM.class.getName();
	
	@DatabaseField(generatedId=true)
	private int userTutorialId;
	
	@DatabaseField()
	private UserTutorialStatusEnum status;
	
	@DatabaseField(canBeNull = false)
	private String dateUserLastOpendApp;
	
	
	
	
	
	private UserTutorialStatusORM()
	{}
	
	
	
	public UserTutorialStatusORM(UserTutorialStatus userTutorial) {
		this.status = userTutorial.getUserTutorialStatus();
		
		this.dateUserLastOpendApp = userTutorial.getDateUserLastOpendApp();
	}



	public UserTutorialStatusEnum getUserTutorialStatus() {
		return status;
	}
	
	
	
	public String getdDateUserLastOpendApp() {
		return dateUserLastOpendApp;
	}
	
	
	
	public static UserTutorialStatus getUserTutorial() {
		UserTutorialStatus ut = null;
		
		UserTutorialStatusORM utORM = new UserTutorialStatusORM().getUserTutorialORM();
		
		if(utORM != null)
		{
			ut = new UserTutorialStatus(utORM);
		}
		else
		{
			ut = new UserTutorialStatus();
		}
		
		return ut;
	}



	private UserTutorialStatusORM getUserTutorialORM()
	{
		List<UserTutorialStatusORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<UserTutorialStatusORM, Integer> queryBuilder = (QueryBuilder<UserTutorialStatusORM, Integer>) getDao().queryBuilder();
			
			data = (List<UserTutorialStatusORM>) queryBuilder.query();
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
