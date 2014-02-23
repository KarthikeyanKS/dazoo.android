
package com.millicom.mitv.enums;



public enum TabSelectedEnum 
{
	TV_GUIDE(0),
	ACTIVITY_FEED(1),
	MY_PROFILE(2);
	
	
	private final int id;
	
	
	
	TabSelectedEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}