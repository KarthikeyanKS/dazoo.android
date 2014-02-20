package com.millicom.mitv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.millicom.mitv.models.AppVersion;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.gson.AdzerkAdJSON;
import com.millicom.mitv.models.gson.AppConfigurationJSON;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.gson.TVBroadcastWithProgramAndChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVFeedItem;
import com.millicom.mitv.models.gson.TVTag;
import com.millicom.mitv.models.gson.UserLike;
import com.mitv.model.OldAppConfigurationData;

public class Storage {
	private ArrayList<TVTag> tvTags;
	private ArrayList<TVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIdsDefault;
	private ArrayList<TVChannelId> tvChannelIdsUser;
	private ArrayList<TVChannelId> tvChannelIdsUsed;
	private ArrayList<TVChannel> tvChannels;
	private HashMap<String, TVGuide> tvGuides; /* Key is the id from the TVDate */
	private HashMap<String, ArrayList<TVBroadcast>> taggedBroadcastsForAllDays; /* Key is the id from the TVDate */

	private ArrayList<UserLike> userLikes;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<TVFeedItem> activityFeed;
	private ArrayList<TVBroadcastWithProgramAndChannelInfo> popularBroadcasts;
	
	private String userToken;
	private TVDate tvDateSelected;
	
	private AppVersion appVersionData;
	private AppConfigurationJSON appConfigData;
		
	/* Ads */
	private HashMap<String, SparseArray<AdzerkAdJSON>> fragmentToAdsMap;
	
	/* NON-PERSISTENT USER DATA, USED FOR PASSING DATA BETWEEN ACTIVITIES */
	private TVBroadcast nonPersistentSelectedBroadcast;
	private ArrayList<TVBroadcast> nonPersistentUpcomingBroadcasts;
	private ArrayList<TVBroadcast> nonPersistentRepeatingBroadcasts;
	private Integer nonPersistentSelectedHour;
	private TVChannelId nonPersistentSelectedTVChannelId;
	
	
	/* Should only be used by the ContentManager */
	public Storage() {
		this.tvGuides = new HashMap<String, TVGuide>();
	}
		
	public boolean isLoggedIn() {
		boolean isLoggedIn = false;
		if(!TextUtils.isEmpty(userToken)) {
			isLoggedIn = true;
		}
		return isLoggedIn;
	}
	
	public HashMap<String, TVGuide> getTvGuides() {
		return tvGuides;
	}
	
	public void addTVGuide(TVDate tvDate, TVGuide tvGuide) {
		this.tvGuides.put(tvDate.getId(), tvGuide);
	}
	
	public TVGuide getTVGuideUsingTVDate(TVDate tvDate) {
		TVGuide tvGuide = tvGuides.get(tvDate.getId());
		return tvGuide;
	}
	
	public TVGuide getTVGuideForToday() {
		TVDate tvDate = tvDates.get(0);
		TVGuide tvGuide = tvGuides.get(tvDate.getId());
		return tvGuide;
	}

	public ArrayList<TVTag> getTvTags() {
		return tvTags;
	}

	public void setTvTags(ArrayList<TVTag> tvTags) {
		this.tvTags = tvTags;
	}

	public ArrayList<TVDate> getTvDates() {
		return tvDates;
	}

	public void setTvDates(ArrayList<TVDate> tvDates) {
		this.tvDates = tvDates;
	}

	public ArrayList<TVChannel> getTvChannels() {
		return tvChannels;
	}

	public void setTvChannels(ArrayList<TVChannel> tvChannels) {
		this.tvChannels = tvChannels;
	}
	
	//TODO dont iterate through a list, change tvChannels to a Map instead?
	public TVChannel getTVChannelById(TVChannelId tvChannelId) {
		for(TVChannel tvChannel : tvChannels) {
			if(tvChannel.getChannelId().equals(tvChannelId)) {
				return tvChannel;
			}
		}
		return null;
	}

	public Calendar getLikeIdsFetchedTimestamp() {
		return likeIdsFetchedTimestamp;
	}

	public void setLikeIdsFetchedTimestamp(Calendar likeIdsFetchedTimestamp) {
		this.likeIdsFetchedTimestamp = likeIdsFetchedTimestamp;
	}

	public Calendar getUserChannelIdsFetchedTimestamp() {
		return userChannelIdsFetchedTimestamp;
	}

