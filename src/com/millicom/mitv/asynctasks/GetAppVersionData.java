
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.AppVersion;
import com.millicom.mitv.models.gson.AppVersionJSON;
import com.mitv.Consts;



public class GetAppVersionData 
	extends AsyncTaskWithRelativeURL<AppVersionJSON[]> 
{
	private static final String TAG = GetAppVersionData.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_VERSION, AppVersionJSON[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		if(requestResultObjectContent != null)
		{
			AppVersionJSON[] appVersionDataRawList = (AppVersionJSON[]) requestResultObjectContent;
			
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