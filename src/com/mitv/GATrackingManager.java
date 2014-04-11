
package com.mitv;



import java.io.File;
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
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.utilities.FileUtils;



public class GATrackingManager 
{	
	private static final String TAG = GATrackingManager.class.getName();
	
	
	private static GATrackingManager instance;
	
	private Tracker tracker;
	private Context context;
	
	
	
	public GATrackingManager(final Context context) 
	{
		this.context = context;
		
		updateConfiguration();
	}

	
	
	public static GATrackingManager sharedInstance()
	{
		if (instance == null) 
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
			instance = new GATrackingManager(context);
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
		
		if(cacheHasAppConfiguration && !forceDefaultGATrackingID)
		{
			String trackingId = ContentManager.sharedInstance().getFromCacheAppConfiguration().getGoogleAnalyticsTrackingId();
			this.tracker.set("&tid", trackingId);
		}
		
		boolean preinstalledCheckingSharedPrefs	= SecondScreenApplication.sharedInstance().isAppPreinstalled();
		
		File file = FileUtils.getFile(Constants.APP_WAS_PREINSTALLED_FILE_NAME);
		boolean preinstalledCheckingExternalStorage = FileUtils.fileExists(file);
		
    	boolean preinstalledUsingSystemAppDetectionCheckLocation = SecondScreenApplication.isApplicationSystemApp();
    	boolean preinstalledUsingSystemAppDetectionCheckFlag = SecondScreenApplication.isApplicationSystemAppUsingFlag();
    	
		String wasPreinstalledSharedPrefs = preinstalledCheckingSharedPrefs ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = preinstalledCheckingExternalStorage ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppLocation = preinstalledUsingSystemAppDetectionCheckLocation ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppFlag = preinstalledUsingSystemAppDetectionCheckFlag ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
			
       	if(cacheHasAppConfiguration)
		{
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
	
	public void setUserIdOnTrackerAndSendSignedIn(String userId) {
		setUserIdOnTracker(userId);
		tracker.send(new HitBuilders.EventBuilder().setCategory(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT).setAction(Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_IN).build());
	}
	
	
	public void setUserIdOnTracker(String userId) {
		tracker.set(Constants.GA_FIELD_USER_ID, userId);
	}


	public void sendUserSignUpSuccessfulUsingEmailEvent()
	{
		sendUserSignUpSuccessfulEvent(false);
	}
	
	public void sendUserSignUpSuccessfulUsingFacebookEvent()
	{
		sendUserSignUpSuccessfulEvent(true);
	}
	
	public void sendUserSignUpSuccessfulEvent(boolean facebook)
	{
		String userId = ContentManager.sharedInstance().getFromCacheUserId();
		
		String actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_EMAIL;
		if(facebook) {
			actionString = Constants.GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_FACEBOOK;
		}
		
		sendUserEventWithLabel(actionString, userId);
	}
	
	
	public void sendUserSharedEvent(TVBroadcast broadcast)
	{	
		String broadcastTitle = broadcast.getTitle();
		sendUserEventWithLabel(Constants.GA_EVENT_KEY_USER_EVENT_USER_SHARE, broadcastTitle);
	}
	
	public void sendUserLikesEvent(UserLike userLike, boolean didJustUnlike) {
		String broadcastTitle = userLike.getTitle();
		
		Long addedLike = 1L;
		if(didJustUnlike) {
			addedLike = 0L;
		}
				
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_USER_LIKE, broadcastTitle, addedLike);
	}
	
	public void sendUserReminderEvent(TVBroadcast broadcast, boolean didJustRemoveReminder)
	{	
		String broadcastTitle = broadcast.getTitle();
		
		Long addedReminder = 1L;
		if(didJustRemoveReminder) {
			addedReminder = 0L;
		}
		
		sendUserEventWithLabelAndValue(Constants.GA_EVENT_KEY_USER_EVENT_USER_REMINDER, broadcastTitle, addedReminder);
	}
	
	public void sendTimeOffSyncEvent() {
		sendSystemEvent(Constants.GA_EVENT_KEY_SYSTEM_EVENT_DEVICE_TIME_UNSYNCED);
	}
	
	public void sendFirstBootEvent() {
		sendSystemEventWithLabel(Constants.GA_EVENT_KEY_ACTION_FIRST_BOOT, Constants.GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT);
	}
	
	/* Methods for sending GA Events */
	private void sendSystemEvent(String action) {
		sendEventBase(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, action, false, null, false, 0);
	}
	
	private void sendSystemEventWithLabel(String action, String label) {
		sendEventWithLabel(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, action, label);
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
		eventBuilder
		.setCategory(category)
		.setAction(action);
		
		if(setLabel) {
			eventBuilder.setLabel(label);
		}
		
		if(setValue) {
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
}