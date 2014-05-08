
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
	
	
	
	public void sendHTTPCoreOutOfMemoryException() 
	{
		TrackingGAManager.sharedInstance().sendHTTPCoreOutOfMemoryException();
		
		if(Constants.ENABLE_AMAZON_INSIGHTS)
		{
			TrackingAIManager.sharedInstance().reportHTTPCoreOutOfMemoryException();
		}
	}
	
	
	
	public void sendUserCompetitionTabCountdownPressed(String competitionName) 
	{
		String eventLabel = competitionName + " Countdown";
		
		TrackingGAManager.sharedInstance().sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_COMPETITION_ENTRY_PRESSED, eventLabel, 0);
	}
	
	
	
	public void sendUserCompetitionTabBannerPressed(String competitionName) 
	{
		String eventLabel = competitionName + " Banner";
		
		TrackingGAManager.sharedInstance().sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_COMPETITION_ENTRY_PRESSED, eventLabel, 0);
	}
		
	
	
	public void sendUserCompetitionTabCalendarPressed(String competitionName) 
	{
		String eventLabel = competitionName + " Calendar";
		
		TrackingGAManager.sharedInstance().sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_COMPETITION_ENTRY_PRESSED, eventLabel, 0);
	}
	
	
	public void sendUserCompetitionBannerPressedInAllTab(String competitionName) 
	{
		String eventLabel = competitionName + " Banner";
		
		TrackingGAManager.sharedInstance().sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_COMPETITION_ENTRY_PRESSED, eventLabel, 0);
	}
	
	
	public void sendUserCompetitionBannerPressedInSportsTab(String competitionName) 
	{
		String eventLabel = competitionName + " Banner";
		
		TrackingGAManager.sharedInstance().sendUserCompetitionEventWithLabelAndValue(Constants.GA_EVENT_ACTION_COMPETITION_ENTRY_PRESSED, eventLabel, 0);
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
}
