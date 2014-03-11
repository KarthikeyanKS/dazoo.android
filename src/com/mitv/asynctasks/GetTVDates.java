
package com.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.TVDate;



public class GetTVDates 
	extends AsyncTaskWithRelativeURL<TVDate[]> 
{	
	private static final String TAG = GetTVDates.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_DATES;

	
	public GetTVDates(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.TV_DATE, TVDate[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			TVDate[] contentAsArray = (TVDate[]) requestResultObjectContent;
			
			ArrayList<TVDate> contentAsArrayList = new ArrayList<TVDate>(Arrays.asList(contentAsArray));
		
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}