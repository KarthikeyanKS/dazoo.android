
package com.millicom.mitv;



import java.util.List;

import com.androidquery.callback.AjaxCallback;
import com.millicom.mitv.asynctasks.CheckNetworkConnectivity;
import com.millicom.mitv.asynctasks.GetAdsAdzerk;
import com.millicom.mitv.asynctasks.GetAppConfigurationData;
import com.millicom.mitv.asynctasks.GetAppVersionData;
import com.millicom.mitv.asynctasks.GetTVBroadcastDetails;
import com.millicom.mitv.asynctasks.GetTVBroadcastsFromProgram;
import com.millicom.mitv.asynctasks.GetTVBroadcastsFromSeries;
import com.millicom.mitv.asynctasks.GetTVBroadcastsPopular;
import com.millicom.mitv.asynctasks.GetTVChannelGuides;
import com.millicom.mitv.asynctasks.GetTVChannelIdsDefault;
import com.millicom.mitv.asynctasks.GetTVChannelsAll;
import com.millicom.mitv.asynctasks.GetTVDates;
import com.millicom.mitv.asynctasks.GetTVSearchResults;
import com.millicom.mitv.asynctasks.GetTVTags;
import com.millicom.mitv.asynctasks.PerformInternalTracking;
import com.millicom.mitv.asynctasks.PerformUserHasSeenAdAdzerk;
import com.millicom.mitv.asynctasks.PerformUserLogin;
import com.millicom.mitv.asynctasks.PerformUserPasswordResetConfirmation;
import com.millicom.mitv.asynctasks.PerformUserPasswordResetSendEmail;
import com.millicom.mitv.asynctasks.PerformUserSignUp;
import com.millicom.mitv.asynctasks.usertoken.AddUserLike;
import com.millicom.mitv.asynctasks.usertoken.GetUserLikes;
import com.millicom.mitv.asynctasks.usertoken.GetUserTVChannelIds;
import com.millicom.mitv.asynctasks.usertoken.GetUserTVFeedItems;
import com.millicom.mitv.asynctasks.usertoken.GetUserTokenUsingFBToken;
import com.millicom.mitv.asynctasks.usertoken.PerformUserLogout;
import com.millicom.mitv.asynctasks.usertoken.RemoveUserLike;
import com.millicom.mitv.asynctasks.usertoken.SetUserTVChannelIds;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;



public class APIClient
{	
	private ContentCallbackListener contentCallbackListener;
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;
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
	public void addUserLike(ActivityCallbackListener activityCallbackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		AddUserLike addUserLikes = new AddUserLike(contentCallbackListener, activityCallbackListener, likeType, contentId);
		addUserLikes.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ActivityCallbackListener activityCallbackListener, LikeTypeRequestEnum likeType, String contentId) 
	{
		RemoveUserLike removeUserLike = new RemoveUserLike(contentCallbackListener, activityCallbackListener, likeType, contentId);
		removeUserLike.execute();
	}
	
	
	public void getTVChannelGuides(ActivityCallbackListener activityCallbackListener, TVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides getTvChannelGuides = new GetTVChannelGuides(contentCallbackListener, activityCallbackListener, tvDate, tvChannelIds);
		getTvChannelGuides.execute();
	}
	
	public void getTVSearchResults(ActivityCallbackListener activityCallbackListener, AjaxCallback<String> ajaxCallback, String searchQuery) {
		GetTVSearchResults getTVSearchResults = new GetTVSearchResults(contentCallbackListener, activityCallbackListener, ajaxCallback, searchQuery);
		getTVSearchResults.execute();
	}
	
	public void performUserHasSeenAd(ActivityCallbackListener activityCallbackListener)
	{
		// TODO - Set correct suffix
		String url = "";
				
		PerformUserHasSeenAdAdzerk performUserHasSeenAd = new PerformUserHasSeenAdAdzerk(contentCallbackListener, activityCallbackListener, url);
		performUserHasSeenAd.execute();
	}
	
	
	/* Uses the facebook token to get the MiTV token */
	public void getUserTokenUsingFBToken(ActivityCallbackListener activityCallbackListener, String facebookToken) 
	{
		GetUserTokenUsingFBToken getUserTokenUsingFBToken = new GetUserTokenUsingFBToken(contentCallbackListener, activityCallbackListener, facebookToken);
		getUserTokenUsingFBToken.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ActivityCallbackListener activityCallbackListener, String username, String password) 
	{
		PerformUserLogin performUserLogin = new PerformUserLogin(contentCallbackListener, activityCallbackListener, username, password);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ActivityCallbackListener activityCallbackListener, String email, String password, String firstname, String lastname) 
	{
		com.millicom.mitv.asynctasks.PerformUserSignUp PerformUserSignUp = new PerformUserSignUp(contentCallbackListener, activityCallbackListener, email, password, firstname, lastname);
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
