
package com.millicom.mitv;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.text.TextUtils;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
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

	
	
	private void getElseFetchFromServiceTVData(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, boolean forceDownload) 
	{
		if(!forceDownload && storage.containsTVGuideForSelectedDay())
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		} 
		else
		{
			fetchFromServiceTVDataOnFirstStart(activityCallBackListener);
		}
	}
	
	
	
	/*
	 * METHODS FOR FETCHING DATA FROM BACKEND USING THE API CLIENT, NEVER USE
	 * THOSE EXTERNALLY, ALL SHOULD BE PRIVATE
	 */
	
	private void fetchFromServiceTVDataOnFirstStart(ActivityCallbackListener activityCallBackListener) 
	{
		apiClient.getTVTags(activityCallBackListener);
		apiClient.getTVDates(activityCallBackListener);
		apiClient.getTVChannelsAll(activityCallBackListener);
		apiClient.getDefaultTVChannelIds(activityCallBackListener);

		if (storage.isLoggedIn()) 
		{
			apiClient.getUserTVChannelIds(activityCallBackListener);
		}
	}

	
	private void fetchFromServiceTVDataOnUserStatusChange(ActivityCallbackListener activityCallBackListener) {
		channelsChange = true;
		apiClient.getUserTVChannelIds(activityCallBackListener);
	}

	private void fetchFromServiceTVGuideForSelectedDay(ActivityCallbackListener activityCallBackListener) {
		TVDate tvDate = storage.getTvDateSelected();
		fetchFromServiceTVGuideUsingTVDate(activityCallBackListener, tvDate);
	}
	
	
	private void fetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallBackListener, TVDate tvDate)
	{
		ArrayList<TVChannelId> tvChannelIds = storage.getTvChannelIdsUsed();
		
		apiClient.getTVChannelGuides(activityCallBackListener, tvDate, tvChannelIds);
	}

	
	
	private void fetchFromServiceActivityFeedData(ActivityCallbackListener activityCallBackListener) 
	{
		apiClient.getUserTVFeedItems(activityCallBackListener);
		apiClient.getUserLikes(activityCallBackListener, false);
	}
	
	
	private void fetchFromServiceUserLikes(ActivityCallbackListener activityCallBackListener) 
	{
		apiClient.getUserLikes(activityCallBackListener, true);
	}
	
	
	private void fetchFromServicePopularBroadcasts(ActivityCallbackListener activityCallbackListener) 
	{
		apiClient.getTVBroadcastsPopular(activityCallbackListener);
	}
	
	
	private void fetchFromServiceUpcomingBroadcasts(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		if (broadcast.getProgram() != null && broadcast.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) 
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			apiClient.getTVBroadcastsFromSeries(activityCallBackListener, tvSeriesId);
		} 
		else 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.BAD_REQUEST, requestIdentifier);
		}
	}

	private void fetchFromServiceRepeatingBroadcasts(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		if (broadcast.getProgram() != null) 
		{
			String programId = broadcast.getProgram().getSeries().getSeriesId();
			apiClient.getTVBroadcastsFromProgram(activityCallBackListener, programId);
		} 
		else 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.BAD_REQUEST, requestIdentifier);
		}
	}
	

	
	/* PUBLIC FETCH METHODS WHERE IT DOES NOT MAKE ANY SENSE TO TRY FETCHING THE DATA FROM STORAGE */
	
	public void checkNetworkConnectivity(ActivityCallbackListener activityCallBackListener) 
	{
		apiClient.getNetworkConnectivityIsAvailable(activityCallBackListener);
	}
	
	
	public void fetchFromServiceAppData(ActivityCallbackListener activityCallBackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener) 
	{
		this.fetchDataProgressCallbackListener = fetchDataProgressCallbackListener;
		apiClient.getAppConfiguration(activityCallBackListener);
		apiClient.getAppVersion(activityCallBackListener);
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
	public void getElseFetchFromServiceTVGuideUsingSelectedTVDate(ActivityCallbackListener activityCallBackListener, boolean forceDownload) {
		TVDate tvDateSelected = getFromStorageTVDateSelected();
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallBackListener, forceDownload, tvDateSelected);
	}
	
	public void getElseFetchFromServiceTVGuideUsingTVDate(ActivityCallbackListener activityCallBackListener, boolean forceDownload, TVDate tvDate)
	{
		if (!forceDownload && storage.containsTVGuideForTVDate(tvDate)) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE);
		} 
		else 
		{
			fetchFromServiceTVGuideUsingTVDate(activityCallBackListener, tvDate);
		}
	}

	
	public void getElseFetchFromServiceActivityFeedData(ActivityCallbackListener activityCallBackListener, boolean forceDownload) 
	{
		if (!forceDownload && storage.containsActivityFeedData() && storage.containsUserLikes()) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM);
		} 
		else 
		{
			fetchFromServiceActivityFeedData(activityCallBackListener);
		}
	}
	
	
	public void getElseFetchFromServiceTaggedBroadcastsForSelectedTVDate(ActivityCallbackListener activityCallBackListener, boolean forceDownload)
	{
		TVDate tvDateSelected = getFromStorageTVDateSelected();
		getElseFetchFromServiceTaggedBroadcastsUsingTVDate(activityCallBackListener, forceDownload, tvDateSelected);
	}
	
	
	public void getElseFetchFromServiceTaggedBroadcastsUsingTVDate(ActivityCallbackListener activityCallBackListener, boolean forceDownload, TVDate tvDate) 
	{
		if (!forceDownload && storage.containsTaggedBroadcastsForTVDate(tvDate)) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_GUIDE);
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
				fetchFromServiceTVGuideUsingTVDate(activityCallBackListener, tvDate);
			}
		}
	}
	
	public void getElseFetchFromServicePopularBroadcasts(ActivityCallbackListener activityCallBackListener, boolean forceDownload)
	{
		if (!forceDownload && storage.containsPopularBroadcasts()) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.POPULAR_ITEMS);
		} 
		else 
		{
			fetchFromServicePopularBroadcasts(activityCallBackListener);
		}
	}
	
	
	public void getElseFetchFromServiceUserLikes(ActivityCallbackListener activityCallBackListener, boolean forceDownload) 
	{
		if (!forceDownload && storage.containsUserLikes()) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_LIKES);
		} 
		else 
		{
			fetchFromServiceUserLikes(activityCallBackListener);
		}
	}
	
	
	public void getElseFetchFromServiceUpcomingBroadcasts(ActivityCallbackListener activityCallBackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey) 
	{
		if (!forceDownload && storage.containsUpcomingBroadcastsForBroadcast(broadcastKey)) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING);
		} 
		else 
		{
			fetchFromServiceUpcomingBroadcasts(activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, broadcastKey);
		}
	}
	
	
	public void getElseFetchFromServiceRepeatingBroadcasts(ActivityCallbackListener activityCallBackListener, boolean forceDownload, TVBroadcastWithChannelInfo broadcastKey)
	{
		if (!forceDownload && storage.containsRepeatingBroadcastsForBroadcast(broadcastKey)) 
		{
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.BROADCASTS_FROM_PROGRAMS);
		} 
		else 
		{
			fetchFromServiceRepeatingBroadcasts(activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_PROGRAMS, broadcastKey);
		}
	}
	
	
	
	@Override
	public void onResult(
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier, 
			FetchRequestResultEnum result,
			Object content)
	{
		switch (requestIdentifier) 
		{
			case INTERNET_CONNECTIVITY:
			{
				handleInternetConnectionDataResponse(activityCallBackListener, requestIdentifier, result);
				break;
			}
		
			case APP_CONFIGURATION:
			case APP_VERSION: 
			{
				handleAppDataResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			case TV_DATE:
			case TV_TAG:
			case TV_CHANNEL:
			case TV_CHANNEL_IDS_USER:
			case TV_CHANNEL_IDS_DEFAULT:
			{
				handleTVDataResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			case TV_GUIDE: 
			{
				handleTVChannelGuidesForSelectedDayResponse(activityCallBackListener, requestIdentifier, result, content);
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
				handleLoginResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			case USER_SIGN_UP: 
			{
				handleSignUpResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			case USER_LOGOUT: 
			{
				handleLogoutResponse(activityCallBackListener);
				break;
			}
			case USER_FB_TOKEN: 
			{
				break;
			}
			case USER_SET_CHANNELS: 
			{
				handleSetChannelsResponse(activityCallBackListener, requestIdentifier, result);
				break;
			}
			case USER_LIKES:
			{
				handleGetUserLikesResponse(activityCallBackListener, requestIdentifier, result, content);
			}
			case USER_ADD_LIKE: 
			{
				handleAddUserLikeResponse(activityCallBackListener, requestIdentifier, result);
				break;
			}
			case USER_REMOVE_LIKE: 
			{
				handleRemoveUserLikeResponse(activityCallBackListener, requestIdentifier, result);
				break;
			}
			case USER_RESET_PASSWORD_SEND_EMAIL: 
			{
				// TODO
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
				handleActivityFeedResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			case POPULAR_ITEMS: 
			{
				handleTVBroadcastsPopularBroadcastsResponse(activityCallBackListener, requestIdentifier, result, content);
				break;
			}
			
			case BROADCAST_DETAILS :
			{
				// TODO
				break;
			}
			case BROADCASTS_FROM_PROGRAMS :
			{
				// TODO
				break;
			}
			case BROADCASTS_FROM_SERIES_UPCOMING :
			{
				// TODO
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
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result)
	{
		activityCallBackListener.onResult(result, requestIdentifier);
	}
	
	
	
	private void handleAppDataResponse(
			ActivityCallbackListener activityCallBackListener,
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
					getElseFetchFromServiceTVData(activityCallBackListener, requestIdentifier, false);
				} 
				else 
				{
					activityCallBackListener.onResult(FetchRequestResultEnum.API_VERSION_TOO_OLD, requestIdentifier);
				}
			}
		} 
		else 
		{
			//TODO handle this better?
			activityCallBackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
	}
	
	
	
	private void handleActivityFeedResponse(
			ActivityCallbackListener activityCallBackListener,
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
				
				activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM);
			}
		} 
		else 
		{
			//TODO handle this better?
			activityCallBackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
	}
	


	private void handleTVDataResponse(
			ActivityCallbackListener activityCallBackListener,
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
				
				case BROADCAST_DETAILS: {
					// TODO
					break;
				}
				case BROADCASTS_FROM_PROGRAMS: {
					// TODO
					break;
				}
				case BROADCASTS_FROM_SERIES_UPCOMING: {
					// TODO
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
				fetchFromServiceTVGuideForSelectedDay(activityCallBackListener);
			}

		} 
		else 
		{
			//TODO handle this better?
			activityCallBackListener.onResult(FetchRequestResultEnum.UNKNOWN_ERROR, requestIdentifier);
		}
	}

	private void handleTVChannelGuidesForSelectedDayResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful() && content != null)
		{
			TVGuideAndTaggedBroadcasts tvGuideAndTaggedBroadcasts = (TVGuideAndTaggedBroadcasts) content;

			TVGuide tvGuide = tvGuideAndTaggedBroadcasts.getTvGuide();
			HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate = tvGuideAndTaggedBroadcasts.getMapTagToTaggedBroadcastForDate();
			
			notifyFetchDataProgressListenerMessage("Fetched tv guide data");
			
			storage.addTVGuideForSelectedDay(tvGuide);
			storage.addTaggedBroadcastsForSelectedDay(mapTagToTaggedBroadcastForDate);

			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		} 
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleTVBroadcastsPopularBroadcastsResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			ArrayList<TVBroadcastWithChannelInfo> broadcastsPopular = (ArrayList<TVBroadcastWithChannelInfo>) content;
			storage.setPopularBroadcasts(broadcastsPopular);
			
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS, requestIdentifier);
		}
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}

	
	public void handleSignUpResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null)
		{
			// TODO change to use SignUpCompleteData object instead??
			UserLoginData userData = (UserLoginData) content;
			storage.setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallBackListener);
		} else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleGetUserLikesResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if (result.wasSuccessful()) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<UserLike> userLikes = (ArrayList<UserLike>) content;
			storage.setUserLikes(userLikes);
		}
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleAddUserLikeResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result)
	{
		if (result.wasSuccessful()) 
		{
			// TODO NewArc - Use storage for likes
//			UserLike userLike = new UserLike();
//			
//			storage.addUserLike(userLike);
		} 
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	public void handleRemoveUserLikeResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if (result.wasSuccessful()) 
		{
			// TODO NewArc - Use storage for likes
//			storage.removeUserLike(userLike);
		} 
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}

	
	public void handleLoginResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
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

	
	public void handleLogoutResponse(ActivityCallbackListener activityCallBackListener) {
		channelsChange = true;

		storage.clearUserData();
		storage.clearTVChannelIdsUser();
		storage.useDefaultChannelIds();

		fetchFromServiceTVGuideForSelectedDay(activityCallBackListener);
	}
	
	
	
	public void handleSetChannelsResponse(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		if (result.wasSuccessful()) 
		{
			fetchFromServiceTVDataOnUserStatusChange(activityCallBackListener);
		} 
		else 
		{
			activityCallBackListener.onResult(result, requestIdentifier);
		}
	}
	
	
	/* This method does not require any ActivityCallbackListener, "fire and forget". */
	public void performInternalTracking(TVBroadcast broadcast) 
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
	
	public void addUserLike(ActivityCallbackListener activityCallBackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		apiClient.addUserLike(activityCallBackListener, likeType, contentId);
	}
	
	
	public void removeUserLike(ActivityCallbackListener activityCallBackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		apiClient.removeUserLike(activityCallBackListener, likeType, contentId);
	}
	
	
	
	/* USER METHODS REGARDING SIGNUP, LOGIN AND LOGOUT */
	
	public void performSignUp(ActivityCallbackListener activityCallBackListener, String email, String password, String firstname, String lastname) {
		apiClient.performUserSignUp(activityCallBackListener, email, password, firstname, lastname);
	}

	public void performLogin(ActivityCallbackListener activityCallBackListener, String username, String password) {
		apiClient.performUserLogin(activityCallBackListener, username, password);
	}

	public void performLogout(ActivityCallbackListener activityCallBackListener) {
		apiClient.performUserLogout(activityCallBackListener);
	}

	public void performSetUserChannels(ActivityCallbackListener activityCallBackListener, List<TVChannelId> tvChannelIds) 
	{
		apiClient.performSetUserTVChannelIds(activityCallBackListener, tvChannelIds);
	}
	
	public ArrayList<TVChannelId> getFromStorageTVChannelIdsUser() 
	{
		ArrayList<TVChannelId> tvChannelIdsUser = storage.getTvChannelIdsUsed();
		return tvChannelIdsUser;
	}

	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ActivityCallbackListener activityCallBackListener, int tvDateIndex) 
	{
		/* Update the index in the storage */
		setTVDateSelectedUsingIndex(tvDateIndex);

		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = storage.getTvDateSelected();

		/*
		 * Since selected TVDate has been changed, set/fetch the TVGuide for
		 * that day
		 */
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallBackListener, false, tvDate);
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
	
	public void setUpcomingBroadcasts(TVBroadcastWithChannelInfo broadcastKey, ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(broadcastKey, upcomingBroadcasts);
		storage.setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
	}
	
	
	public void setRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcastKey, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(broadcastKey, repeatingBroadcasts);
		storage.setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
	}
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageUpcomingBroadcasts(TVBroadcastWithChannelInfo broadcastKey) 
	{
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		
		if(storage.containsUpcomingBroadcastsForBroadcast(broadcastKey)) 
		{
			storage.getNonPersistentUpcomingBroadcasts();
		}
		
		return upcomingBroadcasts;
	}
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getFromStorageRepeatingBroadcasts(TVBroadcastWithChannelInfo broadcastKey) {
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		if(storage.containsRepeatingBroadcastsForBroadcast(broadcastKey)) {
			storage.getNonPersistentRepeatingBroadcasts();
		}
		return upcomingBroadcasts;
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
