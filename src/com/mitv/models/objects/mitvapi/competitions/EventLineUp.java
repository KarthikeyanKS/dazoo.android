
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventLineUpPosition;
import com.mitv.models.gson.mitvapi.competitions.EventLineUpJSON;



public class EventLineUp 
	extends EventLineUpJSON
{
	public EventLineUp(){}
	
	
	
	public EventLineUpPosition getPosition()
	{
		return EventLineUpPosition.getTypeEnumFromCode(getFunctionType());
	}
}
