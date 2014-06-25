
package com.mitv;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

import com.mitv.asynctasks.CustomThreadedPoolExecutor;
import com.mitv.asynctasks.disqus.GetDisqusThreadDetails;
import com.mitv.asynctasks.disqus.GetDisqusThreadPosts;
import com.mitv.asynctasks.mitvapi.GetAppConfigurationData;
import com.mitv.asynctasks.mitvapi.GetAppVersionData;
import com.mitv.asynctasks.mitvapi.GetTVBroadcastDetails;
import com.mitv.asynctasks.mitvapi.GetTVBroadcastsFromProgram;
import com.mitv.asynctasks.mitvapi.GetTVBroadcastsFromSeries;
import com.mitv.asynctasks.mitvapi.GetTVBroadcastsPopular;
import com.mitv.asynctasks.mitvapi.GetTVChannelGuides;
import com.mitv.asynctasks.mitvapi.GetTVChannelIdsDefault;
import com.mitv.asynctasks.mitvapi.GetTVChannelsAll;
import com.mitv.asynctasks.mitvapi.GetTVDates;
import com.mitv.asynctasks.mitvapi.GetTVSearchResults;
import com.mitv.asynctasks.mitvapi.GetTVTags;
import com.mitv.asynctasks.mitvapi.PerformInternalTracking;
import com.mitv.asynctasks.mitvapi.PerformUserLoginWithCredentials;
import com.mitv.asynctasks.mitvapi.PerformUserLoginWithFacebookToken;
import com.mitv.asynctasks.mitvapi.PerformUserPasswordResetConfirmation;
import com.mitv.asynctasks.mitvapi.PerformUserPasswordResetSendEmail;
import com.mitv.asynctasks.mitvapi.PerformUserSignUp;
import com.mitv.asynctasks.mitvapi.competitions.GetCompetitionByID;
import com.mitv.asynctasks.mitvapi.competitions.GetCompetitions;
import com.mitv.asynctasks.mitvapi.competitions.GetEventByID;
import com.mitv.asynctasks.mitvapi.competitions.GetEventHighlights;
import com.mitv.asynctasks.mitvapi.competitions.GetEventLineUp;
import com.mitv.asynctasks.mitvapi.competitions.GetEvents;
import com.mitv.asynctasks.mitvapi.competitions.GetPhaseByID;
import com.mitv.asynctasks.mitvapi.competitions.GetPhaseByTeamID;
import com.mitv.asynctasks.mitvapi.competitions.GetPhases;
import com.mitv.asynctasks.mitvapi.competitions.GetSquadForTeam;
import com.mitv.asynctasks.mitvapi.competitions.GetStandingsForPhase;
import com.mitv.asynctasks.mitvapi.competitions.GetTeamByID;
import com.mitv.asynctasks.mitvapi.competitions.GetTeams;
import com.mitv.asynctasks.mitvapi.usertoken.AddUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserLikes;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVChannelIds;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVFeedItems;
import com.mitv.asynctasks.mitvapi.usertoken.PerformUserLogout;
import com.mitv.asynctasks.mitvapi.usertoken.RemoveUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.SetUserTVChannelIds;
import com.mitv.asynctasks.other.CheckNetworkConnectivity;
import com.mitv.asynctasks.other.CompetitionDataPostProcessingTask;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Phase;



public class APIClient
{	
	private static final int POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE = 15;
	private static final int POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE = 20;
	private static final long POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME = 5000L;
	
	
	private GetTVSearchResults lastSearchTask;
	private ContentCallbackListener contentCallbackListener;
	private CustomThreadedPoolExecutor tvGuideInitialCallPoolExecutor;
	private CustomThreadedPoolExecutor competitionsInitialCallPoolExecutor;
	private CustomThreadedPoolExecutor multipleStandingsCallPoolExecutor;
	
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;

		resetTVGuideInitialCallPoolExecutor();
		
