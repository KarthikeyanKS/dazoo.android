
package com.mitv.models.comparators;



import com.mitv.models.Notification;



public class NotificationComparatorByBeginTime
	extends BaseComparator<Notification> 
{
	@Override
	public int compare(Notification lhs, Notification rhs) 
	{
		long left = lhs.getBeginTimeMillis();
		long right = rhs.getBeginTimeMillis();
	
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
			
			// TODO
//			String leftProgramName = lhs.getProgram().getTitle();
//			String rightProgramName = rhs.getProgram().getTitle();
//			
//			return leftProgramName.compareTo(rightProgramName);
		}
	}
}
