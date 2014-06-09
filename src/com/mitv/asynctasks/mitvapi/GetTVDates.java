
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVDate;



public class GetTVDates 
	extends AsyncTaskBase<TVDate[]> 
{	
	private static final String TAG = GetTVDates.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_DATES;

	
	public GetTVDates(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.TV_DATE, TVDate[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		
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

		TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		
		super.onPostExecute(result);
		
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
	}
}