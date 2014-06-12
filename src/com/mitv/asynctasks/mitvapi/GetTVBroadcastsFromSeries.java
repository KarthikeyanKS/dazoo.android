
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.UpcomingBroadcastsForBroadcast;


/**
 * Use for fetching upcoming broadcasts for series
 * @author consultant_hdme
 *
 */
public class GetTVBroadcastsFromSeries 
	extends AsyncTaskBase<TVBroadcastWithChannelInfo[]>
{
	private static final String TAG = GetTVBroadcastsFromSeries.class.getName();
	
	private static String tvSeriesId;
	
	
	
	private static String buildURL(String tvSeriesId)
	{
		GetTVBroadcastsFromSeries.tvSeriesId = tvSeriesId;
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_SERIES);
		url.append(tvSeriesId);
		url.append(Constants.API_UPCOMING_BROADCASTS);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastsFromSeries(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String tvSeriesId,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.UPCOMING_BROADCASTS_FOR_SERIES, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvSeriesId), false, retryThreshold);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params)
	{
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVBroadcastWithChannelInfo[] contentAsArray = (TVBroadcastWithChannelInfo[]) requestResultObjectContent;
			
			ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(Arrays.asList(contentAsArray));
			
			UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
			
			requestResultObjectContent = upcomingBroadcastsObject;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}