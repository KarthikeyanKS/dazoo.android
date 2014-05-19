
package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.EventLineUp;



public class EventLineUpComparatorByShirtNumberWithGoalKeeperAtTop 
	extends BaseComparator<EventLineUp> 
{
	@Override
	public int compare(EventLineUp lhs, EventLineUp rhs) 
	{
		String left = lhs.getShirtNr();
		String right = rhs.getShirtNr();
	
		if(lhs.isGoalKeeper())
		{
			return 1;
		}
		
		return left.compareTo(right);
	}
}