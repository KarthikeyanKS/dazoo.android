
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVBroadcastWithProgramAndChannelInfo;
import com.mitv.Consts;



public class GetTVBroadcastsPopular 
	extends AsyncTaskWithRelativeURL<TVBroadcastWithProgramAndChannelInfo[]> 
{	
	private static final String URL_SUFFIX = Consts.URL_POPULAR;

	
	public GetTVBroadcastsPopular(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.POPULAR_ITEMS, TVBroadcastWithProgramAndChannelInfo[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}