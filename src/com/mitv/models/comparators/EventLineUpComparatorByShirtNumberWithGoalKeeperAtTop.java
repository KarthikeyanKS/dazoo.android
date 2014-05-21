
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
		else
		{
			Integer leftInteger;
			Integer rightInteger;
			
			try
			{
				leftInteger = Integer.parseInt(left);
			}
			catch(NumberFormatException nfex)
			{
				leftInteger = Integer.valueOf(0);
			}
			
			try
			{
				rightInteger = Integer.parseInt(right);
			}
			catch(NumberFormatException nfex)
			{
				rightInteger = Integer.valueOf(0);
			}
			
			return leftInteger.compareTo(rightInteger);
		}
	}
}