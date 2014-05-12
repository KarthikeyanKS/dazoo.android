
package com.mitv.enums;



public enum EventTabTypeEnum 
{
	GROUP_STAGE(0),
	SECOND_STAGE(1),
	TEAM_STANDINGS(2),
	EVENTS_STAGE(3),
	LINE_UP_STAGE(4);
	
	
	private final int id;
	
	
	
	EventTabTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}
