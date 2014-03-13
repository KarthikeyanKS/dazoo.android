
package com.mitv.models.gson;



import java.util.Date;



public class AppVersionJSON
{
	protected String name;
	protected String value;
	protected Date expires;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public AppVersionJSON()
	{}
		
	
	
	public boolean hasExpires()
	{
		return (expires != null);
	}
	
	
	
	public String getName() 
	{
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	
	
	public Date getExpires() {
		return expires;
	}
}