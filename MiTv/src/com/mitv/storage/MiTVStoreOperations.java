package com.mitv.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

import com.mitv.SecondScreenApplication;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;

public class MiTVStoreOperations {

	private static final String	TAG	= "MiTVStoreOperations";

//	public static void saveDefaultChannels(ArrayList<Channel> defaultChannels) {
//		HashMap<String, Channel> defaultChannelsMap = new HashMap<String, Channel>();
//		ArrayList<String> defaultChannelIds = new ArrayList<String>();
//
//		int size = defaultChannels.size();
//		for (int i = 0; i < size; i++) {
//			defaultChannelsMap.put(defaultChannels.get(i).getChannelId(), defaultChannels.get(i));
//			defaultChannelIds.add(defaultChannels.get(i).getChannelId());
//		}
//		MiTVStore mitvStore = MiTVStore.getInstance();
//		mitvStore.setDefaultChannels(defaultChannelsMap);
//		mitvStore.setDefaultChannelIds(defaultChannelIds);
//	}

	public static void saveChannels(ArrayList<Channel> channels) {
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
		ArrayList<String> channelsIds = new ArrayList<String>();

		int size = channels.size();
		for (int i = 0; i < size; i++) {
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelsIds.add(channels.get(i).getChannelId());
		}

		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setChannels(channelsMap);
		
		/*  */
		if((SecondScreenApplication.isLoggedIn() && !MiTVStore.getInstance().isMyIdsSet()) || !SecondScreenApplication.isLoggedIn()) {
			mitvStore.setChannelIds(channelsIds);
		}
	}

//	public static void saveMyChannels(ArrayList<Channel> channels) {
//		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
//		ArrayList<String> channelIds = new ArrayList<String>();
//
//		int size = channels.size();
//		for (int i = 0; i < size; i++) {
//			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
//			channelIds.add(channels.get(i).getChannelId());
//		}
//		MiTVStore mitvStore = MiTVStore.getInstance();
//		mitvStore.setMyChannels(channelsMap);
//		mitvStore.setMyChannelIds(channelIds);
//	}

	public static void saveTvDates(ArrayList<TvDate> tvDates) {
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTvDates(tvDates);
	}

	public static void setTags(ArrayList<Tag> tags) {
		MiTVStore mitvStore = MiTVStore.getInstance();
		mitvStore.setTags(tags);
	}

	public static boolean saveGuide(ChannelGuide guide, String tvDate, String channelId) {
		MiTVStore mitvStore = MiTVStore.getInstance();
		HashMap<GuideKey, ChannelGuide> guides = mitvStore.getGuides();
		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		
		guides.put(guideKey, guide);
		mitvStore.setGuides(guides);

		return true;
	}

	public static boolean saveGuides(ArrayList<ChannelGuide> guide, String tvDate) {
		int size = guide.size();
		boolean success = false;
		for (int i = 0; i < size; i++) {
			String channelId = guide.get(i).getId();
			ChannelGuide guideToSave = guide.get(i);
			saveGuide(guideToSave, tvDate, channelId);
			success = true;
		}
		return success;
	}


	public static void saveTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts) {
		MiTVStore mitvStore = MiTVStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = mitvStore.getBroadcastsList();

		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		broadcastsList.put(broadcastKey, broadcasts);
		mitvStore.setBroadcastsList(broadcastsList);
	}


	// filtering guides by tags
	public static ArrayList<Broadcast> getTaggedBroadcasts(String date, Tag tag) {
		MiTVStore mitvStore = MiTVStore.getInstance();
		ArrayList<ChannelGuide> guideTable = mitvStore.getGuideTable(date);
		String tagName = tag.getId();
		
		ArrayList<Broadcast> taggedBroadcasts = new ArrayList<Broadcast>();
		int guideTableSize = guideTable.size();
		for (int i = 0; i < guideTableSize; i++) {
			ArrayList<Broadcast> oneGuideBroadcasts = new ArrayList<Broadcast>();
			// Log.d(TAG,"One guide broadcasts: " + oneGuideBroadcasts.size());
			oneGuideBroadcasts = guideTable.get(i).getBroadcasts();
			// Log.d(TAG,"One guide broadcasts AFTER:" + oneGuideBroadcasts.size());

			//get the broadcast channel
			String channelId = guideTable.get(i).getId();
			
			
			int oneGuideBroadcastsSize = oneGuideBroadcasts.size();
			for (int j = 0; j < oneGuideBroadcastsSize; j++) {
				if (oneGuideBroadcasts.get(j).getProgram().getTags().contains(tagName)) {
					// Log.d(TAG,"" + oneGuideBroadcasts.get(j).getBeginTime());
					
					Broadcast broadcastToAdd = oneGuideBroadcasts.get(j);
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
