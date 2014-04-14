
package com.mitv.managers;



import android.app.Activity;
import com.mitv.Constants;



public class TrackingManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = TrackingAIManager.class.getName();

	
	private static TrackingManager instance;

	
	
	
	public TrackingManager() {}
	
	

	public static TrackingManager sharedInstance() 
	{
		if (instance == null) 
		{
			instance = new TrackingManager();
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
}
