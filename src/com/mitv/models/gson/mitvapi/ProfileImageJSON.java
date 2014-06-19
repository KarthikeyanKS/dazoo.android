
package com.mitv.models.gson.mitvapi;



import android.util.Log;



public class ProfileImageJSON 
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
