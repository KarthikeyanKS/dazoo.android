package com.mitv;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders.AppViewBuilder;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.FileUtils;

public class GATrackingManager {
	private static final String TAG = GATrackingManager.class.getName();

	private static GATrackingManager instance;

	private Tracker tracker;
	private Context context;

	public GATrackingManager(final Context context) {
		this.context = context;

		updateConfiguration();
	}

	public static GATrackingManager sharedInstance() {
		if (instance == null) {
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

			instance = new GATrackingManager(context);
		}

		return instance;
	}

	public Tracker getTrackerInstance() {
		return tracker;
	}

	public static Tracker getTracker() {
		return sharedInstance().getTrackerInstance();
	}

	private GoogleAnalytics getGoogleAnalyticsInstance() {
		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
		
		return googleAnalyticsInstance;
	}

	public void updateConfiguration() {
		GoogleAnalytics googleAnalyticsInstance = getGoogleAnalyticsInstance();
		
		googleAnalyticsInstance.getLogger().setLogLevel(LogLevel.WARNING);
		
		this.tracker = googleAnalyticsInstance.newTracker(R.xml.analytics);

		boolean cacheHasAppConfiguration = ContentManager.sharedInstance().getFromCacheHasAppConfiguration();

		boolean forceDefaultGATrackingID = Constants.FORCE_DEFAULT_GOOGLE_TRACKING_ID;

		if (cacheHasAppConfiguration && !forceDefaultGATrackingID) {
			String trackingId = ContentManager.sharedInstance().getFromCacheAppConfiguration().getGoogleAnalyticsTrackingId();
			this.tracker.set("&tid", trackingId);
		}

		boolean preinstalledCheckingSharedPrefs = SecondScreenApplication.sharedInstance().isAppPreinstalled();

		File file = FileUtils.getFile(Constants.APP_WAS_PREINSTALLED_FILE_NAME);
		boolean preinstalledCheckingExternalStorage = FileUtils.fileExists(file);

		boolean preinstalledUsingSystemAppDetectionCheckLocation = SecondScreenApplication.isApplicationSystemApp();
		boolean preinstalledUsingSystemAppDetectionCheckFlag = SecondScreenApplication.isApplicationSystemAppUsingFlag();

		String wasPreinstalledSharedPrefs = preinstalledCheckingSharedPrefs ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = preinstalledCheckingExternalStorage ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledSystemAppLocation = preinstalledUsingSystemAppDetectionCheckLocation ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledSystemAppFlag = preinstalledUsingSystemAppDetectionCheckFlag ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;

		if (cacheHasAppConfiguration) {
			double sampleRateDecimal = ContentManager.sharedInstance().getFromCacheAppConfiguration().getGoogleAnalyticsSampleRate();

			double sampleRateAsPercentage = sampleRateDecimal * 100.0d;
			/* Set the SAMPLE RATE */
			tracker.setSampleRate(sampleRateAsPercentage);
		}

		/* Information regarding if the app was preinstalled or not */

		/* APP_WAS_PREINSTALLED_SHARED_PREFS is at index 1 */
		AppViewBuilder appViewBuilder = new AppViewBuilder();

		appViewBuilder.setCustomDimension(1, wasPreinstalledSharedPrefs);
		appViewBuilder.setCustomDimension(2, wasPreinstalledExternalStorage);
		appViewBuilder.setCustomDimension(3, wasPreinstalledSystemAppLocation);
		appViewBuilder.setCustomDimension(4, wasPreinstalledSystemAppFlag);

		Map<String, String> customDimensionsMap = appViewBuilder.build();

		tracker.send(customDimensionsMap);
	}

	public void sendUserSignedInEventAndSetUserIdOnTracker(String userId) {
		setUserIdOnTracker(userId);
		sendUserSignedInEvent();
	}

	public void setUserIdOnTrackerAndSendSignedOut() {
		// setUserIdOnTracker("");
		sendUserSignedOutEvent();
	}

