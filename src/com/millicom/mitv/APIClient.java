
package com.millicom.mitv;



import java.util.List;
import com.millicom.mitv.asynctasks.GetAds;
import com.millicom.mitv.asynctasks.GetAppConfigurationData;
import com.millicom.mitv.asynctasks.GetAppVersionData;
import com.millicom.mitv.asynctasks.GetTVChannelGuides;
import com.millicom.mitv.asynctasks.GetTVChannelIdsDefault;
import com.millicom.mitv.asynctasks.GetTVChannels;
import com.millicom.mitv.asynctasks.GetTVDates;
import com.millicom.mitv.asynctasks.GetTVTags;
import com.millicom.mitv.asynctasks.PerformUserLogin;
import com.millicom.mitv.asynctasks.PerformUserSignUp;
import com.millicom.mitv.asynctasks.PerformUserHasSeenAd;
import com.millicom.mitv.asynctasks.usertoken.AddUserLikes;
import com.millicom.mitv.asynctasks.usertoken.GetFeedItems;
import com.millicom.mitv.asynctasks.usertoken.GetUserLikes;
import com.millicom.mitv.asynctasks.usertoken.GetUserTVChannelIds;
import com.millicom.mitv.asynctasks.usertoken.GetUserTokenUsingFBToken;
import com.millicom.mitv.asynctasks.usertoken.PerformUserPasswordConfirmation;
import com.millicom.mitv.asynctasks.usertoken.PerformUserPasswordResetSendEmail;
import com.millicom.mitv.asynctasks.usertoken.RemoveUserLikes;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.mitv.model.OldTVDate;



