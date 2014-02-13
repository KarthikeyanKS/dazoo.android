
package com.mitv.storage;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import android.util.SparseArray;

import com.mitv.model.AdzerkAd;
import com.mitv.model.Broadcast;
import com.mitv.model.TVChannel;
import com.mitv.model.TVFeedItem;
import com.mitv.model.TVChannelGuide;
import com.mitv.model.TVTag;
import com.mitv.model.TVDate;



public class MiTVStore 
{
	@SuppressWarnings("unused")
	private static final String							TAG					= "MiTVStore";

	private ArrayList<TVDate>							mTvDates			= new ArrayList<TVDate>();

	private ArrayList<String>							mChannelIds			= new ArrayList<String>();
	private Calendar									mChannelIdsFetchTimestamp = null;	
	
	private HashMap<String, TVChannel>					mChannels			= new HashMap<String, TVChannel>();

	private ArrayList<TVTag>								mTags				= new ArrayList<TVTag>();
	private HashMap<String, SparseArray<AdzerkAd>> 		mFragmentToAdsMap 	= new HashMap<String, SparseArray<AdzerkAd>>();
	private HashMap<GuideKey, TVChannelGuide>				mChannelGuides				= new HashMap<GuideKey, TVChannelGuide>();
	private HashMap<BroadcastKey, ArrayList<Broadcast>>	mTaggedBroadcasts	= new HashMap<BroadcastKey, ArrayList<Broadcast>>();

	private ArrayList<String>							mLikeIds			= new ArrayList<String>();
	private Calendar									mLikeIdsFetchTimestamp = null;	

	private ArrayList<TVFeedItem>							mActivityFeed		= new ArrayList<TVFeedItem>();
	private ArrayList<Broadcast>						mPopularFeed		= new ArrayList<Broadcast>();
	private boolean mMyIdsSet = false;

	
	
	// private constructor prevents instantiation from other classes
	private MiTVStore() {};
	
	
	
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
	public void setTags(ArrayList<TVTag> tags) {
		this.mTags = tags;
	}

	public ArrayList<TVTag> getTags() {
		return this.mTags;
	}

	public TVTag getTag(String tagName) {
		int size = mTags.size();
		for (int i = 0; i < size; i++) {
			if (mTags.get(i).getName().equals(tagName)) {
				return mTags.get(i);
			}
		}
		return null;
	}

	// dates
	public void setTvDates(ArrayList<TVDate> tvDates) {
		this.mTvDates = tvDates;
	}

	public ArrayList<TVDate> getTvDates() {
		return this.mTvDates;
	}

