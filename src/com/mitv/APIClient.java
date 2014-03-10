
package com.mitv;



import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.androidquery.callback.AjaxCallback;
import com.mitv.asynctasks.CheckNetworkConnectivity;
import com.mitv.asynctasks.GetAdsAdzerk;
import com.mitv.asynctasks.GetAppConfigurationData;
import com.mitv.asynctasks.GetAppVersionData;
import com.mitv.asynctasks.GetTVBroadcastDetails;
import com.mitv.asynctasks.GetTVBroadcastsFromProgram;
import com.mitv.asynctasks.GetTVBroadcastsFromSeries;
import com.mitv.asynctasks.GetTVBroadcastsPopular;
import com.mitv.asynctasks.GetTVChannelGuides;
import com.mitv.asynctasks.GetTVChannelIdsDefault;
import com.mitv.asynctasks.GetTVChannelsAll;
import com.mitv.asynctasks.GetTVDates;
import com.mitv.asynctasks.GetTVSearchResults;
import com.mitv.asynctasks.GetTVTags;
import com.mitv.asynctasks.PerformInternalTracking;
import com.mitv.asynctasks.PerformUserHasSeenAdAdzerk;
import com.mitv.asynctasks.PerformUserLoginWithCredentials;
import com.mitv.asynctasks.PerformUserLoginWithFacebookToken;
import com.mitv.asynctasks.PerformUserPasswordResetConfirmation;
import com.mitv.asynctasks.PerformUserPasswordResetSendEmail;
import com.mitv.asynctasks.PerformUserSignUp;
import com.mitv.asynctasks.usertoken.AddUserLike;
import com.mitv.asynctasks.usertoken.GetUserLikes;
import com.mitv.asynctasks.usertoken.GetUserTVChannelIds;
import com.mitv.asynctasks.usertoken.GetUserTVFeedItems;
import com.mitv.asynctasks.usertoken.PerformUserLogout;
import com.mitv.asynctasks.usertoken.RemoveUserLike;
import com.mitv.asynctasks.usertoken.SetUserTVChannelIds;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVDate;
import com.mitv.models.UserLike;



public class APIClient
{	
	private static final int  pool_executor_default_core_pool_size  = 7;
	private static final int  pool_executor_default_max_pool_size   = 10;
	private static final long pool_executor_default_keep_alive_time = 5000L;
	
	
	private ContentCallbackListener contentCallbackListener;
	private CustomThreadedPoolExecutor poolExecutor;
	
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;

