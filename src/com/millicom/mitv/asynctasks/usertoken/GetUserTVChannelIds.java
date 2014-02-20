
package com.millicom.mitv.asynctasks.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.mitv.Consts;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId[]> 
{
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;
	
	
	
	public GetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_USER, TVChannelId[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		TVChannelId[] contentAsArray = (TVChannelId[]) requestResultObjectContent;
		ArrayList<TVChannelId> contentAsArrayList = new ArrayList<TVChannelId>(Arrays.asList(contentAsArray));
		requestResultObjectContent = contentAsArrayList;
		 
		return null;
	}
}