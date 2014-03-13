
package com.mitv;



import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import com.mitv.models.orm.UserLoginDataORM;
import com.mitv.models.orm.base.AbstractOrmLiteClass;



public class Cache 
{
	@SuppressWarnings("unused")
	private static final String TAG = Cache.class.getName();
	
	
	/* We only need to use this variable, and not "tvChannelIdsDefault" or "tvChannelIdsUser",
	 * "tvChannelIdsUsed" will hold either of those variables above mentioned. When you login/logout
	 * the data held by this variable will change between those two. */
	private ArrayList<TVChannelId> tvChannelIdsUsed;
	
	/* Should contain ALL channels provided by the backend, some hundreds or so */
	private ArrayList<TVChannel> tvChannels;
	
	/* Maps a day to a TVGuide, G, using the id of the TVDate as key. G may contain TVChannelGuides for TVChannels that the user have removed from her channel list */
	private HashMap<String, TVGuide> tvGuidesAll;
	
	/* This map has the same structure as 'tvGuidesAll' but it only contains the guides that should be presented to the user */
	private HashMap<String, TVGuide> tvGuidesMy;
	
	/* PERSISTENT USER DATA, WILL BE SAVED TO STORAGE ON EACH SET */
	private UserLoginData userData;
	private ArrayList<UserLike> userLikes;
	
	private AppVersion appVersionData;
	private AppConfiguration appConfigData;
	
	private ArrayList<TVTag> tvTags;
	private ArrayList<TVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIdsDefault;
	private ArrayList<TVChannelId> tvChannelIdsUser;
	
	private ArrayList<TVFeedItem> activityFeed;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;
	
	
	
	
	/* NON-PERSISTENT USER DATA, USED FOR PASSING DATA BETWEEN ACTIVITIES */
	
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
	private HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> nonPersistentTaggedBroadcastsForAllDays;
	
	private int nonPersistentTVDateSelectedIndex;
	private boolean nonPersistentFlagUpdatingGuide;
	private TVBroadcastWithChannelInfo nonPersistentSelectedBroadcastWithChannelInfo;
	private UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts;
	private RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts;
	private SearchResultsForQuery nonPersistentSearchResultsForQuery;
	private Integer nonPersistentSelectedHour;
	private TVChannelId nonPersistentSelectedTVChannelId;
	private Class<?> nonPersistentReturnActivity;
	private UserLike nonPersistentLikeToAddAfterLogin;
		
	
	
	/* Should only be used by the ContentManager */
	public Cache()
	{
		this.nonPersistentFlagUpdatingGuide = false;
		
		this.tvGuidesAll = new HashMap<String, TVGuide>();
		this.userLikes = new ArrayList<UserLike>();
		
		this.appVersionData = null;
		this.appConfigData = null;
		
		/* Default selected day to 0 */
		setTvDateSelectedIndex(0);
		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		AbstractOrmLiteClass.initDB(context, "millicom.db", 1, null);
		
		userData = UserLoginDataORM.getUserLoginData();
	}
	
	
	
	public void clearAllPersistentCacheData()
	{
		// TODO NewArc - Implement this
	}
	
	
	
	public boolean isUpdatingGuide() {
		return nonPersistentFlagUpdatingGuide;
	}


	public void setUpdatingGuide(boolean nonPersistentFlagUpdatingGuide) {
		this.nonPersistentFlagUpdatingGuide = nonPersistentFlagUpdatingGuide;
	}


	public UserLike getLikeToAddAfterLogin() {
		return nonPersistentLikeToAddAfterLogin;
	}

	public void setLikeToAddAfterLogin(UserLike likeToAddAfterLogin) {
		this.nonPersistentLikeToAddAfterLogin = likeToAddAfterLogin;
	}

	public Class<?> getReturnActivity() {
		return nonPersistentReturnActivity;
	}

	public void setReturnActivity(Class<?> returnActivity) {
		this.nonPersistentReturnActivity = returnActivity;
	}
	
