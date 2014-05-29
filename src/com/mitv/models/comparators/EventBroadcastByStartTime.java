
package com.mitv.models.comparators;



import java.util.Calendar;

import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;



public class EventBroadcastByStartTime 
	extends BaseComparator<EventBroadcast> 
{
	@Override
	public int compare(EventBroadcast lhs, EventBroadcast rhs) 
	{
		Calendar left = lhs.getEventBroadcastBeginTimeLocal();
		Calendar right = rhs.getEventBroadcastBeginTimeLocal();
	
		return left.compareTo(right);
	}
}
