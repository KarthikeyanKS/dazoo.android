
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.models.gson.mitvapi.competitions.EventLineUpJSON;



public class EventLineUp 
	extends EventLineUpJSON
{
	public EventLineUp(){}
	
	
	
	public boolean isGoalKeeper()
	{
		boolean isGoalKeeper = (getFunctionType() == Constants.LINE_UP_GOAL_KEEPER_FUNCTION_TYPE);
		
		return isGoalKeeper;
	}
	
	
	
	public boolean isReferee()
	{
		boolean isGoalKeeper = (getFunctionType() == Constants.LINE_UP_REFEREE_FUNCTION_TYPE);
		
		return isGoalKeeper;
	}
	
	
	
	public boolean isCoach()
	{
		boolean isGoalKeeper = (getFunctionType() == Constants.LINE_UP_COACH_FUNCTION_TYPE);
		
		return isGoalKeeper;
	}
}
