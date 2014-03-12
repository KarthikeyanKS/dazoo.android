
package com.mitv.models;



import com.mitv.models.gson.UserLoginDataJSON;
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