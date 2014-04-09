
package com.mitv.populators;



import android.app.Activity;
import android.widget.RelativeLayout;

import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class BroadcastUpcomingBlockPopulator 
	extends TrippleBroadcastBlockPopulator 
{
	private static final String TAG = BroadcastUpcomingBlockPopulator.class.getName();

	
	public BroadcastUpcomingBlockPopulator(Activity activity, RelativeLayout containerView, boolean isSeries, TVBroadcastWithChannelInfo runningBroadcast)
	{
		super(TAG, false, activity, containerView, runningBroadcast);
	}
}