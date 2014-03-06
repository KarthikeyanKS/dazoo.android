
package com.mitv.enums;



public enum ChannelRowTypeEnum
{
	ON_AIR(0),
	
	UP_COMING(1);
	
	
	
	private final int id;
	
	
	
	ChannelRowTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId()
	{
		return id;
	}
}