package com.mitv.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mitv.model.AdzerkAd;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.FeedItem;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;

public class MiTVStore {
	private static final String							TAG					= "MiTVStore";

	private ArrayList<TvDate>							mTvDates			= new ArrayList<TvDate>();

	private ArrayList<String>							mChannelIds			= new ArrayList<String>();
	private HashMap<String, Channel>					mDisplayedChannels			= new HashMap<String, Channel>();

	private ArrayList<Tag>								mTags				= new ArrayList<Tag>();
	private HashMap<String, HashMap<Integer, AdzerkAd>> mFragmentToAdsMap 	= new HashMap<String, HashMap<Integer, AdzerkAd>>();
	private HashMap<GuideKey, ChannelGuide>				mGuides				= new HashMap<GuideKey, ChannelGuide>();
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mTaggedBroadcasts	= new HashMap<BroadcastKey, ArrayList<Broadcast>>();
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mMyTaggedBroadcasts	= new HashMap<BroadcastKey, ArrayList<Broadcast>>();

	private ArrayList<String>							mLikeIds			= new ArrayList<String>();

	private ArrayList<FeedItem>							mActivityFeed		= new ArrayList<FeedItem>();
	private ArrayList<Broadcast>						mPopularFeed		= new ArrayList<Broadcast>();
	private boolean mMyIdsSet = false;

	// private constructor prevents instantiation from other classes
	private MiTVStore() {
	};
	
	public boolean isMyIdsSet() {
		return mMyIdsSet;
	}

	/**
	 * MiTVStoreHolder is loaded on the first execution of the MiTVStore.getInstance() or the first access to MiTVStoreHolder.INSTANCE, not before
	 */

	private static class MiTVStoreHolder {
		public static final MiTVStore	INSTANCE	= new MiTVStore();
	}

