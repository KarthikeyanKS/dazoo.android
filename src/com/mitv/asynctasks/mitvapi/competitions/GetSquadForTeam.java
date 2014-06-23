


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class GetSquadForTeam 
	extends AsyncTaskBase<TeamSquad[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetSquadForTeam.class.getName();
	
	
	
	private static String buildURL(final long teamID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_TEAMS_FULL);
		url.append(Constants.FORWARD_SLASH);
		url.append(teamID);
		url.append(Constants.URL_SQUAD);

		return url.toString();
	}
	
	
	
	public GetSquadForTeam(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long teamID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_TEAM_SQUAD, TeamSquad[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(teamID), false, retryThreshold);
	
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_TEAM_ID_KEY, teamID);
	}
}
