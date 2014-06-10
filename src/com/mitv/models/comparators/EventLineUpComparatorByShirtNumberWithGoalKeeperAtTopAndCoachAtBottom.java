
package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.EventLineUp;



public class EventLineUpComparatorByShirtNumberWithGoalKeeperAtTopAndCoachAtBottom 
	extends BaseComparator<EventLineUp> 
{
	@Override
	public int compare(EventLineUp lhs, EventLineUp rhs) 
	{
		int left = lhs.getShirtNumber();
		int right = rhs.getShirtNumber();
	
		if(lhs.isGoalKeeper())
		{
			return -1;
		}
		else if(rhs.isGoalKeeper())
		{
			return 1;
		}
		else if(lhs.isCoach())
		{
			return 1;
		}
		else if(rhs.isCoach())
		{
			return -1;
		}
		else
		{
			if (left > right) 
			{
				return 1;
			} 
			else if (left < right) 
			{
				return -1;
			} 
			else 
			{
				return 0;
			}
		}
	}
}