	public void clearReturnActivity() {
		setReturnActivity(null);
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
	
	public synchronized void addNewTVChannelGuidesForSelectedDayUsingTvGuide(TVGuide tvGuide) {
		TVDate tvDate = getTvDateSelected();
		addNewTVChannelGuidesUsingDayAndTvGuide(tvDate, tvGuide);
	}
	
	public synchronized void addNewTVChannelGuidesUsingDayAndTvGuide(TVDate tvDate, TVGuide tvGuide) {
		clearTVGuidesMy();
		
		TVGuide guideForAllChannels = getTVGuideUsingTVDateNonFiltered(tvDate);
		
		if(guideForAllChannels != null) {
			/* Maybe unnecessary safety check to verify that the guides are for the same day */
			if(tvGuide.getTvDate().equals(guideForAllChannels.getTvDate())) {
				ArrayList<TVChannelGuide> allChannelGuides = guideForAllChannels.getTvChannelGuides();
				ArrayList<TVChannelGuide> newChannelGuides = tvGuide.getTvChannelGuides();
				
				/* Add all the new TVChannel Guides to the list, results in duplicates sometimes */
				//TODO NewArc fix duplicates problem, happens when logged in then log out and fetching default channels
				allChannelGuides.addAll(newChannelGuides);
				
				ArrayList<TVChannelGuide> allChannelGuidesWithoutDuplicates = new ArrayList<TVChannelGuide>();
				for(TVChannelGuide channelGuide : allChannelGuides) {
					if(!allChannelGuidesWithoutDuplicates.contains(channelGuide)) {
						allChannelGuidesWithoutDuplicates.add(channelGuide);
					} else {
						Log.d(TAG, "Duplicate");
					}
				}
				guideForAllChannels.setTvChannelGuides(allChannelGuidesWithoutDuplicates);
			} else {
				Log.e(TAG, "TVDate for new guide and existing don't match, but they should");
			}
		} else {
			guideForAllChannels = tvGuide;
		}
		this.tvGuidesAll.put(tvDate.getId(), guideForAllChannels);
	}
	
	private TVGuide getTVGuideUsingTVDateNonFiltered(TVDate tvDate) {
		String tvDateId = tvDate.getId();
		TVGuide guideForAllChannels = tvGuidesAll.get(tvDateId);
		
		return guideForAllChannels;
	}
	
	/**
	 * This method return the TVGuide for specified TVDate, but before returning the TVGuide
	 * the TVChannelGuides for channels that the user has not selected are filtered out
	 * @param tvDate
	 * @return
	 */
	public synchronized TVGuide getTVGuideUsingTVDate(TVDate tvDate) {
		if (tvGuidesMy == null) {
			tvGuidesMy = new HashMap<String, TVGuide>();
		}

		TVGuide guideForWithMyChannels = tvGuidesMy.get(tvDate.getId());

		if (guideForWithMyChannels == null) {

			TVGuide guideForAllChannels = getTVGuideUsingTVDateNonFiltered(tvDate);

			if(guideForAllChannels != null) {
				ArrayList<TVChannelGuide> allChannelGuides = guideForAllChannels.getTvChannelGuides();
				ArrayList<TVChannelGuide> myChannelGuides = new ArrayList<TVChannelGuide>();
	
				for (TVChannelGuide channelGuide : allChannelGuides) {
					if (tvChannelIdsUsed.contains(channelGuide.getChannelId())) {
						myChannelGuides.add(channelGuide);
					}
				}
	
				guideForWithMyChannels = new TVGuide(tvDate, myChannelGuides);
				tvGuidesMy.put(tvDate.getId(), guideForWithMyChannels);
			}
		}

		return guideForWithMyChannels;
	}
	
	public synchronized TVGuide getTVGuideForToday() {
		TVDate tvDate = tvDates.get(0);
		return getTVGuideUsingTVDate(tvDate);
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


	public synchronized ArrayList<TVFeedItem> getActivityFeed() 
	{
		return activityFeed;
	}

	
	public synchronized void setActivityFeed(ArrayList<TVFeedItem> activityFeed) 
	{
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
	
	
	public synchronized String getUserLastname()
	{
		return userData.getUser().getLastName();
	}
	
	
	public synchronized String getUserFirstname()
	{
		return userData.getUser().getFirstName();
	}
	
	
	public synchronized String getUserEmail()
	{
		return userData.getUser().getEmail();
	}
	
	
	public synchronized String getUserId()
	{
		return userData.getUser().getUserId();
	}
	
	
	public synchronized String getUserProfileImageUrl() 
	{
		return userData.getProfileImage().getUrl();
	}

	
	public synchronized void setUserData(UserLoginData userData) 
	{
		this.userData = userData;
		
		UserLoginDataORM userLoginDataORM = new UserLoginDataORM(userData);
		userLoginDataORM.saveInAsyncTask();
	}
	
	
	public synchronized ArrayList<UserLike> getUserLikes() 
	{
		//
		return userLikes;
	}

	
	public synchronized void setUserLikes(ArrayList<UserLike> userLikes) 
	{
		this.userLikes = userLikes;
		
		//
	}
	
	
	public synchronized void addUserLike(UserLike userLike) 
	{
		if(userLikes != null)
		{
			this.userLikes.add(userLike);
		}
		else
		{
			Log.w(TAG, "Attempted to add user like without data in cache.");
		}
	}
	
	
	public synchronized void removeUserLike(UserLike userLikeToRemove) 
	{
		if(userLikes != null)
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
		else
		{
			Log.w(TAG, "Attempted to remove user like without data in cache.");
		}
	}
	
	
	
	public synchronized boolean isInUserLikes(UserLike userLikeToCheck) 
	{
		boolean isContained = false;
		
		if(userLikes != null)
		{
			for(UserLike userLike : userLikes)
			{
				boolean isEqual = userLike.equals(userLikeToCheck);
				
				if(isEqual)
				{
					isContained = true;
					break;
				}
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
	
	public void clearUserData() 
	{
		UserLoginDataORM userLoginDataORM = new UserLoginDataORM(userData);
		userLoginDataORM.delete();
		
		userData = null;
	}
	
	public synchronized void clearTVChannelIdsUser() {
		if(tvChannelIdsUser != null) {
			tvChannelIdsUser.clear();
		}
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
		if(tvChannelIdsUsed == null) {
			if(tvChannelIdsDefault != null) {
				tvChannelIdsUsed = tvChannelIdsDefault;
			} else {
				if(tvChannelIdsUser != null) {
					tvChannelIdsUsed = tvChannelIdsUser;
				}
			}
		}
		return tvChannelIdsUsed;
	}

	public synchronized void setTvChannelIdsUsed(ArrayList<TVChannelId> tvChannelIdsUsed) {
		this.tvChannelIdsUsed = tvChannelIdsUsed;
		
		/* When changing to use another list as TVChannel ids, then we must clear the list of the TVGuides that should be presented to the user
		 * since the guide is dependent on the tv channel ids. */
		clearTVGuidesMy();
	}
	
	private void clearTVGuidesMy() {
		tvGuidesMy = null;
	}
	
	public TVChannelGuide getTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) {
		TVDate selectedTVDate = getTvDateSelected();
		return getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
	}
	
	
	public boolean containsTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		boolean containsTVChannelGuideUsingTVChannelIdForSelectedDay = false;
		
		TVDate selectedTVDate = getTvDateSelected();
		
		if(selectedTVDate != null)
		{
			TVChannelGuide guide = getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
			
			containsTVChannelGuideUsingTVChannelIdForSelectedDay = (guide == null);
		}
		
		return containsTVChannelGuideUsingTVChannelIdForSelectedDay;
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
		return nonPersistentTaggedBroadcastsForAllDays;
	}
	
	public synchronized void addTaggedBroadcastsForSelectedDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay) 
	{
		TVDate tvDate = getTvDateSelected();
		addTaggedBroadcastsForDay(taggedBroadcastForDay, tvDate);
	}
	
	public synchronized void addTaggedBroadcastsForDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay, TVDate tvDate) 
	{
		if(nonPersistentTaggedBroadcastsForAllDays == null) {
			nonPersistentTaggedBroadcastsForAllDays = new HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>>();
		}
		nonPersistentTaggedBroadcastsForAllDays.put(tvDate.getId(), taggedBroadcastForDay);
	}
	
	public synchronized HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsUsingTVDate(TVDate tvDateAsKey) 
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = nonPersistentTaggedBroadcastsForAllDays.get(tvDateAsKey.getId());
		return taggedBroadcastForDay;
	}
	
	public synchronized void purgeTaggedBroadcastForDay(TVDate tvDate) 
	{
		nonPersistentTaggedBroadcastsForAllDays.remove(tvDate.getId());
	}
	
	
	
	public synchronized boolean containsAppConfigData() 
	{
		boolean containsAppConfig = (appConfigData != null);
		return containsAppConfig;
	}
	
	
	
	public synchronized boolean containsAppVersionData() 
	{
		boolean containsAppVersionData = (appVersionData != null);
		
		return containsAppVersionData;
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
	
	public synchronized boolean containsTVGuideForSelectedDay() 
	{
		TVDate tvDate = getTvDateSelected();
		
		boolean containsTVGuideForSelectedDay = false;
		
		if(tvDate != null) 
		{
			containsTVGuideForSelectedDay = containsTVGuideForTVDate(tvDate);
		}
		
		return containsTVGuideForSelectedDay;
	}
	
	/* Verifies that the TVGuide we have (if any) contains a TVChannelGuide for all
	 * tv channels we have in tvChannelIdsUsed */
	public synchronized boolean containsTVGuideForTVDate(TVDate tvDate) {
		boolean containsTVGuideForTVDate = true;
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		
		if(tvGuide != null) {
			ArrayList<TVChannelGuide> channelGuides = tvGuide.getTvChannelGuides();
			
			ArrayList<TVChannelId> channelIdsFromChannelGuides = new ArrayList<TVChannelId>();
			for(TVChannelGuide channelGuide : channelGuides) {
				channelIdsFromChannelGuides.add(channelGuide.getChannelId());
			}
	
			for(TVChannelId tvChannelId : tvChannelIdsUsed) {
				if(!channelIdsFromChannelGuides.contains(tvChannelId)) {
					containsTVGuideForTVDate = false;
					break;
				}
			}
		} else {
			containsTVGuideForTVDate = false;
		}
		
		return containsTVGuideForTVDate;
	}
	
	public synchronized boolean containsTaggedBroadcastsForTVDate(TVDate tvDate) {
		boolean containsTaggedBroadcastsForTVDate = false;
		if(nonPersistentTaggedBroadcastsForAllDays != null) {
			containsTaggedBroadcastsForTVDate = nonPersistentTaggedBroadcastsForAllDays.containsKey(tvDate.getId());
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
		boolean containsUserLikes = (userLikes != null);
		return containsUserLikes;
	}

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelectedIndex
	 */
	public synchronized void setTvDateSelectedIndex(int tvDateSelectedIndex) {
		this.nonPersistentTVDateSelectedIndex = tvDateSelectedIndex;
	}
	
	public synchronized TVDate getTvDateSelected() {
		TVDate tvDateSelected = null;
		if(tvDates != null) {
			tvDateSelected = tvDates.get(nonPersistentTVDateSelectedIndex);
		}
		return tvDateSelected;
	}
	
	public synchronized int getTvDateSelectedIndex() {
		return nonPersistentTVDateSelectedIndex;
	}
	
	public synchronized void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) {
		setTvDateSelectedIndex(tvDateSelectedIndex);
	}
	
	/* NON PERSISTENT USER DATA */
	/**
	 * Non-persistent
	 */
	public synchronized Integer getNonPersistentSelectedHour() {
		return nonPersistentSelectedHour;
	}
	
	public synchronized void setNonPersistentSelectedHour(Integer seletectedHour) {
		this.nonPersistentSelectedHour = seletectedHour;
	}
			
	public synchronized void setNonPersistentSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo nonPersistentSelectedBroadcastWithChannelInfo) {
		this.nonPersistentSelectedBroadcastWithChannelInfo = nonPersistentSelectedBroadcastWithChannelInfo;
	}
	
	public synchronized UpcomingBroadcastsForBroadcast getNonPersistentUpcomingBroadcasts() {
		return nonPersistentUpcomingBroadcasts;
	}

	public synchronized void setNonPersistentUpcomingBroadcasts(UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts) {
		this.nonPersistentUpcomingBroadcasts = nonPersistentUpcomingBroadcasts;
	}
	
	public synchronized boolean containsUpcomingBroadcastsForBroadcast(String tvSeriesId) {
		boolean containsUpcomingBroadcastsForBroadcast = false;
		
		if(nonPersistentUpcomingBroadcasts != null) {
			String tvSeriesIdInStorage = nonPersistentUpcomingBroadcasts.getTvSeriesId();
			containsUpcomingBroadcastsForBroadcast = tvSeriesId.equals(tvSeriesIdInStorage);
		}
		
		return containsUpcomingBroadcastsForBroadcast;
	}

	public synchronized RepeatingBroadcastsForBroadcast getNonPersistentRepeatingBroadcasts() {
		return nonPersistentRepeatingBroadcasts;
	}
		
	public synchronized void setNonPersistentRepeatingBroadcasts(RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts) {
		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
	}
	
	public synchronized boolean containsRepeatingBroadcastsForBroadcast(String programId) {
		boolean containsRepeatingBroadcastsForBroadcast = false;
		
		if(nonPersistentRepeatingBroadcasts != null) {
			String programIdInStorage = nonPersistentRepeatingBroadcasts.getProgramId();
			containsRepeatingBroadcastsForBroadcast = programId.equals(programIdInStorage);
		}
		
		return containsRepeatingBroadcastsForBroadcast;
	}

	public synchronized TVBroadcastWithChannelInfo getNonPersistentSelectedBroadcastWithChannelInfo() {
		return nonPersistentSelectedBroadcastWithChannelInfo;
	}
	
	public synchronized void setNonPersistentTVChannelId(TVChannelId tvChannelId) {
		this.nonPersistentSelectedTVChannelId = tvChannelId;
	}
	
	public synchronized TVChannelId getNonPersistentTVChannelId() {
		return nonPersistentSelectedTVChannelId;
	}
}
