package com.millicom.mitv;

import java.util.List;

import com.millicom.mitv.asynctasks.GetTVChannelGuides;
import com.millicom.mitv.asynctasks.usertoken.AddUserLikes;
import com.millicom.mitv.asynctasks.usertoken.GetUserLikes;
import com.millicom.mitv.asynctasks.usertoken.RemoveUserLikes;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.model.OldTVDate;

public class APIClient {
	
	private ContentCallbackListener contentCallbackListener;
	
	public APIClient(ContentCallbackListener contentCallbackListener) {
		this.contentCallbackListener = contentCallbackListener;
	}
	
	public void getAppConfiguration(ActivityCallbackListener activityCallBackListener) {}
	
	public void getAppVersion(ActivityCallbackListener activityCallBackListener) {}
	
	public void getAds(ActivityCallbackListener activityCallBackListener) {}
	
	public void getTVTags(ActivityCallbackListener activityCallBackListener) {}
	
	public void getTVDates(ActivityCallbackListener activityCallBackListener) {}
	
	public void getTVChannelsAll(ActivityCallbackListener activityCallBackListener) {}
	
	public void getDefaultTVChannelIds(ActivityCallbackListener activityCallBackListener) {}
	
	public void getUserTVChannelIds(ActivityCallbackListener activityCallBackListener) {}
	
	public void setUserTVChannelIds(ActivityCallbackListener activityCallBackListener, List<TVChannelId> tvChannelIds) {}
	
	public void getFeedItems(ActivityCallbackListener activityCallBackListener) {}
	
	public void getRepetionsForBroadcast(ActivityCallbackListener activityCallBackListener) {}
	
	public void getUserLikes(ActivityCallbackListener activityCallBackListener) {
	
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
	
	public void getTVChannelGuides(ActivityCallbackListener activityCallBackListener, OldTVDate tvDate, List<TVChannelId> tvChannelIds) {
		GetTVChannelGuides getTvChannelGuides = GetTVChannelGuides.newGetTVChannelGuidesTask(contentCallbackListener, activityCallBackListener, tvDate, tvChannelIds);
		getTvChannelGuides.execute();
	}
	
	/* Uses the facebook token to get the MiTV token */
	public void getUserTokenUsingFBToken(ActivityCallbackListener activityCallBackListener, String facebookToken) {}
	
	/* Email is used as username  */
	public void performUserLogin(ActivityCallbackListener activityCallBackListener, String username, String password) {}
	
	public void performUserSignUp(ActivityCallbackListener activityCallBackListener, String email, String password, String firstname, String lastname) {}
	
	public void performUserPasswordResetSendEmail(ActivityCallbackListener activityCallBackListener, String email) {}
	
	public void performUserPasswordResetConfirmPassword(ActivityCallbackListener activityCallBackListener, String email, String newPassword, String resetPasswordToken) {}
	
	public void performUserLogout(ActivityCallbackListener activityCallBackListener) {}
	
}
