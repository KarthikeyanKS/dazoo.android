package com.millicom.secondscreen.content.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.millicom.secondscreen.content.model.Broadcast;

public class BroadcastRepetitionsBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static String TAG = "BroadcastRepetitionsBlockPopulator";
	
	public BroadcastRepetitionsBlockPopulator(Activity activity, ScrollView containerView, String tvDate, Broadcast runningBroadcast)  {
		super(TAG, true, activity, containerView, tvDate, runningBroadcast);
	}

}