	public static MiTVStore getInstance() {
		return MiTVStoreHolder.INSTANCE;
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
	public void setDisplayedChannels(HashMap<String, Channel> channels) {
		this.mDisplayedChannels = channels;
	}

	public HashMap<String, Channel> getDisplayedChannels() {
		return this.mDisplayedChannels;
	}

	

	public Channel getChannelById(String channelId) {
		for (Entry<String, Channel> entry : mDisplayedChannels.entrySet()) {
			if (entry.getKey().equals(channelId)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public void storeMyChannelIds(ArrayList<String> ids) {
		this.mMyIdsSet = true;
		setChannelIds(ids);
	}

	public void setChannelIds(ArrayList<String> ids) {
		this.mChannelIds = ids;
	}

	public ArrayList<String> getChannelIds() {
		return this.mChannelIds;
	}

	// likes
	public void setLikeIds(ArrayList<String> likeIds) {
		this.mLikeIds = likeIds;
	}

	public ArrayList<String> getLikeIds() {
		return this.mLikeIds;
	}

	public boolean isInTheLikesList(String likeId) {
		return this.mLikeIds.contains(likeId);
	}

	public void deleteLikeIdFromList(String likeId) {
		this.mLikeIds.remove(likeId);
	}

	public void addLikeIdToList(String likeId) {
		this.mLikeIds.add(likeId);
	}

	// guide
	public void setGuides(HashMap<GuideKey, ChannelGuide> guides) {
		this.mGuides = guides;
	}

	public HashMap<GuideKey, ChannelGuide> getGuides() {
		return this.mGuides;
	}

	public ChannelGuide getChannelGuide(String tvDate, String channelId) {
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);

		for (Entry<GuideKey, ChannelGuide> entry : mGuides.entrySet()) {
			if ((entry.getKey().getChannelId().equals(currentKey.getChannelId())) & (entry.getKey().getDate().equals(currentKey.getDate()))) {
				return entry.getValue();
			}
		}
		return null;
	}

	public ArrayList<ChannelGuide> getGuideTable(String tvDate) {
		ArrayList<ChannelGuide> guideTable = new ArrayList<ChannelGuide>();
		int size = mChannelIds.size();
		for (int i = 0; i < size; i++) {
			ChannelGuide guide = getChannelGuide(tvDate, mChannelIds.get(i));
			if (guide != null) {
				guideTable.add(guide);

			}
		}
		return guideTable;
	}

	// check if there is a pre-loaded guide data
	public boolean isGuideForDate(String tvDate){
		if(!getGuideTable(tvDate).isEmpty()){
			return true;
		} else return false;
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

			if (entry.getKey().getDate().getDate().equals(broadcastKey.getDate().getDate()) && entry.getKey().getTag().getName().equals(broadcastKey.getTag().getName())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Broadcast getBroadcast(String date, String channelId, long beginTimeInMillis) {
		ChannelGuide channelGuide = getChannelGuide(date, channelId);
		ArrayList<Broadcast> channelBroadcasts = channelGuide.getBroadcasts();
		int size = channelBroadcasts.size();

		for (int i = 0; i < size; i++) {
			long temp = channelBroadcasts.get(i).getBeginTimeMillisGmt();
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
			if (entry.getKey().getDate().getDate().equals(broadcastKey.getDate().getDate()) && entry.getKey().getTag().getName().equals(broadcastKey.getTag().getName())) {
				return entry.getValue();
			}
		}
		return null;
	}

	// activity feed
	public void setActivityFeed(ArrayList<FeedItem> activityFeed) {
		this.mActivityFeed = activityFeed;
	}

	public ArrayList<FeedItem> getActivityFeed() {
		return this.mActivityFeed;
	}

	public void reinitializeFeed() {
		this.mActivityFeed.clear();
		this.mActivityFeed = new ArrayList<FeedItem>();
	}

	public void addItemsToActivityFeed(ArrayList<FeedItem> newItems) {
		this.mActivityFeed.addAll(newItems);
	}
	
	// popular feed
	public void setPopularFeed(ArrayList<Broadcast> popularFeed){
		this.mPopularFeed = popularFeed;
	}
	
	public ArrayList<Broadcast> getPopularFeed(){
		return this.mPopularFeed;
	}

	// CLEAR DATA WHEN NEW CHANNEL IS ADDED TO THE SELECTION
	public void clearMyGuidesStorage() {
//		this.mMyGuides.clear();
		this.mMyTaggedBroadcasts.clear();
//		this.mMyGuides = new HashMap<GuideKey, ChannelGuide>();
		this.mMyTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
	}

	public void reinitializeAll() {
		this.mTvDates = new ArrayList<TvDate>();
		this.mTags = new ArrayList<Tag>();
		this.mDisplayedChannels = new HashMap<String, Channel>();
		this.mChannelIds = new ArrayList<String>();
		this.mGuides = new HashMap<GuideKey, ChannelGuide>();
		this.mTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
		this.mMyTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
		this.mLikeIds = new ArrayList<String>();
	}

	public void clearAndReinitializeForMyChannels() {
		this.mGuides.clear();
		this.mTaggedBroadcasts.clear();
		this.mMyTaggedBroadcasts.clear();
		this.mGuides = new HashMap<GuideKey, ChannelGuide>();
		this.mTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
		this.mMyTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
	}

	public void clearAll() {
		this.mTvDates.clear();
		this.mTags.clear();
		this.mDisplayedChannels.clear();
		this.mChannelIds.clear();
		this.mGuides.clear();
		this.mTaggedBroadcasts.clear();
		this.mMyTaggedBroadcasts.clear();
		this.mLikeIds.clear();
	}

	public void addAdsForFragment(String fragmentName, HashMap<Integer, AdzerkAd> adItems) {
		mFragmentToAdsMap.put(fragmentName, adItems);
	}
	
	public HashMap<Integer, AdzerkAd> getAdsForFragment(String fragmentName) {
		return mFragmentToAdsMap.get(fragmentName);
	}
}
