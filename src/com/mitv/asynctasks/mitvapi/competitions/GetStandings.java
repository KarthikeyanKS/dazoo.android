


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



public class GetStandings 
	extends AsyncTaskBase<Standings[]>
{
	private static final String TAG = GetStandings.class.getName();
	
	
	private static String buildURL(final String phaseID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_PHASES_FULL);
		url.append(Constants.FORWARD_SLASH);
		
		if(phaseID != null)
		{
			url.append(phaseID);
		}
		else
		{
			Log.w(TAG, "Competition ID is null");
		}
		
		url.append(Constants.URL_STANDINGS);

		return url.toString();
	}
	
	
	public GetStandings(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final String phaseID)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_STANDINGS_BY_PHASE_ID, Standings[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(phaseID));
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
