


package com.mitv.asynctasks.mitvapi.competitions;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.TeamDetails;



public class GetTeamDetails 
	extends AsyncTaskBase<TeamDetails>
{
	private static final String TAG = GetTeamDetails.class.getName();
	
	
	
	private static String buildURL(
			final String competitionID,
			final String teamID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		if(competitionID != null)
		{
			url.append(competitionID);
		}
		else
		{
			Log.w(TAG, "Competition ID is null");
		}
		
		url.append(Constants.URL_TEAMS);
		url.append(Constants.FORWARD_SLASH);
		
		if(teamID != null)
		{
			url.append(teamID);
		}
		else
		{
			Log.w(TAG, "Team ID is null");
		}
		
		
		return url.toString();
	}
	
	
	
	public GetTeamDetails(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final String competitionID,
			final String teamID,
			boolean isRetry)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_TEAM_DETAILS, TeamDetails.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, teamID), false, isRetry);
	}
}
