
package com.millicom.mitv;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;

import com.millicom.mitv.models.AppConfiguration;
import com.millicom.mitv.models.AppVersion;
import com.millicom.mitv.models.RepeatingBroadcastsForBroadcast;
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



public class Storage 
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
//	private ArrayList<TVBroadcastWithChannelInfo> nonPersistentUpcomingBroadcasts;
//	private ArrayList<TVBroadcastWithChannelInfo> nonPersistentRepeatingBroadcasts;
	private UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts;
	private RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts;
	private Integer nonPersistentSelectedHour;
	private TVChannelId nonPersistentSelectedTVChannelId;
		
	/* Should only be used by the ContentManager */
	public Storage() {
		this.tvGuides = new HashMap<String, TVGuide>();
		
		/* Default selected day to 0 */
		setTvDateSelectedIndex(0);
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

	public void setUserData(UserLoginData userData) {
		this.userData = userData;
	} 
	
	public ArrayList<UserLike> getUserLikes() {
		return userLikes;
	}

	public void setUserLikes(ArrayList<UserLike> userLikes) {
		this.userLikes = userLikes;
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
	
	public boolean containsUpcomingBroadcastsForBroadcast(TVBroadcastWithChannelInfo broadcast) {
		boolean containsUpcomingBroadcastsForBroadcast = false;
		
		if(nonPersistentUpcomingBroadcasts != null) {
			TVBroadcastWithChannelInfo broadcastForUpcomingBroadcastsInStorage = nonPersistentUpcomingBroadcasts.getBroadcast();
			containsUpcomingBroadcastsForBroadcast = broadcast.equals(broadcastForUpcomingBroadcastsInStorage);
		}
		
		return containsUpcomingBroadcastsForBroadcast;
	}

	public RepeatingBroadcastsForBroadcast getNonPersistentRepeatingBroadcasts() {
		return nonPersistentRepeatingBroadcasts;
	}
		
	public void setNonPersistentRepeatingBroadcasts(RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts) {
		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
	}
	
	public boolean containsRepeatingBroadcastsForBroadcast(TVBroadcastWithChannelInfo broadcast) {
		boolean containsRepeatingBroadcastsForBroadcast = false;
		
		if(nonPersistentRepeatingBroadcasts != null) {
			TVBroadcastWithChannelInfo broadcastForRepeatingBroadcastsInStorage = nonPersistentRepeatingBroadcasts.getBroadcast();
			containsRepeatingBroadcastsForBroadcast = broadcast.equals(broadcastForRepeatingBroadcastsInStorage);
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
