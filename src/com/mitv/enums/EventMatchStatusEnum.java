
package com.mitv.enums;



public enum EventMatchStatusEnum 
{
	LINE_UP(0),
	NOT_STARTED(4),
	NOT_STARTED_AND_LINE_UP(6),
	IN_PROGRESS(8),
	INTERVAL(16),
	SUSPENDED(32),
	FINISHED(128),
	UNOFFICIAL_RESULT(192),
	NO_LIVE_UPDATES(256),
	ABANDONED(512),
	POSTPONED(1024),
	DELAYED(2014);
	
	
	
	private final int id;
	
	
	
	EventMatchStatusEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	
	
	
	public static EventMatchStatusEnum getTypeEnumFromCode(long code)
	{
		for(EventMatchStatusEnum result: EventMatchStatusEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return EventMatchStatusEnum.LINE_UP;
	}
}
