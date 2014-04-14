
package com.mitv;



import android.content.Context;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.Event;
import com.amazon.insights.EventClient;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;



public class AITrackingManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = AITrackingManager.class.getName();

	
	private static AITrackingManager instance;
	
	
	private Context context;
	private AmazonInsights insights;
	
	
	
	public AITrackingManager(final Context context) 
	{
		this.context = context;
		this.insights = getAmazonInsightsInstance();
	}
	
	
	
	public static AITrackingManager sharedInstance() 
	{
		if (instance == null) 
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

			instance = new AITrackingManager(context);
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
	
	
	
	public void recordCustomEvent()
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
