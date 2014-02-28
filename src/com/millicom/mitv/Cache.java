
package com.millicom.mitv;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.millicom.mitv.models.AppConfiguration;
import com.millicom.mitv.models.AppVersion;
import com.millicom.mitv.models.RepeatingBroadcastsForBroadcast;
import com.millicom.mitv.models.SearchResultsForQuery;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVFeedItem;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVTag;
import com.millicom.mitv.models.UpcomingBroadcastsForBroadcast;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.UserLoginData;
import com.millicom.mitv.models.gson.AdAdzerkJSON;



public class Cache 
{
	private ArrayList<TVTag> tvTags;
	private ArrayList<TVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIdsDefault;
	private ArrayList<TVChannelId> tvChannelIdsUser;
	
	/* We only need to use this variable, and not "tvChannelIdsDefault" or "tvChannelIdsUser",
	 * "tvChannelIdsUsed" will hold either of those variables above mentioned. When you login/logout
	 * the data held by this variable will change between those two. */
	private ArrayList<TVChannelId> tvChannelIdsUsed;
	
	/* Should contain ALL channels provided by the backend, some hundreds or so */
	private ArrayList<TVChannel> tvChannels;
	
	/* Key is the id from the TVDate */
	private HashMap<String, TVGuide> tvGuides;
	
	/* Key for the wrapping Map, wMap, is the id from the TVDate, which gets you an inner Map, iMap, which key is a TVTag
	 * The value stored in iMap is a list of "tagged" broadcasts for the TVTag provided as key to iMap. 
	 * E.g.
	 * 
	 * wMap:
	 * {"2014-02-20" = iMap1,
	 * "2014-02-21" = iMap2}
	 * 
	 * WHERE:
	 * iMap1:
	 * {"MOVIE" = [BroadcastA, BroadcastB],
	 * "SPORT" = [BroadcastX, BroadcastY]} 
	 * 
	 * */
	private HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> taggedBroadcastsForAllDays;
	
	private ArrayList<UserLike> userLikes;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<TVFeedItem> activityFeed;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;
	
	private UserLoginData userData;
	private int tvDateSelectedIndex;
	
	private AppVersion appVersionData;
	private AppConfiguration appConfigData;
		
	/* Ads */
	private HashMap<String, SparseArray<AdAdzerkJSON>> fragmentToAdsMap;
	
	/* NON-PERSISTENT USER DATA, USED FOR PASSING DATA BETWEEN ACTIVITIES */
	private TVBroadcastWithChannelInfo nonPersistentSelectedBroadcastWithChannelInfo;
	private UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts;
	private RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts;
	private SearchResultsForQuery nonPersistentSearchResultsForQuery;
	private Integer nonPersistentSelectedHour;
	private TVChannelId nonPersistentSelectedTVChannelId;
		
	/* Should only be used by the ContentManager */
	public Cache() {
		this.tvGuides = new HashMap<String, TVGuide>();
		
		/* Default selected day to 0 */
		setTvDateSelectedIndex(0);
	}
	
	
	
	public SearchResultsForQuery getNonPersistentSearchResultsForQuery() {
		return nonPersistentSearchResultsForQuery;
	}

	public void setNonPersistentSearchResultsForQuery(SearchResultsForQuery nonPersistentSearchResultsForQuery) {
		this.nonPersistentSearchResultsForQuery = nonPersistentSearchResultsForQuery;
	}

	public boolean containsSearchResultForQuery(String searchQuery) {
		boolean containsSearchResultForQuery = false;
		if(nonPersistentSearchResultsForQuery != null) {
			String queryUsedForResultsInStorage = nonPersistentSearchResultsForQuery.getQueryString();
			if(queryUsedForResultsInStorage.equals(searchQuery)) {
				containsSearchResultForQuery = true;
			}
		}
		return containsSearchResultForQuery;
	}
	
	public boolean containsTVBroadcastWithChannelInfo(TVChannelId channelId, long beginTimeMillis) {
		boolean containsTVBroadcastWithChannelInfo = false;
		if(nonPersistentSelectedBroadcastWithChannelInfo != null && nonPersistentSelectedBroadcastWithChannelInfo.getChannel().getChannelId().equals(channelId.getChannelId()) && beginTimeMillis == nonPersistentSelectedBroadcastWithChannelInfo.getBeginTimeMillis().longValue()) {
			containsTVBroadcastWithChannelInfo = true;
		}
		return containsTVBroadcastWithChannelInfo;
	}
	
	public String getWelcomeMessage() {
		String welcomeMessage = "";
		if(appConfigData != null) {
			welcomeMessage = appConfigData.getWelcomeToast();
		}
		return welcomeMessage;
	}
		
	public boolean isLoggedIn() {
		boolean isLoggedIn = !TextUtils.isEmpty(getUserToken());
		return isLoggedIn;
	}
	
	public HashMap<String, TVGuide> getTvGuides() {
		return tvGuides;
	}
	
