
package com.mitv.asynctasks.other;



import java.util.ArrayList;

import android.os.AsyncTask;

import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class RemoveAlreadyEndedBroadcastsTask
	extends AsyncTask<Void, Void, Void>
{
	private int startIndex;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;


	public RemoveAlreadyEndedBroadcastsTask(
			final ArrayList<TVBroadcastWithChannelInfo> broadcasts, 
			final int startIndex)
	{
		this.startIndex = startIndex;
		this.broadcasts = broadcasts;
	}

	@Override
	protected Void doInBackground(Void... params) {
		ArrayList<TVBroadcast> tvBroadcastsToRemove = new ArrayList<TVBroadcast>();

		if(broadcasts != null && startIndex >= 0)
		{
			for (int i = startIndex; i < broadcasts.size(); i++)
			{
				boolean hasEnded = broadcasts.get(i).hasEnded();

				if(hasEnded) 
				{
					tvBroadcastsToRemove.add(broadcasts.get(i));
				}
			}

			for (TVBroadcast broadcast : tvBroadcastsToRemove) 
			{
				broadcasts.remove(broadcast);
			}
		}

		return null;		
	}
}
