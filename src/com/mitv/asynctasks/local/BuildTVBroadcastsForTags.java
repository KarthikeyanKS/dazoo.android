
package com.mitv.asynctasks.local;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.os.AsyncTask;
import android.util.Log;

import com.mitv.ContentManager;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVProgram;
import com.mitv.models.TVTag;
import com.mitv.models.comparators.TVBroadcastComparatorByTime;



public class BuildTVBroadcastsForTags 
	extends AsyncTask<String, Void, Void> 
{
	private static final String TAG = BuildTVBroadcastsForTags.class.getName();
	
	
	private ArrayList<TVChannelGuide> tvChannelGuides;
	private ContentCallbackListener contentCallbackListener;
	private ActivityCallbackListener activityCallbackListener;
	private Object requestResultObjectContent;
	
	
	
	
	public BuildTVBroadcastsForTags(
			ArrayList<TVChannelGuide> tvChannelGuides,
			ContentCallbackListener contentCallbackListener, 
			ActivityCallbackListener activityCallbackListener)
	{
		this.tvChannelGuides = tvChannelGuides;
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
	}
	
	
	
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		Log.d(TAG, String.format("onPreExecute - Performing TVBroadcasts for TVTags processing: %s", BuildTVBroadcastsForTags.class.getName()));
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = createMapTagToTaggedBroadcastForDate(tvChannelGuides);
				
		requestResultObjectContent = mapTagToTaggedBroadcastForDate;
		
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		Log.d(TAG, String.format("%s onPostExecute - TVBroadcasts for TVTags processing completed, notifying ContentManager", BuildTVBroadcastsForTags.class.getName()));
		
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallbackListener, RequestIdentifierEnum.TV_BROADCASTS_FOR_TAGS, FetchRequestResultEnum.SUCCESS, requestResultObjectContent);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}
	
	
	
	
	/**
	 * This method creates a map containing tagged broadcasts for a specific date.
	 * @param tvChannelGuides
	 * @return
	 */
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> createMapTagToTaggedBroadcastForDate(ArrayList<TVChannelGuide> tvChannelGuides) 
	{
		ArrayList<String> tvTagsAsStrings = tvTagIds();

		/* TVTag id is used as key. STRANGEST JAVA BUG EVER: For some reason we MUST set the size of the map to 3 times as big as the expected
		 * size of that map. We MUST set a size, else the values will be overwritten even though keys are not the same! */
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = new HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>(tvTagsAsStrings.size() * 3);
		double startTime = System.currentTimeMillis();
		for (TVChannelGuide tvChannelGuide : tvChannelGuides) 
		{
			TVChannelId tvChannelId = tvChannelGuide.getChannelId();
			
			TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
			
			ArrayList<TVBroadcast> broadcasts = new ArrayList<TVBroadcast>(tvChannelGuide.getBroadcasts());

			for (TVBroadcast broadcast : broadcasts) 
			{	
				TVProgram program = broadcast.getProgram();

				/* Fetch list of all tags for this broadcast (program), WARNING: may contain irrelevant tags */
				ArrayList<String> tagNames = program.getTags();

				/* Filter out only the relevant tags that are being used in the app */
				ArrayList<String> filteredTagNames = filterOutOnlyRelevantTagNames(tvTagsAsStrings, tagNames);

				/*
				 * For each relevant tag for this broadcast add the broadcast to the list of tagged broadcasts for said tag. E.g. the tag
				 * list could be ["movie", "children"] if it is a movie for children, then this child movie will be both in the tagged
				 * broadcasts for children shows and in the list of movies
				 */
				for (String tagName : filteredTagNames) 
				{
					/* Fetch current list of broadcasts for this tag */
					ArrayList<TVBroadcastWithChannelInfo> broadcastsForTag = mapTagToTaggedBroadcastForDate.get(tagName);

					/* If it is null (this is the first broadcast for this tag), instantiate it! */
					if (broadcastsForTag == null) {
						broadcastsForTag = new ArrayList<TVBroadcastWithChannelInfo>();
					}
					
					/* Create TVBroadcastWithChannelInfo object using broadcast and TVChannel initialized above */
					TVBroadcastWithChannelInfo broadCastWithChannelInfo = new TVBroadcastWithChannelInfo(broadcast);
					broadCastWithChannelInfo.setChannel(tvChannel);

					/* Add the broadcast for this tag to the list of tagged broadcasts for this tag */
					broadcastsForTag.add(broadCastWithChannelInfo);
					
//					Collections.sort(broadcastsForTag, new TVBroadcastComparatorByTime());

					/* Put back the list of tagged broadcasts for this tag */
					mapTagToTaggedBroadcastForDate.put(tagName, broadcastsForTag);
				}
			}
			

		}
		for (String tag : tvTagsAsStrings) {
			ArrayList<TVBroadcastWithChannelInfo> broadcastsForTag = mapTagToTaggedBroadcastForDate.get(tag);

			if (broadcastsForTag != null) {
				Collections.sort(broadcastsForTag, new TVBroadcastComparatorByTime());
			}
		}

        double endTime = System.currentTimeMillis();
        Log.d(TAG, "Time:" + (endTime-startTime));
		return mapTagToTaggedBroadcastForDate;
	}
	
	
	private ArrayList<String> tvTagIds() 
	{
		ArrayList<TVTag> tvTags = ContentManager.sharedInstance().getFromCacheTVTags();
		
		ArrayList<String> tvTagsAsString = new ArrayList<String>();
		
		for(TVTag tvTag : tvTags)
		{
			tvTagsAsString.add(tvTag.getId());
		}
		
		return tvTagsAsString;
	}
	
	
	
	/**
	 * Some TVPrograms in some Broadcasts contains TVTags which are not being used in the app (meaning: matches TVTags fetched from backend).
	 * Those TVTags not matching the TVTags fetched from backend can be considered irrelevant and unnecessary.
	 * We are creating a list of ("tagged") broadcasts for each day for each (relevant) TVTag (fetched from backend), hence we should not 
	 * create lists of broadcasts for irrelevant tags, since those lists won't be used.
	 * @param tagNames the list of TVTags for a specific program (for a broadcast), which may contain irrelevant tags.
	 * @return A list of only the relevant broadcasts.
	 */
	private ArrayList<String> filterOutOnlyRelevantTagNames(ArrayList<String> allRelevantTVTags, ArrayList<String> tagNames)
	{		
		ArrayList<String> onlyRelevantTVTags = new ArrayList<String>();
				
		for(String tagName : tagNames)
		{
			if(allRelevantTVTags.contains(tagName))
			{
				onlyRelevantTVTags.add(tagName);
			}
		}
		
		return onlyRelevantTVTags;
	}
}