	private void sendUserSignedInEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_IN);
	}

	private void sendUserSignedOutEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_OUT);
	}

	public void setUserIdOnTracker(String userId) {
		tracker.set(Constants.GA_FIELD_USER_ID, userId);
	}

	public void sendUserSignUpSuccessfulUsingEmailEvent() {
		sendUserSignUpSuccessfulEvent(false);
	}

	public void sendUserSignUpSuccessfulUsingFacebookEvent() {
		sendUserSignUpSuccessfulEvent(true);
	}

	public void sendUserSignUpSuccessfulEvent(boolean facebook) {
		String userId = ContentManager.sharedInstance().getFromCacheUserId();

		String actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_EMAIL;
		if (facebook) {
			actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_FACEBOOK;
		}

		sendUserEventWithLabel(actionString, userId);
	}

	private String actionByAppendingActivityName(String actionBase, Activity activity) {
		String action = actionBase;
		if (actionBase != null && activity != null) {
			String activityName = activity.getClass().getSimpleName();
			StringBuilder sb = new StringBuilder(Constants.GA_EVENT_KEY_USER_EVENT_USER_SHARE);
			sb.append("_").append(activityName);
			action = sb.toString();
		}
		return action;
	}

	public void sendUserSharedEvent(Activity activity, TVBroadcast broadcast) {
		String action = actionByAppendingActivityName(Constants.GA_EVENT_KEY_USER_EVENT_USER_SHARE, activity);
		String broadcastTitle = broadcast.getTitle();
		sendUserEventWithLabel(action, broadcastTitle);
	}

	public void sendUserLikesEvent(Activity activity, UserLike userLike, boolean didJustUnlike) {
		String action = actionByAppendingActivityName(Constants.GA_EVENT_KEY_USER_EVENT_USER_LIKE, activity);
		String broadcastTitle = userLike.getTitle();

		Long addedLike = 1L;
		if (didJustUnlike) {
			addedLike = 0L;
		}

		sendUserEventWithLabelAndValue(action, broadcastTitle, addedLike);

	}

	public void sendUserReminderEvent(Activity activity, TVBroadcast broadcast, boolean didJustRemoveReminder) {
		String action = actionByAppendingActivityName(Constants.GA_EVENT_KEY_USER_EVENT_USER_REMINDER, activity);
		String broadcastTitle = broadcast.getTitle();

		Long addedReminder = 1L;
		if (didJustRemoveReminder) {
			addedReminder = 0L;
		}

		sendUserEventWithLabelAndValue(action, broadcastTitle, addedReminder);
	}

	public void sendTimeOffSyncEvent() {
		sendSystemEvent(Constants.GA_EVENT_KEY_SYSTEM_EVENT_DEVICE_TIME_UNSYNCED);
	}

	public void sendFirstBootEvent() {
		sendSystemEventWithLabel(Constants.GA_EVENT_KEY_ACTION_FIRST_BOOT, Constants.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT);
	}

	public void sendUserTagSelectionEvent(int tagPosition) {
		List<TVTag> tvTags = ContentManager.sharedInstance().getFromCacheTVTags();
		if (tvTags != null && !tvTags.isEmpty() && tagPosition < tvTags.size()) {
			TVTag tvTag = tvTags.get(tagPosition);
			if (tvTag != null) {
				String selectedTag = tvTag.getId();
				sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_TAG_SELECTED, selectedTag);
			}
		}
	}

	public void sendUserHourSelectionEvent() {
		Integer selectedHour = ContentManager.sharedInstance().getFromCacheSelectedHour();
		if (selectedHour != null) {
			sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_HOUR_SELECTED, selectedHour.toString());
		}
	}

	public void sendUserDaySelectionEvent(Activity activity, int dayIndex) {
		String action = actionByAppendingActivityName(Constants.GA_EVENT_KEY_USER_EVENT_DAY_SELECTED, activity);

		List<TVDate> dates = ContentManager.sharedInstance().getFromCacheTVDates();
		if (dates != null && !dates.isEmpty() && dayIndex < dates.size()) {
			TVDate tvDate = dates.get(dayIndex);

			Calendar calendar = tvDate.getStartOfTVDayCalendar();
			String displayName = tvDate.getDisplayName();
			String dayMonth = DateUtils.buildDayAndMonthCompositionAsString(calendar, false);

			StringBuilder sb = new StringBuilder(displayName);
			sb.append(" ");
			sb.append(dayMonth);
			String dateString = sb.toString();
			sendUserEventWithLabelAndValue(action, dateString, (long) dayIndex);
		}
	}

	/* Methods for sending GA Events */
	private void sendSystemEvent(String action) {
		sendEventBase(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, action, false, null, false, 0);
	}

	private void sendSystemEventWithLabel(String action, String label) {
		sendEventWithLabel(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, action, label);
	}

	private void sendUserEvent(String action) {
		sendEventBase(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, action, false, null, false, 0);
	}

	private void sendUserEventWithLabel(String action, String label) {
		sendEventWithLabel(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, action, label);
	}

	private void sendUserEventWithLabelAndValue(String action, String label, long value) {
		sendEventWithLabelAndValue(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, action, label, value);
	}

	private void sendEventWithLabel(String category, String action, String label) {
		sendEventBase(category, action, true, label, false, 0);
	}

	private void sendEventWithLabelAndValue(String category, String action, String label, long value) {
		sendEventBase(category, action, true, label, true, value);
	}

	private void sendEventBase(String category, String action, boolean setLabel, String label, boolean setValue, long value) {
		EventBuilder eventBuilder = new EventBuilder();
		eventBuilder.setCategory(category).setAction(action);

		if (setLabel) {
			eventBuilder.setLabel(label);
		}

		if (setValue) {
			eventBuilder.setValue(value);
		}
		tracker.send(eventBuilder.build());
	}

	/* Methods for sending screens using autoActivityTracking */
	public static void reportActivityStart(Activity activity) {
		GoogleAnalytics googleAnalyticsInstance = sharedInstance().getGoogleAnalyticsInstance();
		googleAnalyticsInstance.reportActivityStart(activity);
	}

	public static void reportActivityStop(Activity activity) {
		GoogleAnalytics googleAnalyticsInstance = sharedInstance().getGoogleAnalyticsInstance();
		googleAnalyticsInstance.reportActivityStop(activity);
	}
	
	/**
	 * Manually sending the campaign (as a system event) information to analytics.
	 * 
	 * The referrer we get looks like:
	 * utm_source=xxx&utm_medium=xxx&utm_term=xxx&utm_content=xxx&utm_campaign=xxx
	 * 
	 */
	public void sendGooglePlayCampaignToAnalytics(String campaignData) {
		String[] parts = null;
		
		String campaignSource = null;
		String campaignMedium = null; 
		String campaignTerm = null;
		String campaignContent = null;
		String campaignName = null;	
		
		if (campaignData.contains("&")) {
			parts = campaignData.split("&");
			
			for (int i = 0; i < parts.length; i++) {
				parts[i] = parts[i].replaceAll("utm_", "");
				
				if (parts[i].startsWith("source")) {
					campaignSource = parts[i];

				} else if (parts[i].startsWith("medium")) {
					campaignMedium = parts[i];

				} else if (parts[i].startsWith("term")) {
					campaignTerm = parts[i];

				} else if (parts[i].startsWith("content")) {
					campaignContent = parts[i];

				} else if (parts[i].startsWith("campaign")) {
					campaignName = parts[i];
				}
			}
			
			/* Build an event to send to Analytics */
			String category = Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT;
			String action = "Campaign";
			
			StringBuilder sb = new StringBuilder();
			sb.append(campaignSource);
			sb.append(" ");
			sb.append(campaignMedium);
			sb.append(" ");
			sb.append(campaignTerm);
			sb.append(" ");
			sb.append(campaignContent);
			sb.append(" ");
			sb.append(campaignName);

			String label = sb.toString();
			
			sendEventWithLabel(category, action, label);			

		} else {
			throw new IllegalArgumentException("String in sendGooglePlayCampaignToAnalytics: " + campaignData + " does not contain &");
			
		}
	}
	
}