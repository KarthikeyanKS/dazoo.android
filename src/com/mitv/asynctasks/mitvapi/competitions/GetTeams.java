


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class GetTeams 
	extends AsyncTaskBase<Team[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetTeams.class.getName();
	
	
	
	private static String buildURL(final long competitionID)
	{
		StringBuilder url = new StringBuilder();
		
		url.append(Constants.URL_COMPETITIONS_FULL)
		.append(Constants.FORWARD_SLASH)
		.append(competitionID)
		.append(Constants.URL_TEAMS);

		return url.toString();
	}
	
	
	
	public GetTeams(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_TEAMS, Team[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID), false, retryThreshold);
	}
}
