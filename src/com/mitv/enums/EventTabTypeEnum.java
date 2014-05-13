
package com.mitv.enums;



public enum EventTabTypeEnum 
{
	COMPETITION_GROUP_STAGE(0),
	COMPETITION_SECOND_STAGE(1),
	COMPETITION_STANDINGS(2),
	EVENT_HIGHLIGHTS(3),
	EVENT_LINE_UP(4),
	EVENT_GROUP_STAGE(5),
	EVENT_GROUP_STANDINGS(6);
	
	
	
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
