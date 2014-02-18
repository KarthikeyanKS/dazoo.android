
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;



public enum ContentTypeEnum
{
	@SerializedName("SERIES")
	SERIES(0),
	
	@SerializedName("PROGRAM")
	PROGRAM(1),
	
	@SerializedName("CHANNEL")
	CHANNEL(2);
	
	
	
	private final int id;
	
	
	
	ContentTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static ContentTypeEnum getContentTypeEnumFromCode(int code)
	{
		for(ContentTypeEnum result: ContentTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return ContentTypeEnum.SERIES;
	}



	public static ContentTypeEnum getContentTypeEnumFromCode(String codeAsString)
	{
		int value = ContentTypeEnum.SERIES.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return ContentTypeEnum.SERIES;
		}

		return getContentTypeEnumFromCode(value);
	}
	
	
	
	public static ContentTypeEnum getContentTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		ContentTypeEnum contentType = ContentTypeEnum.valueOf(enumStringRepresentation);

		return contentType;
	}
}