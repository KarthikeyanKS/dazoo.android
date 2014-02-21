
package com.millicom.mitv.models.gson;



import java.util.Date;



public class AppVersionJSON
{

	private String name;
	private String value;
	private Date expires;
	
	
	
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