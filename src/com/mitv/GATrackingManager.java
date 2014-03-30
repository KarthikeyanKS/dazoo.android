
package com.mitv;



import java.io.File;

import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.models.TVBroadcast;
import com.mitv.models.UserLike;
import com.mitv.utilities.FileUtils;
import com.mitv.utilities.NetworkUtils;



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
	
	
	
	public void updateConfiguration() 
	{
		String trackingId;
		
		boolean cacheHasAppConfiguration = ContentManager.sharedInstance().getFromCacheHasAppConfiguration();
		
		boolean forceDefaultGATrackingID = Constants.FORCE_DEFAULT_GOOGLE_TRACKING_ID;
		
		if(cacheHasAppConfiguration && !forceDefaultGATrackingID)
		{
			trackingId = ContentManager.sharedInstance().getFromCacheAppConfiguration().getGoogleAnalyticsTrackingId();
		}
		else
		{
			trackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
		}
		
		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
		
		this.tracker = googleAnalyticsInstance.getTracker(trackingId);
		
		boolean preinstalledCheckingSharedPrefs	= SecondScreenApplication.sharedInstance().isAppPreinstalled();
		
		File file = FileUtils.getFile(Constants.APP_WAS_PREINSTALLED_FILE_NAME);
		boolean preinstalledCheckingExternalStorage = FileUtils.fileExists(file);
		
    	boolean preinstalledUsingSystemAppDetectionCheckLocation = SecondScreenApplication.isApplicationSystemApp();
    	boolean preinstalledUsingSystemAppDetectionCheckFlag = SecondScreenApplication.isApplicationSystemAppUsingFlag();
    	
		String wasPreinstalledSharedPrefs = preinstalledCheckingSharedPrefs ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = preinstalledCheckingExternalStorage ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppLocation = preinstalledUsingSystemAppDetectionCheckLocation ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppFlag = preinstalledUsingSystemAppDetectionCheckFlag ? Constants.PREFS_KEY_APP_WAS_PREINSTALLED : Constants.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
			
    	double sampleRateDecimal;
    	
    	if(cacheHasAppConfiguration)
		{
    		sampleRateDecimal = ContentManager.sharedInstance().getFromCacheAppConfiguration().getGoogleAnalyticsSampleRate();
		}
    	else
    	{
    		sampleRateDecimal = context.getResources().getInteger(R.integer.ga_sampleRateHardcoded);
    	}
		
		double sampleRateAsPercentage = sampleRateDecimal * 100.0d;
		
		String sampleRateAsString = String.valueOf(sampleRateAsPercentage);
				
		/* Information regarding if the app was preinstalled or not */
		
		/* APP_WAS_PREINSTALLED_SHARED_PREFS is at index 1 */
		tracker.set(Fields.customDimension(1), wasPreinstalledSharedPrefs);
		
		/* APP_WAS_PREINSTALLED_EXTERNAL_STORAGE is at index 2 */
		tracker.set(Fields.customDimension(2), wasPreinstalledExternalStorage);
		
		/* APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION is at index 3 */
		tracker.set(Fields.customDimension(3), wasPreinstalledSystemAppLocation);
		
		/* APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG is at index 3 */
		tracker.set(Fields.customDimension(4), wasPreinstalledSystemAppFlag);
		
		/* Set the SAMPLE RATE */
		tracker.set(Fields.SAMPLE_RATE, sampleRateAsString);
		
		/* NOW SEND THE DATA!!!! */
		tracker.send(MapBuilder.createAppView().build());
		
		/* BACKUP/RDUNDANCY OF ANALYTICS PREINSTALL FLAGS */
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS, wasPreinstalledSharedPrefs, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE, wasPreinstalledExternalStorage, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION, wasPreinstalledSystemAppLocation, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG, wasPreinstalledSystemAppFlag, null)
				.build());
	}
	
	
	
	public void sendViewInstance(String viewName)
	{
		// Send a screen view for "Home Screen"
		// Set screen name on the tracker to be sent with all hits.
		tracker.set(Fields.SCREEN_NAME, viewName);

		// Send a screen view for "Home Screen"
		tracker.send(MapBuilder
		    .createAppView()
		    .build()
		);

		Log.d(TAG, "GATrackingManager: sendView, viewName: " + viewName);
	}
	
	
	
	
	public void sendUserNetworkTypeEvent()
	{
		String activeNetworkTypeName = NetworkUtils.getActiveNetworkTypeAsString();
		
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT, Constants.GA_KEY_APP_CURRENT_USER_NETWORK_FLAG, activeNetworkTypeName, null)
				.build());
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
		
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, actionString, userId, null)
				.build());
	}
	
	
	public void sendUserSharedEvent(TVBroadcast broadcast)
	{	
		String broadcastTitle = broadcast.getTitle();
		
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, Constants.GA_EVENT_KEY_USER_EVENT_USER_SHARE, broadcastTitle, null)
				.build());
	}
	
	public void sendUserLikesEvent(UserLike userLike, boolean didJustUnlike) {
		String broadcastTitle = userLike.getTitle();
		
		Long addedLike = 1L;
		if(didJustUnlike) {
			addedLike = 0L;
		}
		
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, Constants.GA_EVENT_KEY_USER_EVENT_USER_LIKE, broadcastTitle, addedLike)
				.build());
	}
	
	public void sendUserReminderEvent(TVBroadcast broadcast, boolean didJustRemoveReminder)
	{	
		String broadcastTitle = broadcast.getTitle();
		
		Long addedReminder = 1L;
		if(didJustRemoveReminder) {
			addedReminder = 0L;
		}
		
		tracker.send(MapBuilder
				.createEvent(Constants.GA_EVENT_CATEGORY_KEY_USER_EVENT, Constants.GA_EVENT_KEY_USER_EVENT_USER_REMINDER, broadcastTitle, addedReminder)
				.build());
	}
	
	
	public static void sendView(String viewName)
	{
		// Set screen name on the tracker to be sent with all hits.
		sharedInstance().sendViewInstance(viewName);
	}
	
	
	
	public void stopTrackingViewInstance(String viewName) 
	{
		tracker.set(Fields.SCREEN_NAME, null);
		
		Log.d(TAG, "GATrackingManager: stopTracking, viewName: " + viewName);
	}
	
	
	
	public static void stopTrackingView(String viewName) 
	{
		sharedInstance().stopTrackingViewInstance(viewName);
	}
}