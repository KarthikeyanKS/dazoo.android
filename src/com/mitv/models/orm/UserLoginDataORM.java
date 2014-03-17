
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mitv.models.ProfileImage;
import com.mitv.models.UserFieldsData;
import com.mitv.models.UserLoginData;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class UserLoginDataORM 
	extends AbstractOrmLiteClassWithAsyncSave<UserLoginDataORM> 
{	
	private static final String TAG = UserLoginDataORM.class.getName();
	
	
	
	@DatabaseField(id=true)
	private String userId;
	
	@DatabaseField()
	private String token;
	
	@DatabaseField(canBeNull = false)
	private String email;
	
	@DatabaseField(canBeNull = false)
	private String firstName;
	
	@DatabaseField()
	private String lastName;
	
	@DatabaseField(canBeNull = false)
	private boolean created;

	@DatabaseField(canBeNull = false)
	private String url;
	
	@DatabaseField(canBeNull = false)
	private boolean isDefault;
	
	
	
	private UserLoginDataORM()
	{}
	
	
	public UserLoginDataORM(UserLoginData userLoginData) 
	{
		UserFieldsData user = userLoginData.getUser();
		
		this.token = userLoginData.getToken();
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.created = user.isCreated();

		ProfileImage profileImage = userLoginData.getProfileImage();
		
		this.url = profileImage.getUrl();
		this.isDefault = profileImage.isDefault();
	}
	
	
	
	public static UserLoginData getUserLoginData() 
	{
		UserLoginData userLoginData;
		
		UserLoginDataORM userLoginDataORM = new UserLoginDataORM().getUserLoginDataORM();
		
		if(userLoginDataORM != null)
		{
			userLoginData = new UserLoginData(userLoginDataORM);
			
			return userLoginData;
		}
		else
		{
			return null;
		}
	}
	

	
	private UserLoginDataORM getUserLoginDataORM() 
	{
		List<UserLoginDataORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<UserLoginDataORM, Integer> queryBuilder = (QueryBuilder<UserLoginDataORM, Integer>) getDao().queryBuilder();
			
			data = (List<UserLoginDataORM>) queryBuilder.query();
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
	
	
	
	public void delete()
	{
		try 
		{
			deleteById("userId", userId);
		}
		catch (SQLException sqlex) 
		{
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
	}
	

	
	public String getToken() 
	{
		return token;
	}



	public String getUserId() 
	{
		return userId;
	}



	public String getEmail() {
		return email;
	}



	public String getFirstName() {
		return firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public boolean isCreated() {
		return created;
	}



	public String getUrl() {
		return url;
	}



	public boolean isDefault() {
		return isDefault;
	}



	public Date getModifydate() {
		return modifydate;
	}
}
