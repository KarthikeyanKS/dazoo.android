
package com.mitv.models.gson.mitvapi;



import java.util.Date;
import android.util.Log;
import com.mitv.utilities.DateUtils;



public class AppVersionJSON
{
	private static final String TAG = AppVersionJSON.class.getName();
	
	
	protected String name;
	protected String value;
	protected Date expires;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public AppVersionJSON(){}
		
	
	
	public String getName()
	{
		if (name == null) 
    	{
			name = "";
    	
    		Log.w(TAG, "name is null");
    	}
		
		return name;
	}
	
	
	
	public String getValue() 
	{
		if (value == null) 
    	{
			value = "";
    	
    		Log.w(TAG, "value is null");
    	}
		
		return value;
	}
	
	
	
	public Date getExpires()
	{
		if (expires == null) 
    	{
			expires = DateUtils.getNowWithGMTTimeZone().getTime();
    	
    		Log.w(TAG, "expires is null");
    	}
		
		return expires;
	}
}