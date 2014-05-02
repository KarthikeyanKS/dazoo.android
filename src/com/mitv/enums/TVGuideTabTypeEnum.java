
package com.mitv.enums;



public enum TVGuideTabTypeEnum 
{
	ALL_PROGRAMS(0),
	PROGRAM_CATEGORY(1),
	COMPETITION(2);
	
	
	private final int id;
	
	
	
	TVGuideTabTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}
