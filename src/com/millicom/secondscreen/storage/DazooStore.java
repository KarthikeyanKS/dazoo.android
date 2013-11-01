package com.millicom.secondscreen.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class DazooStore {
	private static final String							TAG	= "DazooStore";

	private ArrayList<String>							mMyChannelIds;
	private ArrayList<TvDate>							mTvDates;
	private HashMap<String, Channel>					mChannels;
	private HashMap<String, Channel>					mMyChannels;

	private ArrayList<String>							mDefaultChannelIds;
	private ArrayList<String>							mListChannelIds;
	private HashMap<String, Channel>					mDefaultChannels;
	private HashMap<String, Channel>					mListChannels;

	private ArrayList<Tag>								mTags;
	private HashMap<GuideKey, Guide>					mGuides;
	private HashMap<GuideKey, Guide>					mMyGuides;
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mTaggedBroadcasts;
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mMyTaggedBroadcasts;

	// private constructor prevents instantiation from other classes
	private DazooStore() {
	};

	/**
	 * DazooStoreHolder is loaded on the first execution of the DazooStore.getInstance() or the first access to DazooStoreHolder.INSTANCE, not before
	 */

	private static class DazooStoreHolder {
		public static final DazooStore	INSTANCE	= new DazooStore();
	}

	public static DazooStore getInstance() {
		return DazooStoreHolder.INSTANCE;
	}

	// tags
	public void setTags(ArrayList<Tag> tags) {
		this.mTags = tags;
	}

	public ArrayList<Tag> getTags() {
		return this.mTags;
	}

	public Tag getTag(String tagName) {
		int size = mTags.size();
		for (int i = 0; i < size; i++) {
			if (mTags.get(i).getName().equals(tagName)) {
				return mTags.get(i);
			}
		}
		return null;
	}

	// dates
	public void setTvDates(ArrayList<TvDate> tvDates) {
		this.mTvDates = tvDates;
	}

	public ArrayList<TvDate> getTvDates() {
		return this.mTvDates;
	}

	public TvDate getDate(String dateRepresentation) {
		int size = mTvDates.size();
		for (int i = 0; i < size; i++) {
			if (mTvDates.get(i).getDate().equals(dateRepresentation)) {
				return mTvDates.get(i);
			}
		}
		return null;
	}

	public int getDateIndex(String dateRepresentation) {
		int size = mTvDates.size();
		for (int i = 0; i < size; i++) {
			if (mTvDates.get(i).getDate().equals(dateRepresentation)) {
				return i;
			}
		}
		return -1;
	}

	// channels
	public void setChannels(HashMap<String, Channel> channels) {
		this.mChannels = channels;
	}

	public HashMap<String, Channel> getChannels() {
		return this.mChannels;
	}

	public void setDefaultChannels(HashMap<String, Channel> defaultChannels) {
		this.mDefaultChannels = defaultChannels;
	}

	public HashMap<String, Channel> getDefaultChannels() {
		return this.mDefaultChannels;
	}

	public void setListChannels(HashMap<String, Channel> listChannels) {
		this.mListChannels = listChannels;
	}

	public HashMap<String, Channel> getListChannels() {
		return this.mListChannels;
	}

	public Channel getChannel(String channelId) {
		for (Entry<String, Channel> entry : mChannels.entrySet()) {
			if (entry.getKey().equals(channelId)) {
				// found the channel by id
				return entry.getValue();
			}
		}
		return null;
	}

	public void setDefaultChannelIds(ArrayList<String> ids) {
		this.mDefaultChannelIds = ids;
	}

	public ArrayList<String> getDefaultChannelIds() {
		return this.mDefaultChannelIds;
	}

	public void setListChannelIds(ArrayList<String> ids) {
		this.mListChannelIds = ids;
	}

	public ArrayList<String> getListChannelIds() {
		return this.mListChannelIds;
	}

	// my channels
	public void setMyChannelIds(ArrayList<String> myIds) {
		this.mMyChannelIds = myIds;
	}

	public ArrayList<String> getMyChannelIds() {
		return this.mMyChannelIds;
	}

	public void setMyChannels(HashMap<String, Channel> myChannels) {
		this.mMyChannels = myChannels;
	}

	public HashMap<String, Channel> getMyChannels() {
		return this.mMyChannels;
	}

	// guide
	public void setGuides(HashMap<GuideKey, Guide> guides) {
		this.mGuides = guides;
	}

	public HashMap<GuideKey, Guide> getGuides() {
		return this.mGuides;
	}

	public void setMyGuides(HashMap<GuideKey, Guide> myGuides) {
		this.mMyGuides = myGuides;
	}

	public HashMap<GuideKey, Guide> getMyGuides() {
		return this.mMyGuides;
	}

	public Guide getChannelGuide(TvDate tvDate, String channelId) {
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);

		for (Entry<GuideKey, Guide> entry : mGuides.entrySet()) {
			if (entry.getKey().equals(currentKey)) {
				// found the guide by date and tag
				return entry.getValue();
			}
		}
		return null;
	}

	public ArrayList<Guide> getMyGuideTable(TvDate tvDate) {
		ArrayList<Guide> myGuideTable = new ArrayList<Guide>();
		int size = mMyChannelIds.size();
		for (int i = 0; i < size; i++) {
			Guide myGuide = getChannelGuide(tvDate, mMyChannelIds.get(i));
			myGuideTable.add(myGuide);
		}
		return myGuideTable;
	}

	public ArrayList<Guide> getGuideTable(TvDate tvDate) {
		ArrayList<Guide> guideTable = new ArrayList<Guide>();
		int size = mListChannelIds.size(); // CLARIFY THAT WITH BACKEND
		for (int i = 0; i < size; i++) {
			Guide guide = getChannelGuide(tvDate, mMyChannelIds.get(i));
			guideTable.add(guide);
		}
		return guideTable;
	}

	// broadcasts
	public void setBroadcastsList(HashMap<BroadcastKey, ArrayList<Broadcast>> taggedBroadcasts) {
		this.mTaggedBroadcasts = taggedBroadcasts;
	}

	public HashMap<BroadcastKey, ArrayList<Broadcast>> getBroadcastsList() {
		return this.mTaggedBroadcasts;
	}

	public ArrayList<Broadcast> getTaggedBroadcasts(TvDate date, Tag tag) {
		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		for (Entry<BroadcastKey, ArrayList<Broadcast>> entry : mTaggedBroadcasts.entrySet()) {
			if (entry.getKey().equals(broadcastKey)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Broadcast getBroadcast(TvDate date, String channelId, long beginTimeInMillis) {
		Guide channelGuide = getChannelGuide(date, channelId);
		ArrayList<Broadcast> channelBroadcasts = channelGuide.getBroadcasts();
		int size = channelBroadcasts.size();

		for (int i = 0; i < size; i++) {
			long temp = channelBroadcasts.get(i).getBeginTimeMillis();
			if (beginTimeInMillis == temp) {
				return channelBroadcasts.get(i);
			}
		}
		return null;
	}

	// my broadcasts
	public void setMyBroadcastsList(HashMap<BroadcastKey, ArrayList<Broadcast>> myTaggedBroadcasts) {
		this.mMyTaggedBroadcasts = myTaggedBroadcasts;
	}

	public HashMap<BroadcastKey, ArrayList<Broadcast>> getMyBroadcastsList() {
		return this.mMyTaggedBroadcasts;
	}

	public ArrayList<Broadcast> getMyTaggedBroadcasts(TvDate date, Tag tag) {
		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);

		for (Entry<BroadcastKey, ArrayList<Broadcast>> entry : mMyTaggedBroadcasts.entrySet()) {
			if (entry.getKey().equals(broadcastKey)) {
				return entry.getValue();
			}
		}
		return null;
	}
}
