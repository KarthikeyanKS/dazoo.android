
package com.mitv.storage;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.util.Log;
import com.mitv.SecondScreenApplication;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;



public class MiTVStoreOperations 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "MiTVStoreOperations";


	
	public static void saveChannels(ArrayList<Channel> channels) 
	{
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
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


	
	public static void setTvDates(ArrayList<TvDate> tvDates) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTvDates(tvDates);
	}

	
	
	public static void setTags(ArrayList<Tag> tags) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTags(tags);
	}

	
	
	public static boolean saveGuide(ChannelGuide guide, String tvDate, String channelId) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		
		HashMap<GuideKey, ChannelGuide> guides = mitvStore.getGuides();
		
		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		
		guides.put(guideKey, guide);
		mitvStore.setGuides(guides);

		return true;
	}
	
	

	public static boolean saveGuides(ArrayList<ChannelGuide> channelGuide, String tvDate) 
	{
		boolean success = false;
		
		for (ChannelGuide channelGuideElement : channelGuide)
		{
			String channelId = channelGuideElement.getId();
			
			saveGuide(channelGuideElement, tvDate, channelId);
			
			success = true;
		}
		
		return success;
	}


	public static void saveTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts) 
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = mitvStore.getBroadcastsList();

		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		broadcastsList.put(broadcastKey, broadcasts);
		mitvStore.setBroadcastsList(broadcastsList);
	}


	// filtering guides by tags
	public static ArrayList<Broadcast> getTaggedBroadcasts(String date, Tag tag)
	{
		MiTVStore mitvStore = MiTVStore.getInstance();
		ArrayList<ChannelGuide> channelGuides = mitvStore.getChannelGuides(date);
		String tagName = tag.getId();
		
		ArrayList<Broadcast> taggedBroadcasts = new ArrayList<Broadcast>();
		
		int channelGuideCount = channelGuides.size();
		
		for (int i = 0; i < channelGuideCount; i++) 
		{
			ArrayList<Broadcast> broadcastsForChannelGuide = channelGuides.get(i).getBroadcasts();

			//get the broadcast channel
			String channelId = channelGuides.get(i).getId();
			
			int broadcastCoundForChannelGuide = broadcastsForChannelGuide.size();
			
			for (int j = 0; j < broadcastCoundForChannelGuide; j++) 
			{
				Broadcast broadcastToAdd = broadcastsForChannelGuide.get(j);
				
				if (broadcastToAdd != null && broadcastToAdd.getProgram().getTags().contains(tagName)) 
				{
					Channel channel = MiTVStore.getInstance().getChannelById(channelId);
					broadcastToAdd.setChannel(channel);
					taggedBroadcasts.add(broadcastToAdd);
				}
			}
		}
		
		// sort by broadcast time
		Collections.sort(taggedBroadcasts, new Broadcast.BroadcastComparatorByTime());

		return taggedBroadcasts;
	}

}
