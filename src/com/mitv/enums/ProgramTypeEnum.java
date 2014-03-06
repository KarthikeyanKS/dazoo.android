
package com.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum ProgramTypeEnum 
{
	UNKNOWN(0),
	
	@SerializedName("MOVIE")
	MOVIE(1),
	
	@SerializedName("TV_EPISODE")
	TV_EPISODE(2),
	
	@SerializedName("SPORT")
	SPORT(3),
	
	@SerializedName("OTHER")
	OTHER(4);
	
	
	private final int id;
	
	
	
	ProgramTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static ProgramTypeEnum getProgramTypeEnumFromCode(int code)
	{
		for(ProgramTypeEnum result: ProgramTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return ProgramTypeEnum.UNKNOWN;
	}



	public static ProgramTypeEnum getProgramTypeEnumFromCode(String codeAsString)
	{
		int value = ProgramTypeEnum.UNKNOWN.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return ProgramTypeEnum.UNKNOWN;
		}

		return getProgramTypeEnumFromCode(value);
	}
	
	
	
	public static ProgramTypeEnum getLikeTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		ProgramTypeEnum programType = ProgramTypeEnum.valueOf(enumStringRepresentation);

		return programType;
	}
}