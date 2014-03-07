
package com.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.UpcomingBroadcastsForBroadcast;


/**
 * Use for fetching upcoming broadcasts for series
 * @author consultant_hdme
 *
 */
public class GetTVBroadcastsFromSeries 
	extends AsyncTaskWithRelativeURL<TVBroadcastWithChannelInfo[]>
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
			ActivityCallbackListener activityCallBackListener,
			String tvSeriesId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_SERIES_UPCOMING, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvSeriesId));
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