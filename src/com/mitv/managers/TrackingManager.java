
package com.mitv.managers;



import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.utilities.DateUtils;



public class TrackingManager 
{
	private static final String TAG = TrackingAIManager.class.getName();

	
	private static TrackingManager instance;

	private static Map<String, Long> measureEventStartTimes;
	
	
	public TrackingManager() {}
	
	

	public static TrackingManager sharedInstance() 
	{
		if (instance == null) 
		{
			instance = new TrackingManager();
			
			measureEventStartTimes = new HashMap<String, Long>();
		}

		return instance;
	}

	
	
	public void reportActivityStart(Activity activity) 
	{
		TrackingGAManager.reportActivityStart(activity);
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportActivityStart(activity);
		}
	}
	
	
	
	public void reportActivityStop(Activity activity) 
	{
		TrackingGAManager.reportActivityStop(activity);
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportActivityStop(activity);
		}
	}
	
	
	
	public void sendUserSignUpSuccessfulUsingEmailEvent() 
	{
		TrackingGAManager.sharedInstance().sendUserSignUpSuccessfulEvent(false);
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportUserSignUpSuccessfulUsingEmailEvent();
		}
	}

	
	
	public void sendUserSignUpSuccessfulUsingFacebookEvent() 
	{
		TrackingGAManager.sharedInstance().sendUserSignUpSuccessfulEvent(true);
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportUserSignUpSuccessfulUsingFacebookEvent();
		}
	}
	
	
	
	public void sendHTTPCoreOutOfMemoryException() 
	{
		TrackingGAManager.sharedInstance().sendHTTPCoreOutOfMemoryException();
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportHTTPCoreOutOfMemoryException();
		}
	}
	
	
	
	public void onPause(Activity activity)
	{
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().pauseSession();
			
			if(Constants.ENABLE_AMAZON_INSIGHTS)
			{
				TrackingAIManager.sharedInstance().submitEvents();
			}
		}
	}
	
	
	
	public void onResume(Activity activity)
	{
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().resumeSession();
		}
	}
	
	
	
	public void sendUserTutorialExitEvent(int page) 
	{	
		TrackingGAManager.sharedInstance().sendUserTutorialExitEvent(page);
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().sendUserTutorialExitEvent(page);
		}
	}
	
	
	private void clearEventStartValues()
	{
		measureEventStartTimes.clear();
	}
	
	
	
	private void setStartValue(String eventName)
	{
		Long now = Long.valueOf(DateUtils.getNowWithGMTTimeZone().getTimeInMillis());
		
		measureEventStartTimes.put(eventName, now);
	}
	
	
	
	private long calculateValueSincePrevious(String previousEventName)
	{
		Long now = Long.valueOf(DateUtils.getNowWithGMTTimeZone().getTimeInMillis());
		
		Long startTime = measureEventStartTimes.get(previousEventName);
		
		long duration;
		
		if(startTime == null)	
		{
			duration = 0;
		}
		else
		{
			duration = now.longValue() - startTime.longValue();
		}
		
		return duration;
	}
	
	
	
	public void sendTestMeasureInitialLoadingScreenStarted(String className)
	{
		clearEventStartValues();
		
		setStartValue(className);
	}
	
	
	
	public void sendTestMeasureInitialLoadingScreenOnResultReached(String className)
	{
		long duration = calculateValueSincePrevious(className);
		
		Log.v(TAG, "Duration for " + "SPLASH_SCREEN_ON_RESULT_REACHED" + " is " + duration);
	}
	
	
	
	
	public void sendTestMeasureInitialLoadingScreenEnded(String className)
	{
		long duration = calculateValueSincePrevious(className);
		
		Log.v(TAG, "Duration for " + "SPLASH_SCREEN_END" + " end is " + duration);
		
		TrackingGAManager.sharedInstance().sendInternalSpeedMeasureEventWithLabelAndValue(className, "SPLASH_SCREEN_START_TO_END", duration);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundStart(String className)
	{
		String measureEventName = className + "AsyncTask" + "DoInBackground";
		
		setStartValue(measureEventName);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundEnd(String className)
	{
		String measureEventName = className + "AsyncTask" + "DoInBackground";
		
		long duration = calculateValueSincePrevious(measureEventName);
		
		Log.v(TAG, "Duration for " + measureEventName + " end is " + duration);
		
		TrackingGAManager.sharedInstance().sendInternalSpeedMeasureEventWithLabelAndValue(className, "ASYNC_TASK_BACKGROUND_START_TO_END", duration);
	}
	
	
	
	public void sendTestMeasureAsycTaskPostExecutionStart(String className)
	{
		String measureEventName = className + "AsyncTask" + "PostExecute";
		
		setStartValue(measureEventName);
	}
	
	
	
	public void sendTestMeasureAsycTaskPostExecutionEnd(String className)
	{
		String measureEventName = className + "AsyncTask" + "PostExecute";
		
		long duration = calculateValueSincePrevious(measureEventName);
		
		Log.v(TAG, "Duration for " + measureEventName + " end is " + duration);
		
		TrackingGAManager.sharedInstance().sendInternalSpeedMeasureEventWithLabelAndValue(className, "ASYNC_TASK_POST_EXECUTE_START_TO_END", duration);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundNetworkRequestStart(String className)
	{
		String measureEventName = className + "AsyncTask" + "Network";
		
		setStartValue(measureEventName);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundNetworkRequestEnd(String className)
	{
		String measureEventName = className + "AsyncTask" + "Network";
		
		long duration = calculateValueSincePrevious(measureEventName);
		
		Log.v(TAG, "Duration for " + measureEventName + " end is " + duration);
		
		TrackingGAManager.sharedInstance().sendInternalSpeedMeasureEventWithLabelAndValue(className, "ASYNC_TASK_BACKGROUND_NETWORK_REQUEST_START_TO_END", duration);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundJSONParsingStart(String className)
	{
		String measureEventName = className + "AsyncTask" + "JSON";
		
		setStartValue(measureEventName);
	}
	
	
	
	public void sendTestMeasureAsycTaskBackgroundJSONParsingEnd(String className)
	{
		String measureEventName = className + "AsyncTask" + "JSON";
		
		long duration = calculateValueSincePrevious(measureEventName);
		
		Log.v(TAG, "Duration for " + measureEventName + " end is " + duration);
		
		TrackingGAManager.sharedInstance().sendInternalSpeedMeasureEventWithLabelAndValue(className, "ASYNC_TASK_BACKGROUND_JSON_PARSING_START_TO_END", duration);
	}
}
