
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannel;
import com.mitv.Consts;



public class GetTVChannelsAll 
	extends AsyncTaskWithRelativeURL<TVChannel[]>
{	
	private static final String URL_SUFFIX = Consts.URL_CHANNELS_ALL;
	
	
	
	public GetTVChannelsAll(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL, TVChannel[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		TVChannel[] contentAsArray = (TVChannel[]) requestResultObjectContent;
		ArrayList<TVChannel> contentAsArrayList = new ArrayList<TVChannel>(Arrays.asList(contentAsArray));
		requestResultObjectContent = contentAsArrayList;
		 
		return null;
	}
}