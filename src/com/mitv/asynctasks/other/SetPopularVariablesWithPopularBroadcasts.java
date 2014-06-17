
package com.mitv.asynctasks.other;



import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.RequestParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVGuide;



public class SetPopularVariablesWithPopularBroadcasts 
	extends AsyncTask<String, Void, Void> 
{
	private static final String TAG = SetPopularVariablesWithPopularBroadcasts.class.getName();
	
	
	private ContentCallbackListener contentCallbackListener;
	private ViewCallbackListener activityCallbackListener;
	private Object requestResultObjectContent;
	private RequestParameters requestParameters;
	
	
	
	public SetPopularVariablesWithPopularBroadcasts(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
		this.requestParameters = new RequestParameters();
	}
	
	
	
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) {
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
		
		setPopularBroadcastVariables();
		
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) {
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
		
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) {
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
		
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallbackListener, RequestIdentifierEnum.TV_BROADCASTS_POUPULAR_PROCESSING, FetchRequestResultEnum.SUCCESS, requestResultObjectContent, requestParameters);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
		
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) {
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
	
	
	
	private void setPopularBroadcastVariables() 
	{
		List<TVBroadcastWithChannelInfo> popularBroacasts = ContentManager.sharedInstance().getCacheManager().getPopularBroadcasts();
		
		TVGuide tvGuide = ContentManager.sharedInstance().getCacheManager().getTVGuideForSelectedDay();
		
		for (TVChannelGuide tvChannelGuide : tvGuide.getTvChannelGuides()) 
		{
			ArrayList<TVBroadcast> broadcasts = tvChannelGuide.getBroadcasts();
			
			for (TVBroadcast broadcast : broadcasts) 
			{
				for (TVBroadcastWithChannelInfo popularBroacast : popularBroacasts) 
				{
					boolean isRunningAtTheSameTime = popularBroacast.getBeginTimeMillis().equals(broadcast.getBeginTimeMillis());
					
					if(isRunningAtTheSameTime)
					{
						TVChannelId popularTVChannelId = popularBroacast.getChannel().getChannelId();
						
						TVChannelId guideTVChannelId = tvChannelGuide.getChannelId();
						
						boolean isOnTheSameChannel = popularTVChannelId.equals(guideTVChannelId);
						
						if(isOnTheSameChannel)
						{
							broadcast.setPopular(true);
							break;
						}
					}
				}
			}
		}
	}
}