	public void addTVGuideForSelectedDay(TVGuide tvGuide) {
		TVDate tvDate = getTvDateSelected();
		addTVGuide(tvDate, tvGuide);
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
	public TVChannel getTVChannelById(TVChannelId tvChannelId) 
	{
		for(TVChannel tvChannel : tvChannels) 
		{
			if(tvChannel.getChannelId().equals(tvChannelId))
			{
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
	
	
	
	public void addMoreActivityFeedItems(ArrayList<TVFeedItem> additionalActivityFeedItems) 
	{
		if(this.activityFeed == null)
		{
			activityFeed = new ArrayList<TVFeedItem>();
		}
		
		activityFeed.addAll(additionalActivityFeedItems);
	}

	
	
	public ArrayList<TVBroadcastWithChannelInfo> getPopularBroadcasts() {
		return popularBroadcasts;
	}

	public void setPopularBroadcasts(ArrayList<TVBroadcastWithChannelInfo> popularFeed) {
		this.popularBroadcasts = popularFeed;
	}

	public String getUserToken() 
	{
		String userToken = null;
		
		if(userData != null) 
		{
			userToken = userData.getToken();
		}
		
		return userToken;
	}
	
	public String getUserLastname() {
		return userData.getUser().getLastName();
	}
	
	public String getUserFirstname() {
		return userData.getUser().getFirstName();
	}
	
	public String getUserEmail() {
		return userData.getUser().getEmail();
	}
	
	public String getUserId() {
		return userData.getUser().getUserId();
	}
	
	// TODO NewArc - This is a dummy method to obtain the user avatar image url
	public String getUserAvatarImageURL() 
	{
		return "http://wiseheartdesign.com/images/articles/default-avatar.png";
	}

	public void setUserData(UserLoginData userData) {
		this.userData = userData;
	} 
	
	public ArrayList<UserLike> getUserLikes() 
	{
		return userLikes;
	}

	public void setUserLikes(ArrayList<UserLike> userLikes) 
	{
		this.userLikes = userLikes;
	}
	
	public void addUserLike(UserLike userLike) 
	{
		this.userLikes.add(userLike);
	}
	
	public void removeUserLike(UserLike userLikeToRemove) 
	{
		int indexToRemove = -1;
		
		for(int i=0; i<userLikes.size(); i++)
		{
			UserLike userLike = userLikes.get(i);
			
			if(userLike.getLikeType() == userLikeToRemove.getLikeType() &&
			   userLike.getTitle().equals(userLikeToRemove.getTitle()))
			{
				indexToRemove = i;
				break;
			}
		}
		
		if(indexToRemove >= 0)
		{
			this.userLikes.remove(indexToRemove);
		}
	}
	
	
	
	public boolean isInUserLikes(UserLike userLikeToCheck) 
	{
		boolean isContained = false;
		
		for(int i=0; i<userLikes.size(); i++)
		{
			UserLike userLike = userLikes.get(i);
			
			boolean isEqual = userLike.isEqual(userLikeToCheck);
			
			if(isEqual)
			{
				isContained = true;
				break;
			}
		}
		
		return isContained;
	}
	
	
	
	public HashMap<String, SparseArray<AdAdzerkJSON>> getFragmentToAdsMap() {
		return fragmentToAdsMap;
	}

	public void setFragmentToAdsMap(HashMap<String, SparseArray<AdAdzerkJSON>> mFragmentToAdsMap) {
		this.fragmentToAdsMap = mFragmentToAdsMap;
	}

	public AppVersion getAppVersionData() {
		return appVersionData;
	}

	public void setAppVersionData(AppVersion appVersionData) {
		this.appVersionData = appVersionData;
	}

	public AppConfiguration getAppConfigData() {
		return appConfigData;
	}
	
	public int getFirstHourOfTVDay() {
		/* Default to value of 6 */
		int firstHourOfTVDay = 6;
		if(appConfigData != null) {
			firstHourOfTVDay = appConfigData.getFirstHourOfDay();
		}
		return firstHourOfTVDay;
	}

	public void setAppConfigData(AppConfiguration appConfigData) {
		this.appConfigData = appConfigData;
	}
	
	public void clearUserData() {
		userData = null;
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
		setTvChannelIdsUsed(tvChannelIdsUser);
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
		
	public HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> getTaggedBroadcastsForAllDays() {
		return taggedBroadcastsForAllDays;
	}
	
	public void addTaggedBroadcastsForSelectedDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay) {
		TVDate tvDate = getTvDateSelected();
		addTaggedBroadcastsForDay(taggedBroadcastForDay, tvDate);
	}
	
	public void addTaggedBroadcastsForDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay, TVDate tvDate) {
		if(taggedBroadcastsForAllDays == null) {
			taggedBroadcastsForAllDays = new HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>>();
		}
		taggedBroadcastsForAllDays.put(tvDate.getId(), taggedBroadcastForDay);
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsUsingTVDate(TVDate tvDateAsKey) {
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = taggedBroadcastsForAllDays.get(tvDateAsKey.getId());
		return taggedBroadcastForDay;
	}
	
	public boolean containsAppConfigData() {
		boolean containsAppConfig = (appConfigData != null);
		return containsAppConfig;
	}
	
	public boolean containsApiVersionData() {
		boolean containsApiVersionData = (appVersionData != null);
		return containsApiVersionData;
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
	
	public boolean containsTVChannelIdsUser()
	{
		boolean containsTVChannelIdsUser = (tvChannelIdsUser != null && !tvChannelIdsUser.isEmpty());
		
		return containsTVChannelIdsUser;
	}
	
	public boolean containsTVGuideForSelectedDay() {
		TVDate tvDate = getTvDateSelected();
		boolean containsTVGuideForSelectedDay = false;
		if(tvDate != null) {
			containsTVGuideForSelectedDay = containsTVGuideForTVDate(tvDate);
		}
		return containsTVGuideForSelectedDay;
	}
	
	public boolean containsTVGuideForTVDate(TVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		boolean containsTVGuideForTVDate = (tvGuide != null);
		return containsTVGuideForTVDate;
	}
	
	public boolean containsTaggedBroadcastsForTVDate(TVDate tvDate) {
		boolean containsTaggedBroadcastsForTVDate = false;
		if(taggedBroadcastsForAllDays != null) {
			containsTaggedBroadcastsForTVDate = taggedBroadcastsForAllDays.containsKey(tvDate.getId());
		}
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
	 * @param tvDateSelectedIndex
	 */
	public void setTvDateSelectedIndex(int tvDateSelectedIndex) {
		this.tvDateSelectedIndex = tvDateSelectedIndex;
	}
	
	public TVDate getTvDateSelected() {
		TVDate tvDateSelected = null;
		if(tvDates != null) {
			tvDateSelected = tvDates.get(tvDateSelectedIndex);
		}
		return tvDateSelected;
	}
	
	public int getTvDateSelectedIndex() {
		return tvDateSelectedIndex;
	}
	
	public void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
		setTvDateSelectedIndex(tvDateSelectedIndex);
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
	
//	public void setNonPersistentDataUpcomingBroadcast(ArrayList<TVBroadcastWithChannelInfo> nonPersistentUpcomingBroadcasts) {
//		this.nonPersistentUpcomingBroadcasts = nonPersistentUpcomingBroadcasts;
//	}
//	
//	public void setNonPersistentDataRepeatingBroadcast(ArrayList<TVBroadcastWithChannelInfo> nonPersistentRepeatingBroadcasts) {
//		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
//	}
//	
//	public ArrayList<TVBroadcastWithChannelInfo> getNonPersistentDataRepeatingBroadcast() {
//		return nonPersistentRepeatingBroadcasts;
//	}
//	
//	public ArrayList<TVBroadcastWithChannelInfo> getNonPersistentDataUpcomingBroadcast() {
//		return nonPersistentUpcomingBroadcasts;
//	}
	
	
		
	public void setNonPersistentSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo nonPersistentSelectedBroadcastWithChannelInfo) {
		this.nonPersistentSelectedBroadcastWithChannelInfo = nonPersistentSelectedBroadcastWithChannelInfo;
	}
	
	public UpcomingBroadcastsForBroadcast getNonPersistentUpcomingBroadcasts() {
		return nonPersistentUpcomingBroadcasts;
	}

	public void setNonPersistentUpcomingBroadcasts(UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts) {
		this.nonPersistentUpcomingBroadcasts = nonPersistentUpcomingBroadcasts;
	}
	
	public boolean containsUpcomingBroadcastsForBroadcast(String tvSeriesId) {
		boolean containsUpcomingBroadcastsForBroadcast = false;
		
		if(nonPersistentUpcomingBroadcasts != null) {
			String tvSeriesIdInStorage = nonPersistentUpcomingBroadcasts.getTvSeriesId();
			containsUpcomingBroadcastsForBroadcast = tvSeriesId.equals(tvSeriesIdInStorage);
		}
		
		return containsUpcomingBroadcastsForBroadcast;
	}

	public RepeatingBroadcastsForBroadcast getNonPersistentRepeatingBroadcasts() {
		return nonPersistentRepeatingBroadcasts;
	}
		
	public void setNonPersistentRepeatingBroadcasts(RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts) {
		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
	}
	
	public boolean containsRepeatingBroadcastsForBroadcast(String programId) {
		boolean containsRepeatingBroadcastsForBroadcast = false;
		
		if(nonPersistentRepeatingBroadcasts != null) {
			String programIdInStorage = nonPersistentRepeatingBroadcasts.getProgramId();
			containsRepeatingBroadcastsForBroadcast = programId.equals(programIdInStorage);
		}
		
		return containsRepeatingBroadcastsForBroadcast;
	}

	public TVBroadcastWithChannelInfo getNonPersistentSelectedBroadcastWithChannelInfo() {
		return nonPersistentSelectedBroadcastWithChannelInfo;
	}
	
	public void setNonPersistentTVChannelId(TVChannelId tvChannelId) {
		this.nonPersistentSelectedTVChannelId = tvChannelId;
	}
	
	public TVChannelId getNonPersistentTVChannelId() {
		return nonPersistentSelectedTVChannelId;
	}
}
