
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVDate;
import com.mitv.Consts;



public class GetTVDates 
	extends AsyncTaskWithRelativeURL<TVDate[]> 
{	
	private static final String TAG = GetTVDates.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_DATES;

	
	public GetTVDates(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_DATE, TVDate[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
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