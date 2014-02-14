
package com.mitv.storage;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.util.Log;
import com.mitv.SecondScreenApplication;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldTVChannel;
import com.mitv.model.OldTVChannelGuide;
import com.mitv.model.OldTVTag;
import com.mitv.model.OldTVDate;



public class MiTVStoreOperations 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "MiTVStoreOperations";


	
	public static void saveChannels(ArrayList<OldTVChannel> channels) 
	{
		HashMap<String, OldTVChannel> channelsMap = new HashMap<String, OldTVChannel>();
		ArrayList<String> channelsIds = new ArrayList<String>();

		int size = channels.size();
		for (int i = 0; i < size; i++) {
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelsIds.add(channels.get(i).getChannelId());
		}

		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setChannels(channelsMap);
		
		if((SecondScreenApplication.isLoggedIn() && !MiTVStore.getInstance().isMyIdsSet()) || !SecondScreenApplication.isLoggedIn()) 
		{
			mitvStore.setChannelIds(channelsIds);
		}
	}


	
	public static void setTvDates(ArrayList<OldTVDate> tvDates) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTvDates(tvDates);
	}

	
	
	public static void setTags(ArrayList<OldTVTag> tags) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTags(tags);
	}

	
	
	public static boolean saveGuide(OldTVChannelGuide guide, String tvDate, String channelId) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		
		HashMap<GuideKey, OldTVChannelGuide> guides = mitvStore.getGuides();
		
		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		
		guides.put(guideKey, guide);
		mitvStore.setGuides(guides);

		return true;
	}
	
	

	public static boolean saveGuides(ArrayList<OldTVChannelGuide> channelGuide, String tvDate) 
	{
		boolean success = false;
		
		for (OldTVChannelGuide channelGuideElement : channelGuide)
		{
			String channelId = channelGuideElement.getId();
			
			saveGuide(channelGuideElement, tvDate, channelId);
			
			success = true;
		}
		
		return success;
	}


	public static void saveTaggedBroadcast(OldTVDate date, OldTVTag tag, ArrayList<OldBroadcast> broadcasts) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		HashMap<BroadcastKey, ArrayList<OldBroadcast>> broadcastsList = mitvStore.getBroadcastsList();

		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		broadcastsList.put(broadcastKey, broadcasts);
		mitvStore.setBroadcastsList(broadcastsList);
	}


	// filtering guides by tags
	public static ArrayList<OldBroadcast> getTaggedBroadcasts(String date, OldTVTag tag)
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		ArrayList<OldTVChannelGuide> channelGuides = mitvStore.getChannelGuides(date);
		String tagName = tag.getId();
		
		ArrayList<OldBroadcast> taggedBroadcasts = new ArrayList<OldBroadcast>();
		
		int channelGuideCount = channelGuides.size();
		
		for (int i = 0; i < channelGuideCount; i++) 
		{
			ArrayList<OldBroadcast> broadcastsForChannelGuide = channelGuides.get(i).getBroadcasts();

			//get the broadcast channel
			String channelId = channelGuides.get(i).getId();
			
			int broadcastCoundForChannelGuide = broadcastsForChannelGuide.size();
			
			for (int j = 0; j < broadcastCoundForChannelGuide; j++) 
			{
				OldBroadcast broadcastToAdd = broadcastsForChannelGuide.get(j);
				
				if (broadcastToAdd != null && broadcastToAdd.getProgram().getTags().contains(tagName)) 
				{
					OldTVChannel channel = MiTVStore.getInstance().getChannelById(channelId);
					broadcastToAdd.setChannel(channel);
					taggedBroadcasts.add(broadcastToAdd);
				}
			}
		}
		
		// sort by broadcast time
		Collections.sort(taggedBroadcasts, new OldBroadcast.BroadcastComparatorByTime());

		return taggedBroadcasts;
	}

}