	public void setUserChannelIdsFetchedTimestamp(Calendar userChannelIdsFetchedTimestamp) {
		this.userChannelIdsFetchedTimestamp = userChannelIdsFetchedTimestamp;
	}

	public ArrayList<TVFeedItem> getActivityFeed() {
		return activityFeed;
	}

	public void setActivityFeed(ArrayList<TVFeedItem> activityFeed) {
		this.activityFeed = activityFeed;
	}
	
	public void addMoreActivityFeedItems(ArrayList<TVFeedItem> additionalActivityFeedItems) {
		if(this.activityFeed == null) {
			activityFeed = new ArrayList<TVFeedItem>();
		}
		activityFeed.addAll(additionalActivityFeedItems);
	}

	public ArrayList<TVBroadcastWithProgramAndChannelInfo> getPopularBroadcasts() {
		return popularBroadcasts;
	}

	public void setPopularBroadcasts(ArrayList<TVBroadcastWithProgramAndChannelInfo> popularFeed) {
		this.popularBroadcasts = popularFeed;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	public ArrayList<UserLike> getUserLikes() {
		return userLikes;
	}

	public void setUserLikes(ArrayList<UserLike> userLikes) {
		this.userLikes = userLikes;
	}
	
	public HashMap<String, SparseArray<AdzerkAdJSON>> getFragmentToAdsMap() {
		return fragmentToAdsMap;
	}

	public void setFragmentToAdsMap(HashMap<String, SparseArray<AdzerkAdJSON>> mFragmentToAdsMap) {
		this.fragmentToAdsMap = mFragmentToAdsMap;
	}

	public AppVersion getAppVersionData() {
		return appVersionData;
	}

	public void setAppVersionData(AppVersion appVersionData) {
		this.appVersionData = appVersionData;
	}

	public AppConfigurationJSON getAppConfigData() {
		return appConfigData;
	}

	public void setAppConfigData(AppConfigurationJSON appConfigData) {
		this.appConfigData = appConfigData;
	}
	
	public void clearUserToken() {
		setUserToken(null);
	}
	
	public void clearTVChannelIdsUser() {
		tvChannelIdsUser.clear();
	}
	
	public void useDefaultChannelIds() {
		this.tvChannelIdsUsed = tvChannelIdsDefault;
	}

	public void setTvChannelIdsDefault(ArrayList<TVChannelId> tvChannelIdsDefault) {
		this.tvChannelIdsDefault = tvChannelIdsDefault;
		setTvChannelIdsUsed(tvChannelIdsDefault);
	}

	public void setTvChannelIdsUser(ArrayList<TVChannelId> tvChannelIdsUser) {
		this.tvChannelIdsUser = tvChannelIdsUser;
		setTvChannelIdsUsed(tvChannelIdsDefault);
	}

	public ArrayList<TVChannelId> getTvChannelIdsUsed() {
		return tvChannelIdsUsed;
	}

	public void setTvChannelIdsUsed(ArrayList<TVChannelId> tvChannelIdsUsed) {
		this.tvChannelIdsUsed = tvChannelIdsUsed;
	}
	
	public TVChannelGuide getTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) {
		TVDate selectedTVDate = getTvDateSelected();
		return getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
	}
	
	public TVChannelGuide getTVChannelGuideUsingTVChannelIdAndTVDate(TVChannelId tvChannelId, TVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		ArrayList<TVChannelGuide> tvChannelGuides = tvGuide.getTvChannelGuides();
		
		TVChannelGuide tvChannelGuideFound = null;
		
		for(TVChannelGuide tvChannelGuide : tvChannelGuides) {
			if(tvChannelGuide.getChannelId().getChannelId().equals(tvChannelId.getChannelId())) {
				tvChannelGuideFound = tvChannelGuide;
				break;
			}
		}
		
		return tvChannelGuideFound;
	}
	
	public void setTaggedBroadcastsForAllDays(HashMap<String, ArrayList<TVBroadcast>> taggedBroadcastsForAllDays) {
		this.taggedBroadcastsForAllDays = taggedBroadcastsForAllDays;
	}
	
	public HashMap<String, ArrayList<TVBroadcast>> getTaggedBroadcastsForAllDays() {
		return taggedBroadcastsForAllDays;
	}
	
