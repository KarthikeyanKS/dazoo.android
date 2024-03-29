
package com.mitv.models.comparators;



import com.mitv.models.objects.mitvapi.TVChannel;



public class TVChannelComparatorByName 
	extends BaseComparator<TVChannel> 
{
	@Override
	public int compare(TVChannel lhs, TVChannel rhs) 
	{
		return lhs.getName().compareTo(rhs.getName());
	}
}
