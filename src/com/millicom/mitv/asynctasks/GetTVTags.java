
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVTag;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



public class GetTVTags 
	extends AsyncTaskWithRelativeURL<TVTag[]> 
{	
	private static final String TAG = GetTVTags.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_TAGS_PAGE;
	
	
	public GetTVTags(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_TAG, TVTag[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}


	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);

		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			TVTag[] contentAsArray = (TVTag[]) requestResultObjectContent;
			
			ArrayList<TVTag> contentAsArrayList = new ArrayList<TVTag>(Arrays.asList(contentAsArray));
			
			/* Manually add the tag for "ALL CATEGORIES" */
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
			String allCategoriesTag = context.getResources().getString(R.string.all_categories_name);
			
			TVTag allCategoriesTVTag = new TVTag(allCategoriesTag, allCategoriesTag);
			
			/* Add the "All Categories" tag to the first slot in the list of TVTags */
			contentAsArrayList.add(0, allCategoriesTVTag);
			
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
}