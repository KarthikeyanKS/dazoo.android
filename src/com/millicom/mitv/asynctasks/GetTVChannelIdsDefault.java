
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



/**
 * This class is used for fetching the default TVChannelIds (and not the TVChannel objects), but actually if fetches
 * the default TVChannel objects and then takes out the TVChannelId for each one of them and that is what gets returned.
 * @author consultant_hdme
 *
 */
public class GetTVChannelIdsDefault
	extends AsyncTaskWithRelativeURL<TVChannel[]> 
{	
	private static final String TAG = GetTVChannelIdsDefault.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_CHANNELS_DEFAULT;

	
	public GetTVChannelIdsDefault(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_DEFAULT, TVChannel[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
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