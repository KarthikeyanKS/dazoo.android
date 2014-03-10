
package com.mitv;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.mitv.asynctasks.local.BuildTVBroadcastsForTags;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.listadapters.AdListAdapter;
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
import com.mitv.utilities.GenericUtils;



public class ContentManager 
	implements ContentCallbackListener 
{
	private static final String TAG = ContentManager.class.getName();
	
	private static ContentManager sharedInstance;
	private Cache cache;
	private APIClient apiClient;
	
	private FetchDataProgressCallbackListener fetchDataProgressCallbackListener;
	
	private static final int ACTIVITY_FEED_ITEMS_BATCH_FETCH_COUNT = 10;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;
	private boolean channelsChange = false;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESHOLD = 1;
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD = 5;
	private static final int COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD = 2;
	private static int completedCountTVDataForProgressMessage = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD + 1; //+
	private int completedCountTVData = 0;
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
	
	
	
	private ContentManager() 
	{
		this.cache = new Cache();
		this.apiClient = new APIClient(this);
		
		/* 1 for guide and parsing of tagged broadcasts */
		completedCountTVDataForProgressMessage = 1;
		
		if (cache.isLoggedIn())
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
		if (sharedInstance == null) 
		{
			sharedInstance = new ContentManager();
		}
		
		return sharedInstance;
	}
	
	
	
	public void buildTVBroadcastsForTags(ActivityCallbackListener activityCallbackListener)
	{
		TVDate tvDate = getFromCacheTVDateSelected();
		
		boolean containsTaggedBroadcastsForTVDate = cache.containsTaggedBroadcastsForTVDate(tvDate);
		
		if(containsTaggedBroadcastsForTVDate == false)
		{
			ArrayList<TVChannelGuide> tvChannelGuides = getFromCacheTVGuideForSelectedDay().getTvChannelGuides();
			
			BuildTVBroadcastsForTags buildTVBroadcastsForTags = new BuildTVBroadcastsForTags(tvChannelGuides, this, activityCallbackListener);
			buildTVBroadcastsForTags.execute();
		}
		else
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_BROADCASTS_FOR_TAGS);
		}
	}
	
	
	
	private void handleBuildTVBroadcastsForTagsResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = (HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>) content;
		
		cache.addTaggedBroadcastsForSelectedDay(mapTagToTaggedBroadcastForDate);
		
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	
	public void fetchFromServiceInitialCall(ActivityCallbackListener activityCallbackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener)
	{		
		this.completedTVDatesRequest = false;
		this.completedTVChannelIdsDefaultRequest = false;
		this.completedTVChannelIdsUserRequest = false;
		this.isFetchingTVGuide = false;
		this.isAPIVersionTooOld = false;
		
		this.fetchDataProgressCallbackListener = fetchDataProgressCallbackListener;
		
		boolean isUserLoggedIn = cache.isLoggedIn();
		
		apiClient.getInitialDataOnPoolExecutor(activityCallbackListener, isUserLoggedIn);
	}
	
	
	
	private void handleInitialDataResponse(
			ActivityCallbackListener activityCallbackListener,
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
		
		if(cache.isLoggedIn())
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
					
					cache.setAppConfigData(appConfigData);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_configuration_data));
				}
				break;
			}
			
			case APP_VERSION: 
			{
				if(result.wasSuccessful() && content != null) 
				{
					AppVersion appVersionData = (AppVersion) content;
					
					cache.setAppVersionData(appVersionData);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_app_version_data));
				
					boolean isAPIVersionSupported = cache.isAPIVersionSupported();
					
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
					cache.setTvDates(tvDates);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_dates_data));
					
					if(!isFetchingTVGuide && 
					   (completedTVChannelIdsDefaultRequest && !cache.isLoggedIn()) || 
					   (completedTVChannelIdsUserRequest && cache.isLoggedIn()))
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = cache.getTvDateSelected();
						
						ArrayList<TVChannelId> tvChannelIds = cache.getTvChannelIdsUsed();
						
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
					
					cache.setTvChannelIdsDefault(tvChannelIdsDefault);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_channel_id_data));
					
					if(!isFetchingTVGuide && completedTVDatesRequest && !cache.isLoggedIn())
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = cache.getTvDateSelected();
						
						ArrayList<TVChannelId> tvChannelIds = cache.getTvChannelIdsUsed();
						
						apiClient.getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
					}
				}
				break;
			}
			
			case TV_CHANNEL_IDS_USER:
			{
				if(result.wasSuccessful() && content != null) 
				{
					completedTVChannelIdsUserRequest = true;
					
					@SuppressWarnings("unchecked")
					ArrayList<TVChannelId> tvChannelIdsUser = (ArrayList<TVChannelId>) content;
					
					cache.setTvChannelIdsUser(tvChannelIdsUser);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_channel_id_data));
					
					if(!isFetchingTVGuide && completedTVDatesRequest && cache.isLoggedIn())
					{
						isFetchingTVGuide = true;
						
						TVDate tvDate = cache.getTvDateSelected();
						
						ArrayList<TVChannelId> tvChannelIds = cache.getTvChannelIdsUsed();
						
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
					
					cache.setTvTags(tvTags);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_genres_data));
				}
				break;
			}
			
			case TV_CHANNEL:
			{
				if(result.wasSuccessful() && content != null) 
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVChannel> tvChannels = (ArrayList<TVChannel>) content;
					
					cache.setTvChannels(tvChannels);
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_channel_data));
				}
				break;
			}
			
			case TV_GUIDE_INITIAL_CALL:
			{
				if(result.wasSuccessful() && content != null) 
				{
					TVGuide tvGuide = (TVGuide) content;
					
					notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_tv_guide_data));
					
					cache.addTVGuideForSelectedDay(tvGuide);
				}
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
			if(activityCallbackListener != null)
			{
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
			}
			else
			{
				Log.w(TAG, "Activity callback is null.");
			}
			
			apiClient.cancelAllPendingRequests();
		}
		else
		{
			if(result.wasSuccessful() == false || content == null)
			{
				apiClient.cancelAllPendingRequests();
				
				activityCallbackListener.onResult(result, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
			}
			else
			{
				Log.d(TAG, "There are pending tasks still running.");
			}
		}
	}
		
	
	
	/*
	 * METHODS FOR FETCHING DATA FROM BACKEND USING THE API CLIENT, NEVER USE
	 * THOSE EXTERNALLY, ALL SHOULD BE PRIVATE
	 */
	
	private void fetchFromServiceTVDataOnFirstStart(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getTVTags(activityCallbackListener);
		apiClient.getTVDates(activityCallbackListener);
		apiClient.getTVChannelsAll(activityCallbackListener);
		apiClient.getDefaultTVChannelIds(activityCallbackListener);

		if (cache.isLoggedIn()) 
		{
			apiClient.getUserTVChannelIds(activityCallbackListener);
		}
	}

	
	private void fetchFromServiceTVDataOnUserStatusChange(ActivityCallbackListener activityCallbackListener) {
		/* Handle TV Channel & Guide data, after login */
		channelsChange = true;
		apiClient.getUserTVChannelIds(activityCallbackListener);
		
		/* Add like if any was set */
		UserLike likeToAddAfterLogin = cache.getLikeToAddAfterLogin();
		if(likeToAddAfterLogin != null) {
			/* Passing null because the login views should not care about if the like was successfully added or not.
			 * According to the current architecture we MUST not allow the method onDataAvailable to be called in LoginViews,
			 * since pattern with returnActivity and method tryStartReturnActivity will break */
			addUserLike(null, likeToAddAfterLogin);
		}
	}

	
	private void fetchFromServiceTVGuideForSelectedDay(ActivityCallbackListener activityCallbackListener) 
	{
		TVDate tvDate = cache.getTvDateSelected();
		
		fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
	}
	
	
	private void fetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallbackListener, TVDate tvDate)
	{
		ArrayList<TVChannelId> tvChannelIds = cache.getTvChannelIdsUsed();
		
		apiClient.getTVChannelGuides(activityCallbackListener, tvDate, tvChannelIds);
	}
	
	
	private void fetchFromServiceActivityFeedData(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getUserTVFeedItems(activityCallbackListener);
		apiClient.getUserLikes(activityCallbackListener, false);
	}
	
	
	private void fetchFromServiceSearchResults(ActivityCallbackListener activityCallbackListener, AjaxCallback<String> ajaxCallback, String searchQuery) 
	{
		apiClient.getTVSearchResults(activityCallbackListener, ajaxCallback, searchQuery);
	}
	
	
	private void fetchFromServiceUserLikes(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getUserLikes(activityCallbackListener, true);
	}
	
	
	private void fetchFromServicePopularBroadcasts(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getTVBroadcastsPopular(activityCallbackListener);
	}
	
	
	private void fetchFromServiceUpcomingBroadcasts(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
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

	
	private void fetchFromServiceRepeatingBroadcasts(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
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
	
	
	private void fetchFromServiceIndividualBroadcast(ActivityCallbackListener activityCallbackListener, TVChannelId channelId, long beginTimeInMillis) 
	{
		apiClient.getTVBroadcastDetails(activityCallbackListener, channelId, beginTimeInMillis);
	}
	
	
	/* PUBLIC FETCH METHODS WHERE IT DOES NOT MAKE ANY SENSE TO TRY FETCHING THE DATA FROM STORAGE */
	
	public void checkNetworkConnectivity(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getNetworkConnectivityIsAvailable(activityCallbackListener);
	}
		
	
	public void fetchFromServiceMoreActivityData(ActivityCallbackListener activityCallbackListener) 
	{
		int offset = cache.getActivityFeed().size();
		
		apiClient.getUserTVFeedItemsWithOffsetAndLimit(activityCallbackListener, offset, ACTIVITY_FEED_ITEMS_BATCH_FETCH_COUNT);
	}

	/*
	 * METHODS FOR "GETTING" THE DATA, EITHER FROM STORAGE, OR FETCHING FROM
	 * BACKEND
	 */
	
	public void getElseFetchFromServiceTVGuideUsingSelectedTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		TVDate tvDateSelected = getFromCacheTVDateSelected();
		
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, forceDownload, tvDateSelected);
	}
	
	
	public void getElseFetchFromServiceSearchResultForSearchQuery(ActivityCallbackListener activityCallbackListener, boolean forceDownload, AjaxCallback<String> ajaxCallback, String searchQuery) 
	{
		if(!forceDownload && cache.containsSearchResultForQuery(searchQuery)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.SEARCH);
		} 
		else 
		{
			fetchFromServiceSearchResults(activityCallbackListener, ajaxCallback, searchQuery);
		}
	}
	
	
	public void getElseFetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVDate tvDate)
	{
		if (!forceDownload && cache.containsTVGuideForTVDate(tvDate)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
		} 
		else 
		{
			fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
		}
	}

	
	public void getElseFetchFromServiceActivityFeedData(ActivityCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		if (!forceDownload && cache.containsActivityFeedData() && cache.containsUserLikes()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM);
		} 
		else 
		{
			fetchFromServiceActivityFeedData(activityCallbackListener);
		}
	}
	
	
	public void getElseFetchFromServiceTaggedBroadcastsForSelectedTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload)
	{
		TVDate tvDateSelected = getFromCacheTVDateSelected();
		
		getElseFetchFromServiceTaggedBroadcastsUsingTVDate(activityCallbackListener, forceDownload, tvDateSelected);
	}
	
	
	public void getElseFetchFromServiceTaggedBroadcastsUsingTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVDate tvDate) 
	{
		boolean containsTaggedBroadcastsForTVDate = cache.containsTaggedBroadcastsForTVDate(tvDate);
		
		if (!forceDownload && containsTaggedBroadcastsForTVDate) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
		} 
		else
		{	
			if(!containsTaggedBroadcastsForTVDate)
			{
				buildTVBroadcastsForTags(activityCallbackListener);
			}
			else
			{
				fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
			}
		}
	}
	
	
	public void getElseFetchFromServicePopularBroadcasts(ActivityCallbackListener activityCallbackListener, boolean forceDownload)
	{
		if (!forceDownload && cache.containsPopularBroadcasts()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.POPULAR_ITEMS);
		} 
		else 
		{
			fetchFromServicePopularBroadcasts(activityCallbackListener);
		}
	}
	
	
	public void getElseFetchFromServiceUserLikes(ActivityCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		if (!forceDownload && cache.containsUserLikes()) 
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
	public void getElseFetchFromServiceBroadcastPageData(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastWithChannelInfo, TVChannelId channelId, long beginTimeInMillis) {
		if (!forceDownload && (broadcastWithChannelInfo != null || cache.containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis))) 
		{
			if(broadcastWithChannelInfo == null) {
				broadcastWithChannelInfo = cache.getNonPersistentSelectedBroadcastWithChannelInfo();
			}
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.BROADCAST_DETAILS, FetchRequestResultEnum.SUCCESS, broadcastWithChannelInfo);
		} 
		else 
		{
			fetchFromServiceIndividualBroadcast(activityCallbackListener, channelId, beginTimeInMillis);
		}
	}
	
	public void getElseFetchFromServiceUpcomingBroadcasts(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey) 
	{
		//TODO NewArc check if program and then if series and then if seriesId is null?
		if (!forceDownload && 
				broadcastKey.getProgram() != null && 
				broadcastKey.getProgram().getSeries() != null &&
				cache.containsUpcomingBroadcastsForBroadcast(broadcastKey.getProgram().getSeries().getSeriesId())) 
		{
			UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = cache.getNonPersistentUpcomingBroadcasts();
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, FetchRequestResultEnum.SUCCESS, upcomingBroadcastsForBroadcast);
		} 
		else 
		{
			fetchFromServiceUpcomingBroadcasts(activityCallbackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, broadcastKey);
		}
	}
	
	
	public void getElseFetchFromServiceRepeatingBroadcasts(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey)
	{
		if (!forceDownload && broadcastKey.getProgram() != null && cache.containsRepeatingBroadcastsForBroadcast(broadcastKey.getProgram().getProgramId())) 
		{
			RepeatingBroadcastsForBroadcast repeatingBroadcastsForBroadcast = cache.getNonPersistentRepeatingBroadcasts();
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, FetchRequestResultEnum.SUCCESS, repeatingBroadcastsForBroadcast);
		} 
		else 
		{
			fetchFromServiceRepeatingBroadcasts(activityCallbackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, broadcastKey);
		}
	}
	
	
	
	@Override
	public void onResult(
			ActivityCallbackListener activityCallbackListener,
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
			case TV_CHANNEL_IDS_USER:
			case TV_GUIDE_INITIAL_CALL: 
			{
				handleInitialDataResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case TV_GUIDE_STANDALONE:
			{
				handleTVChannelGuidesForSelectedDayResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case ADS_ADZERK_GET: 
			{
				break;
			}
			case ADS_ADZERK_SEEN:
			{
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
			case USER_FB_TOKEN: 
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
				// TODO
				break;
			}
			case USER_ACTIVITY_FEED_ITEM:
			case USER_ACTIVITY_FEED_ITEM_MORE:
			case USER_ACTIVITY_FEED_LIKES:
			{
				handleActivityFeedResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case POPULAR_ITEMS: 
			{
				handleTVBroadcastsPopularBroadcastsResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			
			case BROADCAST_DETAILS:
			case REPEATING_BROADCASTS_FOR_PROGRAMS : /* Repetitions */
			case BROADCASTS_FROM_SERIES_UPCOMING : /* Upcoming */
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
				// TODO
				break;
			}
			case TV_BROADCASTS_FOR_TAGS:
			{
				handleBuildTVBroadcastsForTagsResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
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
			ActivityCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result)
	{
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	
	private void handleActivityFeedResponse(
			ActivityCallbackListener activityCallbackListener,
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
					cache.setActivityFeed(feedItems);
					notifyFetchDataProgressListenerMessage(SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_activityfeed));
					break;
				}
				
				case USER_ACTIVITY_FEED_ITEM_MORE:
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVFeedItem> feedItems = (ArrayList<TVFeedItem>) content;
					cache.addMoreActivityFeedItems(feedItems);
					notifyFetchDataProgressListenerMessage(SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_activityfeed_more));
					break;
				}
				
				case USER_ACTIVITY_FEED_LIKES:
				{
					@SuppressWarnings("unchecked")
					ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
					cache.setUserLikes(userLikes);
					notifyFetchDataProgressListenerMessage(SecondScreenApplication.sharedInstance().getResources().getString(R.string.response_user_likes));
					break;
				}
				default:
				{
					// Do nothing
					break;
				}
			}
			
			if (completedCountTVActivityFeed >= COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD) 
			{
				completedCountTVActivityFeed = 0;
				
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM);
			}
		} 
		else 
		{
			//TODO handle this better?
			activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
	}
	
	
	
	private void handleTVChannelGuidesForSelectedDayResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
    {
        if (result.wasSuccessful() && content != null)
        {
            TVGuide tvGuide = (TVGuide) content;

            cache.addTVGuideForSelectedDay(tvGuide);
            
            buildTVBroadcastsForTags(activityCallbackListener);
        }
        else
        {
            activityCallbackListener.onResult(result, requestIdentifier);
        }
    }
	
	
	
	public void handleTVBroadcastsPopularBroadcastsResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<TVBroadcastWithChannelInfo> broadcastsPopular = (ArrayList<TVBroadcastWithChannelInfo>) content;
			cache.setPopularBroadcasts(broadcastsPopular);
			
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		}
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
	public void handleSearchResultResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			SearchResultsForQuery searchResultForQuery = (SearchResultsForQuery) content;
			cache.setNonPersistentSearchResultsForQuery(searchResultForQuery);
			
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
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
	public void handleBroadcastPageDataResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) {
		if ((result == FetchRequestResultEnum.SUCCESS && content != null) || result == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT) {
			completedCountBroadcastPageData++;
			
			switch (requestIdentifier) {
			case BROADCAST_DETAILS: {
				if(content != null) {
					TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) content;
					
					cache.setNonPersistentSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					/* Only fetch upcoming broadcasts if the broadcast is TV Episode */
					if(broadcastWithChannelInfo.getProgram() != null && broadcastWithChannelInfo.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) {
						completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD;
						ContentManager.sharedInstance().getElseFetchFromServiceUpcomingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
					}
					
					/* Always fetch repeating, even though response can be empty */
					ContentManager.sharedInstance().getElseFetchFromServiceRepeatingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
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
				
					cache.setNonPersistentRepeatingBroadcasts(repeatingBroadcasts);
				}
				break;
			}
			case BROADCASTS_FROM_SERIES_UPCOMING: {
				if(content != null) {
					UpcomingBroadcastsForBroadcast upcomingBroadcast = (UpcomingBroadcastsForBroadcast) content;
				
					cache.setNonPersistentUpcomingBroadcasts(upcomingBroadcast);
				}
				break;
			}
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

	
	public void handleSignUpResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null)
		{
			// TODO NewArc - Refactor to SignUpCompleteData object instead?
			UserLoginData userData = (UserLoginData) content;
			cache.setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleGetUserLikesResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful()) 
		{
			ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
			cache.setUserLikes(userLikes);
			
		}
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	public void handleAddUserLikeResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful()) 
		{
			UserLike userLike = (UserLike) content;
			
			cache.addUserLike(userLike);
		} 
		
		if(activityCallbackListener != null) {
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleRemoveUserLikeResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful()) 
		{
			UserLike userLike = (UserLike) content;
			
			cache.removeUserLike(userLike);
		} 
		
		activityCallbackListener.onResult(result, requestIdentifier);
	}

	
	public void handleResetPasswordSendEmailResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
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
	
	
	public void handleUserTokenWithFacebookFBTokenResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;
			
			cache.setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallBackListener);
		} 
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	
	public void handleLoginResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;
			
			cache.setUserData(userData);
			
			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}

	
	public void handleLogoutResponse(ActivityCallbackListener activityCallbackListener) {
		channelsChange = true;
		fetchFromServiceTVGuideForSelectedDay(activityCallbackListener);
	}
	
	
	
	public void handleSetChannelsResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if (result.wasSuccessful()) 
		{
			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 
		else 
		{
			/* ActivityCallbackListener could be null if we came here from MyChannelsActiviy and performSetUserChannels was invoked just before that instance was destroyed (e.g. by "backPress") */
			if(activityCallbackListener != null) {
				activityCallbackListener.onResult(result, requestIdentifier);
			}
		}
	}
	
	
	/* This method does not require any ActivityCallbackListener, "fire and forget". */
	public void performInternalTracking(TVBroadcastWithChannelInfo broadcast) 
	{
		if(broadcast != null && broadcast.getProgram() != null && broadcast.getProgram().getProgramId() != null && !TextUtils.isEmpty(broadcast.getProgram().getProgramId())) 
		{
			String tvProgramId = broadcast.getProgram().getProgramId();
			
			//TODO NewArc use a better method for device id!
			String deviceId = GenericUtils.getDeviceId();
			
			apiClient.performInternalTracking(null, tvProgramId, deviceId);
		}
	}
	
	
	
	/* USER METHODS REGARDING LIKES */
	
	public void addUserLike(ActivityCallbackListener activityCallbackListener, UserLike userLike) 
	{
		apiClient.addUserLike(activityCallbackListener, userLike);
	}
	
	
	public void removeUserLike(ActivityCallbackListener activityCallbackListener, UserLike userLike) 
	{
		apiClient.removeUserLike(activityCallbackListener, userLike);
	}
	

	/* USER METHODS REGARDING SIGNUP, LOGIN AND LOGOUT */
	
	public void performSignUp(ActivityCallbackListener activityCallbackListener, String email, String password, String firstname, String lastname) {
		apiClient.performUserSignUp(activityCallbackListener, email, password, firstname, lastname);
	}

	public void performLogin(ActivityCallbackListener activityCallbackListener, String username, String password) {
		apiClient.performUserLogin(activityCallbackListener, username, password);
	}

	public void performLogout(ActivityCallbackListener activityCallbackListener) {
		/* Important, we need to clear the cache as well */
		cache.clearUserData();
		cache.clearTVChannelIdsUser();
		cache.useDefaultChannelIds();
		
		apiClient.performUserLogout(activityCallbackListener);
	}
	
	public void performResetPassword(ActivityCallbackListener activityCallbackListener, String email) {
		apiClient.performUserPasswordResetSendEmail(activityCallbackListener, email);
	}
	
	public void getUserTokenWithFacebookFBToken(ActivityCallbackListener activityCallBackListener, String facebookToken) 
	{
		apiClient.performUserLoginWithFacebookToken(activityCallBackListener, facebookToken);
	}

	public void performSetUserChannels(ActivityCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		apiClient.performSetUserTVChannelIds(activityCallbackListener, tvChannelIds);
	}
	
	public ArrayList<TVChannelId> getFromCacheTVChannelIdsUser() 
	{
		ArrayList<TVChannelId> tvChannelIdsUser = cache.getTvChannelIdsUsed();
		return tvChannelIdsUser;
	}

	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ActivityCallbackListener activityCallbackListener, int tvDateIndex) 
	{
		/* Update the index in the storage */
		setTVDateSelectedUsingIndex(tvDateIndex);

		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = cache.getTvDateSelected();

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
		TVDate tvDateSelected = cache.getTvDateSelected();
		return tvDateSelected;
	}
	
	
	public int getFromCacheTVDateSelectedIndex() 
	{
		int tvDateSelectedIndex = cache.getTvDateSelectedIndex();
		return tvDateSelectedIndex;
	}
	
	
	public int getFromCacheFirstHourOfTVDay() 
	{
		int firstHourOfDay = cache.getFirstHourOfTVDay();
		return firstHourOfDay;
	}
	
	
	public boolean selectedTVDateIsToday() 
	{
		TVDate tvDateSelected = getFromCacheTVDateSelected();
		
		boolean isToday = tvDateSelected.isToday();
		
		return isToday;
	
	}

	
	private void setTVDateSelectedUsingIndex(int tvDateIndex) 
	{
		/* Update the index in the storage */
		cache.setTvDateSelectedUsingIndex(tvDateIndex);
	}

	/* TVTags */
	public ArrayList<TVTag> getFromCacheTVTags() 
	{
		ArrayList<TVTag> tvTags = cache.getTvTags();
		return tvTags;
	}
	
	
	/* TVChannelGuide */
	public TVGuide getFromCacheTVGuideForSelectedDay() 
	{
		TVDate tvDate = getFromCacheTVDateSelected();
		TVGuide tvGuide = cache.getTVGuideUsingTVDate(tvDate);
		return tvGuide;
	}
	
	public SearchResultsForQuery getFromCacheSearchResults() {
		SearchResultsForQuery searchResultForQuery = cache.getNonPersistentSearchResultsForQuery();
		return searchResultForQuery;
	}
	
	public AppConfiguration getFromCacheAppConfiguration()
	{
		AppConfiguration appConfiguration = cache.getAppConfigData();
		
		return appConfiguration;
	}
	
	
	public TVChannelGuide getFromCacheTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		TVChannelGuide tvChannelGuide = cache.getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
		return tvChannelGuide;
	}

	
	public ArrayList<TVFeedItem> getFromCacheActivityFeedData() 
	{
		ArrayList<TVFeedItem> activityFeedData = cache.getActivityFeed();
		return activityFeedData;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap<String, AdListAdapter> getFromCacheAdapterMap() {
		HashMap<String, AdListAdapter> adapterMap = cache.getAdapterMap();
		return adapterMap;
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
		String userToken = cache.getUserToken();
		return userToken;
	}
	
	public boolean isLoggedIn() 
	{
		boolean isLoggedIn = cache.isLoggedIn();
		return isLoggedIn;
	}
	
	
	
	/* NON-PERSISTENT USER DATA, TEMPORARY SAVED IN STORAGE, IN ORDER TO PASS DATA BETWEEN ACTIVITES */
	
	public void setUpcomingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		//TODO NewArc check if null
		String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
		cache.setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
	}
	
	
	public void setRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(programId, repeatingBroadcasts);
		cache.setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
	}
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a tvSeriesId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheUpcomingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		//TODO NewArc check if null
		String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		
		if(cache.containsUpcomingBroadcastsForBroadcast(tvSeriesId)) {
			UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = cache.getNonPersistentUpcomingBroadcasts();
			if(upcomingBroadcastsForBroadcast != null) {
				upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
			}
		}
		
		return upcomingBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheUpcomingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = cache.getNonPersistentUpcomingBroadcasts();
		if(upcomingBroadcastsForBroadcast != null) {
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
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheRepeatingBroadcastsVerifyCorrect(TVBroadcast broadcast) {
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		if(cache.containsRepeatingBroadcastsForBroadcast(programId)) {
			RepeatingBroadcastsForBroadcast repeatingBroadcastObject = cache.getNonPersistentRepeatingBroadcasts();
			if(repeatingBroadcastObject != null) {
				repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
			}
		}
		return repeatingBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCacheRepeatingBroadcasts() {
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		RepeatingBroadcastsForBroadcast repeatingBroadcastObject = cache.getNonPersistentRepeatingBroadcasts();
		if(repeatingBroadcastObject != null) {
			repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
		}return repeatingBroadcasts;
	}
		
	public void setSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo selectedBroadcast) {
		cache.setNonPersistentSelectedBroadcastWithChannelInfo(selectedBroadcast);
	}
	
	public TVBroadcastWithChannelInfo getFromCacheSelectedBroadcastWithChannelInfo() {
		TVBroadcastWithChannelInfo runningBroadcast = cache.getNonPersistentSelectedBroadcastWithChannelInfo();
		return runningBroadcast;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsForSelectedTVDate() {
		TVDate tvDate = getFromCacheTVDateSelected();
		return getFromCacheTaggedBroadcastsUsingTVDate(tvDate);
	}
	
	public int getFromCacheSelectedHour() {
		int selectedHour = cache.getNonPersistentSelectedHour();
		return selectedHour;
	}
	
	public void setSelectedHour(Integer selectedHour) {
		cache.setNonPersistentSelectedHour(selectedHour);
	}
	
	public void setSelectedTVChannelId(TVChannelId tvChannelId) 
	{
		cache.setNonPersistentTVChannelId(tvChannelId);
	}
	
	public TVChannelId getFromCacheSelectedTVChannelId() 
	{
		TVChannelId tvChannelId = cache.getNonPersistentTVChannelId();
		return tvChannelId;
	}
	
	public String getFromCacheUserLastname() {
		String userLastname = cache.getUserLastname();
		return userLastname;
	}
	
	public String getFromCacheUserFirstname() 
	{
		String userFirstname = cache.getUserFirstname();
		return userFirstname;
	}
	
	public String getFromCacheUserEmail() {
		String userEmail = cache.getUserEmail();
		return userEmail;
	}
	
	public String getFromCacheUserId() {
		String userId = cache.getUserId();
		return userId;
	}
	
	public String getFromCacheUserProfileImage() {
		String userId = cache.getUserProfileImageUrl();
		return userId;
	}
	
	public ArrayList<TVChannel> getFromCacheTVChannelsAll() {
		ArrayList<TVChannel> tvChannelsAll = cache.getTvChannels();
		return tvChannelsAll;
	}
	
	public ArrayList<UserLike> getFromCacheUserLikes()
	{
		ArrayList<UserLike> userLikes = cache.getUserLikes();
		return userLikes;
	}
	
	
	public boolean isContainedInUserLikes(UserLike userLike)
	{
		boolean isContainedInUserLikes = cache.isInUserLikes(userLike);
		return isContainedInUserLikes;
	}
	
	public boolean isContainedInUsedChannelIds(TVChannelId channelId) {
		boolean isContainedInUsedChannelIds = cache.isInUsedChannelIds(channelId);
		return isContainedInUsedChannelIds;
	}
	
	// TODO NewArc remove this?
	public TVChannel getFromCacheTVChannelById(String channelId)
	{
		TVChannelId tvChannelId = new TVChannelId(channelId);
		return getFromCacheTVChannelById(tvChannelId);
	}
	
	public TVChannel getFromCacheTVChannelById(TVChannelId tvChannelId)
	{
		return cache.getTVChannelById(tvChannelId);
	}
	
	public String getFromCacheWelcomeMessage() {
		String welcomeMessage = cache.getWelcomeMessage();
		return welcomeMessage;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromCacheTaggedBroadcastsUsingTVDate(TVDate tvDate) {
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcasts = cache.getTaggedBroadcastsUsingTVDate(tvDate);
		return taggedBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromCachePopularBroadcasts() {
		ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts = cache.getPopularBroadcasts();
		return popularBroadcasts;
	}
	
	public ArrayList<TVDate> getFromCacheTVDates() {
		 ArrayList<TVDate> tvDates = cache.getTvDates();
		 return tvDates;
	}
	
	public void setReturnActivity(Class<?> returnActivity) {
		cache.setReturnActivity(returnActivity);
	}
	
	public Class<?> getReturnActivity() {
		Class<?> returnActivity = cache.getReturnActivity();
		return returnActivity;
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
			cache.clearReturnActivity();
			caller.startActivity(intent);
		}
		
		return returnActivityWasSet;
	}
	
	public void setLikeToAddAfterLogin(UserLike userLikeToAdd) {
		cache.setLikeToAddAfterLogin(userLikeToAdd);
	}
	

}
