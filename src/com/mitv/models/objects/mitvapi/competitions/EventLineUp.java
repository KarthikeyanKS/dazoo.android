
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventLineUpPositionEnum;
import com.mitv.models.gson.mitvapi.competitions.EventLineUpJSON;



public class EventLineUp 
	extends EventLineUpJSON
{
	public EventLineUp(){}
	
	
	
	public EventLineUpPositionEnum getPosition()
	{
		return EventLineUpPositionEnum.getTypeEnumFromCode(getFunctionType());
	}
	
	
	
	public boolean containsLineUpOutMinute()
	{
		boolean containsLineUpOutMinute = (getLineUpOutMinute().isEmpty() == false);
		
		return containsLineUpOutMinute;
	}
	
	
	
	public boolean containsLineUpInMinute()
	{
		boolean containsLineUpInMinute = (getLineUpInMinute().isEmpty() == false);
		
		return containsLineUpInMinute;
	}
}
