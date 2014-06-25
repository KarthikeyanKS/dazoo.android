
package com.mitv.models.gson.mitvapi;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class ProfileImageJSON
	extends BaseObjectJSON
{	
	private static final String TAG = ProfileImageJSON.class.getName();
	
	
	protected String url;
	protected boolean isDefault;

	
	
	public String getUrl() 
	{
		if(url == null)
		{
			url = "";
			
			Log.w(TAG, "url is null");
		}
		
		return url;
	}

	
	
	public boolean isDefault() 
	{
		return isDefault;
	}	
}
