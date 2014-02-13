
package com.millicom.mitv.enums;



public enum ContentTypeEnum {
	/* DO NOT CHANGE THIS NAMES, DEPENDENT ON BACKEND, i think.., check class SearchResultItem, where this ENUM is created from String from JSON */
	SERIES(0),
	PROGRAM(1),
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
}
