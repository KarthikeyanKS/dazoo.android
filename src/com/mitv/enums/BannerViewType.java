
package com.mitv.enums;



public enum BannerViewType 
{
	BANNER_VIEW_TYPE_STANDARD(0),
	
	BANNER_VIEW_TYPE_CUSTOM(1),
	
	BANNER_VIEW_TYPE_AD(2),
	
	BANNER_VIEW_TYPE_COMPETITION(3);

	
	
	private final int id;
	
	
	
	BannerViewType(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}
