
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;



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
	
	public static LikeTypeResponseEnum getLikeTypeEnumFromBroadcast(TVBroadcastWithChannelInfo broadcastWithChannelInfo) {
		ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.PROGRAM;
		switch (programType) {
		case TV_EPISODE: {
			likeTypeFromBroadcast = LikeTypeResponseEnum.SERIES;
			break;
		}
		case SPORT: {
			likeTypeFromBroadcast = LikeTypeResponseEnum.SPORT_TYPE;
			break;
		}
		case MOVIE:
		case OTHER: {
			likeTypeFromBroadcast = LikeTypeResponseEnum.PROGRAM;
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
	
	
	
	public static LikeTypeResponseEnum getLikeTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		LikeTypeResponseEnum likeType = LikeTypeResponseEnum.valueOf(enumStringRepresentation);

		return likeType;
	}
}