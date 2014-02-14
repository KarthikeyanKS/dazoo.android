
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class GetTVChannelIdsDefault
	extends AsyncTaskWithRelativeURL<TVChannelId> 
{	
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;

	
	public GetTVChannelIdsDefault(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_DEFAULT, TVChannelId.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}