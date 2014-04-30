
package com.mitv;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import android.os.AsyncTask;
import com.mitv.asynctasks.CustomThreadedPoolExecutor;
import com.mitv.asynctasks.disqus.GetDisqusThreadDetails;
import com.mitv.asynctasks.disqus.GetDisqusThreadPosts;
import com.mitv.asynctasks.mitvapi.GetAdsAdzerk;
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
import com.mitv.asynctasks.mitvapi.PerformUserHasSeenAdAdzerk;
import com.mitv.asynctasks.mitvapi.PerformUserLoginWithCredentials;
import com.mitv.asynctasks.mitvapi.PerformUserLoginWithFacebookToken;
import com.mitv.asynctasks.mitvapi.PerformUserPasswordResetConfirmation;
import com.mitv.asynctasks.mitvapi.PerformUserPasswordResetSendEmail;
import com.mitv.asynctasks.mitvapi.PerformUserSignUp;
import com.mitv.asynctasks.mitvapi.competitions.GetCompetitionByID;
import com.mitv.asynctasks.mitvapi.competitions.GetCompetitions;
import com.mitv.asynctasks.mitvapi.competitions.GetEvents;
import com.mitv.asynctasks.mitvapi.competitions.GetPhaseByID;
import com.mitv.asynctasks.mitvapi.competitions.GetPhases;
import com.mitv.asynctasks.mitvapi.competitions.GetTeamDetails;
import com.mitv.asynctasks.mitvapi.competitions.GetTeams;
import com.mitv.asynctasks.mitvapi.usertoken.AddUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserLikes;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVChannelIds;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVFeedItems;
import com.mitv.asynctasks.mitvapi.usertoken.PerformUserLogout;
import com.mitv.asynctasks.mitvapi.usertoken.RemoveUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.SetUserTVChannelIds;
import com.mitv.asynctasks.other.CheckNetworkConnectivity;
import com.mitv.asynctasks.other.SNTPAsyncTask;
import com.mitv.asynctasks.other.SetPopularVariablesWithPopularBroadcasts;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.UserLike;



public class APIClient
{	
	private static final int POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE = 15;
	private static final int POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE = 20;
	private static final long POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME = 5000L;
	
	
	
	private GetTVSearchResults lastSearchTask;
	private ContentCallbackListener contentCallbackListener;
	private CustomThreadedPoolExecutor tvGuideInitialCallPoolExecutor;
	private CustomThreadedPoolExecutor competitionsInitialCallPoolExecutor;
	
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;

		resetPoolExecutor(this.tvGuideInitialCallPoolExecutor);
		
