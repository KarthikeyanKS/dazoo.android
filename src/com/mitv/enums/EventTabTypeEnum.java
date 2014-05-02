
package com.mitv.enums;



public enum EventTabTypeEnum 
{
	GROUP_STAGE(0),
	SECOND_STAGE(1),
	TEAM(2);
	
	
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
