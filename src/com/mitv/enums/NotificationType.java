
package com.mitv.enums;



public enum NotificationType 
{
	TV_BROADCAST(0),
	
	COMPETITION_EVENT(1);
	
	

	@SuppressWarnings("unused")
	private static final String	TAG	= NotificationType.class.getName();
	
	
	private final int id;
	
	
	
	NotificationType(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
}
