
package com.mitv.managers;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.mitv.APIClient;
import com.mitv.Constants;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.Cache;
import com.mitv.models.objects.mitvapi.AppConfiguration;
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.models.objects.mitvapi.TVGuide;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.objects.mitvapi.UpcomingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.RandomNumberUtils;



public abstract class ContentManagerBase
	implements ContentCallbackListener
{
	private static final String TAG = ContentManagerBase.class.getName();
	
	
	private static final int TIME_OFFSET_IN_MINUTES_FOR_NTP_COMPARISSON = 5;
	
	private Cache cache;
	private APIClient apiClient;
	private FetchDataProgressCallbackListener fetchDataProgressCallbackListener;
	
	protected boolean completedTVDatesRequest;
	protected boolean completedTVChannelIdsDefaultRequest;
	protected boolean completedTVChannelIdsUserRequest;
	protected boolean completedTVGuideRequest;
	protected boolean completedTVPopularRequest;
	protected boolean isFetchingTVGuide;
	protected boolean isAPIVersionTooOld;
	protected boolean isUpdatingGuide;
	protected boolean isBuildingTaggedBroadcasts;
	protected boolean isFetchingFeedItems;
	protected boolean isGoingToMyChannelsFromSearch;
	protected Boolean isLocalDeviceCalendarOffSync;
	
	
		
	
	
	public ContentManagerBase()
	{	
		this.cache = new Cache();
		
		this.apiClient = new APIClient(this);
		
		this.isLocalDeviceCalendarOffSync = null;
	}
	
	
	
	protected Cache getCache() 
	{
		if(cache == null) 
		{
			Log.w(TAG, "!!! WARNING !!! Cache in ContentManager is null, this should not be happening => reinitializing it");
			
			cache = new Cache();
		}
		
		return cache;
	}
	
	
	
	protected APIClient getAPIClient() 
	{
		if(apiClient == null) 
		{
			Log.w(TAG, "!!! WARNING !!! APIClient in ContentManager is null, this should not be happening => reinitializing it");
			
			apiClient = new APIClient(this);
		}
		
		return apiClient;
	}
	
	
	
	protected FetchDataProgressCallbackListener getFetchDataProgressCallbackListener()
	{
		return fetchDataProgressCallbackListener;
	}
	
	
	
	public void setFetchDataProgressCallbackListener(FetchDataProgressCallbackListener listener)
	{
		this.fetchDataProgressCallbackListener = listener;
	}
	
	
	
	public void setGoingToMyChannelsFromSearch(boolean isGoingToMyChannelsFromSearch) 
	{
		this.isGoingToMyChannelsFromSearch = isGoingToMyChannelsFromSearch;
	}
	
	
	
	public boolean isGoingToMyChannelsFromSearch() 
	{
		return isGoingToMyChannelsFromSearch;
	}
	
	
	
	public boolean isUpdatingGuide() 
	{
		return isUpdatingGuide;
	}

	
	
	public static void clearAllPersistentCacheData()
	{
		Cache.clearAllPersistentCacheData();
	}
	
	
	
	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll)
	{
		/* The change of guide will only affect the selected day, getElseFetchFromServiceTVGuideUsingTVDate will check the cache, and the cache verifies that we have all
		 * the TVChannelGuides for the selected channels. */
		TVDate tvDate = getFromCacheTVDateSelected();
		
		/* Set the flag "updatingGuide" to true */
		isUpdatingGuide = true;
		
		/* Perform setting of TVChannelIds against backend, and fetch only the TVGuide for the new channels */
		apiClient.setNewTVChannelIdsAndFetchGuide(activityCallbackListener, tvDate, tvChannelIdsOnlyNewOnes, tvChannelIdsAll);
		
		/* Directly set the TVChannelIds in the Cache to directly update the UserProfileActivity GUI */
		getCache().setTvChannelIdsUser(tvChannelIdsAll);
	}
	
	
	
	/* This method does not require any ActivityCallbackListener, "fire and forget". */
	public void performInternalTracking(TVBroadcastWithChannelInfo broadcast) 
	{
		if(broadcast != null && 
		   broadcast.getProgram() != null && 
		   broadcast.getProgram().getProgramId() != null && 
		   !TextUtils.isEmpty(broadcast.getProgram().getProgramId())) 
		{
			String tvProgramId = broadcast.getProgram().getProgramId();
			
			String deviceId = GenericUtils.getDeviceID();
			
			apiClient.performInternalTracking(null, tvProgramId, deviceId);
		}
	}
	
	
	
	public List<TVChannelId> getFromCacheTVChannelIdsUser() 
	{
		List<TVChannelId> tvChannelIdsUser = getCache().getTvChannelIdsUsed();
		
		return tvChannelIdsUser;
	}
	
	
	
	public boolean getFromCacheHasInitialData()
	{
		boolean containsAppConfigData = getCache().containsAppConfigData();
		boolean containsAppVersionData = getCache().containsAppVersionData();
		boolean containsTVDates = getCache().containsTVDates();
		boolean containsTVTags = getCache().containsTVTags();
		boolean containsTVChannels = getCache().containsTVChannels();
		boolean containsTVGuideForSelectedDay = getCache().containsTVGuideForSelectedDay();
		
		Log.d(TAG, "Contains AppConfigData " + containsAppConfigData);
		Log.d(TAG, "Contains AppVersionData " + containsAppVersionData);
		Log.d(TAG, "Contains TVDates " + containsTVDates);
		Log.d(TAG, "Contains TVTags " + containsTVTags);
		Log.d(TAG, "Contains containsTVChannels " + containsTVChannels);
		Log.d(TAG, "Contains TVGuideForSelectedDay " + containsTVGuideForSelectedDay);
		
		boolean hasInitialData = containsAppConfigData && 
								 containsAppVersionData &&
								 containsTVDates && 
								 containsTVTags &&
								 containsTVChannels &&
								 containsTVGuideForSelectedDay;
		
		return hasInitialData;
	}
	
	
	
	public boolean getFromCacheHasTVDates()
	{
		return getCache().containsTVDates();
	}
	
	
	
	public boolean getFromCacheHasUserLikes()
	{
		return getCache().containsUserLikes();
	}
	
	
	
	public boolean getFromCacheHasActivityFeed()
	{
		return getCache().containsActivityFeedData();
	}
	
	
	
	public boolean getFromCacheHasTVTags()
	{
		return getCache().containsTVTags();
	}
	
	
	
	public boolean getFromCacheHasTVTagsAndGuideForSelectedTVDate()
	{
		TVDate tvDate = getCache().getTvDateSelected();
		
		if(tvDate != null)
		{
			boolean hasContent = getCache().containsTVTags() && getCache().containsTVGuideForTVDate(tvDate);
			
			return hasContent;
		}
		else
		{
			return false;
		}
	}
	
	
	
	public boolean getFromCacheHasUserTVChannelIds()
	{
		return getCache().containsTVChannelIdsUser();
	}
	
	
	
	public boolean getFromCacheHasTVChannelsAll()
	{
		return getCache().containsTVChannels();
	}
	
	
	
	public boolean getFromCacheHasTVBroadcastWithChannelInfo(TVChannelId channelId, long beginTimeInMillis)
	{
		return getCache().containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis);
	}
	
	
	
	public boolean getFromCacheHasBroadcastPageData()
	{
		boolean hasBroadcastPageData = false;
		
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = getCache().getNonPersistentLastSelectedBroadcastWithChannelInfo();

		if(broadcastWithChannelInfo != null)
		{
			boolean containsUpcomingBroadcasts = getCache().containsUpcomingBroadcastsForBroadcast(broadcastWithChannelInfo.getProgram().getProgramId());
		
			boolean containsRepeatingBroadcasts = getCache().containsRepeatingBroadcastsForBroadcast(broadcastWithChannelInfo.getProgram().getProgramId());
		
			hasBroadcastPageData = containsUpcomingBroadcasts && containsRepeatingBroadcasts;
		}
		
		return hasBroadcastPageData;
	}
	
	
	
	public boolean getFromCacheHasTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId)
	{
		return getCache().containsTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
	}
	
	
	public boolean getFromCacheHasPopularBroadcasts()
	{
		return getCache().containsPopularBroadcasts();
	}
	
	
	
	public boolean getFromCacheHasCompetitionData(long competitionID)
	{
		return getCache().getCompetitionsData().containsCompetitionData(competitionID);
	}
	
	
	
	public boolean getFromCacheHasTeamsGroupedByPhaseForSelectedCompetition()
	{
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		return getFromCacheHasCompetitionData(competitionID);
	}

	
	
	public boolean getFromCacheHasEventsGroupedByPhaseForSelectedCompetition()
	{
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		return getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	/* GETTERS & SETTERS */
	
	/* TVDate getters and setters */
	public TVDate getFromCacheTVDateSelected() 
	{
		TVDate tvDateSelected = getCache().getTvDateSelected();
		return tvDateSelected;
	}
	
	
	public int getFromCacheTVDateSelectedIndex() 
	{
		int tvDateSelectedIndex = getCache().getTvDateSelectedIndex();
		return tvDateSelectedIndex;
	}
	
	
	
	public int getFromCacheFirstHourOfTVDay() 
	{
		int firstHourOfDay = getCache().getFirstHourOfTVDay();
		
		return firstHourOfDay;
	}
	
	
	
	public boolean selectedTVDateIsToday() 
	{
		boolean isToday = false;
		
		TVDate tvDateSelected = getFromCacheTVDateSelected();
		
		if(tvDateSelected != null)
		{
			isToday = tvDateSelected.isToday();
		}
		
		return isToday;
	
	}


	
	/* TVTags */
	public List<TVTag> getFromCacheTVTags() 
	{
		List<TVTag> tvTags = getCache().getTvTags();
		
		return tvTags;
	}
	
	
	
	/* TVChannelGuide */
	public TVGuide getFromCacheTVGuideForSelectedDay() 
	{
		TVDate tvDate = getFromCacheTVDateSelected();
		
		TVGuide tvGuide = getCache().getTVGuideUsingTVDate(tvDate);
		
		return tvGuide;
	}
	
	
	
	public SearchResultsForQuery getFromCacheSearchResults() 
	{
		SearchResultsForQuery searchResultForQuery = getCache().getNonPersistentSearchResultsForQuery();
		
		return searchResultForQuery;
	}
	
	
	
	public AppConfiguration getFromCacheAppConfiguration()
	{
		AppConfiguration appConfiguration = getCache().getAppConfigData();
		
		return appConfiguration;
	}
	
	
	
	public boolean getFromCacheHasAppConfiguration()
	{
		return (getFromCacheAppConfiguration() != null);
	}
	
	
	
	public TVChannelGuide getFromCacheTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		TVChannelGuide tvChannelGuide = getCache().getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
		
		return tvChannelGuide;
	}

	
	
	public ArrayList<TVFeedItem> getFromCacheActivityFeedData() 
	{
		ArrayList<TVFeedItem> activityFeedData = getCache().getActivityFeed();
		
		return activityFeedData;
	}
		
	
	
	/* UserToken related methods */
	
	/**
	 * This method should only be used by the AsyncTaskWithUserToken class. A
	 * part from that class no other class should ever access or modify the user
	 * token directly. All login/logout/sign up logic should be handled by this
	 * class.
	 * 
	 * @return
	 */
	public String getFromCacheUserToken() 
	{
		String userToken = getCache().getUserToken();
		
		return userToken;
	}
	
	
	
	public boolean isLoggedIn() 
	{
		boolean isLoggedIn = getCache().isLoggedIn();
		
		return isLoggedIn;
	}
	
	
	
	/* NON-PERSISTENT USER DATA, TEMPORARY SAVED IN STORAGE, IN ORDER TO PASS DATA BETWEEN ACTIVITES */
	
	public void setUpcomingBroadcasts(final TVBroadcastWithChannelInfo broadcast, final ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		if(broadcast != null)
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		
			UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
		
			getCache().setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
	}
	
	
	public void setRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		if(broadcast != null)
		{
			String programId = broadcast.getProgram().getProgramId();
			
			RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(programId, repeatingBroadcasts);
			
			getCache().setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
	}
	
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a tvSeriesId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheUpcomingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		if (broadcast.getProgram().getSeries() == null) 
		{
			return null;	
		} 
		else 
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			
			ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
			
			if(getCache().containsUpcomingBroadcastsForBroadcast(tvSeriesId)) 
			{
				UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
				
				if(upcomingBroadcastsForBroadcast != null)
				{
					upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
				}
			}
			
			return upcomingBroadcasts;
		}
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheBroadcastsAiringOnDifferentChannels(
			final TVBroadcastWithChannelInfo broadcastWithChannelInfo,
			final boolean randomizeListElements)
	{
		ArrayList<TVBroadcastWithChannelInfo> broadcastsPlayingNow = new ArrayList<TVBroadcastWithChannelInfo>();
		
		TVChannelId inputTvChannelId = broadcastWithChannelInfo.getChannel().getChannelId();
		
		List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
		
		if(tvChannelIds != null && 
		   tvChannelIds.isEmpty() == false)
		{
			for(TVChannelId tvChannelId: tvChannelIds)
			{
				TVChannel tvChannel = getCache().getTVChannelById(tvChannelId);
				
				if(tvChannelId.equals(inputTvChannelId) == false && tvChannel != null)
				{
					TVChannelGuide tvChannelGuide = getCache().getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
			
					List<TVBroadcast> tvBroadcastsPlayingOnChannel = tvChannelGuide.getBroadcastPlayingAtSimilarTimeAs(broadcastWithChannelInfo);
					
					if(tvBroadcastsPlayingOnChannel.isEmpty() == false)
					{
						/* Only use the first of each channel */
						TVBroadcast tvBroadcastPlayingOnChannel = tvBroadcastsPlayingOnChannel.get(0);

						TVBroadcastWithChannelInfo tvBroadcastWithChannelInfoPlayingOnChannel = new TVBroadcastWithChannelInfo(tvBroadcastPlayingOnChannel);
						tvBroadcastWithChannelInfoPlayingOnChannel.setChannel(tvChannel);
						
						broadcastsPlayingNow.add(tvBroadcastWithChannelInfoPlayingOnChannel);
					}
				}
			}
		}
		
		if(randomizeListElements)
		{
			Collections.shuffle(broadcastsPlayingNow);
		}
		
		return broadcastsPlayingNow;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheUpcomingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		
		UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
		
		if(upcomingBroadcastsForBroadcast != null) 
		{
			upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
		}
		
		return upcomingBroadcasts;
	}
	
	
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a programId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheRepeatingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
	
		if(broadcast != null)
		{
			String programId = broadcast.getProgram().getProgramId();
			
			
			
			if(getCache().containsRepeatingBroadcastsForBroadcast(programId)) 
			{
				RepeatingBroadcastsForBroadcast repeatingBroadcastObject = getCache().getNonPersistentRepeatingBroadcasts();
				
				if(repeatingBroadcastObject != null) 
				{
					repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcastsWithExclusions(broadcast);
				}
			}
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
		
		return repeatingBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheRepeatingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		
		RepeatingBroadcastsForBroadcast repeatingBroadcastObject = getCache().getNonPersistentRepeatingBroadcasts();
		
		if(repeatingBroadcastObject != null) 
		{
			repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
		}
		
		return repeatingBroadcasts;
	}
	
	
	
	public void pushToSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo selectedBroadcast) 
	{
		getCache().pushToNonPersistentSelectedBroadcastWithChannelInfo(selectedBroadcast);
	}
	
	
	
	public void popFromSelectedBroadcastWithChannelInfo() 
	{
		getCache().popFromNonPersistentSelectedBroadcastWithChannelInfo();
	}
	
	
	
	public TVBroadcastWithChannelInfo getFromCacheLastSelectedBroadcastWithChannelInfo() 
	{
		TVBroadcastWithChannelInfo runningBroadcast = getCache().getNonPersistentLastSelectedBroadcastWithChannelInfo();
		
		return runningBroadcast;
	}
	
	
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsForSelectedTVDate()
	{
		TVDate tvDate = getFromCacheTVDateSelected();
		
		return getFromCacheTaggedBroadcastsUsingTVDate(tvDate);
	}
	
	
	
	public Integer getFromCacheSelectedHour()
	{
		Integer selectedHour = getCache().getNonPersistentSelectedHour();
		
		return selectedHour;
	}
	
	
	
	public void setSelectedHour(Integer selectedHour) 
	{
		getCache().setNonPersistentSelectedHour(selectedHour);
	}
	
	
	
	public void setSelectedTVChannelId(TVChannelId tvChannelId) 
	{
		getCache().setNonPersistentTVChannelId(tvChannelId);
	}
	
	
	
	public TVChannelId getFromCacheSelectedTVChannelId() 
	{
		TVChannelId tvChannelId = getCache().getNonPersistentTVChannelId();
		
		return tvChannelId;
	}
	
	
	
	public String getFromCacheUserLastname()
	{
		String userLastname = getCache().getUserLastname();
		
		return userLastname;
	}
	
	
	
	public String getFromCacheUserFirstname() 
	{
		String userFirstname = getCache().getUserFirstname();
		
		return userFirstname;
	}
	
	
	
	public String getFromCacheUserEmail() 
	{
		String userEmail = getCache().getUserEmail();
		
		return userEmail;
	}
	
	
	
	public String getFromCacheUserId() 
	{
		String userId = getCache().getUserId();
		
		return userId;
	}
	
	
	
	public String getFromCacheUserProfileImage() 
	{
		String userId = getCache().getUserProfileImageUrl();
		
		return userId;
	}
	
	
	
	public List<TVChannel> getFromCacheTVChannelsAll()
	{
		List<TVChannel> tvChannelsAll = getCache().getTvChannels();
		
		return tvChannelsAll;
	}
	
	
	
	public List<UserLike> getFromCacheUserLikes()
	{
		List<UserLike> userLikes = getCache().getUserLikes();
		
		return userLikes;
	}
	
	
	
	public boolean isContainedInUserLikes(UserLike userLike)
	{
		boolean isContainedInUserLikes = getCache().isInUserLikes(userLike);
		
		return isContainedInUserLikes;
	}
	
	
	
	public boolean isContainedInUsedChannelIds(TVChannelId channelId)
	{
		boolean isContainedInUsedChannelIds = getCache().isInUsedChannelIds(channelId);
		
		return isContainedInUsedChannelIds;
	}
	
	
	
	public TVChannel getFromCacheTVChannelById(TVChannelId tvChannelId)
	{
		return getCache().getTVChannelById(tvChannelId);
	}
	
	
	
	public String getFromCacheWelcomeMessage()
	{
		String welcomeMessage = getCache().getWelcomeMessage();
		
		return welcomeMessage;
	}
	
	
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsUsingTVDate(TVDate tvDate)
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcasts = getCache().getTaggedBroadcastsUsingTVDate(tvDate);
		
		return taggedBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCachePopularBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts = getCache().getPopularBroadcasts();
		
		return popularBroadcasts;
	}
	
	
	
	public List<TVDate> getFromCacheTVDates() 
	{
		 List<TVDate> tvDates = getCache().getTvDates();
		 
		 return tvDates;
	}
	
	
	
	public void setReturnActivity(Class<?> returnActivity) 
	{
		getCache().setReturnActivity(returnActivity);
	}
	
	
	
	/**
	 * This method tries to start the return activity stored in the cache, if null it does nothing and returns false
	 * else it starts the activity and sets it to null and returns true
	 * @param caller
	 * @return
	 */
	public boolean tryStartReturnActivity(Activity caller) 
	{
		boolean returnActivityWasSet = getReturnActivity() != null;
		
		if(returnActivityWasSet) 
		{
			Intent intent = new Intent(caller, getReturnActivity());
			
			getCache().clearReturnActivity();
			
			caller.startActivity(intent);
		}
		
		return returnActivityWasSet;
	}

	
	
	public Class<?> getReturnActivity() 
	{
		Class<?> returnActivity = getCache().getReturnActivity();
		
		return returnActivity;
	}
	
	
	
	public void setLikeToAddAfterLogin(UserLike userLikeToAdd) 
	{
		getCache().setLikeToAddAfterLogin(userLikeToAdd);
	}
	
	
	
	/* This method compares the initial stored SNTP calendar with the local calendar */
	public boolean isLocalDeviceCalendarOffSync()
	{
		if(isLocalDeviceCalendarOffSync == null)
		{
			isLocalDeviceCalendarOffSync = false;
			
			Calendar now = DateUtils.getNow();
	
			Calendar nowFromSNTP = getCache().getInitialCallSNTPCalendar();
	
			if(nowFromSNTP != null)
			{
				Integer difference = DateUtils.calculateDifferenceBetween(now, nowFromSNTP, Calendar.MINUTE, true, 0);
		
				if(difference > TIME_OFFSET_IN_MINUTES_FOR_NTP_COMPARISSON)
				{
					isLocalDeviceCalendarOffSync = true;
				}
			}
			else
			{
				Log.w(TAG, "Calendar from SNTP is null. Assuming local device time as accurate.");
			}
		}
		
		return isLocalDeviceCalendarOffSync;
	}
	
	
	
	public int getDisqusTotalPostsForLatestBroadcast()
	{
		int disqusTotalPostsForLatestBroadcast = getCache().getDisqusTotalPostsForLatestBroadcast();
		
		return disqusTotalPostsForLatestBroadcast;
	}
	
	
	
	/* METHODS TO FETCH COMPETITION DATA FROM CACHE */
	
	public Team getFromCacheTeamByID(long teamID)
	{
		Team matchingTeam = null;
		
		List<Team> teams = getCache().getCompetitionsData().getTeamsForSelectedCompetition();
		
		for(Team team : teams)
		{
			if(team.getTeamId() == teamID)
			{
				matchingTeam = team;
				break;
			}
		}
		
		return matchingTeam;
	}
	
	
	
	public Event getFromCacheNextUpcomingEventForSelectedCompetition()
	{
		Event matchingEvent = null;
		
		List<Event> events = getCache().getCompetitionsData().getEventsForSelectedCompetition();
		
		if(events.isEmpty() == false)
		{
			matchingEvent = events.get(0);
		}
		
		for(Event event : events)
		{
			Calendar eventStartTimeCalendar = event.getEventDateCalendarLocal();
			
			if(matchingEvent.getEventDateCalendarLocal().after(eventStartTimeCalendar))
			{
				matchingEvent = event;
			}
		}
		
		return matchingEvent;
	}

	

	private Map<Long, List<Event>> getFromCacheAllEventsGroupedByPhaseForSelectedCompetition(
			final String stage,
			final boolean reverseComparisson)
	{
		Map<Long, List<Event>> eventsByPhaseID = new HashMap<Long, List<Event>>();
		
		List<Phase> phases = getCache().getCompetitionsData().getPhasesForSelectedCompetition();
		
		List<Event> events = getCache().getCompetitionsData().getEventsForSelectedCompetition();
		
		for(Phase phase : phases)
		{
			boolean isSameStage = phase.getStage().equals(stage);
			
			if(reverseComparisson)
			{
				isSameStage = !isSameStage;
			}
			
			if(isSameStage)
			{
				List<Event> eventsForPhase = new ArrayList<Event>();
				
				for(Event event : events)
				{
					if(event.getPhaseId() == phase.getPhaseId())
					{
						eventsForPhase.add(event);
					}
				}
				
				eventsByPhaseID.put(phase.getPhaseId(), eventsForPhase);
			}
		}
		
		return eventsByPhaseID;
	}
	
		
	
	public Map<Long, List<Event>> getFromCacheAllEventsGroupedByGroupStageForSelectedCompetition()
	{
		return getFromCacheAllEventsGroupedByPhaseForSelectedCompetition(Constants.GROUP_STAGE, false);
	}
	
	
	
	public Map<Long, List<Event>> getFromCacheAllEventsGroupedBySecondStageForSelectedCompetition()
	{
		return getFromCacheAllEventsGroupedByPhaseForSelectedCompetition(Constants.GROUP_STAGE, true);
	}
	
	
	
	public Map<Long, List<Standings>> getFromCacheAllStandingsGroupedByPhaseForSelectedCompetition()
	{
		return getCache().getCompetitionsData().getStandingsByPhaseForSelectedCompetition();
	}
	
	
	
	public Calendar getSelectedCompetitionBeginTime()
	{
		Calendar cal;
		
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		if(selectedCompetition != null)
		{
			cal = selectedCompetition.getBeginTimeCalendarLocal();
		}
		else
		{
			cal = DateUtils.getNow();
		}
		
		return cal;
	}

	
	
	public List<Competition> getFromCacheVisibleCompetitions()
	{
		return getFromCacheAllCompetitions(false);
	}
	
	
	
	public Competition getFromCacheVisibleRandomCompetition()
	{
		List<Competition> competitions = getFromCacheVisibleCompetitions();
		
		// TODO - Revert the random
		//int randomCompetitionIndex = RandomNumberUtils.getRandomIntegerInRange(0, competitions.size());
		
		Competition competition = getFromCacheCompetitionByID(Constants.FIFA_COMPETITION_ID);
		
		return competition;
	}
	
	
	
	public List<Competition> getFromCacheAllCompetitions(boolean inludeInvisible)
	{
		List<Competition> allCompetitions = getCache().getCompetitionsData().getAllCompetitions();
		
		if(inludeInvisible)
		{
			return allCompetitions;
		}
		else
		{
			List<Competition> onlyVisibleCompetitions = new ArrayList<Competition>();
			
			for(Competition competition : allCompetitions)
			{
				if(competition.isVisible())
				{
					onlyVisibleCompetitions.add(competition);
				}
			}
			
			return onlyVisibleCompetitions;
		}
	}
	
	
	
	public void setSelectedCompetition(Competition competition)
	{
		long competitionID = competition.getCompetitionId();
		
		getCache().getCompetitionsData().setSelectedCompetition(competitionID);
	}
	
	
	
	public Competition getFromCacheCompetitionByID(long competitionID)
	{
		return getCache().getCompetitionsData().getSelectedCompetitionByID(competitionID);
	}
	
	
	
	protected void clearUserCache() 
	{
		getCache().clearUserData();
		getCache().clearTVChannelIdsUser();
		getCache().useDefaultChannelIds();
		getCache().clearUserLikes();
	}
	
	
	
	/**
	 * Filter out broadcast with a beginTime that has passed.
	 * 
	 * @param activityFeed
	 */
	protected static ArrayList<TVFeedItem> filterOldBroadcasts(ArrayList<TVFeedItem> activityFeed, ArrayList<TVFeedItem> contentAsArrayList)
	{
		if (activityFeed != null && !activityFeed.isEmpty())
		{	
			ArrayList<TVFeedItem> oldFeedItemsToDelete = new ArrayList<TVFeedItem>();
			
			if (contentAsArrayList == null) 
			{
				contentAsArrayList = activityFeed;
			}

			for (int i = 0; i < activityFeed.size(); i++) 
			{
				TVFeedItem item = activityFeed.get(i);

				if (item.getItemType() != FeedItemTypeEnum.POPULAR_BROADCASTS) 
				{
					TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo = item.getBroadcast();

					boolean isItemTooOld = checkIfTVFeedItemIsOld(tvBroadcastWithChannelInfo);

					/* If item is too old or if it has shown before the item will be removed */
					if (isItemTooOld) 
					{	
						Log.d(TAG, "MMM, Removing broadcast: " + tvBroadcastWithChannelInfo.getTitle() + " Starttime: "
								+ tvBroadcastWithChannelInfo.getBeginTime());
						
						oldFeedItemsToDelete.add(item);
					}
				}
			}
			
			if (oldFeedItemsToDelete != null && !oldFeedItemsToDelete.isEmpty())
			{
				contentAsArrayList.removeAll(oldFeedItemsToDelete);
			}
		}
		
		return contentAsArrayList;
	}
	
	
	
	/**
	 * Check if TVFeedItem is too old and needs to be removed from the activityFeed.
	 * 
	 * @param item
	 * @return true: Item will be removed
	 * @return false: Item will NOT be removed
	 */
	protected static boolean checkIfTVFeedItemIsOld(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo)
	{
		boolean removeItem = false;
		
		Long timeZoneOffsetInMillis = DateUtils.getTimeZoneOffsetInMillis();
		
		Calendar now = DateUtils.getNow();
		
		Long nowLong = now.getTimeInMillis() + timeZoneOffsetInMillis;
		
		Long beginTime = tvBroadcastWithChannelInfo.getBeginTimeMillis();
		
		int difference = beginTime.compareTo(nowLong);
		
		/* beginTime is less than nowLong: Item should NOT be included in the feed activity. Items is old */
		if (difference < 0)
		{
			removeItem = true;
		}		
		
		return removeItem;
	}
	
	
	
	/**
	 * Filter out broadcasts that has been shown for more than two times.
	 * 
	 * @param activityFeed
	 * @param contentAsArrayList
	 * @return
	 */
