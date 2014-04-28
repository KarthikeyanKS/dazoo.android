


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
	
	
	
	public GetCompetitions(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.COMPETITIONS_ALL, Competition[].class, HTTPRequestTypeEnum.HTTP_GET, url);
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
