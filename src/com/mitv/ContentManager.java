
package com.mitv;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.mitv.asynctasks.local.BuildTVBroadcastsForTags;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.AppConfiguration;
import com.mitv.models.AppVersion;
import com.mitv.models.RepeatingBroadcastsForBroadcast;
import com.mitv.models.SearchResultsForQuery;
import com.mitv.models.TVBroadcast;
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
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;



public class ContentManager
	implements ContentCallbackListener
{
	private static final String TAG = ContentManager.class.getName();
	
	private Cache cache;
	private APIClient apiClient;
	
	private FetchDataProgressCallbackListener fetchDataProgressCallbackListener;
	
	private static final int TIME_OFFSET_IN_MINUTES_FOR_NTP_COMPARISSON = 5;
	
	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD = 5;
	private static final int COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD = 2;
	private static int completedCountTVDataForProgressMessage = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD + 1;

	private int completedCountTVActivityFeed = 0;

	/* Variables for fetching of BroadcastPage data */
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD = 2;
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD = 3;
	private int completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD;
	private int completedCountBroadcastPageData = 0;
	
	
	/*
	 * The total completed data fetch count needed for the initial data loading
	 */
	private static int COMPLETED_COUNT_FOR_INITIAL_CALL_NOT_LOGGED_IN = 7;
	private static int COMPLETED_COUNT_FOR_INITIAL_CALL_LOGGED_IN = 8;

	private boolean completedTVDatesRequest;
	private boolean completedTVChannelIdsDefaultRequest;
	private boolean completedTVChannelIdsUserRequest;
	private boolean isFetchingTVGuide;
	private boolean isAPIVersionTooOld;
	
	private boolean isUpdatingGuide;
	private boolean isBuildingTaggedBroadcasts;
	private boolean isFetchingFeedItems;
	private boolean isGoingToMyChannelsFromSearch;
	private Boolean isLocalDeviceCalendarOffSync;
			
	private HashMap<RequestIdentifierEnum, ArrayList<ViewCallbackListener>> mapRequestToCallbackListeners;
	
	public ContentManager()
	{	
		this.cache = new Cache();
		this.apiClient = new APIClient(this);
		this.mapRequestToCallbackListeners = new HashMap<RequestIdentifierEnum, ArrayList<ViewCallbackListener>>();
		this.isLocalDeviceCalendarOffSync = null;
		
		/* 1 for guide and parsing of tagged broadcasts */
		completedCountTVDataForProgressMessage = 1;
		
		if (getCache().isLoggedIn())
		{
			/* Increase global threshold by 1 since we are logged in */
			completedCountTVDataForProgressMessage += COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD;
		}
		else
		{
			completedCountTVDataForProgressMessage += COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD;
		}	
	}
	
	
	
	public static ContentManager sharedInstance() 
	{
		return SecondScreenApplication.sharedInstance().getContentManager();
	}
	
	private Cache getCache() {
		if(cache == null) {
			Log.w(TAG, "!!! WARNING !!! Cache in ContentManager is null, this should not be happening => reinitializing it");
			cache = new Cache();
		}
		return cache;
	}
	
	
	public void setGoingToMyChannelsFromSearch(boolean isGoingToMyChannelsFromSearch) {
		this.isGoingToMyChannelsFromSearch = isGoingToMyChannelsFromSearch;
	}
	
	public boolean isGoingToMyChannelsFromSearch() {
		return isGoingToMyChannelsFromSearch;
	}

	private synchronized HashMap<RequestIdentifierEnum, ArrayList<ViewCallbackListener>> getMapRequestToCallbackListeners() {
		return mapRequestToCallbackListeners;
	}
	
	
	public void registerListenerForRequest(RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener) {
		ArrayList<ViewCallbackListener> listenerList = getMapRequestToCallbackListeners().get(requestIdentifier);

		if (listenerList == null) {
			listenerList = new ArrayList<ViewCallbackListener>();
			mapRequestToCallbackListeners.put(requestIdentifier, listenerList);
		}

		if (!listenerList.contains(listenerList)) {
			listenerList.add(listener);
		}
	}

	public void unregisterListenerFromAllRequests(ViewCallbackListener listener) {
		Collection<ArrayList<ViewCallbackListener>> listenerListCollection = getMapRequestToCallbackListeners().values();

		for (ArrayList<ViewCallbackListener> listenerList : listenerListCollection) {
			if (listenerList.contains(listener)) {
				listenerList.remove(listener);
			}
		}
	}
	
	private void notifyListenersOfRequestResult(RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) {
		ArrayList<ViewCallbackListener> listenerList = getMapRequestToCallbackListeners().get(requestIdentifier);

		if (listenerList != null) {

			/* Remove any null listener */
			listenerList.removeAll(Collections.singleton(null));

			for (ViewCallbackListener listener : listenerList) {
				Log.d(TAG, String.format("PROFILING: notifyListenersOfRequestResult: listener: %s request: %s, result: %s", listener.getClass().getSimpleName(), requestIdentifier.getDescription(),
						result.getDescription()));
				listener.onResult(result, requestIdentifier);
			}
		}
	}
	
	
	public boolean isUpdatingGuide() {
		return isUpdatingGuide;
	}
	
	
	
	public static void clearAllPersistentCacheData()
	{
		Cache.clearAllPersistentCacheData();
	}
	
	
	
	public void buildTVBroadcastsForTags(ViewCallbackListener activityCallbackListener, String tagName) {
		registerListenerForRequest(RequestIdentifierEnum.TV_BROADCASTS_FOR_TAGS, activityCallbackListener);
		
		if(!isBuildingTaggedBroadcasts) {
			isBuildingTaggedBroadcasts = true;
			Log.d(TAG, String.format("PROFILING: buildTVBroadcastsForTags: build tagged, tag: %s", tagName));
			TVGuide tvGuide = getFromCacheTVGuideForSelectedDay();
			if(tvGuide != null) {
				ArrayList<TVChannelGuide> tvChannelGuides = tvGuide.getTvChannelGuides();
			
				BuildTVBroadcastsForTags buildTVBroadcastsForTags = new BuildTVBroadcastsForTags(tvChannelGuides, this, activityCallbackListener);
				buildTVBroadcastsForTags.execute();
			}
		}
	}
	
	
	private void handleBuildTVBroadcastsForTagsResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = (HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>) content;
		
		Log.d(TAG, "PROFILING: handleBuildTVBroadcastsForTagsResponse: addTaggedBroadcastsForSelectedDay");
		
		getCache().addTaggedBroadcastsForSelectedDay(mapTagToTaggedBroadcastForDate);
		
		notifyListenersOfRequestResult(requestIdentifier, result);
		isBuildingTaggedBroadcasts = false;
	}
	
	
	
	public void fetchFromServiceInitialCall(ViewCallbackListener activityCallbackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener)
	{	
		registerListenerForRequest(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, activityCallbackListener);
		
		if(!isUpdatingGuide) 
		{
			isUpdatingGuide = true;
			
			Log.d(TAG, "PROFILING: fetchFromServiceInitialCall");
			
			this.completedTVDatesRequest = false;
			this.completedTVChannelIdsDefaultRequest = false;
			this.completedTVChannelIdsUserRequest = false;
			this.isFetchingTVGuide = false;
			this.isAPIVersionTooOld = false;
			
			this.fetchDataProgressCallbackListener = fetchDataProgressCallbackListener;
			
			boolean isUserLoggedIn = getCache().isLoggedIn();
			
			apiClient.getInitialDataOnPoolExecutor(activityCallbackListener, isUserLoggedIn);
		}
	}
	
	
	
	private void handleInitialDataResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content) 
	{
		if(apiClient.arePendingRequestsCanceled())
		{
			return;
		}
		
		apiClient.incrementCompletedTasks();
		
		int totalStepsCount;
		
		if(getCache().isLoggedIn())
		{
			totalStepsCount = COMPLETED_COUNT_FOR_INITIAL_CALL_LOGGED_IN;
		}
		else
		{
			totalStepsCount = COMPLETED_COUNT_FOR_INITIAL_CALL_NOT_LOGGED_IN;
		}
		
		switch (requestIdentifier) 
		{			
			case APP_CONFIGURATION: 
			{
				if(result.wasSuccessful() && content != null) 
				{
					AppConfiguration appConfigData = (AppConfiguration) content;
					
					getCache().setAppConfigData(appConfigData);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_configuration_data));
				}
				break;
			}
			
			case APP_VERSION: 
			{
				if(result.wasSuccessful() && content != null) 
				{
					AppVersion appVersionData = (AppVersion) content;
					
					getCache().setAppVersionData(appVersionData);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_app_version_data));
				
					boolean isAPIVersionSupported = getCache().isAPIVersionSupported();
					
					if(isAPIVersionSupported == false)
					{
						isAPIVersionTooOld = true;
					}
				}
				break;
			}
			
			case TV_DATE:
			{
				if(result.wasSuccessful() && content != null) 
				{
					completedTVDatesRequest = true;
					
					@SuppressWarnings("unchecked")
					ArrayList<TVDate> tvDates = (ArrayList<TVDate>) content;
					getCache().setTvDates(tvDates);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_dates_data));
					
					if(!isFetchingTVGuide && 
					   (completedTVChannelIdsDefaultRequest && !getCache().isLoggedIn()) || 
					   (completedTVChannelIdsUserRequest && getCache().isLoggedIn()))
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = getCache().getTvDateSelected();
						
						List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
						
						apiClient.getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
					}
				}
				break;
			}
			
			case TV_CHANNEL_IDS_DEFAULT:
			{
				if(result.wasSuccessful() && content != null) 
				{
					completedTVChannelIdsDefaultRequest = true;
					
					@SuppressWarnings("unchecked")
					ArrayList<TVChannelId> tvChannelIdsDefault = (ArrayList<TVChannelId>) content;
					
					getCache().setTvChannelIdsDefault(tvChannelIdsDefault);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_channel_id_data));
					
					if(!isFetchingTVGuide && completedTVDatesRequest && !getCache().isLoggedIn())
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = getCache().getTvDateSelected();
						
						List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
						
						apiClient.getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
					}
				}
				break;
			}
			
			case TV_CHANNEL_IDS_USER_INITIAL_CALL:
			{
				if(result.wasSuccessful() && content != null) 
				{
					completedTVChannelIdsUserRequest = true;
					
					@SuppressWarnings("unchecked")
					ArrayList<TVChannelId> tvChannelIdsUser = (ArrayList<TVChannelId>) content;
					
					getCache().setTvChannelIdsUser(tvChannelIdsUser);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_channel_id_data));
					
					if(!isFetchingTVGuide && completedTVDatesRequest && getCache().isLoggedIn())
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = getCache().getTvDateSelected();
						
						List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
						
						apiClient.getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
					}
				}
				else if(result.hasUserTokenExpired())
				{
					totalStepsCount = COMPLETED_COUNT_FOR_INITIAL_CALL_NOT_LOGGED_IN;
					
					clearUserCache();
					
					if(!isFetchingTVGuide && completedTVChannelIdsDefaultRequest)
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = cache.getTvDateSelected();
						
						List<TVChannelId> tvChannelIds = cache.getTvChannelIdsUsed();
						
						apiClient.getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
					}
				}
				break;
			}
			
			case TV_TAG:
			{
				if(result.wasSuccessful() && content != null) 
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVTag> tvTags = (ArrayList<TVTag>) content;
					
					getCache().setTvTags(tvTags);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_genres_data));
				}
				break;
			}
			
			case TV_CHANNEL:
			{
				if(result.wasSuccessful() && content != null) 
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVChannel> tvChannels = (ArrayList<TVChannel>) content;
					
					getCache().setTvChannels(tvChannels);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_channel_data));
				}
				break;
			}
			
			case TV_GUIDE_INITIAL_CALL:
			{
				if(result.wasSuccessful() && content != null) 
				{
					TVGuide tvGuide = (TVGuide) content;
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_tv_guide_data));
					
					Log.d(TAG, "PROFILING: handleInitialDataResponse: addNewTVChannelGuidesForSelectedDayUsingTvGuide");
					getCache().addNewTVChannelGuidesForSelectedDayUsingTvGuide(tvGuide);
				}
				break;
			}
			
			case SNTP_CALL:
			{
				Calendar calendar = (Calendar) content;
				getCache().setInitialCallSNTPCalendar(calendar);
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unhandled request identifier.");
				break;
			}
		}
		
		if(isAPIVersionTooOld)
		{
			apiClient.cancelAllPendingRequests();
			
			activityCallbackListener.onResult(FetchRequestResultEnum.API_VERSION_TOO_OLD, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
		}
		
		
		if(apiClient.areAllTasksCompleted())
		{
			isUpdatingGuide = false;
			notifyListenersOfRequestResult(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, result);
						
			apiClient.cancelAllPendingRequests();
		}
		else
		{
			if(!result.wasSuccessful() || content == null)
			{
				isUpdatingGuide = false;
				
				apiClient.cancelAllPendingRequests();
				
				notifyListenersOfRequestResult(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, FetchRequestResultEnum.UNKNOWN_ERROR);
			}
			else
			{
				Log.d(TAG, "There are pending tasks still running.");
			}
		}
	}
		
	
	private void fetchFromServiceTVDataOnUserStatusChange(ViewCallbackListener activityCallbackListener) {
		Log.d(TAG, "PROFILING: fetchFromServiceTVDataOnUserStatusChange");
		
		registerListenerForRequest(RequestIdentifierEnum.TV_CHANNEL_IDS_USER_STANDALONE, activityCallbackListener);
		
		/* Handle TV Channel & Guide data, after login */
		apiClient.getUserTVChannelIds(activityCallbackListener, true);
		
		/* Add like if any was set */
		UserLike likeToAddAfterLogin = getCache().getLikeToAddAfterLogin();
		if(likeToAddAfterLogin != null) {
			/* Passing null because the login views should not care about if the like was successfully added or not.
			 * According to the current architecture we MUST not allow the method onDataAvailable to be called in LoginViews,
			 * since pattern with returnActivity and method tryStartReturnActivity will break */
			
			registerListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE, activityCallbackListener);
			addUserLike(null, likeToAddAfterLogin);
		}
	}

	
	private void fetchFromServiceTVGuideForSelectedDay(ViewCallbackListener activityCallbackListener) 
	{
		TVDate tvDate = getCache().getTvDateSelected();
		
		fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
	}
		
	private void fetchFromServiceTVGuideUsingTVDateAndTVChannelIds(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		registerListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE, activityCallbackListener);
		
		if(!isUpdatingGuide) 
		{
			Log.i(TAG, "The guide is still updating.");
			
			isUpdatingGuide = true;
			
			apiClient.getTVChannelGuides(activityCallbackListener, tvDate, tvChannelIds);
		}
	}
	
	private void fetchFromServiceTVGuideUsingTVDate(ViewCallbackListener activityCallbackListener, TVDate tvDate)
	{		
		List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
		
		if(tvChannelIds != null) 
		{
			Log.d(TAG, "PROFILING: fetchFromServiceTVGuideUsingTVDate: fetchFromServiceTVGuideUsingTVDateAndTVChannelIds");
			
			fetchFromServiceTVGuideUsingTVDateAndTVChannelIds(activityCallbackListener, tvDate, tvChannelIds);
		} 
		else 
		{
			Log.d(TAG, "PROFILING: fetchFromServiceTVGuideUsingTVDate: channel Ids null");
			
			activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, RequestIdentifierEnum.TV_GUIDE_STANDALONE);
		}
	}
	
	
	private void fetchFromServiceActivityFeedData(ViewCallbackListener activityCallbackListener) 
	{
		registerListenerForRequest(RequestIdentifierEnum.USER_LIKES, activityCallbackListener);
		registerListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, activityCallbackListener);
		registerListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_INITIAL_DATA, activityCallbackListener);
		apiClient.getUserTVFeedItemsInitial(activityCallbackListener);
		apiClient.getUserLikes(activityCallbackListener, false);
	}
	
	
	private void fetchFromServiceSearchResults(ViewCallbackListener activityCallbackListener, String searchQuery) 
	{
		registerListenerForRequest(RequestIdentifierEnum.SEARCH, activityCallbackListener);
		apiClient.getTVSearchResults(activityCallbackListener, searchQuery);
	}
	
	
	private void fetchFromServiceUserLikes(ViewCallbackListener activityCallbackListener) 
	{
		registerListenerForRequest(RequestIdentifierEnum.USER_LIKES, activityCallbackListener);
		apiClient.getUserLikes(activityCallbackListener, true);
	}
	
	
	private void fetchFromServicePopularBroadcasts(ViewCallbackListener activityCallbackListener) 
	{
		registerListenerForRequest(RequestIdentifierEnum.POPULAR_ITEMS, activityCallbackListener);
		apiClient.getTVBroadcastsPopular(activityCallbackListener);
	}
	
	
	private void fetchFromServiceUpcomingBroadcasts(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		registerListenerForRequest(RequestIdentifierEnum.UPCOMING_BROADCASTS_FOR_SERIES, activityCallbackListener);
		if (broadcast.getProgram() != null && broadcast.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) 
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			apiClient.getTVBroadcastsFromSeries(activityCallbackListener, tvSeriesId);
		} 
		else 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.BAD_REQUEST, requestIdentifier);
		}
	}

	
	private void fetchFromServiceRepeatingBroadcasts(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		registerListenerForRequest(RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, activityCallbackListener);
		if (broadcast.getProgram() != null) 
		{
			String programId = broadcast.getProgram().getProgramId();
			apiClient.getTVBroadcastsFromProgram(activityCallbackListener, programId);
		} 
		else 
		{
			handleBroadcastPageDataResponse(activityCallbackListener, requestIdentifier, FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT, null);
		}
	}
	
	
	private void fetchFromServiceIndividualBroadcast(ViewCallbackListener activityCallbackListener, TVChannelId channelId, long beginTimeInMillis) 
	{
		registerListenerForRequest(RequestIdentifierEnum.BROADCAST_DETAILS, activityCallbackListener);
		apiClient.getTVBroadcastDetails(activityCallbackListener, channelId, beginTimeInMillis);
	}
	
	
	/* PUBLIC FETCH METHODS WHERE IT DOES NOT MAKE ANY SENSE TO TRY FETCHING THE DATA FROM STORAGE */
	
	public void checkNetworkConnectivity(ViewCallbackListener activityCallbackListener) 
	{
		registerListenerForRequest(RequestIdentifierEnum.INTERNET_CONNECTIVITY, activityCallbackListener);
		apiClient.getNetworkConnectivityIsAvailable(activityCallbackListener);
	}
		
	
	public void fetchFromServiceMoreActivityData(ViewCallbackListener activityCallbackListener, int offset) 
	{
		registerListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE, activityCallbackListener);
		if (!isFetchingFeedItems) {
			isFetchingFeedItems = true;
			Log.d(TAG, "FEEDS: count " + offset);
			apiClient.getUserTVFeedItemsWithOffsetAndLimit(activityCallbackListener, offset);
		}
	}

	/*
	 * METHODS FOR "GETTING" THE DATA, EITHER FROM STORAGE, OR FETCHING FROM
	 * BACKEND
	 */
	
	public void getElseFetchFromServiceTVGuideUsingSelectedTVDate(ViewCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		TVDate tvDateSelected = getFromCacheTVDateSelected();
		
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, forceDownload, tvDateSelected);
	}
	
	
	public void getElseFetchFromServiceSearchResultForSearchQuery(ViewCallbackListener activityCallbackListener, boolean forceDownload, String searchQuery) 
	{
		if(!forceDownload && getCache().containsSearchResultForQuery(searchQuery)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.SEARCH);
		} 
		else 
		{
			/* Clear old search result */
			getCache().setNonPersistentSearchResultsForQuery(null);
			fetchFromServiceSearchResults(activityCallbackListener, searchQuery);
		}
	}
	
	
	public void getElseFetchFromServiceTVGuideUsingTVDate(ViewCallbackListener activityCallbackListener, boolean forceDownload, TVDate tvDate)
	{
		if (!forceDownload && getCache().containsTVGuideForTVDate(tvDate)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE_STANDALONE);
		} 
		else 
		{
			fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
		}
	}

	
	public void getElseFetchFromServiceActivityFeedData(ViewCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		if (!forceDownload && getCache().containsActivityFeedData() && getCache().containsUserLikes()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM);
		} 
		else 
		{
			fetchFromServiceActivityFeedData(activityCallbackListener);
		}
	}
	
	
	public void getElseBuildTaggedBroadcastsForSelectedTVDate(ViewCallbackListener activityCallbackListener, String tagName)
	{
			TVDate tvDateSelected = getFromCacheTVDateSelected();
		
			getElseBuildTaggedBroadcastsUsingTVDate(activityCallbackListener, tvDateSelected, tagName);
	}
	
	
	public void getElseBuildTaggedBroadcastsUsingTVDate(ViewCallbackListener activityCallbackListener, TVDate tvDate, String tagName) 
	{
		boolean containsTaggedBroadcastsForTVDate = getCache().containsTaggedBroadcastsForTVDate(tvDate);
		
		if (containsTaggedBroadcastsForTVDate) 
		{
			Log.d(TAG, String.format("PROFILING: getElseFetchFromServiceTaggedBroadcastsUsingTVDate: cache contains tagged, tagName: %s", tagName));
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
		} 
		else
		{	
			buildTVBroadcastsForTags(activityCallbackListener, tagName);		
		}
	}
	
	
	public void getElseFetchFromServicePopularBroadcasts(ViewCallbackListener activityCallbackListener, boolean forceDownload)
	{
		if (!forceDownload && getCache().containsPopularBroadcasts()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.POPULAR_ITEMS);
		} 
		else 
		{
			fetchFromServicePopularBroadcasts(activityCallbackListener);
		}
	}
	
	
	public void getElseFetchFromServiceUserLikes(ViewCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		if (!forceDownload && getCache().containsUserLikes()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_LIKES);
		} 
		else 
		{
			fetchFromServiceUserLikes(activityCallbackListener);
		}
	}
	
	/**
	 * This should only be used from the BroadcastPageActivity
	 * @param activityCallbackListener
	 * @param forceDownload
	 * @param channelId
	 * @param beginTimeInMillis
	 */
	public void getElseFetchFromServiceBroadcastPageData(
			ViewCallbackListener activityCallbackListener, 
			final boolean forceDownload, 
			TVBroadcastWithChannelInfo broadcastWithChannelInfo, 
			final TVChannelId channelId, 
			final long beginTimeInMillis) 
	{
		if (!forceDownload && (broadcastWithChannelInfo != null || getCache().containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis))) 
		{
			if(broadcastWithChannelInfo == null) 
			{
				broadcastWithChannelInfo = getCache().getNonPersistentSelectedBroadcastWithChannelInfo();
			}
			
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.BROADCAST_DETAILS, FetchRequestResultEnum.SUCCESS, broadcastWithChannelInfo);
		} 
		else 
		{
			if(channelId != null) {
				fetchFromServiceIndividualBroadcast(activityCallbackListener, channelId, beginTimeInMillis);
			} else {
				activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, RequestIdentifierEnum.BROADCAST_DETAILS);
			}
		}
	}
	
	public void getElseFetchFromServiceUpcomingBroadcasts(ViewCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey) 
	{
		//TODO NewArc check if program and then if series and then if seriesId is null?
		if (!forceDownload && 
				broadcastKey.getProgram() != null && 
				broadcastKey.getProgram().getSeries() != null &&
				getCache().containsUpcomingBroadcastsForBroadcast(broadcastKey.getProgram().getSeries().getSeriesId())) 
		{
			UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.UPCOMING_BROADCASTS_FOR_SERIES, FetchRequestResultEnum.SUCCESS, upcomingBroadcastsForBroadcast);
		} 
		else 
		{
			fetchFromServiceUpcomingBroadcasts(activityCallbackListener, RequestIdentifierEnum.UPCOMING_BROADCASTS_FOR_SERIES, broadcastKey);
		}
	}
	
	
	public void getElseFetchFromServiceRepeatingBroadcasts(ViewCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey)
	{
		if (!forceDownload && broadcastKey.getProgram() != null && getCache().containsRepeatingBroadcastsForBroadcast(broadcastKey.getProgram().getProgramId())) 
		{
			RepeatingBroadcastsForBroadcast repeatingBroadcastsForBroadcast = getCache().getNonPersistentRepeatingBroadcasts();
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, FetchRequestResultEnum.SUCCESS, repeatingBroadcastsForBroadcast);
		} 
		else 
		{
			fetchFromServiceRepeatingBroadcasts(activityCallbackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, broadcastKey);
		}
	}
	
	
	
	@Override
	public void onResult(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier, 
			FetchRequestResultEnum result,
			Object content)
	{
		switch (requestIdentifier) 
		{
			case INTERNET_CONNECTIVITY:
			{
				handleInternetConnectionDataResponse(activityCallbackListener, requestIdentifier, result);
				break;
			}
		
			case APP_CONFIGURATION:
			case APP_VERSION: 
			case TV_DATE:
			case TV_TAG:
			case TV_CHANNEL:
			case TV_CHANNEL_IDS_DEFAULT:
			case TV_CHANNEL_IDS_USER_INITIAL_CALL:
			case TV_GUIDE_INITIAL_CALL:
			case SNTP_CALL:
			{
				handleInitialDataResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case TV_CHANNEL_IDS_USER_STANDALONE: 
			{
				handleTVChannelIdsUserResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case TV_GUIDE_STANDALONE:
			{
				handleTVChannelGuidesForSelectedDayResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case ADS_ADZERK_GET: 
			{
				// Not implemented yet
				break;
			}
			case ADS_ADZERK_SEEN:
			{
				// Not implemented yet
				break;
			}
			case USER_LOGIN: 
			{
				handleLoginResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_SIGN_UP: 
			{
				handleSignUpResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_LOGOUT: 
			{
				handleLogoutResponse(activityCallbackListener);
				break;
			}
			case USER_LOGIN_WITH_FACEBOOK_TOKEN: 
			{
				handleUserTokenWithFacebookFBTokenResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_SET_CHANNELS: 
			{
				handleSetChannelsResponse(activityCallbackListener, requestIdentifier, result);
				break;
			}
			case USER_LIKES:
			{
				handleGetUserLikesResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_ADD_LIKE: 
			{
				handleAddUserLikeResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_REMOVE_LIKE: 
			{
				handleRemoveUserLikeResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_RESET_PASSWORD_SEND_EMAIL: 
			{
				handleResetPasswordSendEmailResponse(activityCallbackListener, requestIdentifier, result);
				break;
			}
			case USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD: 
			{
				// Not implemented yet
				break;
			}
			case USER_ACTIVITY_FEED_ITEM_MORE:
			{
				handleActivityFeedMoreItemsResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case USER_ACTIVITY_FEED_ITEM:
			case USER_ACTIVITY_FEED_LIKES:
			{
				handleActivityFeedInitalFetchResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case POPULAR_ITEMS: 
			{
				handleTVBroadcastsPopularBroadcastsResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case BROADCAST_DETAILS:
			case REPEATING_BROADCASTS_FOR_PROGRAMS : /* Repetitions */
			case UPCOMING_BROADCASTS_FOR_SERIES : /* Upcoming */
			{
				handleBroadcastPageDataResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case SEARCH:
			{
				handleSearchResultResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case INTERNAL_TRACKING:
			{
				// Not implemented yet
				break;
			}
			case TV_BROADCASTS_FOR_TAGS:
			{
				handleBuildTVBroadcastsForTagsResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			default:{/* do nothing */break;}
		}
	}

	
	
	/* METHODS FOR HANDLING THE RESPONSES */
	
	private void notifyFetchDataProgressListenerMessage(String message) 
	{
		if(fetchDataProgressCallbackListener != null) 
		{
			int totalSteps = completedCountTVDataForProgressMessage + COMPLETED_COUNT_APP_DATA_THRESHOLD;
			
			fetchDataProgressCallbackListener.onFetchDataProgress(totalSteps, message);
		}
	}
	
	
	
	private void notifyFetchDataProgressListenerMessage(int totalSteps, String message) 
	{
		if(fetchDataProgressCallbackListener != null) 
		{
			fetchDataProgressCallbackListener.onFetchDataProgress(totalSteps, message);
		}
	}
	
	
	
	private void handleInternetConnectionDataResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result)
	{
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	private void handleActivityFeedMoreItemsResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<TVFeedItem> feedItems = (ArrayList<TVFeedItem>) content;
			
			if(feedItems.isEmpty()) 
			{
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT, requestIdentifier);
			} 
			else 
			{
				getCache().addMoreActivityFeedItems(feedItems);
				
				isFetchingFeedItems = false;
			
				notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.SUCCESS);
			}
		} 
		else 
		{
			notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.UNKNOWN_ERROR);
		}
	}
	
	
	private void handleActivityFeedInitalFetchResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content)
	{
		completedCountTVActivityFeed++;
		
		if(result.wasSuccessful() && content != null) 
		{
			switch (requestIdentifier) 
			{
				case USER_ACTIVITY_FEED_ITEM:
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVFeedItem> feedItems = (ArrayList<TVFeedItem>) content;
					getCache().setActivityFeed(feedItems);
					isFetchingFeedItems = false;
					notifyFetchDataProgressListenerMessage(SecondScreenApplication.sharedInstance().getString(R.string.response_activityfeed));
					break;
				}
				case USER_ACTIVITY_FEED_LIKES:
				{
					@SuppressWarnings("unchecked")
					ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
					getCache().setUserLikes(userLikes);
					notifyFetchDataProgressListenerMessage(SecondScreenApplication.sharedInstance().getString(R.string.response_user_likes));
					break;
				}
				default:{/* do nothing */break;}
			}
			
			if (completedCountTVActivityFeed >= COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD) 
			{
				completedCountTVActivityFeed = 0;
				
				notifyListenersOfRequestResult(RequestIdentifierEnum.USER_ACTIVITY_FEED_INITIAL_DATA, FetchRequestResultEnum.SUCCESS);
			}
		} 
		else if(result.hasUserTokenExpired())
		{
			notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.FORBIDDEN);
		}
		else
		{			
			notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.UNKNOWN_ERROR);
		}
	}
	
	
	/**
	 * Handle standalone fetching, i.e. not initial data fetching, of the TVChannelGuides
	 * @param activityCallbackListener
	 * @param requestIdentifier
	 * @param result
	 * @param content
	 */
	private void handleTVChannelGuidesForSelectedDayResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		isUpdatingGuide = false;
		
		if (result.wasSuccessful() && content != null) 
		{
			TVGuide tvGuide = (TVGuide) content;
			
			Log.d(TAG, "PROFILING: handleTVChannelGuidesForSelectedDayResponse: addNewTVChannelGuidesForSelectedDayUsingTvGuide");
			
			getCache().addNewTVChannelGuidesForSelectedDayUsingTvGuide(tvGuide);

			getCache().purgeTaggedBroadcastForDay(tvGuide.getTvDate());
			
			ArrayList<TVChannelGuide> guides = tvGuide.getTvChannelGuides();
			
			if(guides.isEmpty())
			{
				notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT);
			}
			else
			{
				notifyListenersOfRequestResult(requestIdentifier, FetchRequestResultEnum.SUCCESS);
			}
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	public void handleTVBroadcastsPopularBroadcastsResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) {
			@SuppressWarnings("unchecked")
			ArrayList<TVBroadcastWithChannelInfo> broadcastsPopular = (ArrayList<TVBroadcastWithChannelInfo>) content;
			getCache().setPopularBroadcasts(broadcastsPopular);
			if (!broadcastsPopular.isEmpty()) {
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
			} else {
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT, requestIdentifier);
			}
		}
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
	public void handleSearchResultResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			SearchResultsForQuery searchResultForQuery = (SearchResultsForQuery) content;
			getCache().setNonPersistentSearchResultsForQuery(searchResultForQuery);
			
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		}
		else if(result == FetchRequestResultEnum.SEARCH_CANCELED_BY_USER)
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SEARCH_CANCELED_BY_USER, requestIdentifier);
		}
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	/**
	 * @param activityCallbackListener
	 * @param requestIdentifier
	 * @param result
	 * @param content
	 */
	public void handleBroadcastPageDataResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) {
		if ((result == FetchRequestResultEnum.SUCCESS && content != null) || result == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT) {
			completedCountBroadcastPageData++;
			
			switch (requestIdentifier) {
			case BROADCAST_DETAILS: {
				if(content != null) {
					TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) content;
					
					getCache().setNonPersistentSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					/* Only fetch upcoming broadcasts if the broadcast is TV Episode */
					if(broadcastWithChannelInfo.getProgram() != null && broadcastWithChannelInfo.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) {
						completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD;
						getElseFetchFromServiceUpcomingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
					}
					
					/* Always fetch repeating, even though response can be empty */
					getElseFetchFromServiceRepeatingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
				} 
				else 
				{
					//TODO NewArc retry here instead?
					activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
				}
				break;
			}
			case REPEATING_BROADCASTS_FOR_PROGRAMS: {
				if(content != null) {
					RepeatingBroadcastsForBroadcast repeatingBroadcasts = (RepeatingBroadcastsForBroadcast) content;
				
					getCache().setNonPersistentRepeatingBroadcasts(repeatingBroadcasts);
				}
				break;
			}
			case UPCOMING_BROADCASTS_FOR_SERIES: {
				if(content != null) {
					UpcomingBroadcastsForBroadcast upcomingBroadcast = (UpcomingBroadcastsForBroadcast) content;
				
					getCache().setNonPersistentUpcomingBroadcasts(upcomingBroadcast);
				}
				break;
			}
			default:{/* do nothing */break;}
			}
			
			if (completedCountBroadcastPageData >= completedCountBroadcastPageDataThresholdUsed) 
			{
				completedCountBroadcastPageData = 0;
				
				/* Reset the threshold to not require upcoming broadcasts, since this is always used */
				completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD;

				/* We have repeating and upcoming broadcast data, and */
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.BROADCAST_PAGE_DATA);
			}
			
		} 
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}

	
	public void handleSignUpResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null)
		{
			// TODO NewArc - Refactor to SignUpCompleteData object instead?
			UserLoginData userData = (UserLoginData) content;
			getCache().setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
			
			GATrackingManager.sharedInstance().sendUserSignUpSuccessfulUsingEmailEvent();
		} 

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_SIGN_UP, result);
	}
	
	
	public void handleGetUserLikesResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful()) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
			getCache().setUserLikes(userLikes);
			
		}
		
		notifyListenersOfRequestResult(requestIdentifier, result);
	}
	
	
	public void handleAddUserLikeResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful()) 
		{
			UserLike userLike = (UserLike) content;
			
			getCache().addUserLike(userLike);
		} else {
			getCache().removeManuallyAddedUserLikes();
		}
		
		notifyListenersOfRequestResult(requestIdentifier, result);
	}
	
	
	public void handleRemoveUserLikeResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful()) 
		{
			UserLike userLike = (UserLike) content;
			
			getCache().removeUserLike(userLike);
		} 
		
		activityCallbackListener.onResult(result, requestIdentifier);
	}

	
	public void handleResetPasswordSendEmailResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if(result.wasSuccessful()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT, requestIdentifier);
		} 
		else
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.USER_RESET_PASSWORD_UNKNOWN_ERROR, requestIdentifier);
		}
	}
	
	
	public void handleUserTokenWithFacebookFBTokenResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;
					
			getCache().setUserData(userData);
			
			boolean wasJustCreated = userData.getUser().isCreated();
			
			if(wasJustCreated) {
				GATrackingManager.sharedInstance().sendUserSignUpSuccessfulUsingFacebookEvent();
			}

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		}

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGIN_WITH_FACEBOOK_TOKEN, result);
	}
	
	/**
	 * Standalone version, we will get here if we just logged in (MiTV or Facebook) or SignedUp (is a kind of login).
	 * This method is not used for the initial data fetching
	 * @param activityCallbackListener
	 * @param requestIdentifier
	 * @param result
	 * @param content
	 */
	private void handleTVChannelIdsUserResponse(ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content) 
	{
		if (result.wasSuccessful() && content != null) 
		{			
			@SuppressWarnings("unchecked")
			ArrayList<TVChannelId> tvChannelIdsUserBackend = (ArrayList<TVChannelId>) content;
	
			/* Store the TVChannelIds for the user to the cache (which also sets them to "used") */
			getCache().setTvChannelIdsUser(tvChannelIdsUserBackend);
			
			/* Now we have the TVChannelIds for the user => fetch guide */
			fetchFromServiceTVGuideForSelectedDay(activityCallbackListener);
		}
		
		if(activityCallbackListener != null) 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	


	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll) {
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
	
	
	public void handleLoginResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;
			
			getCache().setUserData(userData);
			
			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 
		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGIN, result);
	}

	
	public void handleLogoutResponse(ViewCallbackListener activityCallbackListener) {
		fetchFromServiceTVGuideForSelectedDay(activityCallbackListener);

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGOUT, FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT);
	}
	
	
	
	public void handleSetChannelsResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if (result.wasSuccessful()) 
		{
			Log.d(TAG, "No need to do anything");
		} 
		else 
		{			
			/* ActivityCallbackListener could be null if we came here from MyChannelsActiviy and performSetUserChannels was invoked just before that instance was destroyed (e.g. by "backPress") */
			if(activityCallbackListener != null) 
			{
				activityCallbackListener.onResult(result, requestIdentifier);
			}
		}
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
			
			String deviceId = GenericUtils.getDeviceId();
			
			apiClient.performInternalTracking(null, tvProgramId, deviceId);
		}
	}
	
	
	
	/* USER METHODS REGARDING LIKES */
	
	public void addUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		registerListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE, activityCallbackListener);
		/* Manually add like to cache, so that GUI gets updated directly, here we assume that the request was successful, if it was not,
		 * then this manually added like is removed from the cache */
		getCache().addUserLike(userLike);
		apiClient.addUserLike(activityCallbackListener, userLike);
	}
	
	
	public void removeUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		apiClient.removeUserLike(activityCallbackListener, userLike);
	}
	

	/* USER METHODS REGARDING SIGNUP, LOGIN AND LOGOUT */
	
	public void performSignUp(ViewCallbackListener activityCallbackListener, String email, String password, String firstname, String lastname)
	{
		String hashedPassword = GenericUtils.getSHA512PasswordHash(password);
		
		UserRegistrationData data = new UserRegistrationData();
		data.setEmail(email);
		data.setPassword(hashedPassword);
		data.setFirstName(firstname);
		data.setLastName(lastname);
		
		apiClient.performUserSignUp(activityCallbackListener, data, true);
	}

	
	public void performLogin(ViewCallbackListener activityCallbackListener, String username, String password) 
	{		
		String hashedPassword = GenericUtils.getSHA512PasswordHash(password);
		
		UserLoginDataPost data = new UserLoginDataPost();
		data.setEmail(username);
		data.setPassword(hashedPassword);
						
		apiClient.performUserLogin(activityCallbackListener, data, true);
	}

		
	public void performResetPassword(ViewCallbackListener activityCallbackListener, String email) 
	{
		apiClient.performUserPasswordResetSendEmail(activityCallbackListener, email);
	}
	
	
	public void getUserTokenWithFacebookFBToken(ViewCallbackListener activityCallbackListener, String facebookToken) 
	{
		apiClient.performUserLoginWithFacebookToken(activityCallbackListener, facebookToken);
	}

	public void performSetUserChannels(ViewCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		apiClient.performSetUserTVChannelIds(activityCallbackListener, tvChannelIds);
	}
	
	public List<TVChannelId> getFromCacheTVChannelIdsUser() 
	{
		List<TVChannelId> tvChannelIdsUser = getCache().getTvChannelIdsUsed();
		
		return tvChannelIdsUser;
	}
	
	
	
	
	public boolean getFromCacheHasInitialData()
	{
		boolean hasInitialData = getCache().containsAppConfigData() && 
								 getCache().containsAppVersionData() &&
								 getCache().containsTVDates() && 
								 getCache().containsTVTags() &&
								 getCache().containsTVChannels() &&
								 getCache().containsTVGuideForSelectedDay();
		
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
	
	public boolean getFromCacheHasTVTags() {
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
	
	public boolean getFromCacheHasUserTVChannelIds() {
		return getCache().containsTVChannelIdsUser();
	}
	
	public boolean getFromCacheHasTVChannelsAll() {
		return getCache().containsTVChannels();
	}
	
	public boolean getFromCacheHasTVBroadcastWithChannelInfo(TVChannelId channelId, long beginTimeInMillis)
	{
		return getCache().containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis);
	}
	
	
	public boolean getFromCacheHasBroadcastPageData( ) 
	{
		boolean hasBroadcastPageData = false;
		
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = getCache().getNonPersistentSelectedBroadcastWithChannelInfo();

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

	

	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ViewCallbackListener activityCallbackListener, int tvDateIndex) 
	{
		/* Update the index in the storage */
		setTVDateSelectedUsingIndex(tvDateIndex);

		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = getCache().getTvDateSelected();

		/*
		 * Since selected TVDate has been changed, set/fetch the TVGuide for
		 * that day
		 */
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, false, tvDate);
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
		if(tvDateSelected != null) {
			isToday = tvDateSelected.isToday();
		}
		return isToday;
	
	}

	
	private void setTVDateSelectedUsingIndex(int tvDateIndex) 
	{
		/* Update the index in the storage */
		getCache().setTvDateSelectedUsingIndex(tvDateIndex);
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
	
	public SearchResultsForQuery getFromCacheSearchResults() {
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
	
	public void setUpcomingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		//TODO NewArc check if null
		String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		
		UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
		
		getCache().setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
	}
	
	
	public void setRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		
		RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(programId, repeatingBroadcasts);
		
		getCache().setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
	}
	
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a tvSeriesId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheUpcomingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		if (broadcast.getProgram().getSeries() == null) {
			return null;
			
		} else {
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
			
			if(getCache().containsUpcomingBroadcastsForBroadcast(tvSeriesId)) {
				UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
				if(upcomingBroadcastsForBroadcast != null) {
					upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
				}
			}
			
			return upcomingBroadcasts;
		}
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
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		
		if(getCache().containsRepeatingBroadcastsForBroadcast(programId)) 
		{
			RepeatingBroadcastsForBroadcast repeatingBroadcastObject = getCache().getNonPersistentRepeatingBroadcasts();
			
			if(repeatingBroadcastObject != null) 
			{
				repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcastsWithExclusions(broadcast);
			}
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
	
	
	public void setSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo selectedBroadcast) {
		getCache().setNonPersistentSelectedBroadcastWithChannelInfo(selectedBroadcast);
	}
	
	public TVBroadcastWithChannelInfo getFromCacheSelectedBroadcastWithChannelInfo() {
		TVBroadcastWithChannelInfo runningBroadcast = getCache().getNonPersistentSelectedBroadcastWithChannelInfo();
		return runningBroadcast;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsForSelectedTVDate() {
		TVDate tvDate = getFromCacheTVDateSelected();
		return getFromCacheTaggedBroadcastsUsingTVDate(tvDate);
	}
	
	public int getFromCacheSelectedHour() {
		int selectedHourAsInt;
		Integer selectedHour = getCache().getNonPersistentSelectedHour();
		if(selectedHour != null) {
			selectedHourAsInt = selectedHour.intValue();
		} else {
			selectedHourAsInt = DateUtils.getCurrentHourOn24HourFormat();
		}
		return selectedHourAsInt;
	}
	
	public void setSelectedHour(Integer selectedHour) {
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
	
	public String getFromCacheUserLastname() {
		String userLastname = getCache().getUserLastname();
		return userLastname;
	}
	
	public String getFromCacheUserFirstname() 
	{
		String userFirstname = getCache().getUserFirstname();
		return userFirstname;
	}
	
	public String getFromCacheUserEmail() {
		String userEmail = getCache().getUserEmail();
		return userEmail;
	}
	
	public String getFromCacheUserId() {
		String userId = getCache().getUserId();
		return userId;
	}
	
	public String getFromCacheUserProfileImage() {
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
	
	public boolean isContainedInUsedChannelIds(TVChannelId channelId) {
		boolean isContainedInUsedChannelIds = getCache().isInUsedChannelIds(channelId);
		return isContainedInUsedChannelIds;
	}
		
	public TVChannel getFromCacheTVChannelById(TVChannelId tvChannelId)
	{
		return getCache().getTVChannelById(tvChannelId);
	}
	
	public String getFromCacheWelcomeMessage() {
		String welcomeMessage = getCache().getWelcomeMessage();
		return welcomeMessage;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsUsingTVDate(TVDate tvDate) {
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcasts = getCache().getTaggedBroadcastsUsingTVDate(tvDate);
		return taggedBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCachePopularBroadcasts() {
		ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts = getCache().getPopularBroadcasts();
		return popularBroadcasts;
	}
	
	public List<TVDate> getFromCacheTVDates() 
	{
		 List<TVDate> tvDates = getCache().getTvDates();
		 
		 return tvDates;
	}
	
	public void setReturnActivity(Class<?> returnActivity) {
		getCache().setReturnActivity(returnActivity);
	}
	
	/**
	 * This method tries to start the return activity stored in the cache, if null it does nothing and returns false
	 * else it starts the activity and sets it to null and returns true
	 * @param caller
	 * @return
	 */
	public boolean tryStartReturnActivity(Activity caller) {
		boolean returnActivityWasSet = getReturnActivity() != null;
		
		if(returnActivityWasSet) {
			Intent intent = new Intent(caller, getReturnActivity());
			getCache().clearReturnActivity();
			caller.startActivity(intent);
		}
		
		return returnActivityWasSet;
	}

	
	public Class<?> getReturnActivity() {
		Class<?> returnActivity = getCache().getReturnActivity();
		return returnActivity;
	}
		
	public void setLikeToAddAfterLogin(UserLike userLikeToAdd) {
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
	
	
	
	private void clearUserCache() 
	{
		getCache().clearUserData();
		getCache().clearTVChannelIdsUser();
		getCache().useDefaultChannelIds();
		getCache().clearUserLikes();
	}
	
	
	
	public void performLogout(
			ViewCallbackListener activityCallbackListener,
			boolean isSessionExpiredLogout)
	{	
		Log.d(TAG, "PROFILING: performLogout:");
		
		clearUserCache();
		
		if(isSessionExpiredLogout == false)
		{
			apiClient.performUserLogout(activityCallbackListener);
		}
	}
}
