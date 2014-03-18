
package com.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum LikeTypeRequestEnum 
{
	@SerializedName("SERIES")
	SERIES(0),
	
	@SerializedName("PROGRAM")
	PROGRAM(1),
	
	@SerializedName("SPORT_TYPE")
	SPORT_TYPE(2);
	
	private final int id;
	
	
	
	LikeTypeRequestEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static LikeTypeRequestEnum getLikeTypeRequestEnumFromCode(int code)
	{
		for(LikeTypeRequestEnum result: LikeTypeRequestEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return LikeTypeRequestEnum.SERIES;
	}



	public static LikeTypeRequestEnum getLikeTypeRequestEnumFromCode(String codeAsString)
	{
		int value = LikeTypeRequestEnum.SERIES.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return LikeTypeRequestEnum.SERIES;
		}

		return getLikeTypeRequestEnumFromCode(value);
	}
	
	
	
	public static LikeTypeRequestEnum getLikeTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		LikeTypeRequestEnum likeType = LikeTypeRequestEnum.valueOf(enumStringRepresentation);

		return likeType;
	}
}