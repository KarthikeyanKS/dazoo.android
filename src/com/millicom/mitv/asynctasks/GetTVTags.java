
package com.millicom.mitv.asynctasks;



import java.util.ArrayList;
import java.util.Arrays;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;



public class GetTVTags extends AsyncTaskWithRelativeURL<TVTag[]> 
{	
	private static final String URL_SUFFIX = Consts.URL_TAGS_PAGE;
	
	
	public GetTVTags(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_TAG, TVTag[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}


	@Override
	protected Void doInBackground(String... params) {
		super.doInBackground(params);

		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		TVTag[] contentAsArray = (TVTag[]) requestResultObjectContent;
		ArrayList<TVTag> contentAsArrayList = new ArrayList<TVTag>(Arrays.asList(contentAsArray));
		requestResultObjectContent = contentAsArrayList;

		return null;
	}
}