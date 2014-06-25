
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventLineUpPositionEnum;
import com.mitv.models.gson.mitvapi.competitions.TeamSquadJSON;



public class TeamSquad 
	extends TeamSquadJSON 
{	
	public TeamSquad(){}
	
	
	
	public EventLineUpPositionEnum getPosition()
	{
		return EventLineUpPositionEnum.getTypeEnumFromCode(getFunctionType());
	}
}
