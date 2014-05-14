
package com.mitv.asynctasks.mitvapi.competitions;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;



public class GetEventHighlights 
	extends AsyncTaskBase<EventHighlight[]>
{
	private static final String TAG = GetEventHighlights.class.getName();
	
	
	
	private static String buildURL(
			final Long competitionID,
			final Long eventID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(competitionID);
		
		url.append(Constants.URL_EVENTS);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(eventID);

		url.append(Constants.URL_HIGHLIGHTS);
		
		return url.toString();
	}
	
	
	
	public GetEventHighlights(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final Long competitionID,
			final Long eventID)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS, EventHighlight[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, eventID), false);
		
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY, eventID);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			EventHighlight[] contentAsArray = (EventHighlight[]) requestResultObjectContent;
			
			ArrayList<EventHighlight> contentAsArrayList = new ArrayList<EventHighlight>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}