	public TVDate getDate(String dateRepresentation) {
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
	public void setChannels(HashMap<String, TVChannel> channels) {
		this.mChannels = channels;
	}

	
	
	/*
	 * Returns all channels when the user is not logged in and the user channels when the user is logged in
	 */
	public HashMap<String, TVChannel> getChannels()
	{
		return this.mChannels;
	}

	

	public TVChannel getChannelById(String channelId) 
	{
		for (Entry<String, TVChannel> entry : mChannels.entrySet()) 
		{
			if (entry.getKey().equals(channelId)) 
			{
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	

	public void storeMyChannelIds(ArrayList<String> ids) 
	{
		Calendar now = Calendar.getInstance();
		
		this.mMyIdsSet = true;
		this.mChannelIdsFetchTimestamp = now;
		setChannelIds(ids);
	}

	public void setChannelIds(ArrayList<String> ids) {
		this.mChannelIds = ids;
	}

	public ArrayList<String> getChannelIds() {
		return this.mChannelIds;
	}

	// likes
	public void setLikeIds(ArrayList<String> likeIds) 
	{
		Calendar now = Calendar.getInstance();
		
		this.mLikeIds = likeIds;
		this.mLikeIdsFetchTimestamp = now;
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
	public void setGuides(HashMap<GuideKey, TVChannelGuide> guides) {
		this.mChannelGuides = guides;
	}

	public HashMap<GuideKey, TVChannelGuide> getGuides() {
		return this.mChannelGuides;
	}
	
	
	
	public ArrayList<TVChannelGuide> getChannelGuides() 
	{
		ArrayList<TVChannelGuide> channelGuides = new ArrayList<TVChannelGuide>(mChannelGuides.values());
		
		return channelGuides;
	}


	
	public ArrayList<TVChannelGuide> getChannelGuides(String tvDate) 
	{
		ArrayList<TVChannelGuide> guideTable = new ArrayList<TVChannelGuide>();
		
		for (String channelId : mChannelIds) 
		{
			TVChannelGuide guide = getChannelGuide(tvDate, channelId);
			
			if (guide != null) 
			{
				guideTable.add(guide);
			}
		}
		
		return guideTable;
	}
	
	
	
	public TVChannelGuide getChannelGuide(String tvDate, String channelId)
	{
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);

		for (Entry<GuideKey, TVChannelGuide> entry : mChannelGuides.entrySet()) 
		{
			if ((entry.getKey().getChannelId().equals(currentKey.getChannelId())) & (entry.getKey().getDate().equals(currentKey.getDate())))
			{
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	

	// Check if there is a pre-loaded guide data
	public boolean isGuideForDate(String tvDate)
	{
		if(!getChannelGuides(tvDate).isEmpty())
		{
			return true;
		} 
		else return false;
	}
	
	
	
	// broadcasts
	public void setBroadcastsList(HashMap<BroadcastKey, ArrayList<Broadcast>> taggedBroadcasts) {
		this.mTaggedBroadcasts = taggedBroadcasts;
	}

	public HashMap<BroadcastKey, ArrayList<Broadcast>> getBroadcastsList() {
		return this.mTaggedBroadcasts;
	}

	public ArrayList<Broadcast> getTaggedBroadcasts(TVDate date, TVTag tag) {
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
		TVChannelGuide channelGuide = getChannelGuide(date, channelId);
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

	// activity feed
	public void setActivityFeed(ArrayList<TVFeedItem> activityFeed) {
		this.mActivityFeed = activityFeed;
	}

	public ArrayList<TVFeedItem> getActivityFeed() {
		return this.mActivityFeed;
	}

	public void reinitializeFeed() {
		this.mActivityFeed.clear();
		this.mActivityFeed = new ArrayList<TVFeedItem>();
	}

	public void addItemsToActivityFeed(ArrayList<TVFeedItem> newItems) {
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
	public void clearChannelGuides() {
		this.mChannelGuides.clear();
		this.mTaggedBroadcasts.clear();
	}
	
	public void clearChannelIds() {
		this.mChannelIds.clear();
		this.mMyIdsSet = false;
	}
	
	public void clearChannelsAndIds() {
		clearChannelIds();
		this.mChannels.clear();
	}
	
	public void clearChannelsAndIdsAndGuide() {
		clearChannelsAndIds();
		clearChannelGuides();
	}

//	public void reinitializeAll() {
//		this.mTvDates = new ArrayList<TvDate>();
//		this.mTags = new ArrayList<Tag>();
//		this.mChannels = new HashMap<String, Channel>();
//		this.mChannelIds = new ArrayList<String>();
//		this.mGuides = new HashMap<GuideKey, ChannelGuide>();
//		this.mTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
//		this.mLikeIds = new ArrayList<String>();
//	}

//	public void clearAndReinitializeForMyChannels() {
//		this.mGuides.clear();
//		this.mTaggedBroadcasts.clear();
//		this.mGuides = new HashMap<GuideKey, ChannelGuide>();
//		this.mTaggedBroadcasts = new HashMap<BroadcastKey, ArrayList<Broadcast>>();
//	}

//	public void clearAll() {
//		this.mTvDates.clear();
//		this.mTags.clear();
//		this.mChannels.clear();
//		this.mChannelIds.clear();
//		this.mGuides.clear();
//		this.mTaggedBroadcasts.clear();
//		this.mLikeIds.clear();
//	}

	
	
	public void addAdsForFragment(
			String fragmentName, 
			SparseArray<AdzerkAd> adItems) 
	{
		mFragmentToAdsMap.put(fragmentName, adItems);
	}
	
	
	
	public SparseArray<AdzerkAd> getAdsForFragment(String fragmentName) 
	{
		return mFragmentToAdsMap.get(fragmentName);
	}
	
	
	
	public Calendar getmMyChannelIdsFetchTimestamp()
	{
		return mChannelIdsFetchTimestamp;
	}

	
	
	public Calendar getmLikeIdsFetchTimestamp()
	{
		return mLikeIdsFetchTimestamp;
	}
}
