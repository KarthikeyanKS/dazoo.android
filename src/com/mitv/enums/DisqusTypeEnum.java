
package com.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum DisqusTypeEnum
{
	BROADCAST(0),
	
	EVENT(1),
	
	TEAM(2),
	
	OTHER(3);
	
	
	
	private final int id;
	
	
	
	DisqusTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static DisqusTypeEnum getContentTypeEnumFromCode(int code)
	{
		for(DisqusTypeEnum result: DisqusTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return DisqusTypeEnum.OTHER;
	}



	public static DisqusTypeEnum getContentTypeEnumFromCode(String codeAsString)
	{
		int value = DisqusTypeEnum.OTHER.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return DisqusTypeEnum.OTHER;
		}

		return getContentTypeEnumFromCode(value);
	}
	
	
	
	public static DisqusTypeEnum getContentTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		DisqusTypeEnum contentType = DisqusTypeEnum.valueOf(enumStringRepresentation);

		return contentType;
	}
}