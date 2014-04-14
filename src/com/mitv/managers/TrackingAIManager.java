
package com.mitv.managers;



import java.util.HashMap;
import java.util.Map.Entry;
import android.app.Activity;
import android.content.Context;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.Event;
import com.amazon.insights.EventClient;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;
import com.mitv.Constants;
import com.mitv.SecondScreenApplication;



public class TrackingAIManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = TrackingAIManager.class.getName();

	
	private static TrackingAIManager instance;
	
	
	private Context context;
	private AmazonInsights insights;
	
	
	
	public TrackingAIManager(final Context context) 
	{
		this.context = context;
		this.insights = getAmazonInsightsInstance();
	}
	
	
	
	public static TrackingAIManager sharedInstance() 
	{
		if (instance == null) 
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

			instance = new TrackingAIManager(context);
		}

		return instance;
	}
	
	
	
	
	private AmazonInsights getAmazonInsightsInstance() 
	{
		InsightsCredentials credentials = AmazonInsights.newCredentials(Constants.AMAZON_INSIGHTS_PUBLIC_KEY, Constants.AMAZON_INSIGHTS_PRIVATE_KEY);
		
		InsightsOptions options = AmazonInsights.newOptions(true, true);
		
		AmazonInsights insightsInstance = AmazonInsights.newInstance(credentials, context, options);
		
		return insightsInstance;
	}
	
	
	
	private EventClient getEventClient()
	{
		EventClient eventClient = insights.getEventClient();
		
		return eventClient;
	}
	

	
	public void pauseSession()
	{
		insights.getSessionClient().pauseSession();
	}
	
	
	
	public void resumeSession()
	{
		insights.getSessionClient().resumeSession();
	}
	
	
	
	public void submitEvents()
	{
		EventClient eventClient = getEventClient();
		
		eventClient.submitEvents();
	}
	
	
	
	
	public void reportActivityStart(Activity activity) 
	{
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("Action", "start");
		attributes.put("Activity", activity.getClass().getSimpleName());
		
		HashMap<String, String> metrics = new HashMap<String, String>();
		
		recordEventBase("Activity", attributes, metrics);
	}

	
	
	public void reportActivityStop(Activity activity) 
	{
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("Action", "stop");
		attributes.put("Activity", activity.getClass().getSimpleName());
		
		HashMap<String, String> metrics = new HashMap<String, String>();
		
		recordEventBase("Activity", attributes, metrics);
	}
	
	
	
	private void recordEventBase(
			final String eventName, 
			final HashMap<String, String> attributes, 
			final HashMap<String, String> metrics)
	{
		EventClient eventClient = getEventClient();
		
		Event event = eventClient.createEvent(eventName);

		for(Entry<String, String> entry : attributes.entrySet())
		{
			event.addAttribute(entry.getKey(), entry.getValue());
		}
		
		for(Entry<String, String> entry : metrics.entrySet())
		{
			event.addAttribute(entry.getKey(), entry.getValue());
		}
		
		eventClient.recordEvent(event);
	}
	
	
	
	
	public void reportUserSignUpSuccessfulUsingEmailEvent() 
	{
		reportUserSignUpSuccessfulEvent(false);
	}

	
	
	public void reportUserSignUpSuccessfulUsingFacebookEvent() 
	{
		reportUserSignUpSuccessfulEvent(true);
	}

	
	
	private void reportUserSignUpSuccessfulEvent(boolean isFacebookLogin) 
	{
		HashMap<String, String> attributes = new HashMap<String, String>();
		HashMap<String, String> metrics = new HashMap<String, String>();
		
		if (isFacebookLogin) 
		{
			attributes.put("Login type", "Facebook");
		}
		else
		{
			attributes.put("Login type", "MiTV");
		}

		String userID = ContentManager.sharedInstance().getFromCacheUserId();
		String userFirstName = ContentManager.sharedInstance().getFromCacheUserFirstname();
		String userEmail = ContentManager.sharedInstance().getFromCacheUserEmail();
		
		attributes.put("userID", userID);
		attributes.put("userFirstName", userFirstName);
		attributes.put("userEmail", userEmail);
		
		recordEventBase("User login", attributes, metrics);
	}
	
	
	
	public void recordTestEvent()
	{
		EventClient eventClient = getEventClient();
		
		Event event;
		
		boolean isUserLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if(isUserLoggedIn)
		{			
			event = eventClient.createEvent("Example event")
					.withAttribute("user_name", ContentManager.sharedInstance().getFromCacheUserEmail())
			        .withAttribute("user_id", ContentManager.sharedInstance().getFromCacheUserId())
					.withMetric("time_on_page", 60)
			        .withMetric("duration", 30);
		}
		else
		{
			event = eventClient.createEvent("Example event")
					.withAttribute("user_name", "Anonymous")
			        .withAttribute("user_id", "N/A")
					.withMetric("time_on_page", 60)
			        .withMetric("duration", 30);
		}

		eventClient.recordEvent(event);
	}
}
