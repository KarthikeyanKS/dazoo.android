package com.millicom.mitv;

import java.util.List;

import com.millicom.mitv.models.TVChannelId;

public class APIClient {
	
	public void getAppConfiguration() {}
	
	public void getAppVersion() {}
	
	public void getAds() {}
	
	public void getTVTags() {}
	
	public void getTVDates() {}
	
	public void getTVChannelsAll() {}
	
	public void getTVChannelsDefault() {}
	
	public void getUserTVChannelIds() {}
	
	public void setUserTVChannelIds(List<TVChannelId> tvChannelIds) {}
	
	public void getFeedItems() {}
	
	public void getRepetionsForBroadcast() {}
	
	public void getUserLikes() {}
	
	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void addUserLike(String contentId) {}

	/* The content ID is either a seriesId, or a sportTypesId or programId */
	public void removeUserLike(String contentId) {}
	
	public void getChannelGuides() {}
	
	/* Uses the facebook token to get the MiTV token */
	public void getUserTokenUsingFBToken(String facebookToken) {}
	
	/* Email is used as username  */
	public void performUserLogin(String username, String password) {}
	
	public void performUserSignUp(String email, String password, String firstname, String lastname) {}
	
	public void performUserPasswordReset() {}
	
}
