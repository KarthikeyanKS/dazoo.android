
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.TVChannelId;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId[]> 
{
	private static final String TAG = GetUserTVChannelIds.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_MY_CHANNEL_IDS;
	
	
	
	public GetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			boolean standalone) 
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), TVChannelId[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.TV_CHANNEL_IDS_USER_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.TV_CHANNEL_IDS_USER_INITIAL_CALL;
		}
		
		return requestIdentifier;
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVChannelId[] contentAsArray = (TVChannelId[]) requestResultObjectContent;
			
			ArrayList<TVChannelId> contentAsArrayList = new ArrayList<TVChannelId>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		 
		return null;
	}
}