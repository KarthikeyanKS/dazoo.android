
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVGuide;



public class GetTVChannelGuides 
	extends AsyncTaskBase<TVChannelGuide[]>
{	
	private static final String TAG = GetTVChannelGuides.class.getName();
	
	private TVDate tvDate;
	
	
	
	private static String buildURL(TVDate tvDate)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_GUIDE);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(tvDate.getId());
		
		return url.toString();
	}

	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.TV_GUIDE_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL;
		}
		
		return requestIdentifier;
	}
	
	
	
	public GetTVChannelGuides(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean standalone,
			TVDate tvDate,
			List<TVChannelId> tvChannelIds) 
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), TVChannelGuide[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvDate));
		
		this.tvDate = tvDate;
		
		for(TVChannelId tvChannelId : tvChannelIds) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			
			this.urlParameters.add(Constants.API_CHANNEL_ID, tvChannelIdAsString);
		}
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVChannelGuide[] contentAsArray = (TVChannelGuide[]) requestResultObjectContent;
			
			ArrayList<TVChannelGuide> tvChannelGuides = new ArrayList<TVChannelGuide>(Arrays.asList(contentAsArray));
			
			TVGuide tvGuide = new TVGuide(tvDate, tvChannelGuides);

			requestResultObjectContent = tvGuide;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}