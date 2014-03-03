
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.RepeatingBroadcastsForBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.Consts;



/**
 * Fetches repetitions of a broadcasts
 * @author consultant_hdme
 *
 */
public class GetTVBroadcastsFromProgram 
	extends AsyncTaskWithRelativeURL<TVBroadcastWithChannelInfo[]>
{
	private static final String TAG = GetTVBroadcastsFromProgram.class.getName();
	
	private static String tvProgramId;
	
	
	
	private static String buildURL(String tvProgramId)
	{
		GetTVBroadcastsFromProgram.tvProgramId = tvProgramId;
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_PROGRAMS);
		url.append(tvProgramId);
		url.append(Consts.API_BROADCASTS);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastsFromProgram(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvProgramId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.REPEATING_BROADCASTS_FOR_PROGRAMS, TVBroadcastWithChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvProgramId));
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultObjectContent != null)
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