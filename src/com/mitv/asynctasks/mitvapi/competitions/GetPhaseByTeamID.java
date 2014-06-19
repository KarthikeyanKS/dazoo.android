
package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Phase;



public class GetPhaseByTeamID 
	extends AsyncTaskBase<Phase>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetPhaseByTeamID.class.getName();
	
	
	
	private static String buildURL(final long teamID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_TEAMS_FULL);
		url.append(Constants.FORWARD_SLASH);
		url.append(teamID);
		url.append(Constants.URL_PHASE);
		
		return url.toString();
	}
	
	
	
	public GetPhaseByTeamID(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long teamID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_PHASE_BY_TEAM_ID, Phase.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(teamID), false, retryThreshold);
	
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_TEAM_ID_KEY, teamID);
	}
}
