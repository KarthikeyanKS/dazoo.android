
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId> 
{
	
	
	public GetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ActivityCallbackListener activityCallBackListener,
			final String urlSuffix) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_USER, TVChannelId.class, HTTPRequestTypeEnum.HTTP_GET, urlSuffix);
	}
}
