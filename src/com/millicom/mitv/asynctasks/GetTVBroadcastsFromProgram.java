
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.Consts;



public class GetTVBroadcastsFromProgram 
	extends AsyncTaskWithRelativeURL<TVBroadcastWithChannelInfo>
{
	private static String buildURL(String tvProgramId)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_PROGRAMS);
		url.append(tvProgramId);
		url.append(Consts.API_BROADCASTS);
		
		return url.toString();
	}
	
	
	
	public GetTVBroadcastsFromProgram(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvProgramId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_PROGRAMS, TVBroadcastWithChannelInfo.class, HTTPRequestTypeEnum.HTTP_GET, buildURL(tvProgramId));
	}
}