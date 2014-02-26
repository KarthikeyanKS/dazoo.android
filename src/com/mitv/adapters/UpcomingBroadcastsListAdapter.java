package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;

import com.millicom.mitv.models.TVBroadcastWithChannelInfo;

public class UpcomingBroadcastsListAdapter extends UpcomingOrRepeatingBroadcastsListAdapter {
	
	public UpcomingBroadcastsListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) {
		super(activity, upcomingBroadcasts, true);
	}
}
