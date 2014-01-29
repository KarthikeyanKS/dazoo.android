package com.mitv.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.Guide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;

public class DazooStoreOperations {

	private static final String	TAG	= "DazooStoreOperations";

	public static void saveDefaultChannels(ArrayList<Channel> defaultChannels) {
		HashMap<String, Channel> defaultChannelsMap = new HashMap<String, Channel>();
		ArrayList<String> defaultChannelIds = new ArrayList<String>();

		int size = defaultChannels.size();
		for (int i = 0; i < size; i++) {
			defaultChannelsMap.put(defaultChannels.get(i).getChannelId(), defaultChannels.get(i));
			defaultChannelIds.add(defaultChannels.get(i).getChannelId());
		}
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setDefaultChannels(defaultChannelsMap);
		dazooStore.setDefaultChannelIds(defaultChannelIds);
	}

	public static void saveAllChannels(ArrayList<Channel> allChannels) {
		HashMap<String, Channel> allChannelsMap = new HashMap<String, Channel>();
		ArrayList<String> allChannelsIds = new ArrayList<String>();

		int size = allChannels.size();
		for (int i = 0; i < size; i++) {
			allChannelsMap.put(allChannels.get(i).getChannelId(), allChannels.get(i));
			allChannelsIds.add(allChannels.get(i).getChannelId());
		}

		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setAllChannels(allChannelsMap);
		dazooStore.setAllChannelIds(allChannelsIds);
	}

	public static void saveMyChannels(ArrayList<Channel> channels) {
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
		ArrayList<String> channelIds = new ArrayList<String>();

		int size = channels.size();
		for (int i = 0; i < size; i++) {
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelIds.add(channels.get(i).getChannelId());
		}
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setMyChannels(channelsMap);
		dazooStore.setMyChannelIds(channelIds);
	}

	public static void saveTvDates(ArrayList<TvDate> tvDates) {
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setTvDates(tvDates);
	}

	public static void setTags(ArrayList<Tag> tags) {
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setTags(tags);
	}

	public static boolean saveGuide(Guide guide, String tvDate, String channelId) {
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<GuideKey, Guide> guides = dazooStore.getGuides();
		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		
		guides.put(guideKey, guide);
		dazooStore.setGuides(guides);

		return true;
	}

	public static boolean saveMyGuide(Guide guide, String tvDate, String channelId) {
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<GuideKey, Guide> myGuides = dazooStore.getMyGuides();

		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		myGuides.put(guideKey, guide);
		dazooStore.setMyGuides(myGuides);
		return true;
	}

	public static boolean saveGuides(ArrayList<Guide> guide, String tvDate) {
		int size = guide.size();
		boolean success = false;
		for (int i = 0; i < size; i++) {
			String channelId = guide.get(i).getId();
			Guide guideToSave = guide.get(i);
			saveGuide(guideToSave, tvDate, channelId);
			success = true;
		}
		return success;
	}

	public static boolean saveMyGuides(ArrayList<Guide> myGuide, String tvDate) {
		int size = myGuide.size();
		boolean success = false;
		for (int j = 0; j < size; j++) {
			String channelId = myGuide.get(j).getId();
			Guide myGuideToSave = myGuide.get(j);
			saveMyGuide(myGuideToSave, tvDate, channelId);
			success = true;
		}
		return success;
	}

	public static void saveTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts) {
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = dazooStore.getBroadcastsList();

		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		broadcastsList.put(broadcastKey, broadcasts);
		dazooStore.setBroadcastsList(broadcastsList);
	}

	public static void saveMyTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts) {
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = dazooStore.getMyBroadcastsList();

		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		broadcastsList.put(broadcastKey, broadcasts);
		dazooStore.setMyBroadcastsList(broadcastsList);
	}

	// filtering guides by tags
	public static ArrayList<Broadcast> getTaggedBroadcasts(String date, Tag tag) {
		DazooStore dazooStore = DazooStore.getInstance();
		ArrayList<Guide> guideTable = dazooStore.getGuideTable(date);
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
					Channel channel = DazooStore.getInstance().getChannelFromDefault(channelId);
					
					broadcastToAdd.setChannel(channel);
					
					taggedBroadcasts.add(broadcastToAdd);
				}
			}
		}
		
		// sort by broadcast time
		Collections.sort(taggedBroadcasts, new Broadcast.BroadcastComparatorByTime());

		return taggedBroadcasts;
	}

	public static ArrayList<Broadcast> getMyTaggedBroadcasts(String date, Tag tag) {
		DazooStore dazooStore = DazooStore.getInstance();
		ArrayList<Guide> guideTable = dazooStore.getMyGuideTable(date);
		String tagName = tag.getId();
		
		ArrayList<Broadcast> taggedBroadcasts = new ArrayList<Broadcast>();
		int guideTableSize = guideTable.size();
		
		for (int i = 0; i < guideTableSize; i++) {
			ArrayList<Broadcast> oneGuideBroadcasts = guideTable.get(i).getBroadcasts();
			
			//get the broadcast channel
			String channelId = guideTable.get(i).getId();
			
			int oneGuideBroadcastsSize = oneGuideBroadcasts.size();
			for (int j = 0; j < oneGuideBroadcastsSize; j++) {
				if (oneGuideBroadcasts.get(j).getProgram().getTags().contains(tagName)) {
					
					Broadcast broadcastToAdd = oneGuideBroadcasts.get(j);
					Channel channel = DazooStore.getInstance().getChannelFromAll(channelId);
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
