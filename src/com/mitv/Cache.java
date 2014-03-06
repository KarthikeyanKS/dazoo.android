
package com.mitv;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.mitv.listadapters.AdListAdapter;
import com.mitv.models.AppConfiguration;
import com.mitv.models.AppVersion;
import com.mitv.models.RepeatingBroadcastsForBroadcast;
import com.mitv.models.SearchResultsForQuery;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVDate;
import com.mitv.models.TVFeedItem;
import com.mitv.models.TVGuide;
import com.mitv.models.TVTag;
import com.mitv.models.UpcomingBroadcastsForBroadcast;
import com.mitv.models.UserLike;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.AdAdzerkJSON;
import com.mitv.utilities.AppDataUtils;



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
	
	/* This Map is a collection of listAdapters used by HomeActivity in order to save the downloaded ads associated with each listadapter for listviews */
	@SuppressWarnings("rawtypes")
	private HashMap<String, AdListAdapter> adapterMap;
	
	private ArrayList<UserLike> userLikes;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<TVFeedItem> activityFeed;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;
	
	private UserLoginData userData;
	/* Only set when the user logs in from facebook */
	private String userImageURL;
	
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
	public Cache()
	{
		this.tvGuides = new HashMap<String, TVGuide>();
		this.adapterMap = new HashMap<String, AdListAdapter>();
		this.userLikes = new ArrayList<UserLike>();
		
		this.appVersionData = null;
		this.appConfigData = null;
		
		/* Default selected day to 0 */
		setTvDateSelectedIndex(0);
		
		userImageURL = AppDataUtils.getPreference(Constants.SHARED_PREFERENCES_USER_IMAGE_URL, "");
		
		// TODO NewArc - What follows is a very ugly hack for saving the data on permanent storage. Replace with a proper implementation ASAP!
		userData = (UserLoginData) AppDataUtils.loadData(Constants.SHARED_PREFERENCES_USER_DATA);
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
	
	public synchronized boolean containsTVBroadcastWithChannelInfo(TVChannelId channelId, long beginTimeMillis) {
		boolean containsTVBroadcastWithChannelInfo = false;
		if(nonPersistentSelectedBroadcastWithChannelInfo != null && nonPersistentSelectedBroadcastWithChannelInfo.getChannel().getChannelId().equals(channelId.getChannelId()) && beginTimeMillis == nonPersistentSelectedBroadcastWithChannelInfo.getBeginTimeMillis().longValue()) {
			containsTVBroadcastWithChannelInfo = true;
		}
		return containsTVBroadcastWithChannelInfo;
	}
	
	public synchronized String getWelcomeMessage() {
		String welcomeMessage = "";
		if(appConfigData != null) {
			welcomeMessage = appConfigData.getWelcomeToast();
		}
		return welcomeMessage;
	}
		
	public synchronized boolean isLoggedIn() {
		boolean isLoggedIn = !TextUtils.isEmpty(getUserToken());
		
		return isLoggedIn;
	}
	
	public synchronized HashMap<String, TVGuide> getTvGuides() {
		return tvGuides;
	}
	
	public synchronized void addTVGuideForSelectedDay(TVGuide tvGuide) {
		TVDate tvDate = getTvDateSelected();
		addTVGuide(tvDate, tvGuide);
	}
	
	public synchronized void addTVGuide(TVDate tvDate, TVGuide tvGuide) {
		this.tvGuides.put(tvDate.getId(), tvGuide);
	}
	
	public synchronized TVGuide getTVGuideUsingTVDate(TVDate tvDate) {
		String tvDateId = tvDate.getId();
		TVGuide tvGuide = tvGuides.get(tvDateId);
		return tvGuide;
	}
	
	public synchronized TVGuide getTVGuideForToday() {
		TVDate tvDate = tvDates.get(0);
		TVGuide tvGuide = tvGuides.get(tvDate.getId());
		return tvGuide;
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized HashMap<String, AdListAdapter> getAdapterMap() {
		return adapterMap;
	}
	
	public synchronized ArrayList<TVTag> getTvTags() {
		return tvTags;
	}

	public synchronized void setTvTags(ArrayList<TVTag> tvTags) {
		this.tvTags = tvTags;
	}

	public synchronized ArrayList<TVDate> getTvDates() {
		return tvDates;
	}

	public synchronized void setTvDates(ArrayList<TVDate> tvDates) {
		this.tvDates = tvDates;
	}

	public synchronized ArrayList<TVChannel> getTvChannels() {
		return tvChannels;
	}

	public synchronized void setTvChannels(ArrayList<TVChannel> tvChannels) {
		this.tvChannels = tvChannels;
	}
	
	//TODO dont iterate through a list, change tvChannels to a Map instead?
	public synchronized TVChannel getTVChannelById(TVChannelId tvChannelId) 
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

	public synchronized Calendar getLikeIdsFetchedTimestamp() {
		return likeIdsFetchedTimestamp;
	}

	public synchronized void setLikeIdsFetchedTimestamp(Calendar likeIdsFetchedTimestamp) {
		this.likeIdsFetchedTimestamp = likeIdsFetchedTimestamp;
	}

	public synchronized Calendar getUserChannelIdsFetchedTimestamp() {
		return userChannelIdsFetchedTimestamp;
	}

	public synchronized void setUserChannelIdsFetchedTimestamp(Calendar userChannelIdsFetchedTimestamp) {
		this.userChannelIdsFetchedTimestamp = userChannelIdsFetchedTimestamp;
	}

	public synchronized ArrayList<TVFeedItem> getActivityFeed() {
		return activityFeed;
	}

	public synchronized void setActivityFeed(ArrayList<TVFeedItem> activityFeed) {
		this.activityFeed = activityFeed;
	}
	
	
	
	public synchronized void addMoreActivityFeedItems(ArrayList<TVFeedItem> additionalActivityFeedItems) 
	{
		if(this.activityFeed == null)
		{
			activityFeed = new ArrayList<TVFeedItem>();
		}
		
		activityFeed.addAll(additionalActivityFeedItems);
	}

	
	
	public synchronized ArrayList<TVBroadcastWithChannelInfo> getPopularBroadcasts() {
		return popularBroadcasts;
	}

	public synchronized void setPopularBroadcasts(ArrayList<TVBroadcastWithChannelInfo> popularFeed) {
		this.popularBroadcasts = popularFeed;
	}

	public synchronized String getUserToken() 
	{
		String userToken = null;
		
		if(userData != null) 
		{
			userToken = userData.getToken();
		}
		
		return userToken;
	}
	
	public synchronized String getUserLastname() {
		return userData.getUser().getLastName();
	}
	
	public synchronized String getUserFirstname() {
		return userData.getUser().getFirstName();
	}
	
	public synchronized String getUserEmail() {
		return userData.getUser().getEmail();
	}
	
	public synchronized String getUserId() {
		return userData.getUser().getUserId();
	}
	
	
	public synchronized String getUserImageURL() 
	{
		return userImageURL;
	}

	
	public synchronized void setUserData(UserLoginData userData) 
	{
		this.userData = userData;
		
		// TODO NewArc - What follows is a very ugly hack for saving the data on permanent storage. Replace with a proper implementation ASAP!
		final UserLoginData userDataForStorage = this.userData;
		
		// Write to file on different thread to avoid blocking the UI thread
		new Thread(
		new Runnable() 
		{
			public void run() 
			{
				AppDataUtils.saveData(Constants.SHARED_PREFERENCES_USER_DATA, userDataForStorage);
			}
		}).start();
	} 
	
	
	public synchronized void setUserImageURL(String url)
	{
		this.userImageURL = url;
		
		AppDataUtils.setPreference(Constants.SHARED_PREFERENCES_USER_IMAGE_URL, userImageURL);
	}
	
	
	public synchronized ArrayList<UserLike> getUserLikes() 
	{
		return userLikes;
	}

	
	public synchronized void setUserLikes(ArrayList<UserLike> userLikes) 
	{
		this.userLikes = userLikes;
	}
	
	
	public synchronized void addUserLike(UserLike userLike) 
	{
		this.userLikes.add(userLike);
	}
	
	
	public synchronized void removeUserLike(UserLike userLikeToRemove) 
	{
		int indexToRemove = -1;
		
		for(int i=0; i<userLikes.size(); i++)
		{
			UserLike userLike = userLikes.get(i);
			
			if(userLike.equals(userLikeToRemove))
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
	
	
	
	public synchronized boolean isInUserLikes(UserLike userLikeToCheck) 
	{
		boolean isContained = false;
		
		for(UserLike userLike : userLikes)
		{
			boolean isEqual = userLike.equals(userLikeToCheck);
			
			if(isEqual)
			{
				isContained = true;
				break;
			}
		}
		
		return isContained;
	}
	
	
	
	public synchronized boolean isInUsedChannelIds(TVChannelId channelId)
	{
		boolean isContainedInUserChannels = false;
		
		for(TVChannelId channelIdTmp : tvChannelIdsUsed)
		{
			if(channelIdTmp.getChannelId().equals(channelId.getChannelId()))
			{
				isContainedInUserChannels = true;
				break;
			}
		}
		
		return isContainedInUserChannels;
	}
	
	
	/* TODO Should this be synchronized? */
	public HashMap<String, SparseArray<AdAdzerkJSON>> getFragmentToAdsMap() {
		return fragmentToAdsMap;
	}

	/* TODO Should this be synchronized? */
	public void setFragmentToAdsMap(HashMap<String, SparseArray<AdAdzerkJSON>> mFragmentToAdsMap) {
		this.fragmentToAdsMap = mFragmentToAdsMap;
	}

	
	public synchronized AppVersion getAppVersionData() 
	{
		return appVersionData;
	}
	
	
	public synchronized boolean isAPIVersionSupported() 
	{
		boolean isAPIVersionSupported = false;
		
		if(appVersionData != null)
		{
			isAPIVersionSupported = getAppVersionData().isAPIVersionSupported();
		}
		
		return isAPIVersionSupported;
	}

	
	public synchronized void setAppVersionData(AppVersion appVersionData) {
		this.appVersionData = appVersionData;
	}

	public synchronized AppConfiguration getAppConfigData() {
		return appConfigData;
	}
	
	public synchronized int getFirstHourOfTVDay() {
		/* Default to value of 6 */
		int firstHourOfTVDay = 6;
		if(appConfigData != null) {
			firstHourOfTVDay = appConfigData.getFirstHourOfDay();
		}
		return firstHourOfTVDay;
	}

	public synchronized void setAppConfigData(AppConfiguration appConfigData) {
		this.appConfigData = appConfigData;
	}
	
	public void clearUserData() {
		AppDataUtils.clearStorageCompletely();
		userData = null;
	}
	
	public synchronized void clearTVChannelIdsUser() {
		tvChannelIdsUser.clear();
	}
	
	public synchronized void useDefaultChannelIds() {
		this.tvChannelIdsUsed = tvChannelIdsDefault;
	}

	public synchronized void setTvChannelIdsDefault(ArrayList<TVChannelId> tvChannelIdsDefault) {
		this.tvChannelIdsDefault = tvChannelIdsDefault;
		
		/* Only use the default ids if we are not logged in */
		if(!isLoggedIn()) {
			setTvChannelIdsUsed(tvChannelIdsDefault);
		}
	}

	public void setTvChannelIdsUser(ArrayList<TVChannelId> tvChannelIdsUser) {
		this.tvChannelIdsUser = tvChannelIdsUser;
		setTvChannelIdsUsed(tvChannelIdsUser);
	}

	public synchronized ArrayList<TVChannelId> getTvChannelIdsUsed() {
		return tvChannelIdsUsed;
	}

	public synchronized void setTvChannelIdsUsed(ArrayList<TVChannelId> tvChannelIdsUsed) {
		this.tvChannelIdsUsed = tvChannelIdsUsed;
	}
	
	public TVChannelGuide getTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) {
		TVDate selectedTVDate = getTvDateSelected();
		return getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
	}
	
	public synchronized TVChannelGuide getTVChannelGuideUsingTVChannelIdAndTVDate(TVChannelId tvChannelId, TVDate tvDate) {
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
		
	public synchronized HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> getTaggedBroadcastsForAllDays() {
		return taggedBroadcastsForAllDays;
	}
	
	public synchronized void addTaggedBroadcastsForSelectedDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay) 
	{
		TVDate tvDate = getTvDateSelected();
		addTaggedBroadcastsForDay(taggedBroadcastForDay, tvDate);
	}
	
	public synchronized void addTaggedBroadcastsForDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay, TVDate tvDate) 
	{
		if(taggedBroadcastsForAllDays == null) {
			taggedBroadcastsForAllDays = new HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>>();
		}
		taggedBroadcastsForAllDays.put(tvDate.getId(), taggedBroadcastForDay);
	}
	
	public synchronized HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsUsingTVDate(TVDate tvDateAsKey) 
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = taggedBroadcastsForAllDays.get(tvDateAsKey.getId());
		return taggedBroadcastForDay;
	}
	
	
	
	public synchronized boolean containsAppConfigData() 
	{
		boolean containsAppConfig = (appConfigData != null);
		return containsAppConfig;
	}
	
	
	
	public synchronized boolean containsApiVersionData()
	{
		boolean containsApiVersionData = (appVersionData != null);
		return containsApiVersionData;
	}
	
	public boolean containsTVDates() 
	{
		boolean containsTVDates = (tvDates != null && !tvDates.isEmpty());
		return containsTVDates;
	}
	
	public synchronized boolean containsTVTags() {
		boolean containsTVTags = (tvTags != null && !tvTags.isEmpty());
		return containsTVTags;
	}
	
	public synchronized boolean containsTVChannels() {
		boolean containsTVChannels = (tvChannels != null && !tvChannels.isEmpty());
		return containsTVChannels;
	}
	
	public synchronized boolean containsTVChannelIdsUser()
	{
		boolean containsTVChannelIdsUser = (tvChannelIdsUser != null && !tvChannelIdsUser.isEmpty());
		
		return containsTVChannelIdsUser;
	}
	
	public synchronized boolean containsTVGuideForSelectedDay() {
		TVDate tvDate = getTvDateSelected();
		boolean containsTVGuideForSelectedDay = false;
		if(tvDate != null) {
			containsTVGuideForSelectedDay = containsTVGuideForTVDate(tvDate);
		}
		return containsTVGuideForSelectedDay;
	}
	
	public synchronized boolean containsTVGuideForTVDate(TVDate tvDate) {
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		boolean containsTVGuideForTVDate = (tvGuide != null);
		return containsTVGuideForTVDate;
	}
	
	public synchronized boolean containsTaggedBroadcastsForTVDate(TVDate tvDate) {
		boolean containsTaggedBroadcastsForTVDate = false;
		if(taggedBroadcastsForAllDays != null) {
			containsTaggedBroadcastsForTVDate = taggedBroadcastsForAllDays.containsKey(tvDate.getId());
		}
		return containsTaggedBroadcastsForTVDate;
	}
	
	public synchronized boolean containsActivityFeedData() {
		boolean containsActivityFeedData = (activityFeed != null && !activityFeed.isEmpty());
		return containsActivityFeedData;
	}
	
	public synchronized boolean containsPopularBroadcasts() {
		boolean containsPopularBroadcasts = (popularBroadcasts != null && !popularBroadcasts.isEmpty());
		return containsPopularBroadcasts;
	}
	
	public synchronized boolean containsUserLikes() {
		boolean containsUserLikes = (userLikes != null && !userLikes.isEmpty());
		return containsUserLikes;
	}

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelectedIndex
	 */
	public synchronized void setTvDateSelectedIndex(int tvDateSelectedIndex) {
		this.tvDateSelectedIndex = tvDateSelectedIndex;
	}
	
	public synchronized TVDate getTvDateSelected() {
		TVDate tvDateSelected = null;
		if(tvDates != null) {
			tvDateSelected = tvDates.get(tvDateSelectedIndex);
		}
		return tvDateSelected;
	}
	
	public synchronized int getTvDateSelectedIndex() {
		return tvDateSelectedIndex;
	}
	
	public synchronized void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
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
