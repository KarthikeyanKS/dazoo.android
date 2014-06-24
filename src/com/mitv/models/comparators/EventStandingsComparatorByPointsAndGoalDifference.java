

package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.Standings;



public class EventStandingsComparatorByPointsAndGoalDifference
	extends BaseComparator<Standings> 
{
	@Override
	public int compare(Standings lhs, Standings rhs) 
	{
		long left = lhs.getPoints();
		long right = rhs.getPoints();
	
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
			left = lhs.getGoalsForMinusGoalsAgainst();
			right = rhs.getGoalsForMinusGoalsAgainst();
			
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