
package com.mitv.asynctasks;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.AppVersion;
import com.mitv.models.AppVersionElement;
import com.mitv.models.gson.AppVersionJSON;



public class GetAppVersionData 
	extends AsyncTaskWithRelativeURL<AppVersionJSON[]> 
{
	private static final String TAG = GetAppVersionData.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.APP_VERSION, AppVersionJSON[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			AppVersionElement[] appVersionDataRawList = (AppVersionElement[]) requestResultObjectContent;
			
			AppVersion appVersionDataObject = new AppVersion(appVersionDataRawList);
			
			requestResultObjectContent = appVersionDataObject;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}