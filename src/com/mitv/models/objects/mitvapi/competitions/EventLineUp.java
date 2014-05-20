
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.models.gson.mitvapi.competitions.EventLineUpJSON;



public class EventLineUp 
	extends EventLineUpJSON
{
	public EventLineUp(){}
	
	
	
	public boolean isGoalKeeper()
	{
		return getFunctionShort().equalsIgnoreCase(Constants.GOAL_KEEPER_FUNCTION_SORT);
	}
}
