package com.mitv.enums;



public enum EventHighlightPeriodSortEnum 
{
	NOT_STARTED(1),
	FIRST_HALF(2),
	HALFTIME(3),
	SECOND_HALF(4),
	MINUTE_90(5),
	FIRST_EXTRA_TIME(6),
	MINUTE_105(7),
	SECOND_EXTRA_TIME(8),
	MINUTE_120(9),
	PENALTY_SHOOTOUT(10),
	END_OF_GAME(11);
	
	
	private final int code;
	
	
	
	EventHighlightPeriodSortEnum(int code) 
	{
		this.code = code;
	}
	
	
	
	public int getCode()
	{
		return code;
	}
	
	
	
	public static EventHighlightPeriodSortEnum getTypeEnumFromCode(int code)
	{
		for(EventHighlightPeriodSortEnum result: EventHighlightPeriodSortEnum.values())
		{
			if(result.getCode() == code) 
			{
				return result;
			}
			// No need for else
		}

		return EventHighlightPeriodSortEnum.NOT_STARTED;
	}
}
