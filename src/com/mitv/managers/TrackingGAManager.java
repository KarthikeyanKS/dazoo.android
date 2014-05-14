package com.mitv.managers;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders.AppViewBuilder;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.ui.elements.SwipeClockBar;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.FileUtils;

public class TrackingGAManager 
{
	private static final String TAG = TrackingGAManager.class.getName();

	private static TrackingGAManager instance;

	private Tracker tracker;
	private Context context;

	
	
	public TrackingGAManager(final Context context) 
	{
		this.context = context;

		updateConfiguration();
	}

	
	
	public static TrackingGAManager sharedInstance() 
	{
		if (instance == null) 
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

			instance = new TrackingGAManager(context);
		}

		return instance;
	}

	
	
	public Tracker getTrackerInstance() 
	{
		return tracker;
	}

	
	
	public static Tracker getTracker() 
	{
		return sharedInstance().getTrackerInstance();
	}

	
	
	private GoogleAnalytics getGoogleAnalyticsInstance() 
	{
		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);

		return googleAnalyticsInstance;
	}

	
	
	public void updateConfiguration() 
	{
		GoogleAnalytics googleAnalyticsInstance = getGoogleAnalyticsInstance();

		googleAnalyticsInstance.getLogger().setLogLevel(LogLevel.WARNING);

		this.tracker = googleAnalyticsInstance.newTracker(R.xml.analytics);

		boolean cacheHasAppConfiguration = ContentManager.sharedInstance().getFromCacheHasAppConfiguration();

		boolean forceDefaultGATrackingID = Constants.FORCE_DEFAULT_GOOGLE_TRACKING_ID;

		if (cacheHasAppConfiguration && !forceDefaultGATrackingID) 
		{
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

	
	public void sendUserSignUpSuccessfulEvent(boolean facebook) 
	{
		String userId = ContentManager.sharedInstance().getFromCacheUserId();

		String actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_EMAIL;
		if (facebook) {
			actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_FACEBOOK;
		}

		sendUserEventWithLabel(actionString, userId);
	}
	
	
	
	public void sendHTTPCoreOutOfMemoryException() 
	{
		sendSystemEvent(Constants.GA_EVENT_KEY_HTTP_CORE_OUT_OF_MEMORY_EXCEPTION);
	}
	
	public void sendUserSharedEvent(TVBroadcast broadcast) {
		String broadcastTitle = broadcast.getTitle();
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_USER_SHARE, broadcastTitle);
	}

	public void sendUserLikesEvent(UserLike userLike, boolean didJustUnlike) {
		String broadcastTitle = userLike.getTitle();

		Long addedLike = 1L;
		if (didJustUnlike) {
			addedLike = 0L;
		}

		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_USER_LIKE, broadcastTitle, addedLike);

	}

	public void sendUserReminderEvent(TVBroadcast broadcast, boolean didJustRemoveReminder) {
		String broadcastTitle = broadcast.getTitle();

		Long addedReminder = 1L;
		if (didJustRemoveReminder) {
			addedReminder = 0L;
		}

		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_USER_REMINDER, broadcastTitle, addedReminder);
	}
	
	public void sendUserReminderEventCompetition(Event event, boolean didJustRemoveReminder) {
		StringBuilder sb = new StringBuilder();
		sb.append(event.getHomeTeam())
			.append(" - ")
			.append(event.getAwayTeam());
		
		String title = sb.toString();

		Long addedReminder = 1L;
		if (didJustRemoveReminder) {
			addedReminder = 0L;
		}

		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_USER_REMINDER, title, addedReminder);
	}

	public void sendTimeOffSyncEvent() {
		sendSystemEvent(Constants.GA_EVENT_KEY_SYSTEM_EVENT_DEVICE_TIME_UNSYNCED);
	}

	public void sendFirstBootEvent() {
		sendSystemEventWithLabel(Constants.GA_EVENT_KEY_ACTION_FIRST_BOOT, Constants.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT);
	}

	public void sendUserTagSelectionEvent(int tagPosition) 
	{
		List<TVTag> tvTags = ContentManager.sharedInstance().getFromCacheTVTags();
		
		if (tvTags != null && !tvTags.isEmpty() && tagPosition < tvTags.size()) 
		{
			TVTag tvTag = tvTags.get(tagPosition);
			
			if (tvTag != null) 
			{
				String selectedTag = tvTag.getId();
			
				List<Competition> competitions = ContentManager.sharedInstance().getFromCacheVisibleCompetitions();
				
				Competition competition = tvTag.getMatchingCompetition(competitions);
				
				if(competition == null)
				{
					sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_TAG_SELECTED, selectedTag);
				}
				else
				{
					sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_TAG_SELECTED, selectedTag, 0);
				}
			}
		}
	}

	public void sendUserHourSelectionEvent(int lastSelectedHour) {
		Integer selectedHour = ContentManager.sharedInstance().getFromCacheSelectedHour();
		Log.d(TAG, String.format("Last hour: %d, new hour: %d", lastSelectedHour, selectedHour));
		if (selectedHour != null) {
			List<Integer> hours = SwipeClockBar.generate24Hours();
			Integer lastSelectedHourAsInteger = Integer.valueOf(lastSelectedHour);
			int indexOfSelectedHour = hours.indexOf(selectedHour);
			int indexOfLastSelectedHour = hours.indexOf(lastSelectedHourAsInteger);
		
			int timeDiff = (indexOfSelectedHour - indexOfLastSelectedHour); 
						
			String hourString = DateUtils.getHourAndMinuteAsStringUsingHour(selectedHour);
			StringBuilder sb = new StringBuilder("SELECTED HOUR: ");
			sb.append(hourString);
			String label = sb.toString();
			
			sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_HOUR_SELECTED, label, (long)timeDiff);
		}
	}
	
	public void sendUserPressedChannelInHomeActivity(TVChannelId channelId, int position) {
		TVChannel channel = ContentManager.sharedInstance().getFromCacheTVChannelById(channelId);
		String channelName = channel.getName();
		
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_CHANNEL_IN_HOME_ACTIVITY_PRESS, channelName, (long) position);
	}
	
	public void sendUserPressedBroadcastInChannelActivity(TVChannel channel, TVBroadcast broadcast, int position) {
		if(position == 0) {
			/* The top most cell in the channel page activity is clickable, but it is not a cell */
			return;
		} else {
			/* Subtract 1 from position value since this list does not start at index 0. */
			position--;
		}
		
		String channelName = channel.getName();
		String broadcastTitle = broadcast.getTitle();
		String startTime = broadcast.getBeginTimeHourAndMinuteLocalAsString();
		
		StringBuilder sb = new StringBuilder(channelName);
		sb.append(" - ");
		sb.append(startTime);
		sb.append(": ");
		sb.append(broadcastTitle);
		String label = sb.toString();
		
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_BROADCAST_IN_CHANNEL_ACTIVITY_PRESS, label, (long) position);
	}

	public void sendUserDaySelectionEvent(int dayIndex) {

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
			sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_DAY_SELECTED, dateString, (long) dayIndex);
		}
	}
	
	

	public void sendUserPressedRateInRateDialogEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_RATE_DIALOG_RATE_BUTTON_PRESS);
	}
	
	public void sendUserPressedRemindLaterInRateDialogEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_RATE_DIALOG_REMIND_BUTTON_PRESS);
	}
	
	public void sendUserPressedNoThanksInRateDialogEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_RATE_DIALOG_NO_BUTTON_PRESS);
	}
	
	public void sendUserPressedMenuButtonEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_HARDWARE_BUTTON_MENU_PRESS);
	}
	
	public void sendUserPressedUserProfilePageTopViewEvent() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_USER_PROFILE_TOP_VIEW_PRESS);
	}
	
	public void sendUserPressedAddMoreChannelsCell() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_ADD_MORE_CHANNELS_CELL_PRESS);
	}
	
	public void sendUserPressedSearchButtonOnKeyBoard() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_SEARCH_KEYBOARD_BUTTON_PRESS);
	}
	
	public void sendUserPressedLikeInLikesActivity() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_LIKE_IN_LIKES_ACTIVITY_PRESS);
	}
	
	public void sendUserPressedChannelInMyChannelsActivity() {
		sendUserEvent(Constants.GA_EVENT_KEY_USER_EVENT_CHANNEL_IN_MY_CHANNELS_PRESS);
	}
	
	public void sendUserFeedListScrolledToItemAtIndexEvent(int feedItemIndex, boolean scrollDown) {
		String scrollDirection;
		if(scrollDown) {
			scrollDirection = "SCROLLED_DOWN";
		} else {

			scrollDirection = "SCROLLED_UP";
		}
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_FEED_LIST_SCROLLED, scrollDirection, (long)feedItemIndex);
	}

	public void sendUserFeedItemPressedEvent(FeedItemTypeEnum feedItemType, TVBroadcast broadcast, int indexIfMultiplePopularItemCell) {
		String broadcastTitle = broadcast.getTitle();

		switch (feedItemType) {
		case POPULAR_BROADCAST: {
			sendUserFeedItemPopularBroadcastPressedEvent(broadcastTitle);
			break;
		}
		case POPULAR_BROADCASTS: {
			sendUserFeedItemPopularBroadcastsPressedEvent(broadcastTitle, indexIfMultiplePopularItemCell);
			break;
		}
		case POPULAR_TWITTER: {
			sendUserFeedItemTwitterPressedEvent(broadcastTitle);
			break;
		}
		case RECOMMENDED_BROADCAST: {
			sendUserFeedItemRecommendedPressedEvent(broadcastTitle);
			break;
		}
		case BROADCAST: {
			/* Is this always "Te gustan" ("You like") feed items */
			sendUserFeedItemYouLikePressedEvent(broadcastTitle);
			break;
		}
		default: {
			/* Do nothing */
			break;
		}
		}
	}

	private void sendUserFeedItemPopularBroadcastPressedEvent(String broadcastTitle) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_FEED_ITEM_POPULAR_SINGLE, broadcastTitle);
	}

	private void sendUserFeedItemPopularBroadcastsPressedEvent(String broadcastTitle, int indexIfMultiplePopularItemCell) {
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_FEED_ITEM_POPULAR_MULTIPLE, broadcastTitle, (long) indexIfMultiplePopularItemCell);
	}
	
	private void sendUserFeedItemTwitterPressedEvent(String broadcastTitle) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_FEED_ITEM_TWITTER, broadcastTitle);
	}
	
	private void sendUserFeedItemRecommendedPressedEvent(String broadcastTitle) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_FEED_ITEM_RECOMMENDED, broadcastTitle);
	}
	
	private void sendUserFeedItemYouLikePressedEvent(String broadcastTitle) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_FEED_ITEM_YOU_LIKE, broadcastTitle);
	}

	public void sendUserSearchResultPressedEvent(String searchQuery, String hitName, int position) {
		StringBuilder sb = new StringBuilder("Query:\"");
		sb.append(searchQuery)
		.append("\", Clicked:\"")
		.append(hitName)
		.append("\"");
		
		
		String label = sb.toString();
		
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_SEARCH_RESULT_PRESS, label, (long)position);
	}
	
	public void sendUserChannelSearchResultClickedEvent(String searchQuery, String hitName, boolean didJustRemovedChannel) {
		StringBuilder sb = new StringBuilder("Query:\"");
		sb.append(searchQuery)
		.append("\", Clicked:\"")
		.append(hitName)
		.append("\"");
		
		String label = sb.toString();
		
		Long addedChannel = 1L;
		if (didJustRemovedChannel) {
			addedChannel = 0L;
		}
		
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_MY_CHANNELS_SEARCH_RESULT_PRESS, label, addedChannel);
	}
	
	public void sendUserSearchEvent(String searchQuery) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_SEARCH, searchQuery);
	}
	
	public void sendUserTutorialExitEvent(int page) {
		/* 5 pages of total between 0-4, therefore we add 1 */
		page += 1;
		
		String labelPage = Integer.toString(page);
		String label = "User left tutorial on page: " + labelPage;
		
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EXIT_TUTORIAL, label);
	}
	
	public void sendUserMyChannelsPageSearchEvent(String searchedChannel) {
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_MY_CHANNELS_SEARCH, searchedChannel);
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
	
	public void sendUserCompetitionEventWithLabelAndValue(String action, String label, long value) 
	{
		sendEventWithLabelAndValue(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, action, label, value);
	}
	
	
	public void sendInternalSpeedMeasureEventWithLabelAndValue(String action, String label, long value) 
	{
		sendEventWithLabelAndValue(Constants.GA_EVENT_CATEGORY_KEY_INTERNAL_SPEED_MEASUARE_EVENT, action, label, value);
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

		Log.d(TAG, String.format("%s, %s, %s, %d", category, action, label, value));
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
	 * The referrer we get looks like: utm_source=xxx&utm_medium=xxx&utm_term=xxx&utm_content=xxx&utm_campaign=xxx
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