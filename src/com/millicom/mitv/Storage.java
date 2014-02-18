package com.millicom.mitv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.gson.AppConfigurationData;
import com.millicom.mitv.models.gson.AppVersionData;
import com.millicom.mitv.models.gson.Broadcast;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.model.OldAdzerkAd;
import com.mitv.model.OldTVFeedItem;

public class Storage {
	private ArrayList<TVTag> tvTags;
	private ArrayList<TVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIdsDefault;
	private ArrayList<TVChannelId> tvChannelIdsUser;
	private ArrayList<TVChannelId> tvChannelIdsUsed;
	private ArrayList<TVChannel> tvChannels;
	private HashMap<String, TVGuide> tvGuides; /* Key is the id from the TVDate */

	private ArrayList<String> likeIds;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<OldTVFeedItem> activityFeed;
	private ArrayList<Broadcast> popularFeed;
	
	private String userToken;
	private TVDate tvDateSelected;
	
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

	public ArrayList<Broadcast> getPopularFeed() {
		return popularFeed;
	}

	public void setPopularFeed(ArrayList<Broadcast> popularFeed) {
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
	
	public boolean containsTVGuideForTVDate(TVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		boolean containsTVGuideForTVDate = (tvGuide != null);
		return containsTVGuideForTVDate;
	}
	
	public TVDate getTvDateSelected() {
		return tvDateSelected;
	}

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelected
	 */
	public void setTvDateSelected(TVDate tvDateSelected) {
		this.tvDateSelected = tvDateSelected;
	}
	
	public void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
		TVDate tvDateSelected = tvDates.get(tvDateSelectedIndex);
		setTvDateSelected(tvDateSelected);
	}
}
