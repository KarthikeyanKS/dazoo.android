package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.millicom.mitv.models.TVBroadcast;

public class BroadcastUpcomingBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static final String		TAG				= "BroadcastUpcomingBlockPopulator";

	public BroadcastUpcomingBlockPopulator(Activity activity, ScrollView containerView, boolean isSeries, TVBroadcast runningBroadcast) {
		super(TAG, false, activity, containerView, runningBroadcast);
	}
}