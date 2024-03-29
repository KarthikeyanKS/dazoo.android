
package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;



public class GetEventLineUp 
	extends AsyncTaskBase<EventLineUp[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetEventLineUp.class.getName();
	
	
	
	private static String buildURL(
			final Long eventID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_EVENTS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(eventID);

		url.append(Constants.URL_LINE_UP);
		
		return url.toString();
	}
	
	
	
	public GetEventLineUp(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final Long eventID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_EVENT_LINEUP, EventLineUp[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(eventID), false, retryThreshold);
		
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_EVENT_ID_KEY, eventID);
	}
}