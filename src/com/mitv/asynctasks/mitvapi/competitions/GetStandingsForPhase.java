


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
import com.mitv.models.objects.mitvapi.competitions.Standings;



public class GetStandingsForPhase 
	extends AsyncTaskBase<Standings[]>
{
	private static final String TAG = GetStandingsForPhase.class.getName();
	
	
	private static String buildURL(final long phaseID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_PHASES_FULL);
		url.append(Constants.FORWARD_SLASH);
		url.append(phaseID);
		url.append(Constants.URL_STANDINGS);

		return url.toString();
	}
	
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean multiple)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(multiple)
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITION_STANDINGS_BY_PHASE_ID;
		}
		
		return requestIdentifier;
	}
	
	
	
	
	public GetStandingsForPhase(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long phaseID,
			final boolean multiple)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(multiple), Standings[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(phaseID), false);
		
		this.requestParameters.add(Constants.REQUEST_DATA_COMPETITION_PHASE_ID_KEY, phaseID);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			Standings[] contentAsArray = (Standings[]) requestResultObjectContent;
			
			ArrayList<Standings> contentAsArrayList = new ArrayList<Standings>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}
