
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum LikeTypeResponseEnum 
{
	@SerializedName("SERIES")
	SERIES(0),
	
	@SerializedName("PROGRAM")
	PROGRAM(1),
	
	@SerializedName("SPORT_TYPE")
	SPORT_TYPE(2);
	
	
	
	private final int id;
	
	
	
	LikeTypeResponseEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static LikeTypeResponseEnum getLikeTypeEnumFromCode(int code)
	{
		for(LikeTypeResponseEnum result: LikeTypeResponseEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return LikeTypeResponseEnum.SERIES;
	}



	public static LikeTypeResponseEnum getLikeTypeEnumFromCode(String codeAsString)
	{
		int value = LikeTypeResponseEnum.SERIES.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return LikeTypeResponseEnum.SERIES;
		}

		return getLikeTypeEnumFromCode(value);
	}
}