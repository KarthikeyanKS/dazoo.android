
package com.mitv.asynctasks;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.AppConfiguration;



public class GetAppConfigurationData 
	extends AsyncTaskWithRelativeURL<AppConfiguration> 
{
	private static final String URL_SUFFIX = Constants.URL_CONFIGURATION;
	
	
	
	public GetAppConfigurationData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_CONFIGURATION, AppConfiguration.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}