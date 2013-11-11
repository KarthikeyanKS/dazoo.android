package com.millicom.secondscreen.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class DazooStore {
	private static final String							TAG					= "DazooStore";

	private ArrayList<TvDate>							mTvDates			= new ArrayList<TvDate>();

	private ArrayList<String>							mMyChannelIds		= new ArrayList<String>();
	private HashMap<String, Channel>					mMyChannels			= new HashMap<String, Channel>();

	private ArrayList<String>							mDefaultChannelIds	= new ArrayList<String>();
	private HashMap<String, Channel>					mDefaultChannels	= new HashMap<String, Channel>();

	private ArrayList<String>							mAllChannelIds		= new ArrayList<String>();
	private HashMap<String, Channel>					mAllChannels		= new HashMap<String, Channel>();

	private ArrayList<Tag>								mTags				= new ArrayList<Tag>();
	private HashMap<GuideKey, Guide>					mGuides				= new HashMap<GuideKey, Guide>();
	private HashMap<GuideKey, Guide>					mMyGuides			= new HashMap<GuideKey, Guide>();
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mTaggedBroadcasts	= new HashMap<BroadcastKey, ArrayList<Broadcast>>();
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mMyTaggedBroadcasts	= new HashMap<BroadcastKey, ArrayList<Broadcast>>();

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

	public void setDefaultChannels(HashMap<String, Channel> defaultChannels) {
		this.mDefaultChannels = defaultChannels;
	}

	public HashMap<String, Channel> getDefaultChannels() {
		return this.mDefaultChannels;
	}

	public void setAllChannels(HashMap<String, Channel> allChannels) {
		this.mAllChannels = allChannels;
	}

	public HashMap<String, Channel> getAllChannels() {
		return this.mAllChannels;
	}

	public Channel getChannelFromDefault(String channelId) {
		for (Entry<String, Channel> entry : mDefaultChannels.entrySet()) {
			if (entry.getKey().equals(channelId)) {
				// found the channel by id
				return entry.getValue();
			}
		}
		return null;
	}

	public Channel getChannelFromAll(String channelId) {
		for (Entry<String, Channel> entry : mAllChannels.entrySet()) {
			if (entry.getKey().equals(channelId)) {
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

	public void setAllChannelIds(ArrayList<String> ids) {
		this.mAllChannelIds = ids;
	}

	public ArrayList<String> getAllChannelIds() {
		return this.mAllChannelIds;
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

	public Guide getChannelGuideFromDefault(String tvDate, String channelId) {
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);

		for (Entry<GuideKey, Guide> entry : mGuides.entrySet()) {
			if ((entry.getKey().getChannelId().equals(currentKey.getChannelId())) & (entry.getKey().getDate().equals(currentKey.getDate()))) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Guide getChannelGuideFromMy(String tvDate, String channelId) {
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);

		for (Entry<GuideKey, Guide> entry : mMyGuides.entrySet()) {
			if ((entry.getKey().getChannelId().equals(currentKey.getChannelId())) && (entry.getKey().getDate().equals(currentKey.getDate()))) {
				return entry.getValue();
			}
		}
		return null;
	}

	public ArrayList<Guide> getMyGuideTable(String tvDate) {
		ArrayList<Guide> myGuideTable = new ArrayList<Guide>();
		int size = mMyChannelIds.size();
		for (int i = 0; i < size; i++) {
			Guide myGuide = getChannelGuideFromMy(tvDate, mMyChannelIds.get(i));
			if (myGuide != null) {
				myGuideTable.add(myGuide);
			}
		}
		return myGuideTable;
	}

	public ArrayList<Guide> getGuideTable(String tvDate) {
		ArrayList<Guide> guideTable = new ArrayList<Guide>();
		int size = mDefaultChannelIds.size();
		for (int i = 0; i < size; i++) {
			Guide guide = getChannelGuideFromDefault(tvDate, mDefaultChannelIds.get(i));
			if (guide != null) {
				guideTable.add(guide);

			}
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
			Log.d(TAG, "search key: " + broadcastKey.getDate().getDate() + "   " + broadcastKey.getTag().getName());
			Log.d(TAG, "entry KEY: " + entry.getKey().getDate().getDate() + "  " + entry.getKey().getTag().getName());

			if (entry.getKey().getDate().getDate().equals(broadcastKey.getDate().getDate()) && entry.getKey().getTag().getName().equals(broadcastKey.getTag().getName())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Broadcast getBroadcastFromDefault(String date, String channelId, long beginTimeInMillis) {
		Guide channelGuide = getChannelGuideFromDefault(date, channelId);
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

	public Broadcast getBroadcastFromMy(String date, String channelId, long beginTimeInMillis) {
		Guide myChannelGuide = getChannelGuideFromMy(date, channelId);
		ArrayList<Broadcast> myChannelBroadcasts = myChannelGuide.getBroadcasts();
		int size = myChannelBroadcasts.size();

		for (int i = 0; i < size; i++) {
			long temp = myChannelBroadcasts.get(i).getBeginTimeMillis();
			if (beginTimeInMillis == temp) {
				return myChannelBroadcasts.get(i);
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

	// CLEAR DATA WHEN NEW CHANNEL IS ADDED TO THE SELECTION
	public void clearMyGuidesStorage() {
		this.mMyGuides.clear();
		this.mMyTaggedBroadcasts.clear();
		this.mMyGuides = new HashMap<GuideKey, Guide>();
		this.mMyTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
	}
}
