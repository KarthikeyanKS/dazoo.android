
package com.millicom.mitv;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.interfaces.FetchDataProgressCallbackListener;
import com.millicom.mitv.models.AppConfiguration;
import com.millicom.mitv.models.AppVersion;
import com.millicom.mitv.models.RepeatingBroadcastsForBroadcast;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVFeedItem;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVGuideAndTaggedBroadcasts;
import com.millicom.mitv.models.TVTag;
import com.millicom.mitv.models.UpcomingBroadcastsForBroadcast;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.UserLoginData;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;



public class ContentManager 
	implements ContentCallbackListener 
{
	private static final String TAG = ContentManager.class.getName();
	
	
	private static ContentManager sharedInstance;
	private Storage storage;
	private APIClient apiClient;
	
	private FetchDataProgressCallbackListener fetchDataProgressCallbackListener;
	
	private static final int ACTIVITY_FEED_ITEMS_BATCH_FETCH_COUNT = 10;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;
	private int completedCountAppData = 0;
	private boolean channelsChange = false;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESHOLD = 1;
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD = 5;
	private static final int COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD = 2;
	private static int completedCountTVDataForProgressMessage = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD + 1; //+1 for guide and parsing of tagged broadcasts
	private int completedCountTVData = 0;
	private int completedCountTVActivityFeed = 0;

	/* Variables for fetching of BroadcastPage data */
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD = 2;
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD = 3;
	private int completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD;
	private int completedCountBroadcastPageData = 0;
	
	private ContentManager() 
	{
		this.storage = new Storage();
		this.apiClient = new APIClient(this);
	}

	
	
	public static ContentManager sharedInstance() 
	{
		if (sharedInstance == null) 
		{
			sharedInstance = new ContentManager();
		}
		
		return sharedInstance;
	}

	
	
	private void getElseFetchFromServiceTVData(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, boolean forceDownload) 
	{
		if(!forceDownload && storage.containsTVGuideForSelectedDay())
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		} 
		else
		{
			fetchFromServiceTVDataOnFirstStart(activityCallbackListener);
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

		if (storage.isLoggedIn()) 
		{
			apiClient.getUserTVChannelIds(activityCallbackListener);
		}
	}

	
	private void fetchFromServiceTVDataOnUserStatusChange(ActivityCallbackListener activityCallbackListener) 
	{
		channelsChange = true;
		apiClient.getUserTVChannelIds(activityCallbackListener);
	}
	

	
	private void fetchFromServiceTVGuideForSelectedDay(ActivityCallbackListener activityCallbackListener) {
		TVDate tvDate = storage.getTvDateSelected();
		fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
	}
	
	
	private void fetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallbackListener, TVDate tvDate)
	{
		ArrayList<TVChannelId> tvChannelIds = storage.getTvChannelIdsUsed();
		
		apiClient.getTVChannelGuides(activityCallbackListener, tvDate, tvChannelIds);
	}

	
	
	private void fetchFromServiceActivityFeedData(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getUserTVFeedItems(activityCallbackListener);
		apiClient.getUserLikes(activityCallbackListener, false);
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
	
	private void fetchFromServiceIndividualBroadcast(ActivityCallbackListener activityCallbackListener, TVChannelId channelId, long beginTimeInMillis) {
		apiClient.getTVBroadcastDetails(activityCallbackListener, channelId, beginTimeInMillis);
	}
	
	private void fetchFromServiceAppData(ActivityCallbackListener activityCallbackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener) 
	{
		this.fetchDataProgressCallbackListener = fetchDataProgressCallbackListener;
		apiClient.getAppConfiguration(activityCallbackListener);
		apiClient.getAppVersion(activityCallbackListener);
	}
	
	
	/* PUBLIC FETCH METHODS WHERE IT DOES NOT MAKE ANY SENSE TO TRY FETCHING THE DATA FROM STORAGE */
	
	public void checkNetworkConnectivity(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getNetworkConnectivityIsAvailable(activityCallbackListener);
	}
		
	public void fetchFromServiceMoreActivityData(ActivityCallbackListener activityCallbackListener) 
	{
		int offset = storage.getActivityFeed().size();
		
		apiClient.getUserTVFeedItemsWithOffsetAndLimit(activityCallbackListener, offset, ACTIVITY_FEED_ITEMS_BATCH_FETCH_COUNT);
	}
	

	
	
	/*
	 * METHODS FOR "GETTING" THE DATA, EITHER FROM STORAGE, OR FETCHING FROM
	 * BACKEND
	 */
	public void getElseFetchFromServiceAppData(ActivityCallbackListener activityCallbackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener, boolean forceDownload) {
		if(!forceDownload && storage.containsAppConfigData() && storage.containsApiVersionData()) {
			notifyFetchDataProgressListenerMessage("Fetched app configuration data");
			notifyFetchDataProgressListenerMessage("Fetched app version data");
			getElseFetchFromServiceTVData(activityCallbackListener, RequestIdentifierEnum.TV_GUIDE, false);
		} else {
			fetchFromServiceAppData(activityCallbackListener, fetchDataProgressCallbackListener);
		}
	}
	
	public void getElseFetchFromServiceTVGuideUsingSelectedTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload) {
		TVDate tvDateSelected = getFromStorageTVDateSelected();
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, forceDownload, tvDateSelected);
	}
	
	public void getElseFetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVDate tvDate)
	{
		if (!forceDownload && storage.containsTVGuideForTVDate(tvDate)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE);
		} 
		else 
		{
			fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
		}
	}

	
	public void getElseFetchFromServiceActivityFeedData(ActivityCallbackListener activityCallbackListener, boolean forceDownload) 
	{
		if (!forceDownload && storage.containsActivityFeedData() && storage.containsUserLikes()) 
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
		TVDate tvDateSelected = getFromStorageTVDateSelected();
		getElseFetchFromServiceTaggedBroadcastsUsingTVDate(activityCallbackListener, forceDownload, tvDateSelected);
	}
	
	
	public void getElseFetchFromServiceTaggedBroadcastsUsingTVDate(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVDate tvDate) 
	{
		if (!forceDownload && storage.containsTaggedBroadcastsForTVDate(tvDate)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE);
		} 
		else 
		{
			/* We don't have the tagged broadcasts, either they are not created/prepared using the TVGuide for this date, or we don't have the Guide (is that even possible?) */
			if(storage.containsTVGuideForTVDate(tvDate)) 
			{
				/* We have the guide that the tagged broadcasts are using/based upon, but the tagged broadcasts are not created/prepared/sorted/initialized */
				/* Actually this should never happen */
			} 
			else 
			{
				/* Tagged broadcasts should be prepared as part of the process of fetching the TVGuide */
				fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
			}
		}
	}
	
	public void getElseFetchFromServicePopularBroadcasts(ActivityCallbackListener activityCallbackListener, boolean forceDownload)
	{
		if (!forceDownload && storage.containsPopularBroadcasts()) 
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
		if (!forceDownload && storage.containsUserLikes())
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
		if (!forceDownload && (broadcastWithChannelInfo != null || storage.containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis))) 
		{
			if(broadcastWithChannelInfo == null) {
				broadcastWithChannelInfo = storage.getNonPersistentSelectedBroadcastWithChannelInfo();
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
		if (!forceDownload && storage.containsUpcomingBroadcastsForBroadcast(broadcastKey.getProgram().getSeries().getSeriesId())) 
		{
			UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = storage.getNonPersistentUpcomingBroadcasts();
			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, FetchRequestResultEnum.SUCCESS, upcomingBroadcastsForBroadcast);
		} 
		else 
		{
			fetchFromServiceUpcomingBroadcasts(activityCallbackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, broadcastKey);
		}
	}
	
	
	public void getElseFetchFromServiceRepeatingBroadcasts(ActivityCallbackListener activityCallbackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey)
	{
		//TODO NewArc check if program and then if programId is null?
		if (!forceDownload && storage.containsRepeatingBroadcastsForBroadcast(broadcastKey.getProgram().getProgramId())) 
		{
			RepeatingBroadcastsForBroadcast repeatingBroadcastsForBroadcast = storage.getNonPersistentRepeatingBroadcasts();
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
			{
				handleAppDataResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case TV_DATE:
			case TV_TAG:
			case TV_CHANNEL:
			case TV_CHANNEL_IDS_USER:
			case TV_CHANNEL_IDS_DEFAULT:
			{
				handleTVDataResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case TV_GUIDE: 
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
			}
			case USER_ADD_LIKE: 
			{
				handleAddUserLikeResponse(activityCallbackListener, requestIdentifier, result, content);
				break;
			}
			case USER_REMOVE_LIKE: 
			{
				handleRemoveUserLikeResponse(activityCallbackListener, requestIdentifier, result);
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
				// TODO
				break;
			}
			case INTERNAL_TRACKING:
			{
				// TODO
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
	
	
	
	private void handleInternetConnectionDataResponse(
			ActivityCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result)
	{
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	
	private void handleAppDataResponse(
			ActivityCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content) 
	{
		if(result.wasSuccessful() && content != null) 
		{
			completedCountAppData++;

			switch (requestIdentifier) 
			{
				case APP_CONFIGURATION: {
					AppConfiguration appConfigData = (AppConfiguration) content;
					storage.setAppConfigData(appConfigData);
					notifyFetchDataProgressListenerMessage("Fetched app configuration data");
					break;
				}
				case APP_VERSION: {
					AppVersion appVersionData = (AppVersion) content;
					storage.setAppVersionData(appVersionData);
					notifyFetchDataProgressListenerMessage("Fetched app version data");
					break;
				}
				default: {
					// Do nothing
					break;
				}
			}

			if (completedCountAppData >= COMPLETED_COUNT_APP_DATA_THRESHOLD) 
			{
				completedCountAppData = 0;
				String apiVersion = storage.getAppVersionData().getApiVersion();

				boolean apiTooOld = checkApiVersion(apiVersion);
				if (!apiTooOld) 
				{
					/* App version not too old, continue fetching tv data */
					getElseFetchFromServiceTVData(activityCallbackListener, RequestIdentifierEnum.TV_GUIDE, false);
				} 
				else 
				{
					activityCallbackListener.onResult(FetchRequestResultEnum.API_VERSION_TOO_OLD, requestIdentifier);
				}
			}
		} 
		else 
		{
			//TODO handle this better?
			activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
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
					storage.setActivityFeed(feedItems);
					notifyFetchDataProgressListenerMessage("Fetched user activity feed");
					break;
				}
				
				case USER_ACTIVITY_FEED_ITEM_MORE:
				{
					@SuppressWarnings("unchecked")
					ArrayList<TVFeedItem> feedItems = (ArrayList<TVFeedItem>) content;
					storage.addMoreActivityFeedItems(feedItems);
					notifyFetchDataProgressListenerMessage("Fetched more data for the user activity feed");
					break;
				}
				
				case USER_ACTIVITY_FEED_LIKES:
				{
					@SuppressWarnings("unchecked")
					ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
					storage.setUserLikes(userLikes);
					notifyFetchDataProgressListenerMessage("Fetched user likes");
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
	


	private void handleTVDataResponse(
			ActivityCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier, 
			FetchRequestResultEnum result,
			Object content) 
	{
		if (result.wasSuccessful() && content != null)
		{
			completedCountTVData++;
	
			switch (requestIdentifier) 
			{
				case TV_DATE:
				{
					ArrayList<TVDate> tvDates = (ArrayList<TVDate>) content;
					storage.setTvDates(tvDates);
	
					notifyFetchDataProgressListenerMessage("Fetched tv dates data");
					break;
				}
	
				case TV_TAG: 
				{
					ArrayList<TVTag> tvTags = (ArrayList<TVTag>) content;
					storage.setTvTags(tvTags);
					notifyFetchDataProgressListenerMessage("Fetched tv genres data");
					break;
				}
				
				case TV_CHANNEL:
				{
					ArrayList<TVChannel> tvChannels = (ArrayList<TVChannel>) content;
					storage.setTvChannels(tvChannels);
					notifyFetchDataProgressListenerMessage("Fetched tv channel data");
					break;
				}
				
				case TV_CHANNEL_IDS_DEFAULT:
				{
					ArrayList<TVChannelId> tvChannelIdsDefault = (ArrayList<TVChannelId>) content;
					storage.setTvChannelIdsDefault(tvChannelIdsDefault);
					notifyFetchDataProgressListenerMessage("Fetched tv channel id data");
					break;
				}
				
				case TV_CHANNEL_IDS_USER:
				{
					ArrayList<TVChannelId> tvChannelIdsUser = (ArrayList<TVChannelId>) content;
					storage.setTvChannelIdsUser(tvChannelIdsUser);
					notifyFetchDataProgressListenerMessage("Fetched tv channel id data");
					break;
				}
			}

			int completedCountTVDataThreshold;
			if (channelsChange) 
			{
				channelsChange = false;
				completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESHOLD;
			} 
			else
			{
				completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD;
				
				if (storage.isLoggedIn())
				{
					completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD;
					
					/* Increase global threshold by 1 since we are logged in */
					completedCountTVDataForProgressMessage++;
				}
			}

			if (completedCountTVData >= completedCountTVDataThreshold) 
			{
				completedCountTVData = 0;
				fetchFromServiceTVGuideForSelectedDay(activityCallbackListener);
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
			TVGuideAndTaggedBroadcasts tvGuideAndTaggedBroadcasts = (TVGuideAndTaggedBroadcasts) content;

			TVGuide tvGuide = tvGuideAndTaggedBroadcasts.getTvGuide();
			HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = tvGuideAndTaggedBroadcasts.getMapTagToTaggedBroadcastForDate();
			
			notifyFetchDataProgressListenerMessage("Fetched tv guide data");
			
			storage.addTVGuideForSelectedDay(tvGuide);
			storage.addTaggedBroadcastsForSelectedDay(mapTagToTaggedBroadcastForDate);

			/*
			 * If the activityCallbackListener is null, then possibly the activity is already finished and there is no need to notify on the result.
			 */
			if(activityCallbackListener != null) 
			{
				activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
			}
			else 
			{
				Log.w(TAG, "Activity callback is null.");
			}
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
			storage.setPopularBroadcasts(broadcastsPopular);
			
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		}
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}
	
//	public void handleIndividuaBroadcastDetailsRespons(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) {
//		if (result.wasSuccessful() && content != null) {
//			TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) content;
//		
//			storage.setNonPersistentSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
//			
//			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
//		} else {
//			//TODO handle this better?
//			activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
//		}
//	}
	
	
	/**
	 * 	private static final int COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD = 2;
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD = 3;
	private int completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD;
	private int completedCountBroadcastPageData = 0;
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
				
				storage.setNonPersistentSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				/* Only fetch upcoming broadcasts if the broadcast is TV Episode */
				if(broadcastWithChannelInfo.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) {
					completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD;
					ContentManager.sharedInstance().getElseFetchFromServiceUpcomingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
				}
				
				/* Always fetch repeating, even though response can be empty */
				ContentManager.sharedInstance().getElseFetchFromServiceRepeatingBroadcasts(activityCallbackListener, false, broadcastWithChannelInfo);
				} else {
					//TODO NewArc retry here instead?
					activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
				}
				break;
			}
			case REPEATING_BROADCASTS_FOR_PROGRAMS: {
				if(content != null) {
					RepeatingBroadcastsForBroadcast repeatingBroadcasts = (RepeatingBroadcastsForBroadcast) content;
				
					storage.setNonPersistentRepeatingBroadcasts(repeatingBroadcasts);
				}
				break;
			}
			case BROADCASTS_FROM_SERIES_UPCOMING: {
				if(content != null) {
					UpcomingBroadcastsForBroadcast upcomingBroadcast = (UpcomingBroadcastsForBroadcast) content;
				
					storage.setNonPersistentUpcomingBroadcasts(upcomingBroadcast);
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
			
		} else {
			//TODO handle this better?
			activityCallbackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
	}

	
	public void handleSignUpResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null)
		{
			// TODO NewArc - Refactor to SignUpCompleteData object instead?
			UserLoginData userData = (UserLoginData) content;
			storage.setUserData(userData);

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
			storage.setUserLikes(userLikes);
		}
		
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	public void handleAddUserLikeResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful()) 
		{
			UserLike userLike = (UserLike) content;
			
			storage.addUserLike(userLike);
		} 
		
		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	public void handleRemoveUserLikeResponse(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if (result.wasSuccessful()) 
		{
			// TODO NewArc - Implement this
			UserLike userLike = new UserLike(LikeTypeResponseEnum.PROGRAM, "");
			
			storage.removeUserLike(userLike);
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
			
			storage.setUserData(userData);

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
			
			storage.setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 
		else 
		{
			activityCallbackListener.onResult(result, requestIdentifier);
		}
	}

	
	public void handleLogoutResponse(ActivityCallbackListener activityCallbackListener) {
		channelsChange = true;

		storage.clearUserData();
		storage.clearTVChannelIdsUser();
		storage.useDefaultChannelIds();

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
			activityCallbackListener.onResult(result, requestIdentifier);
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
	
	public void addUserLike(ActivityCallbackListener activityCallbackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		apiClient.addUserLike(activityCallbackListener, likeType, contentId);
	}
	
	
	public void removeUserLike(ActivityCallbackListener activityCallbackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		apiClient.removeUserLike(activityCallbackListener, likeType, contentId);
	}
	

	/* USER METHODS REGARDING SIGNUP, LOGIN AND LOGOUT */
	
	public void performSignUp(ActivityCallbackListener activityCallbackListener, String email, String password, String firstname, String lastname) {
		apiClient.performUserSignUp(activityCallbackListener, email, password, firstname, lastname);
	}

	public void performLogin(ActivityCallbackListener activityCallbackListener, String username, String password) {
		apiClient.performUserLogin(activityCallbackListener, username, password);
	}

	public void performLogout(ActivityCallbackListener activityCallbackListener) {
		apiClient.performUserLogout(activityCallbackListener);
	}
	
	public void performResetPassword(ActivityCallbackListener activityCallbackListener, String email) {
		apiClient.performUserPasswordResetSendEmail(activityCallbackListener, email);
	}
	
	public void getUserTokenWithFacebookFBToken(ActivityCallbackListener activityCallBackListener, String facebookToken) 
	{
		apiClient.getUserTokenUsingFBToken(activityCallBackListener, facebookToken);
	}

	public void performSetUserChannels(ActivityCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		apiClient.performSetUserTVChannelIds(activityCallbackListener, tvChannelIds);
	}
	
	public ArrayList<TVChannelId> getFromStorageTVChannelIdsUser() 
	{
		ArrayList<TVChannelId> tvChannelIdsUser = storage.getTvChannelIdsUsed();
		return tvChannelIdsUser;
	}

	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ActivityCallbackListener activityCallbackListener, int tvDateIndex) 
	{
		/* Update the index in the storage */
		setTVDateSelectedUsingIndex(tvDateIndex);

		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = storage.getTvDateSelected();

		/*
		 * Since selected TVDate has been changed, set/fetch the TVGuide for
		 * that day
		 */
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, false, tvDate);
	}

	
	
	/* GETTERS & SETTERS */
	
	/* TVDate getters and setters */
	public TVDate getFromStorageTVDateSelected() 
	{
		TVDate tvDateSelected = storage.getTvDateSelected();
		return tvDateSelected;
	}
	
	
	public int getFromStorageTVDateSelectedIndex() 
	{
		int tvDateSelectedIndex = storage.getTvDateSelectedIndex();
		return tvDateSelectedIndex;
	}
	
	
	public int getFromStorageFirstHourOfTVDay() 
	{
		int firstHourOfDay = storage.getFirstHourOfTVDay();
		return firstHourOfDay;
	}
	
	
	public boolean selectedTVDateIsToday() 
	{
		TVDate tvDateSelected = getFromStorageTVDateSelected();
		
		boolean isToday = tvDateSelected.isToday();
		
		return isToday;
	
	}

	
	private void setTVDateSelectedUsingIndex(int tvDateIndex) 
	{
		/* Update the index in the storage */
		storage.setTvDateSelectedUsingIndex(tvDateIndex);
	}

	/* TVTags */
	public ArrayList<TVTag> getFromStorageTVTags() 
	{
		ArrayList<TVTag> tvTags = storage.getTvTags();
		return tvTags;
	}
	
	
	/* TVChannelGuide */
	public TVGuide getFromStorageTVGuideForSelectedDay() 
	{
		TVDate tvDate = getFromStorageTVDateSelected();
		TVGuide tvGuide = storage.getTVGuideUsingTVDate(tvDate);
		return tvGuide;
	}
	
	
	public AppConfiguration getFromStorageAppConfiguration()
	{
		AppConfiguration appConfiguration = storage.getAppConfigData();
		
		return appConfiguration;
	}
	
	
	public TVChannelGuide getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		TVChannelGuide tvChannelGuide = storage.getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
		return tvChannelGuide;
	}

	
	public ArrayList<TVFeedItem> getFromStorageActivityFeedData() 
	{
		ArrayList<TVFeedItem> activityFeedData = storage.getActivityFeed();
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
	public String getFromStorageUserToken() 
	{
		String userToken = storage.getUserToken();
		return userToken;
	}
	
	public boolean isLoggedIn() 
	{
		boolean isLoggedIn = storage.isLoggedIn();
		return isLoggedIn;
	}
	
	
	
	/* NON-PERSISTENT USER DATA, TEMPORARY SAVED IN STORAGE, IN ORDER TO PASS DATA BETWEEN ACTIVITES */
	
	public void setUpcomingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		//TODO NewArc check if null
		String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
		storage.setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
	}
	
	
	public void setRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(programId, repeatingBroadcasts);
		storage.setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
	}
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a tvSeriesId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageUpcomingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		//TODO NewArc check if null
		String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		
		if(storage.containsUpcomingBroadcastsForBroadcast(tvSeriesId)) {
			UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = storage.getNonPersistentUpcomingBroadcasts();
			if(upcomingBroadcastsForBroadcast != null) {
				upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
			}
		}
		
		return upcomingBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageUpcomingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = storage.getNonPersistentUpcomingBroadcasts();
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
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageRepeatingBroadcastsVerifyCorrect(TVBroadcast broadcast) {
		//TODO NewArc check if null
		String programId = broadcast.getProgram().getProgramId();
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		if(storage.containsRepeatingBroadcastsForBroadcast(programId)) {
			RepeatingBroadcastsForBroadcast repeatingBroadcastObject = storage.getNonPersistentRepeatingBroadcasts();
			if(repeatingBroadcastObject != null) {
				repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
			}
		}
		return repeatingBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageRepeatingBroadcasts() {
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		RepeatingBroadcastsForBroadcast repeatingBroadcastObject = storage.getNonPersistentRepeatingBroadcasts();
		if(repeatingBroadcastObject != null) {
			repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
		}return repeatingBroadcasts;
	}
		
	public void setSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo selectedBroadcast) {
		storage.setNonPersistentSelectedBroadcastWithChannelInfo(selectedBroadcast);
	}
	
	public TVBroadcastWithChannelInfo getFromStorageSelectedBroadcastWithChannelInfo() {
		TVBroadcastWithChannelInfo runningBroadcast = storage.getNonPersistentSelectedBroadcastWithChannelInfo();
		return runningBroadcast;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromStorageTaggedBroadcastsForSelectedTVDate() {
		TVDate tvDate = getFromStorageTVDateSelected();
		return getFromStorageTaggedBroadcastsUsingTVDate(tvDate);
	}
	
	public int getFromStorageSelectedHour() {
		int selectedHour = storage.getNonPersistentSelectedHour();
		return selectedHour;
	}
	
	public void setSelectedHour(Integer selectedHour) {
		storage.setNonPersistentSelectedHour(selectedHour);
	}
	
	public void setSelectedTVChannelId(TVChannelId tvChannelId) 
	{
		storage.setNonPersistentTVChannelId(tvChannelId);
	}
	
	public TVChannelId getFromStorageSelectedTVChannelId() 
	{
		TVChannelId tvChannelId = storage.getNonPersistentTVChannelId();
		return tvChannelId;
	}
	
	public String getFromStorageUserLastname() {
		String userLastname = storage.getUserLastname();
		return userLastname;
	}
	
	public String getFromStorageUserFirstname() 
	{
		String userFirstname = storage.getUserFirstname();
		return userFirstname;
	}
	
	public String getFromStorageUserEmail() {
		String userEmail = storage.getUserEmail();
		return userEmail;
	}
	
	public String getFromStorageUserId() {
		String userId = storage.getUserId();
		return userId;
	}
	
	public String getFromStorageUserAvatarImageURL() 
	{
		String userId = storage.getUserAvatarImageURL();
		return userId;
	}
	
	
	public ArrayList<UserLike> getFromStorageUserLikes()
	{
		ArrayList<UserLike> userLikes = storage.getUserLikes();
		return userLikes;
	}
			
	
	// TODO NewArc remove this?
	public TVChannel getFromStorageTVChannelById(String channelId)
	{
		TVChannelId tvChannelId = new TVChannelId(channelId);
		return getFromStorageTVChannelById(tvChannelId);
	}
	
	public TVChannel getFromStorageTVChannelById(TVChannelId tvChannelId)
	{
		return storage.getTVChannelById(tvChannelId);
	}
	
	public String getFromStorageWelcomeMessage() {
		String welcomeMessage = storage.getWelcomeMessage();
		return welcomeMessage;
	}
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getFromStorageTaggedBroadcastsUsingTVDate(TVDate tvDate) {
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcasts = storage.getTaggedBroadcastsUsingTVDate(tvDate);
		return taggedBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromStoragePopularBroadcasts() {
		ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts = storage.getPopularBroadcasts();
		return popularBroadcasts;
	}
	
	public ArrayList<TVDate> getFromStorageTVDates() {
		 ArrayList<TVDate> tvDates = storage.getTvDates();
		 return tvDates;
	}

	
	
	/* HELPER METHODS */
	
	public boolean checkApiVersion(String apiVersion) 
	{
		if(!TextUtils.isEmpty(apiVersion) && !apiVersion.equals(Consts.API_VERSION)) 
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}
}
