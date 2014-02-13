package com.millicom.mitv;

import android.text.TextUtils;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallback;
import com.millicom.mitv.interfaces.ContentCallback;
import com.millicom.mitv.models.AppConfigurationData;
import com.millicom.mitv.models.AppVersionData;
import com.mitv.Consts;

public class ContentManager implements ContentCallback {
	
	private static ContentManager sharedInstance;
	private Storage storage;
	private APIClient apiClient;
	
	private static final int COMPLETED_COUNT_APP_DATA_THRESHOLD = 2;
	private int completedCountAppData = 0;
	
	private static final int COMPLETED_COUNT_TV_DATA_THRESHOLD = 4;
	private int completedCountTVData = 0;
	
	
	public ContentManager() {
		this.storage = Storage.sharedInstance();
		this.apiClient = APIClient.sharedInstance();
	}
	
	public static ContentManager sharedInstance() {
		if(sharedInstance == null) {
			sharedInstance = new ContentManager();
		}
		return sharedInstance;
	}
	
	public void fetchAppData(ActivityCallback activityCallBack) {
		apiClient.getAppConfiguration(activityCallBack);
		apiClient.getAppVersion(activityCallBack);
	}
		
	private void handleAppDataResponse(ActivityCallback activityCallback, RequestIdentifierEnum requestIdentifier, Object data) {
		completedCountAppData++;;
		switch (requestIdentifier) {
			case APP_CONFIGURATION:
				AppConfigurationData appConfigData = (AppConfigurationData) data;
				//TODO decide if use Storage class here or not (if we should put the app config data in the storage class or not)
				storage.setAppConfigData(appConfigData);
				break;
	
			case APP_VERSION: {
				AppVersionData appVersionData = (AppVersionData) data;
				storage.setAppVersionData(appVersionData);
				break;
			}
		}
		
		
		if(completedCountAppData >= COMPLETED_COUNT_APP_DATA_THRESHOLD) {
			String apiVersion = storage.getAppVersionData().getVersion();
		
			boolean apiTooOld = checkApiVersion(apiVersion);
			if(!apiTooOld) {
				//TODO fetch rest of data
			} else {
				//TODO SplashScreenActivity is responsible for showing force update dialog 
				activityCallback.onResult(false, null);
			}
		}
		
	}

	@Override
	public void onResult(ActivityCallback activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, int httpResponseCode, Object data) {
		if (successful && data != null) {
			switch (requestIdentifier) {
				case TV_DATE: {
					break;
				}
				case TV_TAG: {
					break;
				}
				case TV_CHANNEL: {
					break;
				}
				case TV_GUIDE: {
					break;
				}
				case APP_CONFIGURATION:
				case APP_VERSION: {
					handleAppDataResponse(activityCallback, requestIdentifier, data);
					break;
				}
				case ADS: {
					break;
				}
				case USER_LOGIN: {
					break;
				}
				case USER_SIGN_UP: {
					break;
				}
				case USER_FB_TOKEN: {
					break;
				}
				case USER_LIKES: {
					break;
				}
				case USER_GET_CHANNELS: {
					break;
				}
				case USER_SET_CHANNELS: {
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
		} else {

		}
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
