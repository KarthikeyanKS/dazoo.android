
package com.mitv.models.comparators;



import java.util.Comparator;
import com.mitv.models.TVChannel;



public class TVChannelComparatorByName 
	implements Comparator<TVChannel> 
{
	@Override
	public int compare(TVChannel lhs, TVChannel rhs) 
	{
		return lhs.getName().compareTo(rhs.getName());
	}
}
