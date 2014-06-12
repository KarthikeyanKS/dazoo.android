
package com.mitv.enums;



public enum NotificationTypeEnum 
{
	COMPETITION_EVENT_WITH_LOCAL_CHANNEL(0),
	
	COMPETITION_EVENT_WITH_EMBEDED_CHANNEL(1),
	
	TV_BROADCAST(2);
	
	
	
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