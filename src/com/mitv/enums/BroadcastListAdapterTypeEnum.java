
package com.mitv.enums;



public enum BroadcastListAdapterTypeEnum 
{
	UPCOMING_BROADCASTS(0),
	
	PROGRAM_REPETITIONS(1);

	
	
	private final int id;
	
	
	
	BroadcastListAdapterTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}