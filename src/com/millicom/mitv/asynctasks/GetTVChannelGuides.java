
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVGuideAndTaggedBroadcasts;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.TVTag;
import com.mitv.Consts;



public class GetTVChannelGuides 
	extends AsyncTaskWithRelativeURL<TVChannelGuide[]>
{	
	private TVDate tvDate;
	
	
	
	private static String buildURL(TVDate tvDate)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_GUIDE);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(tvDate.getId());
		
		return url.toString();
	}

	
	
	public GetTVChannelGuides(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			TVDate tvDate,
			List<TVChannelId> tvChannelIds) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_GUIDE, TVChannelGuide[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvDate));
		
		this.tvDate = tvDate;
		
		for(TVChannelId tvChannelId : tvChannelIds) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			
			this.urlParameters.add(Consts.API_CHANNEL_ID, tvChannelIdAsString);
		}
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		/* Important to call super first, which creates the content, which in this case is */
		super.doInBackground(params);
		
		TVChannelGuide[] contentAsArray = (TVChannelGuide[]) requestResultObjectContent;
		
		ArrayList<TVChannelGuide> tvChannelGuides = new ArrayList<TVChannelGuide>(Arrays.asList(contentAsArray));
		
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = createMapTagToTaggedBroadcastForDate(tvChannelGuides);
		
		TVGuide tvGuide = new TVGuide(tvDate, tvChannelGuides);
		
		TVGuideAndTaggedBroadcasts tvGuideAndTaggedBroadcasts = new TVGuideAndTaggedBroadcasts(tvGuide, mapTagToTaggedBroadcastForDate);
		
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		requestResultObjectContent = tvGuideAndTaggedBroadcasts;
		
		return null;
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
				for (String tagName : filteredTagNames) {
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

					/* Put back the list of tagged broadcasts for this tag */
					mapTagToTaggedBroadcastForDate.put(tagName, broadcastsForTag);
				}
			}
		}

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