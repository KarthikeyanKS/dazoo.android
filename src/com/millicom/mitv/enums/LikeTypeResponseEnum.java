
package com.millicom.mitv.enums;



import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.millicom.mitv.models.TVProgram;



public enum LikeTypeResponseEnum 
{
	UNKNOWN(0),
	
	@SerializedName("SERIES")
	SERIES(1),
	
	@SerializedName("PROGRAM")
	PROGRAM(2),
	
	@SerializedName("SPORT_TYPE")
	SPORT_TYPE(3);
	
	

	private static final String	TAG	= LikeTypeResponseEnum.class.getName();
	
	
	private final int id;
	
	
	
	LikeTypeResponseEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	
	
	public static LikeTypeResponseEnum getLikeTypeEnumFromTVProgram(TVProgram tvProgram) 
	{
		ProgramTypeEnum programType = tvProgram.getProgramType();
		
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.PROGRAM;
		
		switch (programType) 
		{
			case TV_EPISODE: 
			{
				likeTypeFromBroadcast = LikeTypeResponseEnum.SERIES;
				break;
			}
			case SPORT: 
			{
				likeTypeFromBroadcast = LikeTypeResponseEnum.SPORT_TYPE;
				break;
			}
			case MOVIE:
			case OTHER: 
			{
				likeTypeFromBroadcast = LikeTypeResponseEnum.PROGRAM;
				break;
			}
			
			default:
			{
				likeTypeFromBroadcast = LikeTypeResponseEnum.PROGRAM;
				Log.w(TAG, "Using the default program type.");
				break;
			}
		}
		
		return likeTypeFromBroadcast;
	}

	

	public static LikeTypeResponseEnum getLikeTypeEnumFromCode(int code)
	{
		for(LikeTypeResponseEnum result: LikeTypeResponseEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
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
	
	
	
	public static LikeTypeResponseEnum getLikeTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		LikeTypeResponseEnum likeType = LikeTypeResponseEnum.valueOf(enumStringRepresentation);

		return likeType;
	}
}