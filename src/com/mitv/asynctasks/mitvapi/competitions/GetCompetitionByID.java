
package com.mitv.asynctasks.mitvapi.competitions;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Competition;



public class GetCompetitionByID 
	extends AsyncTaskBase<Competition>
{
	private static final String TAG = GetCompetitionByID.class.getName();
	
	
	
	private static String buildURL(final String competitionID)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_COMPETITIONS);
		url.append(Constants.FORWARD_SLASH);
		
		if(competitionID != null)
		{
			url.append(competitionID);
		}
		else
		{
			Log.w(TAG, "Competition ID is null");
		}
		
		return url.toString();
	}
	
	
	public GetCompetitionByID(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final String competitionID)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITIONS_ALL, Competition.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(competitionID), false);
	}
}
