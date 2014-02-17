
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class GetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId> 
{
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;
	
	
	
	public GetUserTVChannelIds(
			final ContentCallbackListener contentCallbackListener,
			final ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL_IDS_USER, TVChannelId.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}