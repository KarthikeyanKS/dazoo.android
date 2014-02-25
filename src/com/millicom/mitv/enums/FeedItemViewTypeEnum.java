
package com.millicom.mitv.enums;



import com.google.gson.annotations.SerializedName;



public enum FeedItemViewTypeEnum 
{
	@SerializedName("ADS")
	ADS(0),
	
	@SerializedName("BROADCAST")
	BROADCAST(1),
	
	@SerializedName("RECOMMENDED_BROADCAST")
	RECOMMENDED_BROADCAST(2),
	
	@SerializedName("POPULAR_BROADCASTS")
	POPULAR_BROADCASTS(3),
	
	@SerializedName("POPULAR_BROADCAST")
	POPULAR_BROADCAST(4),
	
	@SerializedName("POPULAR_TWITTER")
	POPULAR_TWITTER(5),
	
	@SerializedName("UNKNOWN")
	UNKNOWN(6);
	
	
	
	private final int id;
	
	
	
	FeedItemViewTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	


	public static FeedItemViewTypeEnum getFeedItemTypeEnumFromCode(int code)
	{
		for(FeedItemViewTypeEnum result: FeedItemViewTypeEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return FeedItemViewTypeEnum.UNKNOWN;
	}



	public static FeedItemViewTypeEnum getFeedItemTypeEnumFromCode(String codeAsString)
	{
		int value = FeedItemViewTypeEnum.UNKNOWN.getId();

		try
		{
			value = Integer.parseInt(codeAsString);
		}
		catch(NumberFormatException nfex)
		{
			return FeedItemViewTypeEnum.UNKNOWN;
		}

		return getFeedItemTypeEnumFromCode(value);
	}
	
	
	
	public static FeedItemViewTypeEnum getFeedItemTypeEnumFromStringRepresentation(String enumStringRepresentation)
	{
		FeedItemViewTypeEnum feedItemType = FeedItemViewTypeEnum.valueOf(enumStringRepresentation);

		return feedItemType;
	}
}