
package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.Notification;



public class NotificationComparatorByBeginTime 
	extends BaseComparator<Notification> 
{
	@Override
	public int compare(Notification lhs, Notification rhs) 
	{
		long left = lhs.getBeginTimeInMilliseconds();
		long right = rhs.getBeginTimeInMilliseconds();
	
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