
package com.mitv.models.comparators;



import java.util.Calendar;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcastDetails;



public class EventBroadcastDetailsByStartTime 
	extends BaseComparator<EventBroadcastDetails> 
{
	@Override
	public int compare(EventBroadcastDetails lhs, EventBroadcastDetails rhs) 
	{
		Calendar left = lhs.getEventBroadcastBeginTimeLocal();
		Calendar right = rhs.getEventBroadcastBeginTimeLocal();
	
		return left.compareTo(right);
	}
}
