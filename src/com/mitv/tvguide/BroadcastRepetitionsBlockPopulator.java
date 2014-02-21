package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.millicom.mitv.models.TVBroadcast;

public class BroadcastRepetitionsBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static String TAG = "BroadcastRepetitionsBlockPopulator";
	
	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, TVBroadcast runningBroadcast)  {
		super(TAG, true, activity, containerView, runningBroadcast);
	}

}