		resetPoolExecutor();
	}
	

	
	public void getInitialDataOnPoolExecutor(ActivityCallbackListener activityCallbackListener, boolean isUserLoggedIn)
	{
		resetPoolExecutor();
		
		GetAppConfigurationData getAppConfigurationData = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener);
		GetAppVersionData getAppVersionData = new GetAppVersionData(contentCallbackListener, activityCallbackListener);
		GetTVTags getTVTags = new GetTVTags(contentCallbackListener, activityCallbackListener);
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener);
		GetTVChannelsAll getTVChannelsAll = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener);
		GetTVChannelIdsDefault getTVChannelIdsDefault = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener);
		
		poolExecutor.addAndExecuteTask(getAppConfigurationData);
		poolExecutor.addAndExecuteTask(getAppVersionData);
		poolExecutor.addAndExecuteTask(getTVTags);
		poolExecutor.addAndExecuteTask(getTVDates);
		poolExecutor.addAndExecuteTask(getTVChannelsAll);
		poolExecutor.addAndExecuteTask(getTVChannelIdsDefault);
				
		if(isUserLoggedIn)
		{
			GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener);
			
			poolExecutor.addAndExecuteTask(getUserTVChannelIds);
		}
	}
	
	
	
	public void getTVChannelGuideOnPoolExecutor(ActivityCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
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
	
	
	
	public void getNetworkConnectivityIsAvailable(ActivityCallbackListener activityCallbackListener) 
	{
		CheckNetworkConnectivity checkNetworkConnectivity = new CheckNetworkConnectivity(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.INTERNET_CONNECTIVITY);
		checkNetworkConnectivity.execute();
	}
	
	
	public void getAppConfiguration(ActivityCallbackListener activityCallbackListener) 
	{
		GetAppConfigurationData getAppConfigurationData = new GetAppConfigurationData(contentCallbackListener, activityCallbackListener);
		getAppConfigurationData.execute();
	}
	
	
	public void getAppVersion(ActivityCallbackListener activityCallbackListener) 
	{
		GetAppVersionData getAppVersionData = new GetAppVersionData(contentCallbackListener, activityCallbackListener);
		getAppVersionData.execute();
	}
	
	
	public void getAds(ActivityCallbackListener activityCallbackListener)
	{
		GetAdsAdzerk getAds = new GetAdsAdzerk(contentCallbackListener, activityCallbackListener);
		getAds.execute();
	}
	
	
	public void getTVTags(ActivityCallbackListener activityCallbackListener)
	{
		GetTVTags getTVTags = new GetTVTags(contentCallbackListener, activityCallbackListener);
		getTVTags.execute();
	}
	
	
	
	public void getTVDates(ActivityCallbackListener activityCallbackListener) 
	{
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallbackListener);
		getTVDates.execute();
	}
	
	
	
	public void getTVChannelsAll(ActivityCallbackListener activityCallbackListener)
	{
		GetTVChannelsAll getTVChannelsAll = new GetTVChannelsAll(contentCallbackListener, activityCallbackListener);
		getTVChannelsAll.execute();
	}
	
	
	
	public void getDefaultTVChannelIds(ActivityCallbackListener activityCallbackListener) 
	{
		GetTVChannelIdsDefault getTVChannelIdsDefault = new GetTVChannelIdsDefault(contentCallbackListener, activityCallbackListener);
		getTVChannelIdsDefault.execute();
	}
	
	
	
	public void getUserTVChannelIds(ActivityCallbackListener activityCallbackListener) 
	{
		GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallbackListener);
		getUserTVChannelIds.execute();
	}
	
	
	
	public void performSetUserTVChannelIds(ActivityCallbackListener activityCallbackListener, List<TVChannelId> tvChannelIds) 
	{
		SetUserTVChannelIds setUserTVChannelIds = new SetUserTVChannelIds(contentCallbackListener, activityCallbackListener, tvChannelIds);
		setUserTVChannelIds.execute();
	}
	
	
	public void getUserTVFeedItems(ActivityCallbackListener activityCallbackListener) 
	{
		GetUserTVFeedItems getFeedItems = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener);
		getFeedItems.execute();
	}
	
	public void getUserTVFeedItemsWithOffsetAndLimit(ActivityCallbackListener activityCallbackListener, int offset, int limit)
	{
		GetUserTVFeedItems getFeedItems = new GetUserTVFeedItems(contentCallbackListener, activityCallbackListener, offset, limit);
		getFeedItems.execute();
	}
	
	
	public void getTVBroadcastDetails(ActivityCallbackListener activityCallbackListener, TVChannelId tvChannelId, long beginTime)
	{
		GetTVBroadcastDetails getTVBroadcastDetails = new GetTVBroadcastDetails(contentCallbackListener, activityCallbackListener, tvChannelId, beginTime);
		getTVBroadcastDetails.execute();
	}
	
	
	
	public void getTVBroadcastsFromSeries(ActivityCallbackListener activityCallbackListener, String tvSeriesId)
	{
		GetTVBroadcastsFromSeries getTVBroadcastsFromSeries = new GetTVBroadcastsFromSeries(contentCallbackListener, activityCallbackListener, tvSeriesId);
		getTVBroadcastsFromSeries.execute();
	}
	
	
	public void getTVBroadcastsPopular(ActivityCallbackListener activityCallbackListener) 
	{
		GetTVBroadcastsPopular getTVBroadcastsPopular = new GetTVBroadcastsPopular(contentCallbackListener, activityCallbackListener);
		getTVBroadcastsPopular.execute();
	}
	
	
	
	public void getTVBroadcastsFromProgram(ActivityCallbackListener activityCallbackListener, String tvProgramId)
	{
		GetTVBroadcastsFromProgram getTVBroadcastsFromProgram = new GetTVBroadcastsFromProgram(contentCallbackListener, activityCallbackListener, tvProgramId);
		getTVBroadcastsFromProgram.execute();
	}
	
	
	public void getUserLikes(ActivityCallbackListener activityCallbackListener, boolean standaLone)
	{
		GetUserLikes getUserLikes = new GetUserLikes(contentCallbackListener, activityCallbackListener, standaLone);
		getUserLikes.execute();
	}
	
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ActivityCallbackListener activityCallbackListener, UserLike userLike) 
	{
		AddUserLike addUserLikes = new AddUserLike(contentCallbackListener, activityCallbackListener, userLike);
		addUserLikes.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ActivityCallbackListener activityCallbackListener, UserLike userLike) 
	{
		RemoveUserLike removeUserLike = new RemoveUserLike(contentCallbackListener, activityCallbackListener, userLike);
		removeUserLike.execute();
	}
	
	
	public void getTVChannelGuides(ActivityCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides getTvChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, true, tvDate, tvChannelIds);
		getTvChannelGuides.execute();
	}
	
	public void getTVSearchResults(ActivityCallbackListener activityCallbackListener, AjaxCallback<String> ajaxCallback, String searchQuery) {
		GetTVSearchResults getTVSearchResults = new GetTVSearchResults(contentCallbackListener, activityCallbackListener, ajaxCallback, searchQuery);
		getTVSearchResults.execute();
	}
	
	public void performUserHasSeenAd(ActivityCallbackListener activityCallbackListener)
	{
		// TODO NewArc - Set correct suffix
		String url = "";
				
		PerformUserHasSeenAdAdzerk performUserHasSeenAd = new PerformUserHasSeenAdAdzerk(contentCallbackListener, activityCallbackListener, url);
		performUserHasSeenAd.execute();
	}
	
	
	public void performUserLoginWithFacebookToken(ActivityCallbackListener activityCallbackListener, String facebookToken) 
	{
		PerformUserLoginWithFacebookToken performUserLoginWithFacebookToken = new PerformUserLoginWithFacebookToken(contentCallbackListener, activityCallbackListener, facebookToken);
		performUserLoginWithFacebookToken.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ActivityCallbackListener activityCallbackListener, String username, String password) 
	{
		PerformUserLoginWithCredentials performUserLogin = new PerformUserLoginWithCredentials(contentCallbackListener, activityCallbackListener, username, password);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ActivityCallbackListener activityCallbackListener, String email, String password, String firstname, String lastname) 
	{
		PerformUserSignUp PerformUserSignUp = new PerformUserSignUp(contentCallbackListener, activityCallbackListener, email, password, firstname, lastname);
		PerformUserSignUp.execute();
	}
	
	
	public void performUserPasswordResetSendEmail(ActivityCallbackListener activityCallbackListener, String email)
	{
		PerformUserPasswordResetSendEmail performUserPasswordResetSendEmail = new PerformUserPasswordResetSendEmail(contentCallbackListener, activityCallbackListener, email);
		performUserPasswordResetSendEmail.execute();
	}
	
	
	public void performUserPasswordResetConfirmPassword(ActivityCallbackListener activityCallbackListener, String email, String newPassword, String resetPasswordToken)
	{
		PerformUserPasswordResetConfirmation performUserPasswordConfirmation = new PerformUserPasswordResetConfirmation(contentCallbackListener, activityCallbackListener, email, newPassword, resetPasswordToken);
		performUserPasswordConfirmation.execute();
	}
	
	
	public void performUserLogout(ActivityCallbackListener activityCallbackListener) 
	{
		PerformUserLogout performuserLogout = new PerformUserLogout(contentCallbackListener, activityCallbackListener);
		performuserLogout.execute();
	}
	

	public void performInternalTracking(ActivityCallbackListener activityCallbackListener, String tvProgramId, String deviceId)
	{
		PerformInternalTracking performInternalTracking = new PerformInternalTracking(contentCallbackListener, activityCallbackListener, tvProgramId, deviceId);
		performInternalTracking.execute();
	}
}
