
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventLineUpPosition;
import com.mitv.models.gson.mitvapi.competitions.TeamSquadJSON;



public class TeamSquad 
	extends TeamSquadJSON 
{	
	public TeamSquad(){}
	
	
	
	public EventLineUpPosition getPosition()
	{
		return EventLineUpPosition.getTypeEnumFromCode(getFunctionType());
	}
}
