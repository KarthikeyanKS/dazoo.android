
package com.millicom.mitv.enums;



public enum UIStatusEnum 
{
	LOADING(1),
	SUCCEEDED_WITH_DATA(2),
	SUCCEEDED_WITH_EMPTY_DATA(2),
	NO_CONNECTION_AVAILABLE(3),
	FAILED(3);
	
	
	
	private final int id;
	
	
	
	UIStatusEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}