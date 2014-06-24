
package com.mitv.enums;



public enum ActivityHeaderStatusEnum 
{
	SHOW_HEADER(0),
	
	SHOW_IF_NO_LIKES(1),
	
	SHOW_IF_NO_LIKES_SECOND_TIME(2),
	
	NEVER_SHOW_AGAIN(3);

	
	
	private final int id;
	
	
	
	ActivityHeaderStatusEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	
	
	
	public static ActivityHeaderStatusEnum getTypeEnumFromCode(long code)
	{
		for(ActivityHeaderStatusEnum result: ActivityHeaderStatusEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return ActivityHeaderStatusEnum.SHOW_HEADER;
	}
}
