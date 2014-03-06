
package com.mitv.models;



import java.util.Arrays;
import java.util.List;

import com.mitv.Constants;
import com.mitv.models.gson.AppVersionJSON;



public class AppVersion 
{
	private List<AppVersionJSON> appVersionDataParts;

	
	
	public AppVersion(AppVersionJSON[] appVersionDataParts)
	{
		this.appVersionDataParts = Arrays.asList(appVersionDataParts);
	}
	
	
	
	public AppVersion(List<AppVersionJSON> appVersionDataParts)
	{
		this.appVersionDataParts = appVersionDataParts;
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
}
