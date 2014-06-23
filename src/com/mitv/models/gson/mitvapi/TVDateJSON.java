
package com.mitv.models.gson.mitvapi;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class TVDateJSON 
	extends BaseObjectJSON
{
	private static final String TAG = TVDateJSON.class.getName();
	
	
	protected String id;
	protected String displayName;
	protected String date;
	
	
	public TVDateJSON(){}
	
	
	
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
			
			Log.w(TAG, "url is null");
		}
		
		return displayName;
	}


	
	public String getDateString() 
	{
		if(date == null)
		{
			date = "";
			
			Log.w(TAG, "date is null");
		}
		
		return date;
	}
}