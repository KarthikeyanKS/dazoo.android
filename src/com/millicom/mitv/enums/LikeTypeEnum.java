
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum LikeTypeEnum 
{
	@SerializedName("SERIES")
	SERIES(0),
	
	@SerializedName("PROGRAM")
	PROGRAM(1),
	
	@SerializedName("SPORT_TYPE")
	SPORT_TYPE(2);
	
	
	
	private final int id;
	
	
	
	LikeTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static LikeTypeEnum getLikeTypeEnumFromCode(int code)
	{
		for(LikeTypeEnum result: LikeTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return LikeTypeEnum.SERIES;
	}



	public static LikeTypeEnum getLikeTypeEnumFromCode(String codeAsString)
	{
		int value = LikeTypeEnum.SERIES.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return LikeTypeEnum.SERIES;
		}

		return getLikeTypeEnumFromCode(value);
	}
}