
package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.Event;



public class CompetitionEventsComparatorByTime 
	extends BaseComparator<Event> 
{
	@Override
	public int compare(Event lhsEvent, Event rhsEvent) 
	{
		long left = lhsEvent.getBeginTimeLocalInMillis();
		long right = rhsEvent.getBeginTimeLocalInMillis();
	
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