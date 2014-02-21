
package com.millicom.mitv.models;



import java.util.Arrays;
import java.util.List;

import com.millicom.mitv.models.gson.AppVersionJSON;
import com.mitv.Consts;



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
			
			if(name.equalsIgnoreCase(Consts.JSON_VERSIONS_KEY_NAME))
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
			
			if(name.equalsIgnoreCase(Consts.JSON_VERSIONS_KEY_ANDROID))
			{
				value = name;
				break;
			}
		}
		
		return value;
	}
}
