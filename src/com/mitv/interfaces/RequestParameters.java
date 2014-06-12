
package com.mitv.interfaces;



import java.util.HashMap;
import java.util.Map;

import android.util.Log;



public class RequestParameters 
{
	private static final String TAG = RequestParameters.class.getName();
	
	
	
	private Map<String, String> requestParameters;
	
	
	
	public RequestParameters()
	{
		requestParameters = new HashMap<String, String>();
	}

	
	
	public void add(
			final String header,
			final String value)
	{
		requestParameters.put(header, value);
	}
	
	
	
	public void add(
			final String header,
			final Long value)
	{
		Long valueAsLong = Long.valueOf(value);
		
		add(header, valueAsLong.toString());
	}
	
	
	
	public Long getAsLong(final String header)
	{
		Long value = Long.valueOf(0);
		
		String valueAsString = requestParameters.get(header);
		
		if(valueAsString != null)
		{
			try
			{
				value = Long.parseLong(valueAsString);
			}
			catch(NumberFormatException nfex)
			{
				Log.w(TAG, "Failed to parse value");
			}
		}
		
		return value;
	}
}