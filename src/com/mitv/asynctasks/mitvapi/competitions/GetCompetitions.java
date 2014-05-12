


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
import com.mitv.models.objects.mitvapi.competitions.Competition;



public class GetCompetitions 
	extends AsyncTaskBase<Competition[]>
{
	private static final String TAG = GetCompetitions.class.getName();
	
	
	private static String url = Constants.URL_COMPETITIONS;
	
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITIONS_ALL_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITIONS_ALL_INITIAL;
		}
		
		return requestIdentifier;
	}
	
	
	
	public GetCompetitions(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean standalone)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), Competition[].class, HTTPRequestTypeEnum.HTTP_GET, url, true);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			Competition[] contentAsArray = (Competition[]) requestResultObjectContent;
			
			ArrayList<Competition> contentAsArrayList = new ArrayList<Competition>(Arrays.asList(contentAsArray));
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}
