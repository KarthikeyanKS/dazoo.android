package com.mitv.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.DeviceUtilities;


public class GATrackingManager {
	
	//TODO don't hard code!
	private static final boolean FORCE_HARDCODED = true;
	private static final String TAG = "GATrackingManager";
	
	private Tracker 					mTracker;
	private Context						mContext;
	private static GATrackingManager 	selfInstance;

	public GATrackingManager(Context context) {
		this.mContext = context;
		updateConfiguration();
	}

	public static GATrackingManager getInstance() {
		if (selfInstance == null) {
			Context context = SecondScreenApplication.getInstance().getApplicationContext();
			selfInstance = new GATrackingManager(context);
		}
		return selfInstance;
	}

	public Tracker getTrackerInstance() {
		return mTracker;
	}

	public static Tracker getTracker() {
		return getInstance().getTrackerInstance();
	}
	
	public void updateConfiguration() {
		String trackingId = AppConfigurationManager.getInstance().getGoogleAnalyticsTrackingId();
		if(FORCE_HARDCODED || trackingId == null || trackingId.length() == 0) {
			trackingId = mContext.getString(R.string.ga_trackingId_mitv_hardcoded);
		}

		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(mContext);
		this.mTracker = googleAnalyticsInstance.getTracker(trackingId);
		
		boolean preinstalledCheckingSharedPrefs	= SecondScreenApplication.getInstance().getWasPreinstalled();
		boolean preinstalledCheckingExternalStorage = SecondScreenApplication.getInstance().wasPreinstalledFileExists();
    	boolean preinstalledUsingSystemAppDetectionCheckLocation = SecondScreenApplication.applicationIsSystemApp(mContext);
    	boolean preinstalledUsingSystemAppDetectionCheckFlag = SecondScreenApplication.applicationIsSystemAppUsingFlag(mContext);
    	
		String wasPreinstalledSharedPrefs = preinstalledCheckingSharedPrefs ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		String wasPreinstalledExternalStorage = preinstalledCheckingExternalStorage ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppLocation = preinstalledUsingSystemAppDetectionCheckLocation ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
    	String wasPreinstalledSystemAppFlag = preinstalledUsingSystemAppDetectionCheckFlag ? Consts.PREFS_KEY_APP_WAS_PREINSTALLED : Consts.PREFS_KEY_APP_WAS_NOT_PREINSTALLED;
		
		
		String deviceId = DeviceUtilities.getDeviceId();
		String appVersion = Consts.GA_APP_VERSION_NOT_SET;
		try {
			PackageInfo pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			appVersion = "" + pinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		
		double sampleRateDecimal = AppConfigurationManager.getInstance().getGoogleAnalyticsSampleRate();
		double sampleRateAsPercentage = sampleRateDecimal * 100.0d;
		String sampleRateAsString = String.valueOf(sampleRateAsPercentage);
		
		
		mTracker.set(Consts.GA_KEY_APP_VERSION, appVersion);
		mTracker.set(Consts.GA_KEY_DEVICE_ID, deviceId);
		
		/* Information regarding if the app was preinstalled or not */
		mTracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS, wasPreinstalledSharedPrefs);
		mTracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE, wasPreinstalledExternalStorage);
		mTracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION, wasPreinstalledSystemAppLocation);
		mTracker.set(Consts.GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG, wasPreinstalledSystemAppFlag);	
		mTracker.set(Fields.SAMPLE_RATE, sampleRateAsString);
	}
	

	public void sendViewInstance(String viewName) {
		// Set screen name on the tracker to be sent with all hits.
		mTracker.set(Fields.SCREEN_NAME, viewName);

		// Send a screen view for "Home Screen"
		mTracker.send(MapBuilder
		    .createAppView()
		    .build()
		);

		Log.d(TAG, "GATrackingManager: sendView, viewName: " + viewName);
	}
	
	public static void sendView(String viewName) {
		// Set screen name on the tracker to be sent with all hits.
		getInstance().sendViewInstance(viewName);
	}
	
	public void stopTrackingViewInstance(String viewName) {
		mTracker.set(Fields.SCREEN_NAME, null);
		Log.d(TAG, "GATrackingManager: stopTracking, viewName: " + viewName);
	}
	
	public static void stopTrackingView(String viewName) {
		getInstance().stopTrackingViewInstance(viewName);
	}
	
}
