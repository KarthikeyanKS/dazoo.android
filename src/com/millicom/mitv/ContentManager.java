package com.millicom.mitv;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.millicom.mitv.enums.FetchRequestResult;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.AppConfigurationData;
import com.millicom.mitv.models.AppVersionData;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVGuide;
import com.mitv.Consts;
import com.mitv.model.TVChannel;
import com.mitv.model.TVChannelGuide;
import com.mitv.model.TVDate;
import com.mitv.model.TVTag;

public class ContentManager implements ContentCallbackListener {

	private static ContentManager sharedInstance;
	private Storage storage;
	private APIClient apiClient;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV data
	 */
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;
	private int completedCountAppData = 0;
	private boolean channelsChange = false;

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESHOLD = 1;
	private static final int COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD = 4;
	private static final int COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD = 5;
	private int completedCountTVData = 0;

	public ContentManager() {
		this.storage = Storage.sharedInstance();
		this.apiClient = APIClient.sharedInstance();
	}

	public static ContentManager sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ContentManager();
		}
		return sharedInstance;
	}

	/* METHODS FOR FETCHING DATA FROM BACKEND USING THE API CLIENT */
	public void fetchAppData(ActivityCallbackListener activityCallBack) {
		apiClient.getAppConfiguration(activityCallBack);
		apiClient.getAppVersion(activityCallBack);
	}

	public void fetchTVDataOnFirstStart(ActivityCallbackListener activityCallBack) {
		apiClient.getTVTags(activityCallBack);
		apiClient.getTVDates(activityCallBack);
		apiClient.getTVChannelsAll(activityCallBack);
		apiClient.getDefaultTVChannelIds(activityCallBack);

		if (storage.isLoggedIn()) {
			apiClient.getUserTVChannelIds(activityCallBack);
		}
	}
	
	public void fetchTVDataOnUserStatusChange(ActivityCallbackListener activityCallBack) {
		channelsChange = true;
		apiClient.getUserTVChannelIds(activityCallBack);
	}
	
	public void fetchTVGuideForSelectedDay(ActivityCallbackListener activityCallBack) {
		TVDate tvDate = storage.getTvDateSelected();
		fetchTVGuide(activityCallBack, tvDate);
	}
			
	public void fetchTVGuide(ActivityCallbackListener activityCallBack, TVDate tvDate) {
		ArrayList<TVChannelId> tvChannelIds = storage.getTvChannelIdsUsed();
		apiClient.getTVChannelGuides(activityCallBack, tvDate, tvChannelIds);
	}
	
	/* METHODS FOR "GETTING" THE TV DATA, EITHER FROM STORAGE, OR FETCHING FROM BACKEND */
	public void getTVGuideUsingTVDate(ActivityCallbackListener activityCallBack, boolean forceDownload, TVDate tvDate) {
		if(!forceDownload && storage.containsTVGuideForTVDate(tvDate)) {
			activityCallBack.onResult(FetchRequestResult.SUCCESS);
		} else {
			
		}
	}

	@Override
	public void onResult(ActivityCallbackListener activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, int httpResponseCode,
			Object data) {
	
			switch (requestIdentifier) {
			case APP_CONFIGURATION:
			case APP_VERSION: {
				handleAppDataResponse(activityCallback, successful, requestIdentifier, data);
				break;
			}
			case TV_DATE:
			case TV_TAG:
			case TV_CHANNEL:
			case TV_CHANNEL_IDS_USER:
			case TV_CHANNEL_IDS_DEFAULT: {
				handleTVDataResponse(activityCallback, successful, requestIdentifier, data);
				break;
			}
			case TV_GUIDE: {
				handleTVChannelGuidesForSelectedDayResponse(activityCallback, successful, data);
				break;
			}
			case ADS: {
				break;
			}
			case USER_LOGIN: {
				handleLoginResponse(activityCallback, successful, data);
				break;
			}
			case USER_SIGN_UP: {
				handleSignUpResponse(activityCallback, successful, data);
				break;
			}
			case USER_LOGOUT: {
				handleLogoutResponse(activityCallback);
				break;
			}
			case USER_FB_TOKEN: {
				break;
			}
			case USER_LIKES: {
				break;
			}
			case USER_SET_CHANNELS: {
				handleSetChannelsResponse(activityCallback, successful);
				break;
			}
			case USER_ADD_LIKE: {
				break;
			}
			case USER_REMOVE_LIKE: {
				break;
			}
			case USER_RESET_PASSWORD: {
				break;
			}
			case USER_ACTIVITY_FEED_ITEM: {
				break;
			}
			case POPULAR_ITEMS: {
				break;
			}
			}
	}
	
	/* METHODS FOR HANDLING THE RESPONSES */
	private void handleAppDataResponse(ActivityCallbackListener activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, Object data) {
		if (successful && data != null) {
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
	
			if (completedCountAppData >= COMPLETED_COUNT_APP_DATA_THRESHOLD) {
				completedCountAppData = 0;
				String apiVersion = storage.getAppVersionData().getApiVersion();
	
				boolean apiTooOld = checkApiVersion(apiVersion);
				if (!apiTooOld) {
					/* App version not too old, continue fetching tv data */
					fetchTVDataOnFirstStart(activityCallback);
				} else {
					activityCallback.onResult(FetchRequestResult.API_VERSION_TOO_OLD);
				}
			}
		} else {
			//TODO handle this
		}
	}

	private void handleTVDataResponse(ActivityCallbackListener activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, Object data) {
		if (successful && data != null) {
			completedCountTVData++;
	
			switch (requestIdentifier) {
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
			}
	
			int completedCountTVDataThreshold;
			if(channelsChange) {
				channelsChange = false;
				completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_CHANNEL_CHANGE_THRESHOLD;
			} else {
				completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_NOT_LOGGED_IN_THRESHOLD;
				if(storage.isLoggedIn()) {
					completedCountTVDataThreshold = COMPLETED_COUNT_TV_DATA_LOGGED_IN_THRESHOLD;
				}
			}
			
			if (completedCountTVData >= completedCountTVDataThreshold) {
				completedCountTVData = 0;
				fetchTVGuideForSelectedDay(activityCallback);
			}
			
		} else {
			//TODO handle this 
		}
	}
	
	private void handleTVChannelGuidesForSelectedDayResponse(ActivityCallbackListener activityCallback, boolean successful, Object data) {
		if (successful && data != null) {
			ArrayList<TVChannelGuide> tvChannelGuides = (ArrayList<TVChannelGuide>) data;
			
			TVDate tvDate = storage.getTvDateSelected();
			TVGuide tvGuide = new TVGuide(tvDate, tvChannelGuides);
			
			storage.addTVGuide(tvDate, tvGuide);
			
			activityCallback.onResult(FetchRequestResult.SUCCESS);
		} else {
			//TODO handle this
		}
	}

	public void handleSignUpResponse(ActivityCallbackListener activityCallBack, boolean successful, Object data) {
		if (successful && data != null) {
			//TODO change to use SignUpCompleteData object instead??
			String userToken = (String) data;
			storage.setUserToken(userToken);
			
			fetchTVDataOnUserStatusChange(activityCallBack);
		} else {
			//TODO handle this
		}
	}
	
	public void handleLoginResponse(ActivityCallbackListener activityCallBack, boolean successful, Object data) {
		if (successful && data != null) {
			String userToken = (String) data;
			storage.setUserToken(userToken);
			
			fetchTVDataOnUserStatusChange(activityCallBack);
		} else {
			//TODO handle this
		}
	}
	
	public void handleLogoutResponse(ActivityCallbackListener activityCallBack) {
		channelsChange = true;
		
		storage.clearUserToken();
		storage.clearTVChannelIdsUser();
		storage.useDefaultChannelIds();
		
		fetchTVGuideForSelectedDay(activityCallBack);
	}
	
	public void handleSetChannelsResponse(ActivityCallbackListener activityCallBack, boolean successful) {
		if (successful) {
			fetchTVDataOnUserStatusChange(activityCallBack);
		} else {
			//TODO handle this
		}
	}
	
	
	/* USER METHODS REGUARDING SIGNUP, LOGIN AND LOGOUT */
	public void signUp(ActivityCallbackListener activityCallBack, String email, String password, String firstname, String lastname) {
		apiClient.performUserSignUp(activityCallBack, email, password, firstname, lastname);
	}
	
	public void login(ActivityCallbackListener activityCallBack, String username, String password) {
		apiClient.performUserLogin(activityCallBack, username, password);
	}
	
	public void logout(ActivityCallbackListener activityCallBack) {
		apiClient.performUserLogout(activityCallBack);
	}
	
	public void setUserChannels(ActivityCallbackListener activityCallBack, List<TVChannelId> tvChannelIds) {
		apiClient.setUserTVChannelIds(activityCallBack, tvChannelIds);
	}
	
	public ArrayList<TVChannelId> getTVChannelIdsUser() {
		ArrayList<TVChannelId> tvChannelIdsUser = storage.getTvChannelIdsUsed();
		return tvChannelIdsUser;
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
