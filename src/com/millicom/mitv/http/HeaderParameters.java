
package com.millicom.mitv.http;



import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



public class HeaderParameters 
{
	@SuppressWarnings("unused")
	private static final String TAG = HeaderParameters.class.getName();
	
	
	private Map<String, String> headerParameters;
	
	
	
	public HeaderParameters()
	{
		headerParameters = new HashMap<String, String>();
	}
	
	
	
	public void add(
			final String key, 
			final String value)
	{
		headerParameters.put(key, value);
	}
	
	
	
	public Set<Entry<String, String>> entrySet()
	{
		return headerParameters.entrySet();
	}
}