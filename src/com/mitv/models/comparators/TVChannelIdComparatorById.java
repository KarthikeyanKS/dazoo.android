package com.mitv.models.comparators;

import com.mitv.models.objects.mitvapi.TVChannelId;

public class TVChannelIdComparatorById extends BaseComparator<TVChannelId> 
{
	@Override
	public int compare(TVChannelId lhs, TVChannelId rhs) 
	{
		return lhs.getChannelId().compareTo(rhs.getChannelId());
	}
}
