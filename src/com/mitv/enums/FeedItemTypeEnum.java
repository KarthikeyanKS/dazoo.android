
package com.mitv.enums;



import com.google.gson.annotations.SerializedName;



public enum FeedItemTypeEnum 
{
	UNKNOWN(0),
	
	@SerializedName("BROADCAST")
	BROADCAST(1),
	
	@SerializedName("RECOMMENDED_BROADCAST")
	RECOMMENDED_BROADCAST(2),
	
	@SerializedName("POPULAR_BROADCASTS")
	POPULAR_BROADCASTS(3),
	
	@SerializedName("POPULAR_BROADCAST")
	POPULAR_BROADCAST(4),
	
	@SerializedName("POPULAR_TWITTER")
	POPULAR_TWITTER(5);
	
	
	
	private final int id;
	
	
	
	FeedItemTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static FeedItemTypeEnum getFeedItemTypeEnumFromCode(int code)
	{
		for(FeedItemTypeEnum result: FeedItemTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return FeedItemTypeEnum.UNKNOWN;
	}



	public static FeedItemTypeEnum getFeedItemTypeEnumFromCode(String codeAsString)
	{
		int value = FeedItemTypeEnum.UNKNOWN.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return FeedItemTypeEnum.UNKNOWN;
		}

		return getFeedItemTypeEnumFromCode(value);
	}
	
	
	
	public static FeedItemTypeEnum getFeedItemTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		FeedItemTypeEnum feedItemType = FeedItemTypeEnum.valueOf(enumStringRepresentation);

		return feedItemType;
	}
}