
package com.mitv.adapters;



import java.util.ArrayList;

import android.app.Activity;

import com.millicom.mitv.models.TVBroadcastWithChannelInfo;



public class RepetitionsListAdapter 
extends UpcomingOrRepeatingBroadcastsListAdapter
{
	public RepetitionsListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) {
		super(activity, repeatingBroadcasts, true);
	}
}