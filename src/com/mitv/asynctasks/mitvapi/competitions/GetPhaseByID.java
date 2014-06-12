
package com.mitv.asynctasks.mitvapi.competitions;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Phase;



public class GetPhaseByID 
	extends AsyncTaskBase<Phase>
{
	private static final String TAG = GetPhaseByID.class.getName();
	
	
	
	private static String buildURL(
			final long competitionID,
			final String phaseID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(competitionID);
		
		url.append(Constants.URL_PHASES);
		url.append(Constants.FORWARD_SLASH);
		
		if(phaseID != null)
		{
			url.append(phaseID);
		}
		else
		{
			Log.w(TAG, "Phase ID is null");
		}
		
		
		return url.toString();
	}
	
	
	
	public GetPhaseByID(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID,
			final String phaseID,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_PHASE_BY_ID, Phase.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID, phaseID), false, retryThreshold);
	}
}
