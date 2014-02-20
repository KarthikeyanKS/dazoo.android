
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.Broadcast;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVGuideAndTaggedBroadcasts;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVProgram;
import com.millicom.mitv.models.gson.TVTag;
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
	protected Void doInBackground(String... params) {
		/* Important to call super first, which creates the content, which in this case is */
		super.doInBackground(params);
		
		ArrayList<TVChannelGuide> tvChannelGuides = (ArrayList<TVChannelGuide>) requestResultObjectContent;
		HashMap<String, ArrayList<Broadcast>> mapTagToTaggedBroadcastForDate = createMapTagToTaggedBroadcastForDate(tvChannelGuides);
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
	public HashMap<String, ArrayList<Broadcast>> createMapTagToTaggedBroadcastForDate(ArrayList<TVChannelGuide> tvChannelGuides) {
		/* TVTag id is used as key */
		HashMap<String, ArrayList<Broadcast>> mapTagToTaggedBroadcastForDate = new HashMap<String, ArrayList<Broadcast>>();

		for (TVChannelGuide tvChannelGuide : tvChannelGuides) {
			ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>(tvChannelGuide.getBroadcasts());

			for (Broadcast broadcast : broadcasts) {
				TVProgram program = broadcast.getProgram();

				/* Fetch list of all tags for this broadcast (program), WARNING: may contain irrelevant tags */
				ArrayList<String> tagNames = program.getTags();

				/* Filter out only the relevant tags that are being used in the app */
				ArrayList<String> filteredTagNames = filterOutOnlyRelevantTagNames(tagNames);

				/*
				 * For each relevant tag for this broadcast add the broadcast to the list of tagged broadcasts for said tag. E.g. the tag
				 * list could be ["movie", "children"] if it is a movie for children, then this child movie will be both in the tagged
				 * broadcasts for children shows and in the list of movies
				 */
				for (String tagName : filteredTagNames) {
					/* Fetch current list of broadcasts for this tag */
					ArrayList<Broadcast> broadcastsForTag = mapTagToTaggedBroadcastForDate.get(tagName);

					/* If it is null (this is the first broadcast for this tag), instantiate it! */
					if (broadcastsForTag == null) {
						broadcastsForTag = new ArrayList<Broadcast>();
					}

					/* Add the broadcast for this tag to the list of tagged broadcasts for this tag */
					broadcastsForTag.add(broadcast);

					/* Put back the list of tagged broadcasts for this tag */
					mapTagToTaggedBroadcastForDate.put(tagName, broadcastsForTag);
				}
			}
		}
		return mapTagToTaggedBroadcastForDate;
	}
	
	/**
	 * Some TVPrograms in some Broadcasts contains TVTags which are not being used in the app (meaning: matches TVTags fetched from backend).
	 * Those TVTags not matching the TVTags fetched from backend can be considered irrelevant and unnecessary.
	 * We are creating a list of ("tagged") broadcasts for each day for each (relevant) TVTag (fetched from backend), hence we should not 
	 * create lists of broadcasts for irrelevant tags, since those lists won't be used.
	 * @param tagNames the list of TVTags for a specific program (for a broadcast), which may contain irrelevant tags.
	 * @return A list of only the relevant broadcasts.
	 */
	private ArrayList<String> filterOutOnlyRelevantTagNames(ArrayList<String> tagNames) {
		return null;
	}
	
}