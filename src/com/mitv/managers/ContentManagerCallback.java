
package com.mitv.managers;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.ListenerHolder;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.RequestParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.disqus.DisqusThreadDetails;
import com.mitv.models.objects.mitvapi.AppConfiguration;
import com.mitv.models.objects.mitvapi.AppVersion;
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
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
import com.mitv.models.objects.mitvapi.UserLoginData;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public abstract class ContentManagerCallback 
	extends ContentManagerServiceFetching	
	implements ContentCallbackListener
{
	private static final String TAG = ContentManagerCallback.class.getName();


	private HashMap<RequestIdentifierEnum, ArrayList<ListenerHolder>> mapRequestToCallbackListeners;



	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD = 5;
	private static final int COMPLETED_COUNT_FOR_TV_ACTIVITY_FEED_DATA_THRESHOLD = 2;
	private static int completedCountTVDataForProgressMessage = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD + 1;

	/*
	 * The total completed data fetch count needed for the initial data loading
	 */
	private static int COMPLETED_COUNT_FOR_TV_GUIDE_INITIAL_CALL_NOT_LOGGED_IN = 9;
	private static int COMPLETED_COUNT_FOR_TV_GUIDE_INITIAL_CALL_LOGGED_IN = 10;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;

	/* Variables for fetching of BroadcastPage data */
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD = 2;
	private static final int COMPLETED_COUNT_BROADCAST_PAGE_WAIT_FOR_UPCOMING_BROADCAST_THRESHOLD = 3;
	private int completedCountBroadcastPageDataThresholdUsed = COMPLETED_COUNT_BROADCAST_PAGE_NO_UPCOMING_BROADCAST_THRESHOLD;
	private int completedCountBroadcastPageData = 0;

	private int completedCountTVActivityFeed = 0;

	private boolean isProcessingPopularBroadcasts;

	private boolean competitionTeamsFetchFinished;
	private boolean competitionPhasesFetchFinished;
	private boolean competitionEventsFetchFinished;



	public ContentManagerCallback()
	{
		super();

		this.mapRequestToCallbackListeners = new HashMap<RequestIdentifierEnum, ArrayList<ListenerHolder>>();

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



	/**
	 * This enum is only used by the method "useRequestToCallBackListenerMap"
	 * @author Alexander Cyon
	 *
	 */
	private enum RequestToCallBackMapAccessIdentifier 
	{
		REGISTER_LISTENER,
		NOTIFY_LISTENER,
		UNREGISTER_LISTENER;
	}



	private synchronized void useRequestToCallBackListenerMap(RequestToCallBackMapAccessIdentifier variableAccessIdentifier, RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener, FetchRequestResultEnum result) 
	{
		switch (variableAccessIdentifier) 
		{
		case REGISTER_LISTENER: 
		{
			registerListenerForRequestHelper(requestIdentifier, listener);
			break;
		}

		case NOTIFY_LISTENER: 
		{
			notifyListenersOfRequestResultHelper(requestIdentifier, result);
			break;
		}
		case UNREGISTER_LISTENER: 
		{
			unregisterListenerFromAllRequestsHelper(listener);
			break;
		}
		}
	}



	protected void notifyListenersOfRequestResult(RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		useRequestToCallBackListenerMap(RequestToCallBackMapAccessIdentifier.NOTIFY_LISTENER, requestIdentifier, null, result);
	}



	protected void setListenerForRequest(RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener) 
	{
		useRequestToCallBackListenerMap(RequestToCallBackMapAccessIdentifier.REGISTER_LISTENER, requestIdentifier, listener, null);
	}



	protected void unsetListenerFromAllRequests(ViewCallbackListener listener) 
	{
		useRequestToCallBackListenerMap(RequestToCallBackMapAccessIdentifier.UNREGISTER_LISTENER, null, listener, null);
	}



	private void registerListenerForRequestHelper(RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener) 
	{
		ArrayList<ListenerHolder> listenerList = mapRequestToCallbackListeners.get(requestIdentifier);

		if (listenerList == null) 
		{
			listenerList = new ArrayList<ListenerHolder>();

			mapRequestToCallbackListeners.put(requestIdentifier, listenerList);
		}

		ListenerHolder listenerHolder = new ListenerHolder(listener);

		if (!listenerList.contains(listenerHolder))
		{
			listenerList.add(listenerHolder);
		}
	}



	private void unregisterListenerFromAllRequestsHelper(ViewCallbackListener listener) 
	{
		Collection<ArrayList<ListenerHolder>> listenerListCollection = mapRequestToCallbackListeners.values();

		ListenerHolder comparison = new ListenerHolder(listener);

		for (ArrayList<ListenerHolder> listenerList : listenerListCollection) 
		{
			if (listenerList.contains(comparison)) 
			{
				listenerList.remove(comparison);
			}
		}
	}



	private void notifyListenersOfRequestResultHelper(RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result) 
	{
		ArrayList<ListenerHolder> listenerList = mapRequestToCallbackListeners.get(requestIdentifier);

		if (listenerList != null) 
		{
			/* Remove any null listener */
			listenerList.removeAll(Collections.singleton(null));

			for (ListenerHolder listenerHolder : listenerList) 
			{
				if (listenerHolder.isListenerAlive()) 
				{
					ViewCallbackListener listener = listenerHolder.getListener();

					Log.d(TAG, String.format("notifyListenersOfRequestResult: listener: %s request: %s, result: %s", listener.getClass()
							.getSimpleName(), requestIdentifier.getDescription(), result.getDescription()));

					listener.onResult(result, requestIdentifier);
				}
			}
		}
	}




	@Override
	public void onResult(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier, 
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
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
		case POPULAR_ITEMS_INITIAL_CALL:
		case TV_BROADCASTS_POUPULAR_PROCESSING:
		case COMPETITIONS_ALL_INITIAL:
		{
			handleInitialDataResponse(activityCallbackListener, requestIdentifier, result, content);
			break;
		}

		case COMPETITION_TEAMS:
		case COMPETITION_PHASES:
		case COMPETITION_EVENTS:
		case COMPETITION_POST_PROCESSING:
		{
			handleCompetitionInitialDataResponse(activityCallbackListener, requestIdentifier, result, content);
			break;
		}

		case COMPETITIONS_ALL_STANDALONE:
		{
			handleCompetitionsStandaloneResponse(activityCallbackListener, requestIdentifier, result, content);
			break;
		}

		case COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID:
		{
			handleCompetitionStandingsMultipleByPhaseIDResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_STANDINGS_BY_PHASE_ID:
		{
			handleCompetitionStandingsByPhaseIDResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_TEAM_BY_ID:
		{
			handleCompetitionEventTeamByIDResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_EVENT_HIGHLIGHTS:
		{
			handleCompetitionEventHighlightsResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_EVENT_LINEUP:
		{
			handleCompetitionEventLineUpResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_TEAM_SQUAD:
		{
			handleCompetitionTeamSquadResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
			break;
		}

		case COMPETITION_EVENT_BY_ID:
		{
			handleCompetitionEventByIDResponse(activityCallbackListener, requestIdentifier, result, content, requestParameters);
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
			handleUserTokenWithFacebookTokenResponse(activityCallbackListener, requestIdentifier, result, content);
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

		case POPULAR_ITEMS_STANDALONE: 
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

		case DISQUS_THREAD_COMMENTS:
		{
			handleDisqusThreadCommentsResponse(activityCallbackListener, requestIdentifier, result, content);
			break;
		}

		case DISQUS_THREAD_DETAILS:
		{
			handleDisqusThreadDetailsResponse(activityCallbackListener, requestIdentifier, result, content);
			break;
		}

		default:{/* do nothing */break;}
		}
	}



	/* METHODS FOR HANDLING THE RESPONSES */

	private void notifyFetchDataProgressListenerMessage(String message) 
	{
		if(getFetchDataProgressCallbackListener() != null) 
		{
			int totalSteps = completedCountTVDataForProgressMessage + COMPLETED_COUNT_APP_DATA_THRESHOLD;

			getFetchDataProgressCallbackListener().onFetchDataProgress(totalSteps, message);
		}
	}



	private void notifyFetchDataProgressListenerMessage(int totalSteps, String message) 
	{
		if(getFetchDataProgressCallbackListener() != null) 
		{
			getFetchDataProgressCallbackListener().onFetchDataProgress(totalSteps, message);
		}
	}



	/* HANDLES */

	private void handleCompetitionStandingsMultipleByPhaseIDResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters) 
	{
		if(getAPIClient().areMultipleStandingsPendingRequestsCanceled())
		{
			return;
		}

		getAPIClient().incrementCompletedTasksForMultipleStandingsCall();

		switch (requestIdentifier) 
		{
		case COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID:
		{
			@SuppressWarnings("unchecked")
			ArrayList<Standings> standings = (ArrayList<Standings>) content;

			Long phaseID = requestParameters.getAsLong(Constants.REQUEST_DATA_COMPETITION_PHASE_ID_KEY);

			getCache().getCompetitionsData().addStandingsForPhaseIDForSelectedCompetition(standings, phaseID);
			break;
		}

		default: 
		{
			Log.w(TAG, "Unhandled request identifier.");
			break;
		}
		}

		if(getAPIClient().areAllTasksCompletedForMultipleStandingsCall())
		{
			notifyListenersOfRequestResult(RequestIdentifierEnum.COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID, result);

			getAPIClient().cancelAllMultipleStandingsCallPendingRequests();
		}
		else
		{
			if(!result.wasSuccessful() || content == null)
			{
				getAPIClient().cancelAllMultipleStandingsCallPendingRequests();

				notifyListenersOfRequestResult(RequestIdentifierEnum.COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID, FetchRequestResultEnum.UNKNOWN_ERROR);
			}
			else
			{
				Log.d(TAG, "There are pending tasks still running.");
			}
		}
	}





	private void handleCompetitionInitialDataResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content) 
	{
		if(getAPIClient().areInitialCallCompetitionPendingRequestsCanceled())
		{
			return;
		}

		getAPIClient().incrementCompletedTasksForCompetitionInitialCall();

		switch (requestIdentifier) 
		{
		case COMPETITION_TEAMS:
		{
			if(result.wasSuccessful() && content != null) 
			{
				competitionTeamsFetchFinished = true;

				@SuppressWarnings("unchecked")
				ArrayList<Team> teams = (ArrayList<Team>) content;
				getCache().getCompetitionsData().setTeamsForSelectedCompetition(teams);

				if(competitionTeamsFetchFinished && competitionPhasesFetchFinished && competitionEventsFetchFinished)
				{
					getAPIClient().setCompetitionDataPostProcessingTask(activityCallbackListener);
				}
			}
			break;
		}

		case COMPETITION_PHASES:
		{
			if(result.wasSuccessful() && content != null) 
			{
				competitionPhasesFetchFinished = true;

				@SuppressWarnings("unchecked")
				ArrayList<Phase> phases = (ArrayList<Phase>) content;
				getCache().getCompetitionsData().setPhasesForSelectedCompetition(phases);

				if(competitionTeamsFetchFinished && competitionPhasesFetchFinished && competitionEventsFetchFinished)
				{
					getAPIClient().setCompetitionDataPostProcessingTask(activityCallbackListener);
				}
			}
			break;
		}

		case COMPETITION_EVENTS:
		{
			if(result.wasSuccessful() && content != null) 
			{
				competitionEventsFetchFinished = true;

				@SuppressWarnings("unchecked")
				ArrayList<Event> events = (ArrayList<Event>) content;
				getCache().getCompetitionsData().setEventsForSelectedCompetition(events);

				if(competitionTeamsFetchFinished && competitionPhasesFetchFinished && competitionEventsFetchFinished)
				{
					getAPIClient().setCompetitionDataPostProcessingTask(activityCallbackListener);
				}
			}
			break;
		}

		case COMPETITION_POST_PROCESSING:
		{
			// Do nothing
			break;
		}

		default: 
		{
			Log.w(TAG, "Unhandled request identifier.");
			break;
		}
		}

		if(getAPIClient().areAllTasksCompletedForCompetitionInitialCall())
		{
			notifyListenersOfRequestResult(RequestIdentifierEnum.COMPETITION_INITIAL_DATA, result);

			getAPIClient().cancelAllCompetitionInitialCallPendingRequests();
		}
		else
		{
			if(!result.wasSuccessful() || content == null)
			{
				getAPIClient().cancelAllCompetitionInitialCallPendingRequests();

				notifyListenersOfRequestResult(RequestIdentifierEnum.COMPETITION_INITIAL_DATA, FetchRequestResultEnum.UNKNOWN_ERROR);
			}
			else
			{
				Log.d(TAG, "There are pending tasks still running.");
			}
		}
	}



	private void handleInitialDataResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content) 
	{
		if(getAPIClient().areInitialCallPendingRequestsCanceled())
		{
			return;
		}

		getAPIClient().incrementCompletedTasksForTVGuideInitialCall();

		int totalStepsCount;

		if(getCache().isLoggedIn())
		{
			totalStepsCount = COMPLETED_COUNT_FOR_TV_GUIDE_INITIAL_CALL_LOGGED_IN;
		}
		else
		{
			totalStepsCount = COMPLETED_COUNT_FOR_TV_GUIDE_INITIAL_CALL_NOT_LOGGED_IN;
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

					getAPIClient().getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
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

					getAPIClient().getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
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

					getAPIClient().getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
				}
			}
			else if(result.hasUserTokenExpired())
			{
				totalStepsCount = COMPLETED_COUNT_FOR_TV_GUIDE_INITIAL_CALL_NOT_LOGGED_IN;

				getCacheManager().clearUserCache();

				if(!isFetchingTVGuide && completedTVChannelIdsDefaultRequest)
				{
					isFetchingTVGuide = true;

					TVDate tvDate = getCache().getTvDateSelected();

					List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();

					getAPIClient().getTVChannelGuideOnPoolExecutor(activityCallbackListener, tvDate, tvChannelIds);
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

				completedTVGuideRequest = true;

				if(!isProcessingPopularBroadcasts && completedTVPopularRequest)
				{
					isProcessingPopularBroadcasts = true;

					getAPIClient().setPopularVariablesWithPopularBroadcastsOnPoolExecutor(activityCallbackListener);
				}
			}
			break;
		}

		case POPULAR_ITEMS_INITIAL_CALL:
		{
			if(result.wasSuccessful() && content != null) 
			{
				@SuppressWarnings("unchecked")
				ArrayList<TVBroadcastWithChannelInfo> broadcastsWithChannelInfo = (ArrayList<TVBroadcastWithChannelInfo>) content;

				getCache().setPopularBroadcasts(broadcastsWithChannelInfo);

				notifyFetchDataProgressListenerMessage(totalStepsCount, SecondScreenApplication.sharedInstance().getString(R.string.response_pouplar_broadcasts));

				completedTVPopularRequest = true;

				if(!isProcessingPopularBroadcasts && completedTVGuideRequest)
				{
					isProcessingPopularBroadcasts = true;

					getAPIClient().setPopularVariablesWithPopularBroadcastsOnPoolExecutor(activityCallbackListener);
				}
			}
			break;
		}

		case TV_BROADCASTS_POUPULAR_PROCESSING:
		{
			// Do nothing
			break;
		}

		case SNTP_CALL:
		{
			if(result.wasSuccessful())
			{
				Calendar calendar = (Calendar) content;
				getCache().setInitialCallSNTPCalendar(calendar);
			}
			break;
		}

		case COMPETITIONS_ALL_INITIAL:
		{
			if(result.wasSuccessful() && content != null) 
			{
				@SuppressWarnings("unchecked")
				ArrayList<Competition> competitions = (ArrayList<Competition>) content;
				getCache().getCompetitionsData().setAllCompetitions(competitions);

				/* Setting the initially selected competition as the first competition in the list */
				if(competitions.isEmpty() == false)
				{
					long competitionID = getCache().getCompetitionsData().getAllCompetitions().get(0).getCompetitionId();

					getCache().getCompetitionsData().setSelectedCompetition(competitionID);
				}
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
			Log.d(TAG, "API version too old");
			getAPIClient().cancelAllTVGuideInitialCallPendingRequests();

			activityCallbackListener.onResult(FetchRequestResultEnum.API_VERSION_TOO_OLD, RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL);
		}

		if (getAPIClient().areAllTasksCompletedForTVGuideInitialCall()) 
		{
			Log.d(TAG, "Initial loading: Task finished: " + requestIdentifier);
			Log.d(TAG, "Initial loading: All initial tasks finished");

			isUpdatingGuide = false;
			notifyListenersOfRequestResult(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, result);
			getAPIClient().cancelAllTVGuideInitialCallPendingRequests();
		}
		else if((result.wasSuccessful() == false || content == null))
		{
			Log.d(TAG, "Initial loading: Task failed: " + requestIdentifier);

			if(requestIdentifier != RequestIdentifierEnum.SNTP_CALL && requestIdentifier != RequestIdentifierEnum.TV_BROADCASTS_POUPULAR_PROCESSING)
			{
				isUpdatingGuide = false;
				getAPIClient().cancelAllTVGuideInitialCallPendingRequests();
				notifyListenersOfRequestResult(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, FetchRequestResultEnum.UNKNOWN_ERROR);
			}
		}
		else
		{
			Log.d(TAG, "Initial loading: Task finished: " + requestIdentifier);
		}
	}



	private void handleCompetitionEventTeamByIDResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			Team team = (Team) content;

			getCache().getCompetitionsData().addOrModifyTeamForSelectedCompetition(team);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}





	private void handleCompetitionStandingsByPhaseIDResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<Standings> standings = (ArrayList<Standings>) content;

			Long phaseID = requestParameters.getAsLong(Constants.REQUEST_DATA_COMPETITION_PHASE_ID_KEY);

			getCache().getCompetitionsData().addStandingsForPhaseIDForSelectedCompetition(standings, phaseID);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}
	
	
	
	private void handleCompetitionEventByIDResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			Event event = (Event) content;

			getCache().getCompetitionsData().setEvent(event);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}




	private void handleCompetitionEventHighlightsResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<EventHighlight> highlights = (ArrayList<EventHighlight>) content;

			Long eventID = requestParameters.getAsLong(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY);

			getCache().getCompetitionsData().setHighlightsForEventInSelectedCompetition(eventID, highlights);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}



	private void handleCompetitionEventLineUpResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<EventLineUp> eventLineUp = (ArrayList<EventLineUp>) content;

			Long eventID = requestParameters.getAsLong(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY);

			getCache().getCompetitionsData().setLineUpForEventInSelectedCompetition(eventID, eventLineUp);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}



	private void handleCompetitionTeamSquadResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content,
			RequestParameters requestParameters)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<TeamSquad> squad = (ArrayList<TeamSquad>) content;

			Long teamID = requestParameters.getAsLong(Constants.REQUEST_DATA_COMPETITION_TEAM_ID_KEY);

			getCache().getCompetitionsData().setSquadForTeamInSelectedCompetition(teamID, squad);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
	}



	private void handleCompetitionsStandaloneResponse(
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier,
			FetchRequestResultEnum result,
			Object content)
	{
		if(result.wasSuccessful() && content != null) 
		{
			@SuppressWarnings("unchecked")
			ArrayList<Competition> competitions = (ArrayList<Competition>) content;
			getCache().getCompetitionsData().setAllCompetitions(competitions);

			/* Setting the initially selected competition as the first competition in the list */
			if(competitions.isEmpty() == false)
			{
				long competitionID = getCache().getCompetitionsData().getAllCompetitions().get(0).getCompetitionId();

				getCache().getCompetitionsData().setSelectedCompetition(competitionID);
			}
		}

		activityCallbackListener.onResult(result, requestIdentifier);
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



	private void handleDisqusThreadCommentsResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		activityCallbackListener.onResult(result, requestIdentifier);
	}



	private void handleDisqusThreadDetailsResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if(result.wasSuccessful() && content != null) 
		{
			DisqusThreadDetails disqusThreadDetails = (DisqusThreadDetails) content;

			int totalComments = disqusThreadDetails.getResponse().getPosts();

			getCache().setDisqusTotalPostsForLatestBroadcast(totalComments);
		}

		activityCallbackListener.onResult(result, requestIdentifier);
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
				/* NOT IN USE */
				/* Filter the feed items */
				//				if (Constants.ENABLE_FILTER_IN_FEEDACTIVITY && feedItems != null && getFromCacheHasActivityFeed()) {
				//					Log.d(TAG, "MMM Starting filtering MORE old broadcasts");
				//					
				//					ArrayList<TVFeedItem> activityFeed = getFromCacheActivityFeedData();
				//					
				//					activityFeed.addAll(feedItems);
				//					
				//					feedItems = filterSimilarBroadcasts(activityFeed, feedItems);
				//				}

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


	
	@SuppressWarnings("unused")
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

				/* Filter the feed items */
				if (Constants.ENABLE_FILTER_IN_FEEDACTIVITY && feedItems != null) 
				{
					feedItems = filterOldBroadcasts(feedItems, null);
				}

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



	/**
	 * @param activityCallbackListener
	 * @param requestIdentifier
	 * @param result
	 * @param content
	 */
	protected void handleBroadcastPageDataResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
		if ((result == FetchRequestResultEnum.SUCCESS && content != null) || result == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT) 
		{
			completedCountBroadcastPageData++;

			switch (requestIdentifier) 
			{
			case BROADCAST_DETAILS: 
			{
				if(content != null) 
				{
					TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) content;

					getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);

					/* Only fetch upcoming broadcasts if the broadcast is TV Episode */
					if(broadcastWithChannelInfo.getProgram() != null && broadcastWithChannelInfo.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) 
					{
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

			case REPEATING_BROADCASTS_FOR_PROGRAMS: 
			{
				if(content != null) 
				{
					RepeatingBroadcastsForBroadcast repeatingBroadcasts = (RepeatingBroadcastsForBroadcast) content;

					getCache().setNonPersistentRepeatingBroadcasts(repeatingBroadcasts);
				}
				break;
			}

			case UPCOMING_BROADCASTS_FOR_SERIES: 
			{
				if(content != null) 
				{
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
			UserLoginData userData = (UserLoginData) content;
			getCache().setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);

			TrackingManager.sharedInstance().sendUserSignUpSuccessfulUsingEmailEvent();
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



	public void handleUserTokenWithFacebookTokenResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;

			getCache().setUserData(userData);

			boolean wasJustCreated = userData.getUser().isCreated();

			if(wasJustCreated) 
			{
				TrackingManager.sharedInstance().sendUserSignUpSuccessfulUsingFacebookEvent();
			}

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		}

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGIN_WITH_FACEBOOK_TOKEN, result);
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


	private void handleLoginResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content)
	{
		if (result.wasSuccessful() && content != null) 
		{
			UserLoginData userData = (UserLoginData) content;

			getCache().setUserData(userData);

			fetchFromServiceTVDataOnUserStatusChange(activityCallbackListener);
		} 

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGIN, result);
	}



	private void handleLogoutResponse(ViewCallbackListener activityCallbackListener) 
	{
		fetchFromServiceTVGuideForSelectedDay(activityCallbackListener);

		notifyListenersOfRequestResult(RequestIdentifierEnum.USER_LOGOUT, FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT);
	}
}
