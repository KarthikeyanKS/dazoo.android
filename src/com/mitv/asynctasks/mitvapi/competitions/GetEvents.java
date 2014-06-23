


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Event;



public class GetEvents 
	extends AsyncTaskBase<Event[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetEvents.class.getName();
	
	
	private static String buildURL(
			final long competitionID,
			final String teamID,
			final String phaseID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		
		
		url.append(Constants.FORWARD_SLASH);
		url.append(competitionID);
		
		if(teamID != null)
		{
			url.append(Constants.URL_TEAMS);
			url.append(Constants.FORWARD_SLASH);
			url.append(teamID);
		}
		else if(phaseID != null)
		{
			url.append(Constants.URL_PHASES);
			url.append(Constants.FORWARD_SLASH);
			url.append(phaseID);
		}
		
		url.append(Constants.URL_EVENTS);
		
		return url.toString();
	}
	
	
	
	public GetEvents(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			final String teamID,
			final String phaseID,
			int retryThreshold)
	{
		this(contentCallbackListener, activityCallbackListener, competitionID, teamID, phaseID, false, false, null, false, null, retryThreshold);
	}
	
	
	
	public GetEvents(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			final String teamID,
			final String phaseID,
			final boolean useOnlyOngoing,
			final boolean useBroadcastBeginTimeAfter,
			final String broadcastBeginTimeAfter,
			final boolean useBroadcastBeginTimeBefore,
			final String broadcastBeginTimeBefore,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_EVENTS, Event[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, teamID, phaseID), false, retryThreshold);
		
		if(useOnlyOngoing)
		{
			urlParameters.add("onlyOngoing", "true");
		}
		
		if(useBroadcastBeginTimeAfter)
		{
			urlParameters.add("begintime", broadcastBeginTimeAfter);
		}
		
		if(useBroadcastBeginTimeBefore)
		{
			urlParameters.add("endtime", broadcastBeginTimeBefore);
		}
	}
}
