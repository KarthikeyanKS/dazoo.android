
package com.mitv.models.gson;



import java.util.Calendar;



public class TVDateJSON 
{
	protected String id;
	protected String date;
	protected String displayName;
	
	protected transient Calendar dateCalendar;
	
	
	
	public TVDateJSON()
	{}
	
	
	public String getId() 
	{
		return id;
	}
	
	public String getDisplayName() 
	{
		return displayName;
	}
}
