


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
import com.mitv.models.objects.mitvapi.competitions.Phase;



public class GetPhases 
	extends AsyncTaskBase<Phase[]>
{
	private static final String TAG = GetPhases.class.getName();
	
	
	
	private static String buildURL(final long competitionID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS);
		url.append(Constants.FORWARD_SLASH);
		
		url.append(competitionID);
		
		url.append(Constants.URL_PHASES);

		return url.toString();
	}
	
	
	
	public GetPhases(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final long competitionID)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITION_PHASES, Phase[].class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID), false);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			Phase[] contentAsArray = (Phase[]) requestResultObjectContent;
			
			ArrayList<Phase> contentAsArrayList = new ArrayList<Phase>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}
