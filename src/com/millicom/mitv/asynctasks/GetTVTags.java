
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldTVTag;



public class GetTVTags extends AsyncTaskWithRelativeURL<OldTVTag> 
{	
	private static final String URL_SUFFIX = Consts.URL_TAGS_PAGE;
	
	
	public GetTVTags(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_TAG, OldTVTag.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}