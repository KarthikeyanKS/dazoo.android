package com.millicom.secondscreen.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class DazooStoreOperations {

	public static void saveChannels(ArrayList<Channel> channels) {
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
		ArrayList<String> channelIds = new ArrayList<String>();

		int size = channels.size();
		for (int i = 0; i < size; i++) {
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelIds.add(channels.get(i).getChannelId());
		}
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setChannels(channelsMap);
		dazooStore.setChannelIds(channelIds);
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

	public static void saveGuide(Guide guide, TvDate tvDate, String channelId) {
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<GuideKey, Guide> guides = dazooStore.getGuides();

		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		guides.put(guideKey, guide);
		dazooStore.setGuides(guides);
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
	public static ArrayList<Broadcast> getTaggedBroadcasts(TvDate date, Tag tag) {
		DazooStore dazooStore = DazooStore.getInstance();
		ArrayList<Guide> guideTable = dazooStore.getGuideTable(date);
		String tagName = tag.getName();

		ArrayList<Broadcast> taggedBroadcasts = new ArrayList<Broadcast>();
		int guideTableSize = guideTable.size();
		for (int i = 0; i < guideTableSize; i++) {
			ArrayList<Broadcast> oneGuideBroadcasts = guideTable.get(i).getBroadcasts();
			int oneGuideBroadcastsSize = oneGuideBroadcasts.size();
			for (int j = 0; j < oneGuideBroadcastsSize; j++) {
				if (oneGuideBroadcasts.get(j).getProgram().getTags().contains(tagName)) {
					taggedBroadcasts.add(oneGuideBroadcasts.get(j));
				}
			}
		}

		// sort by broadcast time
		Collections.sort(taggedBroadcasts, new Broadcast.BroadcastComparatorByTime());

		return taggedBroadcasts;
	}

	public static ArrayList<Broadcast> getMyTaggedBrodcasts(TvDate date, Tag tag) {
		DazooStore dazooStore = DazooStore.getInstance();
		ArrayList<Guide> guideTable = dazooStore.getMyGuideTable(date);
		String tagName = tag.getName();

		ArrayList<Broadcast> taggedBroadcasts = new ArrayList<Broadcast>();
		int guideTableSize = guideTable.size();
		for (int i = 0; i < guideTableSize; i++) {
			ArrayList<Broadcast> oneGuideBroadcasts = guideTable.get(i).getBroadcasts();
			int oneGuideBroadcastsSize = oneGuideBroadcasts.size();
			for (int j = 0; j < oneGuideBroadcastsSize; j++) {
				if (oneGuideBroadcasts.get(j).getProgram().getTags().contains(tagName)) {
					taggedBroadcasts.add(oneGuideBroadcasts.get(j));
				}
			}
		}

		// sort by broadcast time
		Collections.sort(taggedBroadcasts, new Broadcast.BroadcastComparatorByTime());

		return taggedBroadcasts;
	}
}
