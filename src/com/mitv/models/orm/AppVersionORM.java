
package com.mitv.models.orm;



import com.mitv.models.objects.mitvapi.AppVersion;
import com.mitv.models.objects.mitvapi.AppVersionElement;



public class AppVersionORM
{
	@SuppressWarnings("unused")
	private static final String TAG = AppVersionORM.class.getName();
	
	
	
	public static void createAndSaveInAsyncTask(AppVersion data)
	{
		for(AppVersionElement element : data.getAppVersionDataParts())
		{
			AppVersionElementORM appVersionElementORM = new AppVersionElementORM(element);
			
			appVersionElementORM.saveInAsyncTask();
		}
	}
	
	
	
	public static AppVersion getAppVersion()
	{
		return AppVersionElementORM.getAppVersion();
	}
}