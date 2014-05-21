

package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.Standings;



public class EventStandingsComparatorByPoints
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
			return 0;
		}
	}
}