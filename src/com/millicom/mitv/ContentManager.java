package com.millicom.mitv;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.gson.AppConfigurationData;
import com.millicom.mitv.models.gson.AppVersionData;
import com.millicom.mitv.models.gson.TVBroadcastWithProgramAndChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;

public class ContentManager implements ContentCallbackListener {

	private static ContentManager sharedInstance;
	private Storage storage;
	private APIClient apiClient;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESH = 2;
	private int completedCountAppData = 0;
	private boolean channelsChange = false;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESH = 1;
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESH = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESH = 5;
	private int completedCountTVData = 0;

	private ContentManager() {
		this.storage = new Storage();
		this.apiClient = new APIClient(this);
	}

	public static ContentManager sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ContentManager();
		}
		return sharedInstance;
	}

	/* METHODS FOR FETCHING DATA FROM BACKEND USING THE API CLIENT */
	public void fetchAppData(ActivityCallbackListener activityCallBackListener) {
		apiClient.getAppConfiguration(activityCallBackListener);
		apiClient.getAppVersion(activityCallBackListener);
	}

	public void fetchTVDataOnFirstStart(ActivityCallbackListener activityCallBackListener) {
		apiClient.getTVTags(activityCallBackListener);
		apiClient.getTVDates(activityCallBackListener);
		apiClient.getTVChannelsAll(activityCallBackListener);
		apiClient.getDefaultTVChannelIds(activityCallBackListener);

		if (storage.isLoggedIn()) {
			apiClient.getUserTVChannelIds(activityCallBackListener);
		}
	}
	
	public void fetchTVDataOnUserStatusChange(ActivityCallbackListener activityCallBackListener) {
		channelsChange = true;
		apiClient.getUserTVChannelIds(activityCallBackListener);
	}
	
	public void fetchTVGuideForSelectedDay(ActivityCallbackListener activityCallBackListener) {
		TVDate tvDate = storage.getTvDateSelected();
		fetchTVGuideUsingTVDate(activityCallBackListener, tvDate);
	}
			
	public void fetchTVGuideUsingTVDate(ActivityCallbackListener activityCallBackListener, TVDate tvDate) {
		ArrayList<TVChannelId> tvChannelIds = storage.getTvChannelIdsUsed();
		apiClient.getTVChannelGuides(activityCallBackListener, tvDate, tvChannelIds);
	}
	
	public void fetchTVBroadcastsPopular(ActivityCallbackListener activityCallbackListener) {
		apiClient.getTVBroadcastsPopular(activityCallbackListener);
	}
	
	/* METHODS FOR "GETTING" THE TV DATA, EITHER FROM STORAGE, OR FETCHING FROM BACKEND */
	public void getTVGuideUsingTVDate(ActivityCallbackListener activityCallBackListener, boolean forceDownload, TVDate tvDate) {
		if(!forceDownload && storage.containsTVGuideForTVDate(tvDate)) {
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS);
		} else {
			fetchTVGuideUsingTVDate(activityCallBackListener, tvDate);
		}
	}

	@Override
	public void onResult(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) 
	{
			switch (requestIdentifier)
			{
				case APP_CONFIGURATION:
				case APP_VERSION: {
					handleAppDataResponse(activityCallBackListener, result, requestIdentifier, content);
					break;
				}
				case TV_DATE:
				case TV_TAG:
				case TV_CHANNEL:
				case TV_CHANNEL_IDS_USER:
				case TV_CHANNEL_IDS_DEFAULT: {
					handleTVDataResponse(activityCallBackListener, result, requestIdentifier, content);
					break;
				}
				case TV_GUIDE: {
					handleTVChannelGuidesForSelectedDayResponse(activityCallBackListener, result, content);
					break;
				}
				case ADS: {
					break;
				}
				case USER_LOGIN: {
					handleLoginResponse(activityCallBackListener, result, content);
					break;
				}
				case USER_SIGN_UP: {
					handleSignUpResponse(activityCallBackListener, result, content);
					break;
				}
				case USER_LOGOUT: {
					handleLogoutResponse(activityCallBackListener);
					break;
				}
				case USER_FB_TOKEN: {
					break;
				}
				case USER_LIKES: {
					break;
				}
				case USER_SET_CHANNELS: {
					handleSetChannelsResponse(activityCallBackListener, result);
					break;
				}
				case USER_ADD_LIKE: {
					break;
				}
				case USER_REMOVE_LIKE:
				{
					// TODO
					break;
				}
				case USER_RESET_PASSWORD_SEND_EMAIL: 
				{
					// TODO
					break;
				}
				case USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD: 
				{
					// TODO
					break;
				}
				case USER_ACTIVITY_FEED_ITEM:
				{
					// TODO
					break;
				}
				case POPULAR_ITEMS: {
					handleTVBroadcastsPopularResponse(activityCallBackListener, result, content);
					break;
				}
				case BROADCAST_DETAILS :
				{
					// TODO
					break;
				}
				case BROADCASTS_FROM_PROGRAMS :
				{
					// TODO
					break;
				}
				case BROADCASTS_FROM_SERIES_UPCOMING :
				{
					// TODO
					break;
				}
			}
	}
	
	/* METHODS FOR HANDLING THE RESPONSES */
	private void handleAppDataResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, RequestIdentifierEnum requestIdentifier, Object data) {
		if (result.wasSuccessful() && data != null) {
			completedCountAppData++;
			
			switch (requestIdentifier) {
			case APP_CONFIGURATION:
				AppConfigurationData appConfigData = (AppConfigurationData) data;
				// TODO decide if use Storage class here or not (if we should put
				// the app config data in the storage class or not)
				storage.setAppConfigData(appConfigData);
				break;
	
			case APP_VERSION: {
				AppVersionData appVersionData = (AppVersionData) data;
				storage.setAppVersionData(appVersionData);
				break;
			}
			}
	
			if (completedCountAppData >= COMPLETED_COUNT_APP_DATA_THRESH) {
				completedCountAppData = 0;
				String apiVersion = storage.getAppVersionData().getApiVersion();
	
				boolean apiToo = checkApiVersion(apiVersion);
				if (!apiToo) {
					/* App version not too , continue fetching tv data */
					fetchTVDataOnFirstStart(activityCallBackListener);
				} else {
					activityCallBackListener.onResult(FetchRequestResultEnum.API_VERSION_TOO_OLD);
				}
			}
		} else {
			//TODO handle this
		}
	}

	private void handleTVDataResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, RequestIdentifierEnum requestIdentifier, Object data) {
		if (result.wasSuccessful() && data != null) {
			completedCountTVData++;
	
			switch (requestIdentifier) 
			{
				case TV_DATE: {
					ArrayList<TVDate> tvDates = (ArrayList<TVDate>) data;
					storage.setTvDates(tvDates);
					
					/* We will only get here ONCE, at the start of the app, no TVDate has been selected, set it! */
					if(!tvDates.isEmpty()) {
						TVDate tvDate = tvDates.get(0);
						storage.setTvDateSelected(tvDate);
					} else {
						//TODO handle this...?
					}
					break;
				}
				case TV_TAG: {
					ArrayList<TVTag> tvTags = (ArrayList<TVTag>) data;
					storage.setTvTags(tvTags);
					break;
				}
				case TV_CHANNEL: {
					ArrayList<TVChannel> tvChannels = (ArrayList<TVChannel>) data;
					storage.setTvChannels(tvChannels);
					break;
				}
				case TV_CHANNEL_IDS_DEFAULT: {
					ArrayList<TVChannelId> tvChannelIdsDefault = (ArrayList<TVChannelId>) data;
					storage.setTvChannelIdsDefault(tvChannelIdsDefault);
					break;
				}
				case TV_CHANNEL_IDS_USER: {
					ArrayList<TVChannelId> tvChannelIdsUser = (ArrayList<TVChannelId>) data;
					storage.setTvChannelIdsUser(tvChannelIdsUser);
					break;
				}
				case BROADCAST_DETAILS :
				{
					// TODO
					break;
				}
				case BROADCASTS_FROM_PROGRAMS :
				{
					// TODO
					break;
				}
				case BROADCASTS_FROM_SERIES_UPCOMING :
				{
					// TODO
					break;
				}
			}
	
			int completedCountTVDataThresh;
			if(channelsChange) {
				channelsChange = false;
				completedCountTVDataThresh = COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESH;
			} else {
				completedCountTVDataThresh = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESH;
				if(storage.isLoggedIn()) {
					completedCountTVDataThresh = COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESH;
				}
			}
			
			if (completedCountTVData >= completedCountTVDataThresh) {
				completedCountTVData = 0;
				fetchTVGuideForSelectedDay(activityCallBackListener);
			}
			
		} else {
			//TODO handle this 
		}
	}
	
	private void handleTVChannelGuidesForSelectedDayResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, Object data) {
		if (result.wasSuccessful() && data != null) {
			ArrayList<TVChannelGuide> tvChannelGuides = (ArrayList<TVChannelGuide>) data;
			
			TVDate tvDate = storage.getTvDateSelected();
			TVGuide tvGuide = new TVGuide(tvDate, tvChannelGuides);
			
			storage.addTVGuide(tvDate, tvGuide);
			
			activityCallBackListener.onResult(FetchRequestResultEnum.SUCCESS);
		} else {
			//TODO handle this
		}
	}
	
	public void handleTVBroadcastsPopularResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, Object data) {
		if (result.wasSuccessful() && data != null) {
			ArrayList<TVBroadcastWithProgramAndChannelInfo> broadcastsPopular = (ArrayList<TVBroadcastWithProgramAndChannelInfo>) data;
			storage.setPopularFeed(broadcastsPopular);
		} else {
			//TODO handle this
		}
	}

	public void handleSignUpResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, Object data) {
		if (result.wasSuccessful() && data != null) {
			//TODO change to use SignUpCompleteData object instead??
			String userToken = (String) data;
			storage.setUserToken(userToken);
			
			fetchTVDataOnUserStatusChange(activityCallBackListener);
		} else {
			//TODO handle this
		}
	}
		
	public void handleLoginResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result, Object data) {
		if (result.wasSuccessful() && data != null) {
			String userToken = (String) data;
			storage.setUserToken(userToken);
			
			fetchTVDataOnUserStatusChange(activityCallBackListener);
		} else {
			//TODO handle this
		}
	}
	
	public void handleLogoutResponse(ActivityCallbackListener activityCallBackListener) {
		channelsChange = true;
		
		storage.clearUserToken();
		storage.clearTVChannelIdsUser();
		storage.useDefaultChannelIds();
		
		fetchTVGuideForSelectedDay(activityCallBackListener);
	}
	
	public void handleSetChannelsResponse(ActivityCallbackListener activityCallBackListener, FetchRequestResultEnum result) {
		//TODO use switch case instead???
		if (result.wasSuccessful()) {
			fetchTVDataOnUserStatusChange(activityCallBackListener);
		} else {
			//TODO handle this
		}
	}
	
	
	/* USER METHODS REGUARDING SIGNUP, LOGIN AND LOGOUT */
	public void signUp(ActivityCallbackListener activityCallBackListener, String email, String password, String firstname, String lastname) {
		apiClient.performUserSignUp(activityCallBackListener, email, password, firstname, lastname);
	}
	
	public void login(ActivityCallbackListener activityCallBackListener, String username, String password) {
		apiClient.performUserLogin(activityCallBackListener, username, password);
	}
	
	public void logout(ActivityCallbackListener activityCallBackListener) {
		apiClient.performUserLogout(activityCallBackListener);
	}
	
	public void setUserChannels(ActivityCallbackListener activityCallBackListener, List<TVChannelId> tvChannelIds) {
		apiClient.setUserTVChannelIds(activityCallBackListener, tvChannelIds);
	}
	
	public ArrayList<TVChannelId> getTVChannelIdsUser() {
		ArrayList<TVChannelId> tvChannelIdsUser = storage.getTvChannelIdsUsed();
		return tvChannelIdsUser;
	}
	
	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ActivityCallbackListener activityCallBackListener, int tvDateIndex) {
		/* Update the index in the storage */
		setTVDateSelectedUsingIndex(tvDateIndex);
		
		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = storage.getTvDateSelected();
		
		/* Since selected TVDate has been changed, set/fetch the TVGuide for that day */
		getTVGuideUsingTVDate(activityCallBackListener, false, tvDate);
	}
	
	/* GETTERS & SETTERS */
	/* TVDate getters and setters */
	public TVDate getTVDateSelected() {
		TVDate tvDateSelected = storage.getTvDateSelected();
		return tvDateSelected;
	}
	
	private void setTVDateSelectedUsingIndex(int tvDateIndex) {
		/* Update the index in the storage */
		storage.setTvDateSelectedUsingIndex(tvDateIndex);
	}
	
	/* TVTags getters (and setters?) */
	public ArrayList<TVTag> getTVTags() {
		ArrayList<TVTag> tvTags = storage.getTvTags();
		return tvTags;
	}
	
	/* UserToken getter */
	/**
	 * This method should only be used by the AsyncTaskWithUserToken class. A part from that class
	 * no other class should ever access or modify the user token directly. All login/logout/sign up
	 * logic should be handled by this class.
	 * @return
	 */
	public String getUserToken() {
		String userToken = storage.getUserToken();
		return userToken;
	}
	
	public boolean isLoggedIn() {
		boolean isLoggedIn = storage.isLoggedIn();
		return isLoggedIn;
	}
	
	/* HELPER METHODS */
	public boolean checkApiVersion(String apiVersion) {
		if (apiVersion != null && !TextUtils.isEmpty(apiVersion) && !apiVersion.equals(Consts.API_VERSION)) {
			return true;
		} else {
			return false;
		}
	}
}
