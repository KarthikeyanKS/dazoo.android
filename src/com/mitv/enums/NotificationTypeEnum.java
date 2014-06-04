
package com.mitv.enums;



public enum NotificationTypeEnum 
{
	COMPETITION_EVENT(0),
	
	TV_BROADCAST(1);
	
	
	
	private final int id;
	
	
	
	NotificationTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}