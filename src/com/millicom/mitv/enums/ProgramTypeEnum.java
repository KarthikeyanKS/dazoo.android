
package com.millicom.mitv.enums;

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
	


	public static ProgramTypeEnum getContentTypeEnumFromCode(int code)
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



	public static ProgramTypeEnum getContentTypeEnumFromCode(String codeAsString)
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

		return getContentTypeEnumFromCode(value);
	}
}
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum ProgramTypeEnum 
{
	@SerializedName("MOVIE")
	MOVIE(0),
	
	@SerializedName("OTHER")
	OTHER(1);

	
	
	private final int id;
	
	
	
	ProgramTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static ProgramTypeEnum getLikeTypeEnumFromCode(int code)
	{
		for(ProgramTypeEnum result: ProgramTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return ProgramTypeEnum.OTHER;
	}



	public static ProgramTypeEnum getLikeTypeEnumFromCode(String codeAsString)
	{
		int value = ProgramTypeEnum.OTHER.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return ProgramTypeEnum.OTHER;
		}

		return getLikeTypeEnumFromCode(value);
	}
}