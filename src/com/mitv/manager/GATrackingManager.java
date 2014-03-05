package com.mitv.manager;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.millicom.mitv.utilities.FileUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



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

	
	
	public static GATrackingManager getInstance()
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
		return getInstance().getTrackerInstance();
	}
	
	
	
	public void updateConfiguration() 
	{
		String trackingId = AppConfigurationManager.getInstance().getGoogleAnalyticsTrackingId();
		
		boolean useDefaultGATrackingID = Consts.USE_DEFAULT_GOOGLE_TRACKING_ID;
		
		if(useDefaultGATrackingID || trackingId == null || trackingId.isEmpty()) 
		{
			trackingId = context.getString(R.string.ga_trackingId_mitv_hardcoded);
		}

		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(context);
		
		this.tracker = googleAnalyticsInstance.getTracker(trackingId);
		
		boolean preinstalledCheckingSharedPrefs	= SecondScreenApplication.sharedInstance().getWasPreinstalled();
		
		File file = FileUtils.getFile(Consts.APP_WAS_PREINSTALLED_FILE_NAME);
		boolean preinstalledCheckingExternalStorage = FileUtils.fileExists(file);
		
    	boolean preinstalledUsingSystemAppDetectionCheckLocation = SecondScreenApplication.applicationIsSystemApp(context);
    	boolean preinstalledUsingSystemAppDetectionCheckFlag = SecondScreenApplication.applicationIsSystemAppUsingFlag(context);
    	
		String wasPreinstalledSharedPrefs = preinstalledCheckingSharedPrefs ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = preinstalledCheckingExternalStorage ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppLocation = preinstalledUsingSystemAppDetectionCheckLocation ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppFlag = preinstalledUsingSystemAppDetectionCheckFlag ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
				
		double sampleRateDecimal = AppConfigurationManager.getInstance().getGoogleAnalyticsSampleRate();
		
		if(sampleRateDecimal == 0) 
		{
			sampleRateDecimal = 1.0d;
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
				.createEvent(Consts.GA_EVENT_KEY_SYSTEM_EVENT, Consts.GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS, wasPreinstalledSharedPrefs, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Consts.GA_EVENT_KEY_SYSTEM_EVENT, Consts.GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE, wasPreinstalledExternalStorage, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Consts.GA_EVENT_KEY_SYSTEM_EVENT, Consts.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION, wasPreinstalledSystemAppLocation, null)
				.build());

		tracker.send(MapBuilder
				.createEvent(Consts.GA_EVENT_KEY_SYSTEM_EVENT, Consts.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG, wasPreinstalledSystemAppFlag, null)
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
	
	
	
	public static void sendView(String viewName)
	{
		// Set screen name on the tracker to be sent with all hits.
		getInstance().sendViewInstance(viewName);
	}
	
	
	
	public void stopTrackingViewInstance(String viewName) 
	{
		tracker.set(Fields.SCREEN_NAME, null);
		
		Log.d(TAG, "GATrackingManager: stopTracking, viewName: " + viewName);
	}
	
	
	
	public static void stopTrackingView(String viewName) 
	{
		getInstance().stopTrackingViewInstance(viewName);
	}
}