
package com.mitv.models.comparators;



import java.util.Comparator;
import com.mitv.models.TVBroadcast;



public class TVBroadcastComparatorByTime 
	implements Comparator<TVBroadcast> 
{
	@Override
	public int compare(TVBroadcast lhs, TVBroadcast rhs) 
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
			String leftProgramName = lhs.getProgram().getTitle();
			String rightProgramName = rhs.getProgram().getTitle();
			return leftProgramName.compareTo(rightProgramName);
		}
	}
}