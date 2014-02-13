package com.millicom.mitv;

import java.util.ArrayList;

import android.text.TextUtils;

import com.millicom.mitv.enums.FetchRequestResult;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.AppConfigurationData;
import com.millicom.mitv.models.AppVersionData;
import com.millicom.mitv.models.TVChannelId;
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

	/*
	 * The total completed data fetch count needed in order to proceed with
	 * fetching the TV guide, using TV data
	 */
	private static final int COMPLETED_COUNT_TV_DATA_THRESHOLD = 4;
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

	public void fetchAppData(ActivityCallbackListener activityCallBack) {
		apiClient.getAppConfiguration(activityCallBack);
		apiClient.getAppVersion(activityCallBack);
	}

	public void fetchTVData(ActivityCallbackListener activityCallBack) {
		apiClient.getTVTags(activityCallBack);
		apiClient.getTVDates(activityCallBack);
		apiClient.getTVChannelsAll(activityCallBack);

		if (storage.isLoggedIn()) {
			apiClient.getUserTVChannelIds(activityCallBack);
		} else {
			apiClient.getDefaultTVChannelIds(activityCallBack);
		}
	}
		
	public void fetchTVGuide(ActivityCallbackListener activityCallBack) {
		ArrayList<TVDate> tvDates = storage.getTvDates();
		int dateIndex = storage.getDateSelectedIndex();
		TVDate tvDate = tvDates.get(dateIndex);
		ArrayList<TVChannelId> tvChannelIds = storage.getTvChannelIds();
		apiClient.getTVChannelGuides(activityCallBack, tvDate, tvChannelIds);
	}

	private void handleAppDataResponse(ActivityCallbackListener activityCallback, RequestIdentifierEnum requestIdentifier, Object data) {
		completedCountAppData++;
		;
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
				fetchTVData(activityCallback);
			} else {
				activityCallback.onResult(FetchRequestResult.API_VERSION_TOO_OLD);
			}
		}

	}

	private void handleTVDataResponse(ActivityCallbackListener activityCallback, RequestIdentifierEnum requestIdentifier, Object data) {
		completedCountTVData++;

		switch (requestIdentifier) {
		case TV_DATE: {
			ArrayList<TVDate> tvDates = (ArrayList<TVDate>) data;
			storage.setTvDates(tvDates);
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
		case TV_CHANNEL_IDS_DEFAULT:
		case TV_CHANNEL_IDS_USER: {
			ArrayList<TVChannelId> tvChannelIds = (ArrayList<TVChannelId>) data;
			storage.setTvChannelIds(tvChannelIds);
			break;
		}
		}

		if (completedCountTVData >= COMPLETED_COUNT_TV_DATA_THRESHOLD) {
			completedCountTVData = 0;
			fetchTVGuide(activityCallback);
		}
	}
	
	private void handleTVChannelGuidesResponse(ActivityCallbackListener activityCallback, Object data) {
		ArrayList<TVChannelGuide> tvChannelGuides = (ArrayList<TVChannelGuide>) data;
		storage.setTVChannelGuides(tvChannelGuides);
		
		activityCallback.onResult(FetchRequestResult.SUCCESS);
	}

	@Override
	public void onResult(ActivityCallbackListener activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, int httpResponseCode,
			Object data) {
		if (successful && data != null) {
			switch (requestIdentifier) {
			case APP_CONFIGURATION:
			case APP_VERSION: {
				handleAppDataResponse(activityCallback, requestIdentifier, data);
				break;
			}
			case TV_DATE:
			case TV_TAG:
			case TV_CHANNEL:
			case TV_CHANNEL_IDS_USER:
			case TV_CHANNEL_IDS_DEFAULT: {
				handleTVDataResponse(activityCallback, requestIdentifier, data);
				break;
			}
			case TV_GUIDE: {
				handleTVChannelGuidesResponse(activityCallback, data);
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
			//TODO handle this
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
