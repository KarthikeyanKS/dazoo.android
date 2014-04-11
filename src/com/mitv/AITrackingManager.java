
package com.mitv;



import android.content.Context;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;



public class AITrackingManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = AITrackingManager.class.getName();

	
	private static AITrackingManager instance;
	
	
	private Context context;
	
	
	
	public AITrackingManager(final Context context) 
	{
		this.context = context;
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
}
