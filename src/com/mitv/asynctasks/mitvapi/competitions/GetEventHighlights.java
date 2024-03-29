
package com.mitv.asynctasks.mitvapi.competitions;



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
	@SuppressWarnings("unused")
	private static final String TAG = GetEventHighlights.class.getName();
	
	
	
	private static String buildURL(
			final Long competitionID,
			final Long eventID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_EVENT_LINEUP);
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
			final Long eventID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_EVENT_HIGHLIGHTS, EventHighlight[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, eventID), false, retryThreshold);
		
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY, eventID);
	}
}