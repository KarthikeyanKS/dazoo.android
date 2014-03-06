package com.mitv.populators;

import android.app.Activity;
import android.widget.ScrollView;

import com.mitv.models.TVBroadcastWithChannelInfo;

public class BroadcastUpcomingBlockPopulator 
	extends TrippleBroadcastBlockPopulator 
{
	private static final String TAG = BroadcastUpcomingBlockPopulator.class.getName();

	
	public BroadcastUpcomingBlockPopulator(Activity activity, ScrollView containerView, boolean isSeries, TVBroadcastWithChannelInfo runningBroadcast)
	{
		super(TAG, false, activity, containerView, runningBroadcast);
	}
}