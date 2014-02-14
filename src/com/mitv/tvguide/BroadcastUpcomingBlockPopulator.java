package com.mitv.tvguide;

import android.app.Activity;
import android.widget.ScrollView;

import com.mitv.model.OldBroadcast;

public class BroadcastUpcomingBlockPopulator extends TrippleBroadcastBlockPopulator {

	private static final String		TAG				= "BroadcastUpcomingBlockPopulator";

	public BroadcastUpcomingBlockPopulator(Activity activity, ScrollView containerView, boolean isSeries, OldBroadcast runningBroadcast) {
		super(TAG, false, activity, containerView, runningBroadcast);
	}
}