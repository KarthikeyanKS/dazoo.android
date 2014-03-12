
package com.mitv.models;



import com.mitv.models.gson.UserFieldsDataJSON;
import com.mitv.models.orm.UserLoginDataORM;



public class UserFieldsData
	extends UserFieldsDataJSON
{
	public UserFieldsData(UserLoginDataORM userLoginDataORM)
	{
		this.userId = userLoginDataORM.getUserId();
		this.email = userLoginDataORM.getEmail();
		this.firstName = userLoginDataORM.getFirstName();
		this.lastName = userLoginDataORM.getLastName();
		this.created = userLoginDataORM.isCreated();
	}
}