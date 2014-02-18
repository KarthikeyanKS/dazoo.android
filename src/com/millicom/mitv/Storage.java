package com.millicom.mitv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.gson.AppConfigurationData;
import com.millicom.mitv.models.gson.AppVersionData;
import com.millicom.mitv.models.gson.TVChannelId;
import com.mitv.model.OldAdzerkAd;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldTVChannel;
import com.mitv.model.OldTVDate;
import com.mitv.model.OldTVFeedItem;
import com.mitv.model.OldTVTag;

public class Storage {
	private ArrayList<OldTVTag> tvTags;
	private ArrayList<OldTVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIdsDefault;
	private ArrayList<TVChannelId> tvChannelIdsUser;
	private ArrayList<TVChannelId> tvChannelIdsUsed;
	private ArrayList<OldTVChannel> tvChannels;
	private HashMap<String, TVGuide> tvGuides; /* Key is the id from the TVDate */

	private ArrayList<String> likeIds;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<OldTVFeedItem> activityFeed;
	private ArrayList<OldBroadcast> popularFeed;
	
	private String userToken;
	private OldTVDate tvDateSelected;
	
	private AppVersionData appVersionData;
	private AppConfigurationData appConfigData;
		
	/* Ads */
	private HashMap<String, SparseArray<OldAdzerkAd>> fragmentToAdsMap;
	
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
	
	public void addTVGuide(OldTVDate tvDate, TVGuide tvGuide) {
		this.tvGuides.put(tvDate.getId(), tvGuide);
	}
	
	public TVGuide getTVGuideUsingTVDate(OldTVDate tvDate) {
		TVGuide tvGuide = tvGuides.get(tvDate.getId());
		return tvGuide;
	}
	
	public TVGuide getTVGuideForToday() {
		OldTVDate tvDate = tvDates.get(0);
		TVGuide tvGuide = tvGuides.get(tvDate.getId());
		return tvGuide;
	}

	public ArrayList<OldTVTag> getTvTags() {
		return tvTags;
	}

	public void setTvTags(ArrayList<OldTVTag> tvTags) {
		this.tvTags = tvTags;
	}

	public ArrayList<OldTVDate> getTvDates() {
		return tvDates;
	}

	public void setTvDates(ArrayList<OldTVDate> tvDates) {
		this.tvDates = tvDates;
	}

	public ArrayList<OldTVChannel> getTvChannels() {
		return tvChannels;
	}

	public void setTvChannels(ArrayList<OldTVChannel> tvChannels) {
		this.tvChannels = tvChannels;
	}

	public ArrayList<String> getLikeIds() {
		return likeIds;
	}

	public void setLikeIds(ArrayList<String> likeIds) {
		this.likeIds = likeIds;
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

	public ArrayList<OldTVFeedItem> getActivityFeed() {
		return activityFeed;
	}

	public void setActivityFeed(ArrayList<OldTVFeedItem> activityFeed) {
		this.activityFeed = activityFeed;
	}
	
	public void addMoreActivityFeedItems(ArrayList<OldTVFeedItem> additionalActivityFeedItems) {
		if(this.activityFeed == null) {
			activityFeed = new ArrayList<OldTVFeedItem>();
		}
		activityFeed.addAll(additionalActivityFeedItems);
	}

	public ArrayList<OldBroadcast> getPopularFeed() {
		return popularFeed;
	}

	public void setPopularFeed(ArrayList<OldBroadcast> popularFeed) {
		this.popularFeed = popularFeed;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public HashMap<String, SparseArray<OldAdzerkAd>> getFragmentToAdsMap() {
		return fragmentToAdsMap;
	}

	public void setFragmentToAdsMap(HashMap<String, SparseArray<OldAdzerkAd>> mFragmentToAdsMap) {
		this.fragmentToAdsMap = mFragmentToAdsMap;
	}

	public AppVersionData getAppVersionData() {
		return appVersionData;
	}

	public void setAppVersionData(AppVersionData appVersionData) {
		this.appVersionData = appVersionData;
	}

	public AppConfigurationData getAppConfigData() {
		return appConfigData;
	}

	public void setAppConfigData(AppConfigurationData appConfigData) {
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
	
	public boolean containsTVGuideForTVDate(OldTVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		boolean containsTVGuideForTVDate = (tvGuide != null);
		return containsTVGuideForTVDate;
	}
	
	public boolean containsActivityFeedData() {
		boolean containsActivityFeedData = (activityFeed != null && !activityFeed.isEmpty());
		return containsActivityFeedData;
	}
	
	public OldTVDate getTvDateSelected() {
		return tvDateSelected;
	}

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelected
	 */
	public void setTvDateSelected(OldTVDate tvDateSelected) {
		this.tvDateSelected = tvDateSelected;
	}
	
	public void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
		OldTVDate tvDateSelected = tvDates.get(tvDateSelectedIndex);
		setTvDateSelected(tvDateSelected);
	}
}