	public void addTaggedBroadcastsForDay(ArrayList<TVBroadcast> taggedBroadcastForDay, TVDate tvDate) {
		if(taggedBroadcastsForAllDays == null) {
			taggedBroadcastsForAllDays = new HashMap<String, ArrayList<TVBroadcast>>();
		}
		taggedBroadcastsForAllDays.put(tvDate.getId(), taggedBroadcastForDay);
	}
	
	public ArrayList<TVBroadcast> getTaggedBroadcastsUsingTVDate(TVDate tvDateAsKey) {
		ArrayList<TVBroadcast> taggedBroadcastForDay = taggedBroadcastsForAllDays.get(tvDateAsKey.getId());
		return taggedBroadcastForDay;
	}
	
	
	public boolean containsTVDates() {
		boolean containsTVDates = (tvDates != null && !tvDates.isEmpty());
		return containsTVDates;
	}
	
	public boolean containsTVTags() {
		boolean containsTVTags = (tvTags != null && !tvTags.isEmpty());
		return containsTVTags;
	}
	
	public boolean containsTVChannels() {
		boolean containsTVChannels = (tvChannels != null && !tvChannels.isEmpty());
		return containsTVChannels;
	}
	
	public boolean containsTVGuideForTVDate(TVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		boolean containsTVGuideForTVDate = (tvGuide != null);
		return containsTVGuideForTVDate;
	}
	
	public boolean containsTaggedBroadcastsForTVDate(TVDate tvDate) {
		boolean containsTaggedBroadcastsForTVDate = taggedBroadcastsForAllDays.containsKey(tvDate.getId());
		return containsTaggedBroadcastsForTVDate;
	}
	
	public boolean containsActivityFeedData() {
		boolean containsActivityFeedData = (activityFeed != null && !activityFeed.isEmpty());
		return containsActivityFeedData;
	}
	
	public boolean containsPopularBroadcasts() {
		boolean containsPopularBroadcasts = (popularBroadcasts != null && !popularBroadcasts.isEmpty());
		return containsPopularBroadcasts;
	}
	
	public boolean containsUserLikes() {
		boolean containsUserLikes = (userLikes != null && !userLikes.isEmpty());
		return containsUserLikes;
	}

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelected
	 */
	public void setTvDateSelected(TVDate tvDateSelected) {
		this.tvDateSelected = tvDateSelected;
	}
	
	public TVDate getTvDateSelected() {
		return tvDateSelected;
	}
	
	public void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
		TVDate tvDateSelected = tvDates.get(tvDateSelectedIndex);
		setTvDateSelected(tvDateSelected);
	}
	
	/* NON PERSISTENT USER DATA */
	/**
	 * Non-persistent
	 */
	public Integer getNonPersistentSelectedHour() {
		return nonPersistentSelectedHour;
	}
	
	public void setNonPersistentSelectedHour(Integer seletectedHour) {
		this.nonPersistentSelectedHour = seletectedHour;
	}
	
	public void setNonPersistentDataUpcomingBroadcast(ArrayList<TVBroadcast> nonPersistentUpcomingBroadcasts) {
		this.nonPersistentUpcomingBroadcasts = nonPersistentUpcomingBroadcasts;
	}
	
	public void setNonPersistentDataRepeatingBroadcast(ArrayList<TVBroadcast> nonPersistentRepeatingBroadcasts) {
		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
	}
	
	public ArrayList<TVBroadcast> getNonPersistentDataRepeatingBroadcast() {
		return nonPersistentRepeatingBroadcasts;
	}
	
	public ArrayList<TVBroadcast> getNonPersistentDataUpcomingBroadcast() {
		return nonPersistentUpcomingBroadcasts;
	}
	
	public void setNonPersistentSelectedBroadcast(TVBroadcast runningBroadcast) {
		this.nonPersistentSelectedBroadcast = runningBroadcast;
	}
	
	public TVBroadcast getNonPersistentSelectedBroadcast() {
		return nonPersistentSelectedBroadcast;
	}
	
	public void setNonPersistentTVChannelId(TVChannelId tvChannelId) {
		this.nonPersistentSelectedTVChannelId = tvChannelId;
	}
	
	public TVChannelId getNonPersistentTVChannelId() {
		return nonPersistentSelectedTVChannelId;
	}
}
