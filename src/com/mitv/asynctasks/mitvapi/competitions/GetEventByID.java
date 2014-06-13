
package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Event;



public class GetEventByID 
	extends AsyncTaskBase<Event>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetEventByID.class.getName();
	
	
	
	private static String buildURL(final long eventId)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_EVENTS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(eventId);
		
		return url.toString();
	}
	
	
	
	public GetEventByID(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long eventID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_EVENT_BY_ID, Event.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(eventID), false, retryThreshold);
	
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY, eventID);
	}
}
