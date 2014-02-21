
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.AppConfigurationJSON;
import com.mitv.Consts;



public class GetAppConfigurationData extends AsyncTaskWithRelativeURL<AppConfigurationJSON> 
{
	private static final String URL_SUFFIX = Consts.URL_CONFIGURATION;
	
	
	
	public GetAppConfigurationData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_CONFIGURATION, AppConfigurationJSON.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}