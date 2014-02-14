
package com.millicom.mitv.enums;



public enum HTTPRequestTypeEnum 
{
	HTTP_POST(0),
	HTTP_PUT(1),
	HTTP_GET(2),
	HTTP_DELETE(3);
	
	
	
	private final int id;
	
	HTTPRequestTypeEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}
