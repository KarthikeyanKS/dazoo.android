
package com.mitv.models;



import com.mitv.models.gson.AppVersionJSON;
import com.mitv.models.orm.AppVersionElementORM;



public class AppVersionElement 
	extends AppVersionJSON
{
	public AppVersionElement(AppVersionElementORM appVersionElementORM)
	{
		this.name = appVersionElementORM.getName();
		this.value = appVersionElementORM.getValue();
		this.expires = appVersionElementORM.getExpires();
	}
}
