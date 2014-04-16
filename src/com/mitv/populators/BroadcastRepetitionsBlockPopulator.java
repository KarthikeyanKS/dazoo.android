
package com.mitv.populators;



import android.app.Activity;
import android.widget.RelativeLayout;

import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class BroadcastRepetitionsBlockPopulator 
	extends TrippleBroadcastBlockPopulator 
{
	private static String TAG = BroadcastRepetitionsBlockPopulator.class.getName();

	
	public BroadcastRepetitionsBlockPopulator(Activity activity, RelativeLayout containerView, TVBroadcastWithChannelInfo runningBroadcast) 
	{
		super(TAG, true, activity, containerView, runningBroadcast);
	}
}