public class APIClient
{	
	private ContentCallbackListener contentCallbackListener;
	
	
	public APIClient(ContentCallbackListener contentCallbackListener) 
	{
		this.contentCallbackListener = contentCallbackListener;
	}
	
	
	public void getAppConfiguration(ActivityCallbackListener activityCallBackListener) 
	{
		GetAppConfigurationData getAppConfigurationData = new GetAppConfigurationData(contentCallbackListener, activityCallBackListener);
		getAppConfigurationData.execute();
	}
	
	
	public void getAppVersion(ActivityCallbackListener activityCallBackListener) 
	{
		GetAppVersionData getAppVersionData = new GetAppVersionData(contentCallbackListener, activityCallBackListener);
		getAppVersionData.execute();
	}
	
	
	public void getAds(ActivityCallbackListener activityCallBackListener)
	{
		GetAds getAds = new GetAds(contentCallbackListener, activityCallBackListener);
		getAds.execute();
	}
	
	
	public void getTVTags(ActivityCallbackListener activityCallBackListener)
	{
		GetTVTags getTVTags = new GetTVTags(contentCallbackListener, activityCallBackListener);
		getTVTags.execute();
	}
	
	
	public void getTVDates(ActivityCallbackListener activityCallBackListener) 
	{
		GetTVDates getTVDates = new GetTVDates(contentCallbackListener, activityCallBackListener);
		getTVDates.execute();
	}
	
	
	public void getTVChannelsAll(ActivityCallbackListener activityCallBackListener)
	{
		// TODO - Set correct suffix
		String urlSuffix = "";
		
		GetTVChannels getTVChannels = new GetTVChannels(contentCallbackListener, activityCallBackListener, urlSuffix);
		getTVChannels.execute();
	}
	
	
	public void getDefaultTVChannelIds(ActivityCallbackListener activityCallBackListener) 
	{
		GetTVChannelIdsDefault getTVChannelIdsDefault = new GetTVChannelIdsDefault(contentCallbackListener, activityCallBackListener);
		getTVChannelIdsDefault.execute();
	}
	
	
	public void getUserTVChannelIds(ActivityCallbackListener activityCallBackListener) 
	{
		// TODO - Set correct suffix
		String urlSuffix = "";
		
		GetUserTVChannelIds getUserTVChannelIds = new GetUserTVChannelIds(contentCallbackListener, activityCallBackListener, urlSuffix);
		getUserTVChannelIds.execute();
	}
	
	
	public void setUserTVChannelIds(ActivityCallbackListener activityCallBackListener, List<TVChannelId> tvChannelIds) 
	{}
	
	
	public void getFeedItems(ActivityCallbackListener activityCallBackListener) 
	{
		GetFeedItems getFeedItems = new GetFeedItems(contentCallbackListener, activityCallBackListener);
		getFeedItems.execute();
	}
	
	
	public void getRepetionsForBroadcast(ActivityCallbackListener activityCallBackListener)
	{}
	
	
	public void getUserLikes(ActivityCallbackListener activityCallBackListener)
	{
		GetUserLikes getUserLikes = new GetUserLikes(contentCallbackListener, activityCallBackListener);
		getUserLikes.execute();
	}
	
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ActivityCallbackListener activityCallBackListener, ContentTypeEnum likeType, String contentId) 
	{
		AddUserLikes addUserLikes = new AddUserLikes(contentCallbackListener, activityCallBackListener, likeType, contentId);
		addUserLikes.execute();
	}

	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ActivityCallbackListener activityCallBackListener, ContentTypeEnum likeType, String contentId) 
	{
		RemoveUserLikes removeUserLikes = new RemoveUserLikes(contentCallbackListener, activityCallBackListener, likeType, contentId);
		removeUserLikes.execute();
	}
	
	
	public void getTVChannelGuides(ActivityCallbackListener activityCallBackListener, OldTVDate tvDate, List<TVChannelId> tvChannelIds)
	{
		GetTVChannelGuides getTvChannelGuides = GetTVChannelGuides.newGetTVChannelGuidesTask(contentCallbackListener, activityCallBackListener, tvDate, tvChannelIds);
		getTvChannelGuides.execute();
	}
	
	
	public void PerformUserHasSeenAd(ActivityCallbackListener activityCallBackListener)
	{
		// TODO - Set correct suffix
		String url = "";
				
		PerformUserHasSeenAd performUserHasSeenAd = new PerformUserHasSeenAd(contentCallbackListener, activityCallBackListener, url);
		performUserHasSeenAd.execute();
	}
	
	
	/* Uses the facebook token to get the MiTV token */
	public void getUserTokenUsingFBToken(ActivityCallbackListener activityCallBackListener, String facebookToken) 
	{
		GetUserTokenUsingFBToken getUserTokenUsingFBToken = new GetUserTokenUsingFBToken(contentCallbackListener, activityCallBackListener, facebookToken);
		getUserTokenUsingFBToken.execute();
	}
	
	
	/* Email is used as username  */
	public void performUserLogin(ActivityCallbackListener activityCallBackListener, String username, String password) 
	{
		PerformUserLogin performUserLogin = new PerformUserLogin(contentCallbackListener, activityCallBackListener, username, password);
		performUserLogin.execute();
	}
	
	
	public void performUserSignUp(ActivityCallbackListener activityCallBackListener, String email, String password, String firstname, String lastname) 
	{
		com.millicom.mitv.asynctasks.PerformUserSignUp PerformUserSignUp = new PerformUserSignUp(contentCallbackListener, activityCallBackListener, email, password, firstname, lastname);
		PerformUserSignUp.execute();
	}
	
	
	public void performUserPasswordResetSendEmail(ActivityCallbackListener activityCallBackListener, String email)
	{
		PerformUserPasswordResetSendEmail performUserPasswordResetSendEmail = new PerformUserPasswordResetSendEmail(contentCallbackListener, activityCallBackListener, email);
		performUserPasswordResetSendEmail.execute();
	}
	
	
	public void performUserPasswordResetConfirmPassword(ActivityCallbackListener activityCallBackListener, String email, String newPassword, String resetPasswordToken)
	{
		PerformUserPasswordConfirmation performUserPasswordConfirmation = new PerformUserPasswordConfirmation(contentCallbackListener, activityCallBackListener, email, newPassword, resetPasswordToken);
		performUserPasswordConfirmation.execute();
	}
	
	
	public void performUserLogout(ActivityCallbackListener activityCallBackListener) 
	{
		// No task is currently called for logout
	}
}
