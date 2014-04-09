
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
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;



/**
 * This class is used for fetching the default TVChannelIds (and not the TVChannel objects), but actually if fetches
 * the default TVChannel objects and then takes out the TVChannelId for each one of them and that is what gets returned.
 * @author consultant_hdme
 *
 */
public class GetTVChannelIdsDefault
	extends AsyncTaskBase<TVChannel[]> 
{	
	private static final String TAG = GetTVChannelIdsDefault.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_CHANNELS_DEFAULT;

	
	public GetTVChannelIdsDefault(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_DEFAULT, TVChannel[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVChannel[] channelObjectsAsArray = (TVChannel[]) requestResultObjectContent;
			ArrayList<TVChannel> channelObjectsAsArrayList = new ArrayList<TVChannel>(Arrays.asList(channelObjectsAsArray));
			
			ArrayList<TVChannelId> tvChannelIds = new ArrayList<TVChannelId>();
			
			for(TVChannel tvChannel : channelObjectsAsArrayList) 
			{
				TVChannelId tvChannelId = tvChannel.getChannelId();
				tvChannelIds.add(tvChannelId);
			} 
			
			requestResultObjectContent = tvChannelIds;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}