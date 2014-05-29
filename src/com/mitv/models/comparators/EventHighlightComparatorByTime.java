

package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.competitions.EventHighlight;



public class EventHighlightComparatorByTime
	extends BaseComparator<EventHighlight> 
{
	@Override
	public int compare(EventHighlight lhs, EventHighlight rhs) 
	{
		long left = lhs.getActionTime();
		long right = rhs.getActionTime();
	
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