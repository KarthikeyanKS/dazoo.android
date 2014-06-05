package com.mitv.enums;

import com.google.gson.annotations.SerializedName;

public enum UserTutorialStatusEnum {

	NEVER_SEEN_TUTORIAL(0),
	
	SEEN_ONCE(1),
	
	NEVER_SHOW_AGAIN(2);
	
	private final int id;
	
	
	
	UserTutorialStatusEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	
	
	
	public static UserTutorialStatusEnum getTypeEnumFromCode(long code)
	{
		for(UserTutorialStatusEnum result: UserTutorialStatusEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return UserTutorialStatusEnum.NEVER_SEEN_TUTORIAL;
	}
	
}
