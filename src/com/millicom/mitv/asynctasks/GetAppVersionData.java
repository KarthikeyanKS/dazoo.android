
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.AppVersion;
import com.mitv.Consts;



public class GetAppVersionData 
	extends AsyncTaskWithRelativeURL<AppVersion[]> 
{
	private static final String URL_SUFFIX = Consts.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_VERSION, AppVersion[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}