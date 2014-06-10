
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
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



/**
 * Fetches repetitions of a broadcasts
 * @author consultant_hdme
 *
 */
public class GetTVBroadcastsFromProgram 
	extends AsyncTaskBase<TVBroadcastWithChannelInfo[]>
{
	private static final String TAG = GetTVBroadcastsFromProgram.class.getName();
	
	private static String tvProgramId;
	
	
	
	private static String buildURL(String tvProgramId)
	{
		GetTVBroadcastsFromProgram.tvProgramId = tvProgramId;
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_PROGRAMS);
		url.append(tvProgramId);
		url.append(Constants.API_BROADCASTS);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastsFromProgram(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String tvProgramId,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvProgramId), false, retryThreshold);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVBroadcastWithChannelInfo[] contentAsArray = (TVBroadcastWithChannelInfo[]) requestResultObjectContent;
			
			ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(Arrays.asList(contentAsArray));
			
			RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(tvProgramId, repeatingBroadcasts);
			
			requestResultObjectContent = repeatingBroadcastsObject;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}