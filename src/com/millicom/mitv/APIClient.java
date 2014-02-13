package com.millicom.mitv;

import java.util.List;

import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.model.TVDate;

public class APIClient {
	
	private static APIClient sharedInstance;
	
	public static APIClient sharedInstance() {
		if(sharedInstance == null) {
			sharedInstance = new APIClient();
		}
		return sharedInstance;
	}
	
	public void getAppConfiguration(ActivityCallbackListener activityCallBack) {}
	
	public void getAppVersion(ActivityCallbackListener activityCallBack) {}
	
	public void getAds(ActivityCallbackListener activityCallBack) {}
	
	public void getTVTags(ActivityCallbackListener activityCallBack) {}
	
	public void getTVDates(ActivityCallbackListener activityCallBack) {}
	
	public void getTVChannelsAll(ActivityCallbackListener activityCallBack) {}
	
	public void getDefaultTVChannelIds(ActivityCallbackListener activityCallBack) {}
	
	public void getUserTVChannelIds(ActivityCallbackListener activityCallBack) {}
	
	public void setUserTVChannelIds(ActivityCallbackListener activityCallBack, List<TVChannelId> tvChannelIds) {}
	
	public void getFeedItems(ActivityCallbackListener activityCallBack) {}
	
	public void getRepetionsForBroadcast(ActivityCallbackListener activityCallBack) {}
	
	public void getUserLikes(ActivityCallbackListener activityCallBack) {}
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(ActivityCallbackListener activityCallBack, String contentId) {}

	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(ActivityCallbackListener activityCallBack ,String contentId) {}
	
	public void getTVChannelGuides(ActivityCallbackListener activityCallBack, TVDate tvDate, List<TVChannelId> tvChannelIds) {}
	
	/* Uses the facebook token to get the MiTV token */
	public void getUserTokenUsingFBToken(ActivityCallbackListener activityCallBack, String facebookToken) {}
	
	/* Email is used as username  */
	public void performUserLogin(ActivityCallbackListener activityCallBack, String username, String password) {}
	
	public void performUserSignUp(ActivityCallbackListener activityCallBack, String email, String password, String firstname, String lastname) {}
	
	public void performUserPasswordReset(ActivityCallbackListener activityCallBack) {}
	
}
