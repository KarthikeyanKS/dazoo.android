
package com.mitv.asynctasks.mitvapi;



import java.util.ArrayList;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVTag;



public class GetTVTags 
	extends AsyncTaskBase<TVTag[]> 
{	
	private static final String TAG = GetTVTags.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_TAGS_PAGE;
	
	
	public GetTVTags(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int retryThreshold) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.TV_TAG, TVTag[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}


	
	@Override
	protected Void doInBackground(String... params) 
	{
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			@SuppressWarnings("unchecked")
			ArrayList<TVTag> contentAsArrayList = (ArrayList<TVTag>) requestResultObjectContent;
					
			TVTag allCategoriesTVTag = TVTag.getAllCategoriesTVTag();
			
			/* Add the "All Categories" tag to the first slot in the list of TVTags */
			contentAsArrayList.add(0, allCategoriesTVTag);
			
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