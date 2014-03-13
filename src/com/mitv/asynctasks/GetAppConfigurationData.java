
package com.mitv.asynctasks;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.AppConfiguration;



public class GetAppConfigurationData 
	extends AsyncTaskWithRelativeURL<AppConfiguration> 
{
	private static final String URL_SUFFIX = Constants.URL_CONFIGURATION;
	
	
	
	public GetAppConfigurationData(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.APP_CONFIGURATION, AppConfiguration.class, true, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}