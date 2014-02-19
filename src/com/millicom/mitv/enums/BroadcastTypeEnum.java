
package com.millicom.mitv.enums;

import com.google.gson.annotations.SerializedName;

public enum BroadcastTypeEnum {
	UNKNOWN(0),
	
	@SerializedName("LIVE")
	LIVE(1),
	
	@SerializedName("RECORDED")
	RECORDED(2),
	
	@SerializedName("RERUN")
	RERUN(3),
	
	@SerializedName("DELAYED")
	DELAYED(4),
	
	@SerializedName("OTHER")
	OTHER(5);
	
	
	private final int id;
	
	
	
	BroadcastTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static BroadcastTypeEnum getContentTypeEnumFromCode(int code)
	{
		for(BroadcastTypeEnum result: BroadcastTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return BroadcastTypeEnum.UNKNOWN;
	}



	public static BroadcastTypeEnum getContentTypeEnumFromCode(String codeAsString)
	{
		int value = BroadcastTypeEnum.UNKNOWN.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return BroadcastTypeEnum.UNKNOWN;
		}

		return getContentTypeEnumFromCode(value);
	}
	
	
	
	public static BroadcastTypeEnum getBroadcastTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		BroadcastTypeEnum broadcastType = BroadcastTypeEnum.valueOf(enumStringRepresentation);

		return broadcastType;
	}
}