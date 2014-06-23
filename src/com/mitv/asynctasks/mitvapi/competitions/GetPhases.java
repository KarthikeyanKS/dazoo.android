


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Phase;



public class GetPhases 
	extends AsyncTaskBase<Phase[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetPhases.class.getName();
	
	
	
	private static String buildURL(final long competitionID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(competitionID);
		
		url.append(Constants.URL_PHASES);

		return url.toString();
	}
	
	
	
	public GetPhases(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_PHASES, Phase[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID), false, retryThreshold);
	}
}
