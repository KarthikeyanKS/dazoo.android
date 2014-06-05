


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
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class GetSquadForTeam 
	extends AsyncTaskBase<TeamSquad[]>
{
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
			boolean isRetry)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_TEAM_SQUAD, TeamSquad[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(teamID), false, isRetry);
	
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_TEAM_ID_KEY, teamID);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			TeamSquad[] contentAsArray = (TeamSquad[]) requestResultObjectContent;
			
			ArrayList<TeamSquad> contentAsArrayList = new ArrayList<TeamSquad>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}