		resetPoolExecutor(this.competitionsInitialCallPoolExecutor);
	}
	
	
	
	/* THREAD POLL EXECUTOR METHODS */
	
	private static void resetPoolExecutor(CustomThreadedPoolExecutor executor)
	{
		if(executor != null)
		{
			if(executor.isShutdown() == false)
			{
				executor.shutdownNow();
			}

			executor.purge();
			executor.resetTaskCount();
		}
		
		executor = new CustomThreadedPoolExecutor(
				POOL_EXECUTOR_DEFAULT_CORE_POOL_SIZE,
				POOL_EXECUTOR_DEFAULT_MAXIMUM_POOL_SIZE,
				POLL_EXECUTOR_DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
	
	
	/* THREAD POLL EXECUTOR METHODS FOR TV GUIDE INITIAL CALL */
	
	public void getTVGuideInitialDataOnPoolExecutor(ViewCallbackListener activityCallbackListener, boolean isUserLoggedIn)
	{
		resetPoolExecutor(tvGuideInitialCallPoolExecutor);
		
		List<AsyncTask<String, Void, Void>> tasks = new ArrayList<AsyncTask<String,Void,Void>>();
		
		tasks.add(new GetAppConfigurationData(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetAppVersionData(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetTVTags(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetTVDates(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetTVChannelsAll(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener));
		tasks.add(new SNTPAsyncTask(contentCallbackListener, activityCallbackListener));
		tasks.add(new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener, false));
		tasks.add(new GetCompetitions(contentCallbackListener, activityCallbackListener));
		
		for(AsyncTask<String, Void, Void> task : tasks)
		{
			tvGuideInitialCallPoolExecutor.addAndExecuteTask(task);
		}
		
		if(isUserLoggedIn)
		{
			GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, false);
			
			tvGuideInitialCallPoolExecutor.addAndExecuteTask(getUserTVChannelIds);
		}
	}
	
	
	
	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, TVDate tvDate, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll) 
	{
		resetPoolExecutor(tvGuideInitialCallPoolExecutor);
		
		GetTVChannelGuides getNewGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIdsOnlyNewOnes);
		SetUserTVChannelIds setUserTVChanelIds = new SetUserTVChannelIds(contentCallbackListener, null, tvChannelIdsAll);
		
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(getNewGuides);
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(setUserTVChanelIds);
	}
	
	
	
	public void getTVChannelGuideOnPoolExecutor(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides task = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, false, tvDate, tvChannelIds);
		
		tvGuideInitialCallPoolExecutor.addAndExecuteTask(task);
	}
	
	
	
	public void setPopularVariablesWithPopularBroadcastsOnPoolExecutor(ViewCallbackListener activityCallbackListener)
	{
		SetPopularVariablesWithPopularBroadcasts task = new SetPopularVariablesWithPopularBroadcasts(contentCallbackListener, activityCallbackListener);
		
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
	
	public void getCompetitionInitialDataOnPoolExecutor(ViewCallbackListener activityCallbackListener, String competitionID)
	{
		resetPoolExecutor(competitionsInitialCallPoolExecutor);
		
		List<AsyncTask<String, Void, Void>> tasks = new ArrayList<AsyncTask<String,Void,Void>>();
		
		tasks.add(new GetTeams(contentCallbackListener, activityCallbackListener, competitionID));
		tasks.add(new GetPhases(contentCallbackListener, activityCallbackListener, competitionID));
		
		for(AsyncTask<String, Void, Void> task : tasks)
		{
			competitionsInitialCallPoolExecutor.addAndExecuteTask(task);
		}
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
		GetAppConfigurationData task = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	public void getAppVersion(ViewCallbackListener activityCallbackListener) 
	{
		GetAppVersionData task = new GetAppVersionData(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	public void getAds(ViewCallbackListener activityCallbackListener)
	{
		GetAdsAdzerk task = new GetAdsAdzerk(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	public void getTVTags(ViewCallbackListener activityCallbackListener)
	{
		GetTVTags task = new GetTVTags(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	
	public void getTVDates(ViewCallbackListener activityCallbackListener) 
	{
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener);
		getTVDates.execute();
	}
	
	
	
	public void getTVChannelsAll(ViewCallbackListener activityCallbackListener)
	{
		GetTVChannelsAll task = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	
	public void getDefaultTVChannelIds(ViewCallbackListener activityCallbackListener) 
	{
		GetTVChannelIdsDefault task = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	/**
	 * 
	 * @param activityCallbackListener
	 * @param standalone
	 */
	public void getUserTVChannelIds(ViewCallbackListener activityCallbackListener, boolean standalone) 
	{
		GetUserTVChannelIds task = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, standalone);
		task.execute();
	}
	
	
	
	public void performSetUserTVChannelIds(ViewCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		SetUserTVChannelIds task = new SetUserTVChannelIds(contentCallbackListener, activityCallbackListener, tvChannelIds);
		task.execute();
	}
	
	
	public void getUserTVFeedItemsInitial(ViewCallbackListener activityCallbackListener) 
	{
		GetUserTVFeedItems task = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, 0, Constants.FEED_ACTIVITY_FEED_ITEM_INITIAL_COUNT);
		task.execute();
	}
	
	public void getUserTVFeedItemsWithOffsetAndLimit(ViewCallbackListener activityCallbackListener, int offset)
	{
		GetUserTVFeedItems task = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, offset, Constants.FEED_ACTIVITY_FEED_ITEM_MORE_COUNT);
		task.execute();
	}
	
	
	public void getTVBroadcastDetails(ViewCallbackListener activityCallbackListener, TVChannelId tvChannelId, long beginTime)
	{
		GetTVBroadcastDetails task = new GetTVBroadcastDetails(contentCallbackListener, activityCallbackListener, tvChannelId, beginTime);
		task.execute();
	}
	
	
	
	public void getTVBroadcastsFromSeries(ViewCallbackListener activityCallbackListener, String tvSeriesId)
	{
		GetTVBroadcastsFromSeries task = new GetTVBroadcastsFromSeries(contentCallbackListener, activityCallbackListener, tvSeriesId);
		task.execute();
	}
	
	
	public void getTVBroadcastsPopular(ViewCallbackListener activityCallbackListener, boolean standalone) 
	{
		GetTVBroadcastsPopular task = new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener, standalone);
		task.execute();
	}
	
	
	
	public void getTVBroadcastsFromProgram(ViewCallbackListener activityCallbackListener, String tvProgramId)
	{
		GetTVBroadcastsFromProgram task = new GetTVBroadcastsFromProgram(contentCallbackListener, activityCallbackListener, tvProgramId);
		task.execute();
	}
	
	
	public void getUserLikes(ViewCallbackListener activityCallbackListener, boolean standaLone)
	{
		GetUserLikes task = new GetUserLikes(contentCallbackListener, activityCallbackListener, standaLone);
		task.execute();
	}
	
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		AddUserLike task = new AddUserLike(contentCallbackListener, activityCallbackListener, userLike);
		task.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		RemoveUserLike task = new RemoveUserLike(contentCallbackListener, activityCallbackListener, userLike);
		task.execute();
	}
	
	
	
	public void getTVChannelGuides(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides task = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIds);
		task.execute();
	}
	
	
	
	public void getTVSearchResults(ViewCallbackListener activityCallbackListener, String searchQuery)
	{
		if(lastSearchTask != null)
		{
			lastSearchTask.cancel(true);
		}
		
		GetTVSearchResults task = new GetTVSearchResults(contentCallbackListener, activityCallbackListener, searchQuery);
		task.execute();
		
		lastSearchTask = task;
	}
	
	
	public void performUserHasSeenAd(ViewCallbackListener activityCallbackListener)
	{		
		PerformUserHasSeenAdAdzerk task = new PerformUserHasSeenAdAdzerk(contentCallbackListener, activityCallbackListener, "");
		task.execute();
	}
	
	
	public void performUserLoginWithFacebookToken(ViewCallbackListener activityCallbackListener, String facebookToken) 
	{
		PerformUserLoginWithFacebookToken task = new PerformUserLoginWithFacebookToken(contentCallbackListener, activityCallbackListener, facebookToken);
		task.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ViewCallbackListener activityCallbackListener, UserLoginDataPost userLoginDataPost, boolean usingHashedPassword) 
	{
		PerformUserLoginWithCredentials performUserLogin = new PerformUserLoginWithCredentials(contentCallbackListener, activityCallbackListener, userLoginDataPost, usingHashedPassword);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ViewCallbackListener activityCallbackListener, UserRegistrationData userRegistrationData, boolean usingHashedPassword)
	{
		PerformUserSignUp task = new PerformUserSignUp(contentCallbackListener, activityCallbackListener, userRegistrationData, usingHashedPassword);
		task.execute();
	}
	
	
	public void performUserPasswordResetSendEmail(ViewCallbackListener activityCallbackListener, String email)
	{
		PerformUserPasswordResetSendEmail task = new PerformUserPasswordResetSendEmail(contentCallbackListener, activityCallbackListener, email);
		task.execute();
	}
	
	
	public void performUserPasswordResetConfirmPassword(ViewCallbackListener activityCallbackListener, String email, String newPassword, String resetPasswordToken)
	{
		PerformUserPasswordResetConfirmation task = new PerformUserPasswordResetConfirmation(contentCallbackListener, activityCallbackListener, email, newPassword, resetPasswordToken);
		task.execute();
	}
	
	
	public void performUserLogout(ViewCallbackListener activityCallbackListener) 
	{
		PerformUserLogout task = new PerformUserLogout(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	

	public void performInternalTracking(ViewCallbackListener activityCallbackListener, String tvProgramId, String deviceId)
	{
		PerformInternalTracking task = new PerformInternalTracking(contentCallbackListener, activityCallbackListener, tvProgramId, deviceId);
		task.execute();
	}
	
	
	
	/* METHODS FOR DISCUS COMMENTS */
	
	public void getDisqusThreadPosts(ViewCallbackListener activityCallbackListener, String contentID)
	{
		GetDisqusThreadPosts task = new GetDisqusThreadPosts(contentCallbackListener, activityCallbackListener, contentID);
		task.execute();
	}
	
	
	public void getDisqusThreadDetails(ViewCallbackListener activityCallbackListener, String contentID)
	{
		GetDisqusThreadDetails task = new GetDisqusThreadDetails(contentCallbackListener, activityCallbackListener, contentID);
		task.execute();
	}
	
	
	
	/* METHODS FOR COMPETITIONS */
	
	public void getCompetitions(ViewCallbackListener activityCallbackListener)
	{
		GetCompetitions task = new GetCompetitions(contentCallbackListener, activityCallbackListener);
		task.execute();
	}
	
	
	public void getCompetitionsByID(ViewCallbackListener activityCallbackListener, String competitionID)
	{
		GetCompetitionByID task = new GetCompetitionByID(contentCallbackListener, activityCallbackListener, competitionID);
		task.execute();
	}
	
	
	public void getTeams(ViewCallbackListener activityCallbackListener, String competitionID)
	{
		GetTeams task = new GetTeams(contentCallbackListener, activityCallbackListener, competitionID);
		task.execute();
	}
	
	
	public void getTeamDetails(ViewCallbackListener activityCallbackListener, String competitionID, String teamID)
	{
		GetTeamDetails task = new GetTeamDetails(contentCallbackListener, activityCallbackListener, competitionID, teamID);
		task.execute();
	}
	
	
	public void getPhases(ViewCallbackListener activityCallbackListener, String competitionID)
	{
		GetPhases task = new GetPhases(contentCallbackListener, activityCallbackListener, competitionID);
		task.execute();
	}
	
	
	public void getPhaseByID(ViewCallbackListener activityCallbackListener, String competitionID, String phaseID)
	{
		GetPhaseByID task = new GetPhaseByID(contentCallbackListener, activityCallbackListener, competitionID, phaseID);
		task.execute();
	}
	
	
	public void getEventsForPhase(ViewCallbackListener activityCallbackListener, String competitionID, String phaseID)
	{
		GetEvents task = new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, null, phaseID);
		task.execute();
	}
	
	
	public void getEventsForTeam(ViewCallbackListener activityCallbackListener, String competitionID, String teamID)
	{
		GetEvents task = new GetEvents(contentCallbackListener, activityCallbackListener, competitionID, teamID, null);
		task.execute();
	}
}
