
package com.mitv.enums;



public enum UIStatusEnum 
{
	LOADING(1),
	SUCCEEDED_WITH_DATA(2),
	SUCCESS_WITH_NO_CONTENT(2),
	NO_CONNECTION_AVAILABLE(3),
	FAILED_VALIDATION(4),
	FAILED(5);
	
	
	
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