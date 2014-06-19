
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.UserFieldsDataJSON;
import com.mitv.models.orm.UserLoginDataORM;



public class UserFieldsData
	extends UserFieldsDataJSON
{
	public UserFieldsData(){}
	
	
	public UserFieldsData(UserLoginDataORM userLoginDataORM)
	{
		this.userId = userLoginDataORM.getUserId();
		this.email = userLoginDataORM.getEmail();
		this.firstName = userLoginDataORM.getFirstName();
		this.lastName = userLoginDataORM.getLastName();
		this.created = userLoginDataORM.isCreated();
	}
}