		resetCompetitionsInitialCallPoolExecutor();
	}
	
	
	
	/* THREAD POLL EXECUTOR METHODS */
	
	private void resetTVGuideInitialCallPoolExecutor()
	{
		if(tvGuideInitialCallPoolExecutor != null)
		{
			if(tvGuideInitialCallPoolExecutor.isShutdown() == false)
			{
				tvGuideInitialCallPoolExecutor.shutdownNow();
			}

			tvGuideInitialCallPoolExecutor.purge();
			tvGuideInitialCallPoolExecutor.resetTaskCount();
		}
		
		tvGuideInitialCallPoolExecutor = new CustomThreadedPoolExecutor(
				POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE,
				POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE,
				POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
	
	
	private void resetCompetitionsInitialCallPoolExecutor()
	{
		if(competitionsInitialCallPoolExecutor != null)
		{
			if(competitionsInitialCallPoolExecutor.isShutdown() == false)
			{
				competitionsInitialCallPoolExecutor.shutdownNow();
			}

			competitionsInitialCallPoolExecutor.purge();
			competitionsInitialCallPoolExecutor.resetTaskCount();
		}
		
		competitionsInitialCallPoolExecutor = new CustomThreadedPoolExecutor(
				POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE,
				POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE,
				POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
	
	
	private void resetMultipleStandingsCallPoolExecutor()
	{
		if(multipleStandingsCallPoolExecutor != null)
		{
			if(multipleStandingsCallPoolExecutor.isShutdown() == false)
			{
				multipleStandingsCallPoolExecutor.shutdownNow();
			}

			multipleStandingsCallPoolExecutor.purge();
			multipleStandingsCallPoolExecutor.resetTaskCount();
		}
		
		multipleStandingsCallPoolExecutor = new CustomThreadedPoolExecutor(
				POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE,
				POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE,
				POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
	
	
	/* THREAD POLL EXECUTOR METHODS FOR MULTIPLE STANDINGS CALL */
	
	public void getMultipleStandingsOnCallPoolExecutor(ViewCallbackListener activityCallbackListener, List<Phase> phases)
	{
		resetMultipleStandingsCallPoolExecutor();
		
		List<AsyncTask<String, Void, Void>> tasks = new ArrayList<AsyncTask<String,Void,Void>>();
		
		for(Phase phase : phases)
		{
			long phaseID = phase.getPhaseId();
			
			tasks.add(new GetStandingsForPhase(contentCallbackListener, activityCallbackListener, phaseID, true, Constants.RETRY_COUNT_THRESHOLD));
		}
		
		for(AsyncTask<String, Void, Void> task : tasks)
		{
			multipleStandingsCallPoolExecutor.addAndExecuteTask(task);
		}
	}
	
	
	
	public void cancelAllMultipleStandingsCallPendingRequests()
	{
		multipleStandingsCallPoolExecutor.shutdown();
	}
	
	
	
	public boolean areMultipleStandingsPendingRequestsCanceled()
	{
		return (multipleStandingsCallPoolExecutor.isShutdown() || multipleStandingsCallPoolExecutor.isTerminated() || multipleStandingsCallPoolExecutor.isTerminating());
	}
	
	
	
	public void incrementCompletedTasksForMultipleStandingsCall()
	{
		multipleStandingsCallPoolExecutor.incrementCompletedTasks();
	}
	
	
	
	public boolean areAllTasksCompletedForMultipleStandingsCall()
	{
		return multipleStandingsCallPoolExecutor.areAllTasksCompleted();
	}
	
	
	
	/* THREAD POLL EXECUTOR METHODS FOR TV GUIDE INITIAL CALL */
	
	public void getTVGuideInitialDataOnPoolExecutor(ViewCallbackListener activityCallbackListener, boolean isUserLoggedIn)
	{
		resetTVGuideInitialCallPoolExecutor();
		
		List<AsyncTask<String, Void, Void>> tasks = new ArrayList<AsyncTask<String,Void,Void>>();
		
		tasks.add(new GetAppConfigurationData(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetAppVersionData(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetTVTags(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetTVChannelsAll(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		//tasks.add(new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetCompetitions(contentCallbackListener, activityCallbackListener, false, Constants.RETRY_COUNT_THRESHOLD));
		
		if (Constants.USE_LOCAL_GENERATED_TVDATES == false)
		{
			tasks.add(new GetTVDates(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD));
		}
		
		for(AsyncTask<String, Void, Void> task : tasks)
		{
			tvGuideInitialCallPoolExecutor.addAndExecuteTask(task);
		}
		
		if(isUserLoggedIn)
		{
			GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, false, Constants.RETRY_COUNT_THRESHOLD);
			GetUserLikes getUserLikes = new GetUserLikes(contentCallbackListener, activityCallbackListener, true, false, Constants.RETRY_COUNT_THRESHOLD);
			
			tvGuideInitialCallPoolExecutor.addAndExecuteTask(getUserTVChannelIds);
			tvGuideInitialCallPoolExecutor.addAndExecuteTask(getUserLikes);
		}
	}
	
	
	
	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, TVDate tvDate, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll) 
	{
		resetTVGuideInitialCallPoolExecutor();
		
		GetTVChannelGuides getNewGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIdsOnlyNewOnes, Constants.RETRY_COUNT_THRESHOLD);
		SetUserTVChannelIds setUserTVChanelIds = new SetUserTVChannelIds(contentCallbackListener, null, tvChannelIdsAll, Constants.RETRY_COUNT_THRESHOLD);
		
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(getNewGuides);
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(setUserTVChanelIds);
	}
	
	
	
	public void getTVChannelGuideOnPoolExecutor(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides task = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, false, tvDate, tvChannelIds, Constants.RETRY_COUNT_THRESHOLD);
		
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(task);
	}
	
	

	public void cancelAllTVGuideInitialCallPendingRequests()
	{
		tvGuideInitialCallPoolExecutor.shutdown();
	}
	
	
	
	public boolean areInitialCallPendingRequestsCanceled()
	{
		return (tvGuideInitialCallPoolExecutor.isShutdown() || tvGuideInitialCallPoolExecutor.isTerminated() || tvGuideInitialCallPoolExecutor.isTerminating());
	}
	
	
	
	public void incrementCompletedTasksForTVGuideInitialCall()
	{
		tvGuideInitialCallPoolExecutor.incrementCompletedTasks();
	}
	
	
	
	public boolean areAllTasksCompletedForTVGuideInitialCall()
	{
		return tvGuideInitialCallPoolExecutor.areAllTasksCompleted();
	}
	

	
	/* THREAD POLL EXECUTOR METHODS FOR COMPETITIONS INITIAL CALL */
	
	public void getCompetitionInitialDataOnPoolExecutor(ViewCallbackListener activityCallbackListener, long competitionID)
	{
		resetCompetitionsInitialCallPoolExecutor();
		
		List<AsyncTask<String, Void, Void>> tasks = new ArrayList<AsyncTask<String,Void,Void>>();
		
		tasks.add(new GetTeams(contentCallbackListener, activityCallbackListener, competitionID, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetPhases(contentCallbackListener, activityCallbackListener, competitionID, Constants.RETRY_COUNT_THRESHOLD));
		tasks.add(new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, null, null, Constants.RETRY_COUNT_THRESHOLD));
		
		for(AsyncTask<String, Void, Void> task : tasks)
		{
			competitionsInitialCallPoolExecutor.addAndExecuteTask(task);
		}
	}
	
	
	
	public void setCompetitionDataPostProcessingTask(ViewCallbackListener activityCallbackListener) 
	{
		resetTVGuideInitialCallPoolExecutor();
		
		CompetitionDataPostProcessingTask task = new CompetitionDataPostProcessingTask(contentCallbackListener, activityCallbackListener);
		
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(task);
	}
	
	
	
	public void cancelAllCompetitionInitialCallPendingRequests()
	{
		competitionsInitialCallPoolExecutor.shutdown();
	}
	
	
	
	public boolean areInitialCallCompetitionPendingRequestsCanceled()
	{
		return (competitionsInitialCallPoolExecutor.isShutdown() || competitionsInitialCallPoolExecutor.isTerminated() || competitionsInitialCallPoolExecutor.isTerminating());
	}
	
	
	
	public void incrementCompletedTasksForCompetitionInitialCall()
	{
		competitionsInitialCallPoolExecutor.incrementCompletedTasks();
	}
	
	
	
	public boolean areAllTasksCompletedForCompetitionInitialCall()
	{
		return competitionsInitialCallPoolExecutor.areAllTasksCompleted();
	}
	

	
	/* TASK EXECUTION METHODS */
	
	public void getNetworkConnectivityIsAvailable(ViewCallbackListener activityCallbackListener) 
	{
		CheckNetworkConnectivity task = new CheckNetworkConnectivity(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.INTERNET_CONNECTIVITY);
		task.execute();
	}
	
	
	public void getAppConfiguration(ViewCallbackListener activityCallbackListener) 
	{
		GetAppConfigurationData task = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getAppVersion(ViewCallbackListener activityCallbackListener) 
	{
		GetAppVersionData task = new GetAppVersionData(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	

	public void getTVTags(ViewCallbackListener activityCallbackListener)
	{
		GetTVTags task = new GetTVTags(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getTVDates(ViewCallbackListener activityCallbackListener) 
	{
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		getTVDates.execute();
	}
	
	
	
	public void getTVChannelsAll(ViewCallbackListener activityCallbackListener)
	{
		GetTVChannelsAll task = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getDefaultTVChannelIds(ViewCallbackListener activityCallbackListener) 
	{
		GetTVChannelIdsDefault task = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	/**
	 * 
	 * @param activityCallbackListener
	 * @param standalone
	 */
	public void getUserTVChannelIds(ViewCallbackListener activityCallbackListener, boolean standalone) 
	{
		GetUserTVChannelIds task = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, standalone, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void performSetUserTVChannelIds(ViewCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		SetUserTVChannelIds task = new SetUserTVChannelIds(contentCallbackListener, activityCallbackListener, tvChannelIds, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getUserTVFeedItemsInitial(ViewCallbackListener activityCallbackListener) 
	{
		GetUserTVFeedItems task = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, 0, Constants.FEED_ACTIVITY_FEED_ITEM_INITIAL_COUNT, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	public void getUserTVFeedItemsWithOffsetAndLimit(ViewCallbackListener activityCallbackListener, int offset)
	{
		GetUserTVFeedItems task = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, offset, Constants.FEED_ACTIVITY_FEED_ITEM_MORE_COUNT, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getTVBroadcastDetails(ViewCallbackListener activityCallbackListener, TVChannelId tvChannelId, long beginTime)
	{
		GetTVBroadcastDetails task = new GetTVBroadcastDetails(contentCallbackListener, activityCallbackListener, tvChannelId, beginTime, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getTVBroadcastsFromSeries(ViewCallbackListener activityCallbackListener, String tvSeriesId)
	{
		GetTVBroadcastsFromSeries task = new GetTVBroadcastsFromSeries(contentCallbackListener, activityCallbackListener, tvSeriesId, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getTVBroadcastsPopular(ViewCallbackListener activityCallbackListener) 
	{
		GetTVBroadcastsPopular task = new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getTVBroadcastsFromProgram(ViewCallbackListener activityCallbackListener, String tvProgramId)
	{
		GetTVBroadcastsFromProgram task = new GetTVBroadcastsFromProgram(contentCallbackListener, activityCallbackListener, tvProgramId, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getUserLikes(ViewCallbackListener activityCallbackListener, boolean standaLone)
	{
		GetUserLikes task = new GetUserLikes(contentCallbackListener, activityCallbackListener, false, standaLone, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike)
	{
		AddUserLike task = new AddUserLike(contentCallbackListener, activityCallbackListener, userLike, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		RemoveUserLike task = new RemoveUserLike(contentCallbackListener, activityCallbackListener, userLike, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getTVChannelGuides(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides task = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIds, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getTVSearchResults(ViewCallbackListener activityCallbackListener, String searchQuery)
	{
		if(lastSearchTask != null)
		{
			lastSearchTask.cancel(true);
		}
		
		GetTVSearchResults task = new GetTVSearchResults(contentCallbackListener, activityCallbackListener, searchQuery, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
		
		lastSearchTask = task;
	}
	
	
	
	public void performUserLoginWithFacebookToken(ViewCallbackListener activityCallbackListener, String facebookToken) 
	{
		PerformUserLoginWithFacebookToken task = new PerformUserLoginWithFacebookToken(contentCallbackListener, activityCallbackListener, facebookToken, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ViewCallbackListener activityCallbackListener, UserLoginDataPost userLoginDataPost, boolean usingHashedPassword) 
	{
		PerformUserLoginWithCredentials performUserLogin = new PerformUserLoginWithCredentials(contentCallbackListener, activityCallbackListener, userLoginDataPost, usingHashedPassword, Constants.RETRY_COUNT_THRESHOLD);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ViewCallbackListener activityCallbackListener, UserRegistrationData userRegistrationData, boolean usingHashedPassword)
	{
		PerformUserSignUp task = new PerformUserSignUp(contentCallbackListener, activityCallbackListener, userRegistrationData, usingHashedPassword, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void performUserPasswordResetSendEmail(ViewCallbackListener activityCallbackListener, String email)
	{
		PerformUserPasswordResetSendEmail task = new PerformUserPasswordResetSendEmail(contentCallbackListener, activityCallbackListener, email, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void performUserPasswordResetConfirmPassword(ViewCallbackListener activityCallbackListener, String email, String newPassword, String resetPasswordToken)
	{
		PerformUserPasswordResetConfirmation task = new PerformUserPasswordResetConfirmation(contentCallbackListener, activityCallbackListener, email, newPassword, resetPasswordToken, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void performUserLogout(ViewCallbackListener activityCallbackListener) 
	{
		PerformUserLogout task = new PerformUserLogout(contentCallbackListener, activityCallbackListener, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	

	public void performInternalTracking(ViewCallbackListener activityCallbackListener, String tvProgramId, String deviceId)
	{
		PerformInternalTracking task = new PerformInternalTracking(contentCallbackListener, activityCallbackListener, tvProgramId, deviceId, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	/* METHODS FOR DISCUS COMMENTS */
	
	public void getDisqusThreadPosts(ViewCallbackListener activityCallbackListener, String contentID)
	{
		GetDisqusThreadPosts task = new GetDisqusThreadPosts(contentCallbackListener, activityCallbackListener, contentID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getDisqusThreadDetails(ViewCallbackListener activityCallbackListener, String contentID)
	{
		GetDisqusThreadDetails task = new GetDisqusThreadDetails(contentCallbackListener, activityCallbackListener, contentID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	/* METHODS FOR COMPETITIONS */
	
	public void getCompetitions(ViewCallbackListener activityCallbackListener, boolean standalone)
	{
		GetCompetitions task = new GetCompetitions(contentCallbackListener, activityCallbackListener, standalone, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getCompetitionsByID(ViewCallbackListener activityCallbackListener, String competitionID)
	{
		GetCompetitionByID task = new GetCompetitionByID(contentCallbackListener, activityCallbackListener, competitionID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getTeams(ViewCallbackListener activityCallbackListener, long competitionID)
	{
		GetTeams task = new GetTeams(contentCallbackListener, activityCallbackListener, competitionID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getTeamByID(ViewCallbackListener activityCallbackListener, long competitionID, long teamID)
	{
		GetTeamByID task = new GetTeamByID(contentCallbackListener, activityCallbackListener, competitionID, teamID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getPhases(ViewCallbackListener activityCallbackListener, long competitionID)
	{
		GetPhases task = new GetPhases(contentCallbackListener, activityCallbackListener, competitionID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getPhaseByID(ViewCallbackListener activityCallbackListener, long competitionID, String phaseID)
	{
		GetPhaseByID task = new GetPhaseByID(contentCallbackListener, activityCallbackListener, competitionID, phaseID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getPhaseByTeamID(ViewCallbackListener activityCallbackListener, long teamID)
	{
		GetPhaseByTeamID task = new GetPhaseByTeamID(contentCallbackListener, activityCallbackListener, teamID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getEventsForPhase(ViewCallbackListener activityCallbackListener, long competitionID, String phaseID)
	{
		GetEvents task = new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, null, phaseID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getEventsForTeam(ViewCallbackListener activityCallbackListener, long competitionID, String teamID)
	{
		GetEvents task = new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, teamID, null, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	public void getEvents(ViewCallbackListener activityCallbackListener, long competitionID)
	{
		GetEvents task = new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, null, null, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getEventByID(ViewCallbackListener activityCallbackListener, long eventID)
	{
		GetEventByID task = new GetEventByID(contentCallbackListener, activityCallbackListener, eventID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void GetStandingsForPhase(final ViewCallbackListener activityCallbackListener, final long phaseID)
	{
		GetStandingsForPhase task = new GetStandingsForPhase(contentCallbackListener, activityCallbackListener, phaseID, false, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void GetEventLineUp(final ViewCallbackListener activityCallbackListener, final Long phaseID)
	{
		GetEventLineUp task = new GetEventLineUp(contentCallbackListener, activityCallbackListener, phaseID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void GetEventHighlights(final ViewCallbackListener activityCallbackListener, final Long competitionID, final Long phaseID)
	{
		GetEventHighlights task = new GetEventHighlights(contentCallbackListener, activityCallbackListener, competitionID, phaseID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
	
	
	
	public void getSquadForTeam(final ViewCallbackListener activityCallbackListener, final Long teamID)
	{
		GetSquadForTeam task = new GetSquadForTeam(contentCallbackListener, activityCallbackListener, teamID, Constants.RETRY_COUNT_THRESHOLD);
		task.execute();
	}
}
