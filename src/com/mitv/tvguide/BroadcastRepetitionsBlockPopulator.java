package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.millicom.mitv.models.TVBroadcastWithChannelInfo;

public class BroadcastRepetitionsBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static String TAG = BroadcastRepetitionsBlockPopulator.class.getName();
	
	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, TVBroadcastWithChannelInfo runningBroadcast)  {
		super(TAG, true, activity, containerView, runningBroadcast);
	}

}
