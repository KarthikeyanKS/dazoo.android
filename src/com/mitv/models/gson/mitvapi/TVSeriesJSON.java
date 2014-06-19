
package com.mitv.models.gson.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import android.util.Log;



public class TVSeriesJSON
	implements GSONDataFieldValidation
{
	private static final String TAG = TVSeriesJSON.class.getName();
	
	
	private String seriesId;
	private String name;
	
	
	
	public String getSeriesId() 
	{
		if(seriesId == null)
		{
			seriesId = "";
			
			Log.w(TAG, "seriesId is null");
		}
		
		return seriesId;
	}
	
	
	
	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (getSeriesId().isEmpty() == false &&
								  getName().isEmpty() == false);

		return areFieldsValid;
	}
}