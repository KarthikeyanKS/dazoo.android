
package com.mitv;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
import com.mitv.asynctasks.mitvapi.usertoken.AddUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserLikes;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVChannelIds;
import com.mitv.asynctasks.mitvapi.usertoken.GetUserTVFeedItems;
import com.mitv.asynctasks.mitvapi.usertoken.PerformUserLogout;
import com.mitv.asynctasks.mitvapi.usertoken.RemoveUserLike;
import com.mitv.asynctasks.mitvapi.usertoken.SetUserTVChannelIds;
import com.mitv.asynctasks.other.CheckNetworkConnectivity;
import com.mitv.asynctasks.other.SNTPAsyncTask;
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
	private static final int  pool_executor_default_core_pool_size  = 7;
	private static final int  pool_executor_default_max_pool_size   = 10;
	private static final long pool_executor_default_keep_alive_time = 5000L;
	
	
	
	private GetTVSearchResults lastSearch;
	private ContentCallbackListener contentCallbackListener;
	private CustomThreadedPoolExecutor poolExecutor;
	
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;

		resetPoolExecutor();
	}
	

	
	public void getInitialDataOnPoolExecutor(ViewCallbackListener activityCallbackListener, boolean isUserLoggedIn)
	{
		resetPoolExecutor();
		
		GetAppConfigurationData getAppConfigurationData = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener);
		GetAppVersionData getAppVersionData = new GetAppVersionData(contentCallbackListener, activityCallbackListener);
		GetTVTags getTVTags = new GetTVTags(contentCallbackListener, activityCallbackListener);
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener);
		GetTVChannelsAll getTVChannelsAll = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener);
		GetTVChannelIdsDefault getTVChannelIdsDefault = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener);
		SNTPAsyncTask sntpAsyncTask = new SNTPAsyncTask(contentCallbackListener, activityCallbackListener);
		
		poolExecutor.addAndExecuteTask(sntpAsyncTask);
		poolExecutor.addAndExecuteTask(getAppConfigurationData);
		poolExecutor.addAndExecuteTask(getAppVersionData);
		poolExecutor.addAndExecuteTask(getTVTags);
		poolExecutor.addAndExecuteTask(getTVDates);
		poolExecutor.addAndExecuteTask(getTVChannelsAll);
		poolExecutor.addAndExecuteTask(getTVChannelIdsDefault);
		
		if(isUserLoggedIn)
		{
			GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, false);
			
			poolExecutor.addAndExecuteTask(getUserTVChannelIds);
		}
	}
	
	
	
	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, TVDate tvDate, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll) {
		resetPoolExecutor();
		
		GetTVChannelGuides getNewGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIdsOnlyNewOnes);
		SetUserTVChannelIds setUserTVChanelIds = new SetUserTVChannelIds(contentCallbackListener, null, tvChannelIdsAll);
		
		poolExecutor.addAndExecuteTask(getNewGuides);
		poolExecutor.addAndExecuteTask(setUserTVChanelIds);
	}
	
	
	
	public void getTVChannelGuideOnPoolExecutor(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides getTvChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, false, tvDate, tvChannelIds);
		
		poolExecutor.addAndExecuteTask(getTvChannelGuides);
	}
	
	
	
	private void resetPoolExecutor()
	{
		if(poolExecutor != null)
		{
			if(poolExecutor.isShutdown() == false)
			{
				poolExecutor.shutdownNow();
			}

			poolExecutor.purge();
			poolExecutor.resetTaskCount();
		}
		
		this.poolExecutor = new CustomThreadedPoolExecutor(
				pool_executor_default_core_pool_size,
				pool_executor_default_max_pool_size,
				pool_executor_default_keep_alive_time,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
	
	
	public void cancelAllPendingRequests()
	{
		poolExecutor.shutdown();
	}
	
	
	
	public boolean arePendingRequestsCanceled()
	{
		return (poolExecutor.isShutdown() || poolExecutor.isTerminated() || poolExecutor.isTerminating());
	}
	
	
	
	public void incrementCompletedTasks()
	{
		poolExecutor.incrementCompletedTasks();
	}
	
	
	
	public boolean areAllTasksCompleted()
	{
		return poolExecutor.areAllTasksCompleted();
	}
	
	
	
	public void getNetworkConnectivityIsAvailable(ViewCallbackListener activityCallbackListener) 
	{
		CheckNetworkConnectivity checkNetworkConnectivity = new CheckNetworkConnectivity(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.INTERNET_CONNECTIVITY);
		checkNetworkConnectivity.execute();
	}
	
	
	public void getAppConfiguration(ViewCallbackListener activityCallbackListener) 
	{
		GetAppConfigurationData getAppConfigurationData = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener);
		getAppConfigurationData.execute();
	}
	
	
	public void getAppVersion(ViewCallbackListener activityCallbackListener) 
	{
		GetAppVersionData getAppVersionData = new GetAppVersionData(contentCallbackListener, activityCallbackListener);
		getAppVersionData.execute();
	}
	
	
	public void getAds(ViewCallbackListener activityCallbackListener)
	{
		GetAdsAdzerk getAds = new GetAdsAdzerk(contentCallbackListener, activityCallbackListener);
		getAds.execute();
	}
	
	
	public void getTVTags(ViewCallbackListener activityCallbackListener)
	{
		GetTVTags getTVTags = new GetTVTags(contentCallbackListener, activityCallbackListener);
		getTVTags.execute();
	}
	
	
	
	public void getTVDates(ViewCallbackListener activityCallbackListener) 
	{
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener);
		getTVDates.execute();
	}
	
	
	
	public void getTVChannelsAll(ViewCallbackListener activityCallbackListener)
	{
		GetTVChannelsAll getTVChannelsAll = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener);
		getTVChannelsAll.execute();
	}
	
	
	
	public void getDefaultTVChannelIds(ViewCallbackListener activityCallbackListener) 
	{
		GetTVChannelIdsDefault getTVChannelIdsDefault = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener);
		getTVChannelIdsDefault.execute();
	}
	
	
	/**
	 * 
	 * @param activityCallbackListener
	 * @param standalone
	 */
	public void getUserTVChannelIds(ViewCallbackListener activityCallbackListener, boolean standalone) 
	{
		GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener, standalone);
		getUserTVChannelIds.execute();
	}
	
	
	
	public void performSetUserTVChannelIds(ViewCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		SetUserTVChannelIds setUserTVChannelIds = new SetUserTVChannelIds(contentCallbackListener, activityCallbackListener, tvChannelIds);
		setUserTVChannelIds.execute();
	}
	
	
	public void getUserTVFeedItemsInitial(ViewCallbackListener activityCallbackListener) 
	{
		GetUserTVFeedItems getFeedItems = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, 0, Constants.FEED_ACTIVITY_FEED_ITEM_INITIAL_COUNT);
		getFeedItems.execute();
	}
	
	public void getUserTVFeedItemsWithOffsetAndLimit(ViewCallbackListener activityCallbackListener, int offset)
	{
		GetUserTVFeedItems getFeedItems = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, offset, Constants.FEED_ACTIVITY_FEED_ITEM_MORE_COUNT);
		getFeedItems.execute();
	}
	
	
	public void getTVBroadcastDetails(ViewCallbackListener activityCallbackListener, TVChannelId tvChannelId, long beginTime)
	{
		GetTVBroadcastDetails getTVBroadcastDetails = new GetTVBroadcastDetails(contentCallbackListener, activityCallbackListener, tvChannelId, beginTime);
		getTVBroadcastDetails.execute();
	}
	
	
	
	public void getTVBroadcastsFromSeries(ViewCallbackListener activityCallbackListener, String tvSeriesId)
	{
		GetTVBroadcastsFromSeries getTVBroadcastsFromSeries = new GetTVBroadcastsFromSeries(contentCallbackListener, activityCallbackListener, tvSeriesId);
		getTVBroadcastsFromSeries.execute();
	}
	
	
	public void getTVBroadcastsPopular(ViewCallbackListener activityCallbackListener) 
	{
		GetTVBroadcastsPopular getTVBroadcastsPopular = new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener);
		getTVBroadcastsPopular.execute();
	}
	
	
	
	public void getTVBroadcastsFromProgram(ViewCallbackListener activityCallbackListener, String tvProgramId)
	{
		GetTVBroadcastsFromProgram getTVBroadcastsFromProgram = new GetTVBroadcastsFromProgram(contentCallbackListener, activityCallbackListener, tvProgramId);
		getTVBroadcastsFromProgram.execute();
	}
	
	
	public void getUserLikes(ViewCallbackListener activityCallbackListener, boolean standaLone)
	{
		GetUserLikes getUserLikes = new GetUserLikes(contentCallbackListener, activityCallbackListener, standaLone);
		getUserLikes.execute();
	}
	
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		AddUserLike addUserLikes = new AddUserLike(contentCallbackListener, activityCallbackListener, userLike);
		addUserLikes.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ViewCallbackListener activityCallbackListener, UserLike userLike) 
	{
		RemoveUserLike removeUserLike = new RemoveUserLike(contentCallbackListener, activityCallbackListener, userLike);
		removeUserLike.execute();
	}
	
	
	public void getTVChannelGuides(ViewCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides getTvChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIds);
		getTvChannelGuides.execute();
	}
	
	public void getTVSearchResults(ViewCallbackListener activityCallbackListener, String searchQuery) {
		if(lastSearch != null) {
			lastSearch.cancel(true);
		}
		GetTVSearchResults getTVSearchResults = new GetTVSearchResults(contentCallbackListener, activityCallbackListener, searchQuery);
		getTVSearchResults.execute();
		lastSearch = getTVSearchResults;
		
	}
	
	public void performUserHasSeenAd(ViewCallbackListener activityCallbackListener)
	{		
		PerformUserHasSeenAdAdzerk performUserHasSeenAd = new PerformUserHasSeenAdAdzerk(contentCallbackListener, activityCallbackListener, "");
		performUserHasSeenAd.execute();
	}
	
	
	public void performUserLoginWithFacebookToken(ViewCallbackListener activityCallbackListener, String facebookToken) 
	{
		PerformUserLoginWithFacebookToken performUserLoginWithFacebookToken = new PerformUserLoginWithFacebookToken(contentCallbackListener, activityCallbackListener, facebookToken);
		performUserLoginWithFacebookToken.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ViewCallbackListener activityCallbackListener, UserLoginDataPost userLoginDataPost, boolean usingHashedPassword) 
	{
		PerformUserLoginWithCredentials performUserLogin = new PerformUserLoginWithCredentials(contentCallbackListener, activityCallbackListener, userLoginDataPost, usingHashedPassword);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ViewCallbackListener activityCallbackListener, UserRegistrationData userRegistrationData, boolean usingHashedPassword)
	{
		PerformUserSignUp PerformUserSignUp = new PerformUserSignUp(contentCallbackListener, activityCallbackListener, userRegistrationData, usingHashedPassword);
		PerformUserSignUp.execute();
	}
	
	
	public void performUserPasswordResetSendEmail(ViewCallbackListener activityCallbackListener, String email)
	{
		PerformUserPasswordResetSendEmail performUserPasswordResetSendEmail = new PerformUserPasswordResetSendEmail(contentCallbackListener, activityCallbackListener, email);
		performUserPasswordResetSendEmail.execute();
	}
	
	
	public void performUserPasswordResetConfirmPassword(ViewCallbackListener activityCallbackListener, String email, String newPassword, String resetPasswordToken)
	{
		PerformUserPasswordResetConfirmation performUserPasswordConfirmation = new PerformUserPasswordResetConfirmation(contentCallbackListener, activityCallbackListener, email, newPassword, resetPasswordToken);
		performUserPasswordConfirmation.execute();
	}
	
	
	public void performUserLogout(ViewCallbackListener activityCallbackListener) 
	{
		PerformUserLogout performuserLogout = new PerformUserLogout(contentCallbackListener, activityCallbackListener);
		performuserLogout.execute();
	}
	

	public void performInternalTracking(ViewCallbackListener activityCallbackListener, String tvProgramId, String deviceId)
	{
		PerformInternalTracking performInternalTracking = new PerformInternalTracking(contentCallbackListener, activityCallbackListener, tvProgramId, deviceId);
		performInternalTracking.execute();
	}
	
	
	public void getDisqusThreadPosts(ViewCallbackListener activityCallbackListener, String contentID)
	{
		GetDisqusThreadPosts getDisqusThreadPosts = new GetDisqusThreadPosts(contentCallbackListener, activityCallbackListener, contentID);
		getDisqusThreadPosts.execute();
	}
}
