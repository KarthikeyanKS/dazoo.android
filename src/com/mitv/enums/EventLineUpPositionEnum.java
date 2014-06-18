
package com.mitv.enums;



public enum EventLineUpPositionEnum 
{
	GOALKEEPER(1, "A"),
	DEFENSE(2, "D"),
	MIDFIELDER(4, "MC"),
	FORWARD(8, "DEL"),
	COACH(16, "EN"),
	REFEREE(64, ""),
	LINESMAN(128, ""),
	FORTHTHOFFICIAL(1073741824, "");
	
	
	
	private final int code;
	private final String positionShort;
	
	
	
	EventLineUpPositionEnum(int code, String positionShort) 
	{
		this.code = code;
		this.positionShort = positionShort;
	}
	
	
	
	public int getCode()
	{
		return code;
	}
	
	
	
	public String getPositionShort()
	{
		return positionShort;
	}
	
	
	
	public static EventLineUpPositionEnum getTypeEnumFromCode(int code)
	{
		for(EventLineUpPositionEnum result: EventLineUpPositionEnum.values())
		{
			if(result.getCode() == code) 
			{
				return result;
			}
			// No need for else
		}

		return EventLineUpPositionEnum.DEFENSE;
	}
}
