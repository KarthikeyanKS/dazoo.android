
package com.mitv.enums;



public enum ActivityFeedAdapterTypeEnum 
{
	ADS(0),
	
	BROADCAST(1),
	
	RECOMMENDED_BROADCAST(2),
	
	POPULAR_BROADCASTS(3),
	
	POPULAR_BROADCAST(4),
	
	POPULAR_TWITTER(5);

	
	
	private final int id;
	
	
	
	ActivityFeedAdapterTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}