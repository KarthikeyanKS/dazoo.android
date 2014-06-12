


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class GetTeamByID 
	extends AsyncTaskBase<Team>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetTeamByID.class.getName();
	
	
	
	private static String buildURL(
			final long competitionID,
			final long teamID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(competitionID);
		
		url.append(Constants.URL_TEAMS);
		
		url.append(Constants.FORWARD_SLASH);
		
		url.append(teamID);

		return url.toString();
	}
	
	
	
	public GetTeamByID(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			final long teamID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_TEAM_BY_ID, Team.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, teamID), false, retryThreshold);
	}
}
