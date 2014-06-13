
package com.mitv.enums;



public enum EventLineUpPosition 
{
	GOALKEEPER(1),
	DEFENSE(2),
	MIDFIELDER(4),
	FORWARD(8),
	COACH(16),
	REFEREE(64),
	LINESMAN(128),
	FORTHTHOFFICIAL(1073741824);
	
	
	
	private final int code;
	
	
	
	EventLineUpPosition(int code) 
	{
		this.code = code;
	}
	
	
	
	public int getCode()
	{
		return code;
	}
	
	
	
	public static EventLineUpPosition getTypeEnumFromCode(int code)
	{
		for(EventLineUpPosition result: EventLineUpPosition.values())
		{
			if(result.getCode() == code) 
			{
				return result;
			}
			// No need for else
		}

		return EventLineUpPosition.DEFENSE;
	}
}
