package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.mitv.model.OldBroadcast;

public class BroadcastRepetitionsBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static String TAG = "BroadcastRepetitionsBlockPopulator";
	
	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, OldBroadcast runningBroadcast)  {
		super(TAG, true, activity, containerView, runningBroadcast);
	}

}
