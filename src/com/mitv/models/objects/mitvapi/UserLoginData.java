
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.UserLoginDataJSON;
import com.mitv.models.orm.UserLoginDataORM;



public class UserLoginData 
	extends UserLoginDataJSON
{
	public UserLoginData(UserLoginDataORM userLoginDataORM)
	{
		this.token = userLoginDataORM.getToken();
		
		this.user = new UserFieldsData(userLoginDataORM);
	
		this.profileImage = new ProfileImage(userLoginDataORM);
	}
}