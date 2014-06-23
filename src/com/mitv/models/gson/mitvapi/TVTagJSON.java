
package com.mitv.models.gson.mitvapi;



import android.util.Log;



public class TVTagJSON 
{
	private static final String TAG = TVTagJSON.class.getName();
	
	
	protected String id;
	protected String displayName;
	
	
	
	public String getId() 
	{
		if(id == null)
		{
			id = "";
			
			Log.w(TAG, "id is null");
		}
		
		return id;
	}
	
	
	
	public String getDisplayName() 
	{
		if(displayName == null)
		{
			displayName = "";
			
			Log.w(TAG, "displayName is null");
		}
		
		return displayName;
	}
}