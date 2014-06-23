
package com.mitv.http;



import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;

import android.util.Log;



public class HeaderParameters 
{
	private static final String TAG = HeaderParameters.class.getName();
	
	
	private Map<String, String> headerParameters;
	
	
	
	public HeaderParameters()
	{
		headerParameters = new HashMap<String, String>();
	}
	
	
	
	public HeaderParameters(Header[] headers)
	{
		this();
		
		for(Header header : headers)
		{
			String key = header.getName();
			String value = header.getValue();
			
			if(key != null && value != null)
			{
				add(key, value);
			}
			else
			{
				Log.w(TAG, "Failed to add header value");
			}
		}
	}
	
	
	
	public void add(
			final String key, 
			final String value)
	{
		headerParameters.put(key, value);
	}
	
	
	
	public boolean contains(String key)
	{
		return headerParameters.containsKey(key);
	}
	
	
	
	public String get(String key)
	{
		return headerParameters.get(key);
	}
	
	
	
	public Set<Entry<String, String>> entrySet()
	{
		return headerParameters.entrySet();
	}
}