//	private static ArrayList<TVFeedItem> filterSimilarBroadcasts(ArrayList<TVFeedItem> activityFeed, ArrayList<TVFeedItem> contentAsArrayList) 
//	{
//		if (activityFeed != null && !activityFeed.isEmpty()) {
//			
//			ArrayList<TVFeedItem> similarFeedItemsToDelete = new ArrayList<TVFeedItem>();
//			
//			if (contentAsArrayList == null) {
//				contentAsArrayList = activityFeed;
//			}
//
//			for (int i = contentAsArrayList.size() - 1; i >= 0; i--) {
//
//				TVFeedItem item = contentAsArrayList.get(i);
//
//				if (item.getItemType() != FeedItemTypeEnum.POPULAR_BROADCASTS) {
//
//					TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo = item.getBroadcast();
//
//					boolean hasItemBeenShownBefore = checkIfTVFeedItemHasShownBefore(activityFeed, tvBroadcastWithChannelInfo);
//
//					/* If item is too old or if it has shown before the item will be removed */
//					if (hasItemBeenShownBefore) {
//						Log.d(TAG, "MMM, Removing shows more than 2 times, Broadcast: " + tvBroadcastWithChannelInfo.getTitle());
//						
//						similarFeedItemsToDelete.add(item);
//					}
//				}
//			}
//			
//			if (similarFeedItemsToDelete != null && !similarFeedItemsToDelete.isEmpty()) {
//				contentAsArrayList.removeAll(similarFeedItemsToDelete);
//			}
//		}
//		
//		return contentAsArrayList;
//	}
	
	
	
	/**
	 * Start checking from item number 3 in the list if it is a repetition or not.
	 * 
	 * @param activityFeed
	 * @param tvBroadcastWithChannelInfo
	 * @return TRUE: If repetition
	 * @return FALSE: If NOT repetition
	 */
//	private static boolean checkIfTVFeedItemHasShownBefore(ArrayList<TVFeedItem> activityFeed, TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo) 
//	{
//		int counterHowManyTimesItemAppearsInList = 0;
//		
//		String nameOfItem = tvBroadcastWithChannelInfo.getTitle();
//		
//		for (int i = 0; i < activityFeed.size(); i++) 
//		{	
//			TVFeedItem item = activityFeed.get(i);
//			
//			TVBroadcastWithChannelInfo tvBroadcastWithChannelInfoFromList = item.getBroadcast();
//			
//			if (tvBroadcastWithChannelInfoFromList != null) 
//			{
//				String nameOfItemInList = tvBroadcastWithChannelInfoFromList.getTitle();
//				
//				if (nameOfItemInList.equals(nameOfItem)) 
//				{
//					counterHowManyTimesItemAppearsInList++;
//				}
//				
//				if (counterHowManyTimesItemAppearsInList > 2) {
//					return true;
//				}
//			}
//			
//		}
//		
//		return false;
//	}
}
