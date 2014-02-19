
package com.millicom.mitv.models;



import java.util.Arrays;
import java.util.List;

import com.millicom.mitv.models.gson.AppVersion;
import com.mitv.Consts;



public class AppVersionData 
{
	private List<AppVersion> appVersionDataParts;

	
	
	public AppVersionData(AppVersion[] appVersionDataParts)
	{
		this.appVersionDataParts = Arrays.asList(appVersionDataParts);
	}
	
	
	
	public AppVersionData(List<AppVersion> appVersionDataParts)
	{
		this.appVersionDataParts = appVersionDataParts;
	}
	
	
	
	public String getApiVersion() 
	{
		String value = "";
		
		for(AppVersion dataPart : appVersionDataParts)
		{
			String name = dataPart.getName();
			
			if(name.equalsIgnoreCase(Consts.JSON_VERSIONS_KEY_NAME))
			{
				value = name;
				break;
			}
		}
		
		return value;
	}
	
	
	
	public String getAndroidVersion()
	{
		String value = "";
		
		for(AppVersion dataPart : appVersionDataParts)
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
