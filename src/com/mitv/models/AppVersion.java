
package com.mitv.models;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

import com.mitv.Constants;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.AppVersionJSON;
import com.mitv.models.orm.AppVersionElementORM;



public class AppVersion implements GSONDataFieldValidation
{
	private List<AppVersionElement> appVersionDataParts;

	
	
	public AppVersion(AppVersionElement[] appVersionDataParts)
	{
		this.appVersionDataParts = Arrays.asList(appVersionDataParts);
	}
	
	
	
	public AppVersion(List<AppVersionElement> appVersionDataParts)
	{
		this.appVersionDataParts = appVersionDataParts;
	}
	
	
	
	public AppVersion(List<AppVersionElementORM> AppVersionElementsORM, boolean ex)
	{
		this.appVersionDataParts = new ArrayList<AppVersionElement>(AppVersionElementsORM.size());
		
		for(AppVersionElementORM appVersionORMElement : AppVersionElementsORM)
		{
			AppVersionElement appVersionElement = new AppVersionElement(appVersionORMElement);
			
			appVersionDataParts.add(appVersionElement);
		}
	}
	
	
	
	public String getApiVersion() 
	{
		String value = "";
		
		for(AppVersionJSON dataPart : appVersionDataParts)
		{
			String name = dataPart.getName();
			
			if(name.equalsIgnoreCase(Constants.JSON_VERSIONS_KEY_NAME))
			{
				value = dataPart.getValue();
				break;
			}
		}
		
		return value;
	}
	
	
	
	public String getAndroidVersion()
	{
		String value = "";
		
		for(AppVersionJSON dataPart : appVersionDataParts)
		{
			String name = dataPart.getName();
			
			if(name.equalsIgnoreCase(Constants.JSON_VERSIONS_KEY_ANDROID))
			{
				value = name;
				break;
			}
		}
		
		return value;
	}
	
	
	
	public boolean isAPIVersionSupported() 
	{
		String apiVersion = this.getApiVersion();
		
		boolean isAPIVersionSupported = apiVersion.equalsIgnoreCase(Constants.SUPPORTED_API_VERSION);

		return isAPIVersionSupported;
	}



	public List<AppVersionElement> getAppVersionDataParts() 
	{
		return appVersionDataParts;
	}



	@Override
	public boolean areDataFieldsValid() {
		boolean androidVersionDataOk = !TextUtils.isEmpty(getAndroidVersion());
		boolean apiVersionDataOk = !TextUtils.isEmpty(getApiVersion());
		boolean areDataFieldsValid = androidVersionDataOk && apiVersionDataOk;
		
		return areDataFieldsValid;
	}
}
