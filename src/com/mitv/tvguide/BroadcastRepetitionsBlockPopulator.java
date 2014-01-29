package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.mitv.model.Broadcast;

public class BroadcastRepetitionsBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static String TAG = "BroadcastRepetitionsBlockPopulator";
	
	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, String tvDate, Broadcast runningBroadcast)  {
		super(TAG, true, activity, containerView, tvDate, runningBroadcast);
	}

}
