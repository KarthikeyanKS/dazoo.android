
package com.mitv.enums;



import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.mitv.models.objects.mitvapi.TVProgram;



public enum LikeTypeResponseEnum 
{
	UNKNOWN(0),
	
	@SerializedName("SERIES")
	SERIES(1),
	
	@SerializedName("PROGRAM")
	PROGRAM(2),
	
	@SerializedName("SPORT_TYPE")
	SPORT_TYPE(3),
	
	@SerializedName("COMPETITION")
	COMPETITION(3),
	
	@SerializedName("TEAM")
	TEAM(4);
	
	

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
				likeTypeFromBroadcast = LikeTypeResponseEnum.UNKNOWN;
				Log.w(TAG, "The program type is unknown");
				break;
			}
		}
		
		return likeTypeFromBroadcast;
	}

	

	public static LikeTypeResponseEnum getTypeEnumFromCode(int code)
	{
		for(LikeTypeResponseEnum result: LikeTypeResponseEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
		}

		return LikeTypeResponseEnum.UNKNOWN;
	}



	public static LikeTypeResponseEnum getTypeEnumFromCode(String codeAsString)
	{
		int value = LikeTypeResponseEnum.UNKNOWN.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return LikeTypeResponseEnum.UNKNOWN;
		}

		return getTypeEnumFromCode(value);
	}
	
	
	
	public static LikeTypeResponseEnum getTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		LikeTypeResponseEnum likeType = LikeTypeResponseEnum.valueOf(enumStringRepresentation);

		return likeType;
	}
}