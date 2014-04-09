
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.AppVersionJSON;
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
