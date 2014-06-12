
package com.mitv.managers;



import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.mitv.asynctasks.other.BuildTVBroadcastsForTags;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVGuide;
import com.mitv.models.objects.mitvapi.UpcomingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.utilities.GenericUtils;



public abstract class ContentManagerServiceFetching 
	extends ContentManagerBase
{
	private static final String TAG = ContentManagerBase.class.getName();
	
	
	// Forward declaration. Reusing handle method and avoiding code duplication
	protected abstract void handleBroadcastPageDataResponse(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content);
	protected abstract void setListenerForRequest(RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener);
	
	
	
	
	/* PUBLIC FETCH METHODS WHERE IT DOES NOT MAKE ANY SENSE TO TRY FETCHING THE DATA FROM STORAGE */
	
	public void checkNetworkConnectivity(ViewCallbackListener activityCallbackListener) 
	{
		setListenerForRequest(RequestIdentifierEnum.INTERNET_CONNECTIVITY, activityCallbackListener);
		
		getAPIClient().getNetworkConnectivityIsAvailable(activityCallbackListener);
	}
	
	
	
	public void fetchFromServiceInitialCall(ViewCallbackListener activityCallbackListener, FetchDataProgressCallbackListener fetchDataProgressCallbackListener)
	{	
		resetTvGuideInitialRetryCount();
		
		setListenerForRequest(RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL, activityCallbackListener);
		
		if(!isUpdatingGuide) 
		{
			isUpdatingGuide = true;
			
			Log.d(TAG, "PROFILING: fetchFromServiceInitialCall");
			
			this.completedTVDatesRequest = false;
			this.completedTVChannelIdsDefaultRequest = false;
			this.completedTVChannelIdsUserRequest = false;
			this.completedTVGuideRequest = false;
			this.completedTVPopularRequest = false;
			this.isFetchingTVGuide = false;
			this.isAPIVersionTooOld = false;
			
			setFetchDataProgressCallbackListener(fetchDataProgressCallbackListener);
			
			boolean isUserLoggedIn = getCache().isLoggedIn();
			
			getAPIClient().getTVGuideInitialDataOnPoolExecutor(activityCallbackListener, isUserLoggedIn);
		}
	}
	
	
	
	public void fetchFromServiceMoreActivityData(ViewCallbackListener activityCallbackListener, int offset) 
	{
		setListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE, activityCallbackListener);
		
		if (!isFetchingFeedItems) 
		{
			isFetchingFeedItems = true;
			
			Log.d(TAG, "FEEDS: count " + offset);
			
			getAPIClient().getUserTVFeedItemsWithOffsetAndLimit(activityCallbackListener, offset);
		}
	}
	
	
	
	public void fetchFromServiceDisqusComments(ViewCallbackListener activityCallbackListener, String contentID) 
	{
		setListenerForRequest(RequestIdentifierEnum.DISQUS_THREAD_COMMENTS, activityCallbackListener);
		
		getAPIClient().getDisqusThreadPosts(activityCallbackListener, contentID);
	}
	
	
	
	public void fetchFromServiceDisqusThreadDetails(ViewCallbackListener activityCallbackListener, String contentID) 
	{
		setListenerForRequest(RequestIdentifierEnum.DISQUS_THREAD_DETAILS, activityCallbackListener);
		
		getAPIClient().getDisqusThreadDetails(activityCallbackListener, contentID);
	}
	
	
	
	
	/* PROTECTD FETCH METHODS - TO BE USED FROM WITHIN THE HANDLES ON THE CALLBACK METHODS */
	
	protected void fetchFromServiceTVDataOnUserStatusChange(ViewCallbackListener activityCallbackListener) 
	{
		Log.d(TAG, "PROFILING: fetchFromServiceTVDataOnUserStatusChange");
		
		setListenerForRequest(RequestIdentifierEnum.TV_CHANNEL_IDS_USER_STANDALONE, activityCallbackListener);
		
		/* Handle TV Channel & Guide data, after login */
		getAPIClient().getUserTVChannelIds(activityCallbackListener, true);
		
		/* Add like if any was set */
		UserLike likeToAddAfterLogin = getCache().getLikeToAddAfterLogin();
		
		if(likeToAddAfterLogin != null) 
		{
			/* Passing null because the login views should not care about if the like was successfully added or not.
			 * According to the current architecture we MUST not allow the method onDataAvailable to be called in LoginViews,
			 * since pattern with returnActivity and method tryStartReturnActivity will break */
			
			setListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE, activityCallbackListener);
			
			addUserLike(null, likeToAddAfterLogin);
		}
	}

	
	
	protected void fetchFromServiceTVGuideForSelectedDay(ViewCallbackListener activityCallbackListener) 
	{
		TVDate tvDate = getCache().getTvDateSelected();
		
		fetchFromServiceTVGuideUsingTVDate(activityCallbackListener, tvDate);
	}
		
	
	
	protected void fetchFromServiceTVGuideUsingTVDateAndTVChannelIds(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		setListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE, activityCallbackListener);
		
		if(!isUpdatingGuide) 
		{
			Log.i(TAG, "The guide is still updating.");
			
			isUpdatingGuide = true;
			
			getAPIClient().getTVChannelGuides(activityCallbackListener, tvDate, tvChannelIds);
		}
	}
	
	
	
	protected void fetchFromServiceTVGuideUsingTVDate(ViewCallbackListener activityCallbackListener, TVDate tvDate)
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
	
	
	
	protected void fetchFromServiceActivityFeedData(ViewCallbackListener activityCallbackListener) 
	{
		setListenerForRequest(RequestIdentifierEnum.USER_LIKES, activityCallbackListener);
		setListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM, activityCallbackListener);
		setListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_INITIAL_DATA, activityCallbackListener);
		
		getAPIClient().getUserTVFeedItemsInitial(activityCallbackListener);
		getAPIClient().getUserLikes(activityCallbackListener, false);
	}
	
	
	
	protected void fetchFromServiceSearchResults(ViewCallbackListener activityCallbackListener, String searchQuery) 
	{
		setListenerForRequest(RequestIdentifierEnum.SEARCH, activityCallbackListener);
		
		getAPIClient().getTVSearchResults(activityCallbackListener, searchQuery);
	}
	
	
	
	protected void fetchFromServiceUserLikes(ViewCallbackListener activityCallbackListener) 
	{
		setListenerForRequest(RequestIdentifierEnum.USER_LIKES, activityCallbackListener);
		
		getAPIClient().getUserLikes(activityCallbackListener, true);
	}
	
	
	
	protected void fetchFromServicePopularBroadcasts(ViewCallbackListener activityCallbackListener, boolean standalone) 
	{
		setListenerForRequest(RequestIdentifierEnum.POPULAR_ITEMS_STANDALONE, activityCallbackListener);
		
		getAPIClient().getTVBroadcastsPopular(activityCallbackListener, standalone);
	}
	
	
	
	protected void fetchFromServiceIndividualBroadcast(ViewCallbackListener activityCallbackListener, TVChannelId channelId, long beginTimeInMillis) 
	{
		setListenerForRequest(RequestIdentifierEnum.BROADCAST_DETAILS, activityCallbackListener);
		
		getAPIClient().getTVBroadcastDetails(activityCallbackListener, channelId, beginTimeInMillis);
	}
	
	
	
	protected void fetchFromServiceUpcomingBroadcasts(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		setListenerForRequest(RequestIdentifierEnum.UPCOMING_BROADCASTS_FOR_SERIES, activityCallbackListener);
		
		if (broadcast.getProgram() != null && broadcast.getProgram().getProgramType() == ProgramTypeEnum.TV_EPISODE) 
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			
			getAPIClient().getTVBroadcastsFromSeries(activityCallbackListener, tvSeriesId);
		} 
		else 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.BAD_REQUEST, requestIdentifier);
		}
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
		
		getAPIClient().performUserSignUp(activityCallbackListener, data, true);
	}

	
	public void performLogin(ViewCallbackListener activityCallbackListener, String username, String password) 
	{		
		String hashedPassword = GenericUtils.getSHA512PasswordHash(password);
		
		UserLoginDataPost data = new UserLoginDataPost();
		data.setEmail(username);
		data.setPassword(hashedPassword);
						
		getAPIClient().performUserLogin(activityCallbackListener, data, true);
	}

	
	
	public void performResetPassword(ViewCallbackListener activityCallbackListener, String email) 
	{
		getAPIClient().performUserPasswordResetSendEmail(activityCallbackListener, email);
	}
	
	
	
	public void getUserTokenWithFacebookFBToken(ViewCallbackListener activityCallbackListener, String facebookToken) 
	{
		getAPIClient().performUserLoginWithFacebookToken(activityCallbackListener, facebookToken);
	}
	
	
	
	public void performLogout(
			final ViewCallbackListener activityCallbackListener,
			final boolean isSessionExpiredLogout)
	{	
		Log.d(TAG, "PROFILING: performLogout:");
		
		clearUserCache();
		
		TrackingGAManager.sharedInstance().setUserIdOnTrackerAndSendSignedOut();
		
		if(isSessionExpiredLogout == false)
		{
			getAPIClient().performUserLogout(activityCallbackListener);
		}
	}
	
	
	
	/* USER METHODS REGARDING LIKES */
	
	public void addUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		setListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE, activityCallbackListener);
		
		/* Manually add like to cache, so that GUI gets updated directly, here we assume that the request was successful, if it was not,
		 * then this manually added like is removed from the cache */
		getCache().addUserLike(userLike);
		
		getAPIClient().addUserLike(activityCallbackListener, userLike);
	}
	
	
	public void removeUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		getAPIClient().removeUserLike(activityCallbackListener, userLike);
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
	
	
	
	public void getElseFetchFromServicePopularBroadcasts(ViewCallbackListener activityCallbackListener, boolean forceDownload)
	{
		if (!forceDownload && getCache().containsPopularBroadcasts()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.POPULAR_ITEMS_STANDALONE);
		} 
		else 
		{
			fetchFromServicePopularBroadcasts(activityCallbackListener, true);
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
			final TVChannelId channelId, 
			final long beginTimeInMillis) 
	{
		if (!forceDownload && getCache().containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis)) 
		{
			TVBroadcastWithChannelInfo broadcastWithChannelInfoUsed = getCache().getNonPersistentLastSelectedBroadcastWithChannelInfo();

			handleBroadcastPageDataResponse(activityCallbackListener, RequestIdentifierEnum.BROADCAST_DETAILS, FetchRequestResultEnum.SUCCESS, broadcastWithChannelInfoUsed);
		} 
		else 
		{
			if(channelId != null) 
			{
				fetchFromServiceIndividualBroadcast(activityCallbackListener, channelId, beginTimeInMillis);
			} 
			else 
			{
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
	
	
	
	public void getElseFetchFromServiceCompetitionsData(ViewCallbackListener activityCallbackListener, boolean forceDownload)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsCompetitionsData())
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITIONS_ALL_STANDALONE);
		} 
		else 
		{
			getAPIClient().getCompetitions(activityCallbackListener, true);
		}
	}
	
	
	
	public void getElseFetchFromServiceEventHighlighstData(
			ViewCallbackListener activityCallbackListener, 
			boolean forceDownload,
			Long competitionID,
			Long eventID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsEventHighlightsData(competitionID, eventID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS);
		} 
		else 
		{
			getAPIClient().GetEventHighlights(activityCallbackListener, competitionID, eventID);
		}
	}
	
	
	/* TODO */
	public void getElseFetchFromServicePhaseForTeamData(
			ViewCallbackListener activityCallbackListener, 
			boolean forceDownload,
			Long teamID)
	{
//		if (!forceDownload && getCache().getCompetitionsData().containsEventHighlightsData(competitionID, eventID))
//		{
//			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS);
//		} 
//		else 
//		{
//			getAPIClient().GetEventHighlights(activityCallbackListener, competitionID, eventID);
//		}
	}
	
	
	
	public void getElseFetchFromServiceTeamData(
			ViewCallbackListener activityCallbackListener, 
			boolean forceDownload,
			Long competitionID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsTeamData(competitionID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_TEAMS);
		} 
		else 
		{
			getAPIClient().getTeams(activityCallbackListener, competitionID);
		}
	}
	
	

	public void getElseFetchFromServiceTeamByID(
			final ViewCallbackListener activityCallbackListener, 
			final boolean forceDownload,
			final Long competitionID,
			final Long teamID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsTeamData(competitionID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_TEAM_BY_ID);
		} 
		else 
		{
			getAPIClient().getTeamByID(activityCallbackListener, competitionID, teamID);
		}
	}
	
	
	
	public void getElseFetchFromServiceSquadByTeamID(
			final ViewCallbackListener activityCallbackListener, 
			final boolean forceDownload,
			final Long competitionID,
			final Long teamID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsSquadData(competitionID, teamID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_TEAM_SQUAD);
		} 
		else 
		{
			getAPIClient().getSquadForTeam(activityCallbackListener, teamID);
		}
	}
	
	
	
	public void getElseFetchFromServiceEventStandingsData(
			ViewCallbackListener activityCallbackListener, 
			boolean forceDownload,
			Long competitionID,
			Long phaseID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsStandingsData(competitionID, phaseID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_STANDINGS_BY_PHASE_ID);
		}
		else 
		{
			getAPIClient().GetStandingsForPhase(activityCallbackListener, phaseID);
		}
	}
	
	
	
	public void getElseFetchFromServiceEventLineUpData(
			ViewCallbackListener activityCallbackListener, 
			boolean forceDownload,
			Long competitionID,
			Long eventID)
	{
		if (!forceDownload && getCache().getCompetitionsData().containsEventLineUpData(competitionID, eventID))
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_EVENT_LINEUP);
		} 
		else 
		{
			getAPIClient().GetEventLineUp(activityCallbackListener, eventID);
		}
	}
	
	
	
	public void getElseFetchFromServiceCompetitionInitialData(ViewCallbackListener activityCallbackListener, boolean forceDownload, long competitionID)
	{
		if (!forceDownload && getFromCacheHasCompetitionData(competitionID)) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		} 
		else 
		{
			getCache().getCompetitionsData().clearCompetition(competitionID);
			
			getAPIClient().getCompetitionInitialDataOnPoolExecutor(activityCallbackListener, competitionID);
		}
	}
	
	
	
	public void getElseFetchFromServiceStandingsForMultiplePhases(ViewCallbackListener activityCallbackListener, boolean forceDownload, List<Phase> phases)
	{
		List<Phase> phasesToFetch = new ArrayList<Phase>();
		
		for(Phase phase : phases)
		{
			long phaseID = phase.getPhaseId();
			
			boolean containsStanding = getFromCacheHasStandingsForPhaseInSelectedCompetition(phaseID);
			
			if(containsStanding == false)
			{
				phasesToFetch.add(phase);
			}
		}
		
		if (!forceDownload && phasesToFetch.isEmpty()) 
		{
			activityCallbackListener.onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID);
		} 
		else 
		{
			getAPIClient().getMultipleStandingsOnCallPoolExecutor(activityCallbackListener, phases);
		}
	}
	
	
	
	/* PRIVATE METHODS - TO BE USED ONLY IN THIS SCOPE */
	
	
	private void fetchFromServiceRepeatingBroadcasts(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, TVBroadcastWithChannelInfo broadcast) 
	{
		setListenerForRequest(RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, activityCallbackListener);
		
		if (broadcast.getProgram() != null) 
		{
			String programId = broadcast.getProgram().getProgramId();
			
			getAPIClient().getTVBroadcastsFromProgram(activityCallbackListener, programId);
		} 
		else 
		{
			handleBroadcastPageDataResponse(activityCallbackListener, requestIdentifier, FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT, null);
		}
	}
	
	
	
	private void getElseBuildTaggedBroadcastsUsingTVDate(ViewCallbackListener activityCallbackListener, TVDate tvDate, String tagName) 
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
	
	
	
	private void buildTVBroadcastsForTags(ViewCallbackListener activityCallbackListener, String tagName) 
	{
		setListenerForRequest(RequestIdentifierEnum.TV_BROADCASTS_FOR_TAGS, activityCallbackListener);
		
		if(!isBuildingTaggedBroadcasts) 
		{
			isBuildingTaggedBroadcasts = true;
			
			Log.d(TAG, String.format("PROFILING: buildTVBroadcastsForTags: build tagged, tag: %s", tagName));
			
			TVGuide tvGuide = getFromCacheTVGuideForSelectedDay();
			
			if(tvGuide != null) 
			{
				ArrayList<TVChannelGuide> tvChannelGuides = tvGuide.getTvChannelGuides();
			
				BuildTVBroadcastsForTags buildTVBroadcastsForTags = new BuildTVBroadcastsForTags(tvChannelGuides, this, activityCallbackListener);
				
				buildTVBroadcastsForTags.execute();
			}
		}
	}	
}
