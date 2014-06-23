
package com.mitv.models.gson.mitvapi;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class TVSeriesSeasonJSON 
	extends BaseObjectJSON
{
	private static final String TAG = TVSeriesSeasonJSON.class.getName();
	
	
	protected Integer number;
	
	
	
	public Integer getNumber() 
	{
		if(number == null)
		{
			number = Integer.valueOf(0);
			
			Log.w(TAG, "number is null");
		}
		
		return number;
	}
}