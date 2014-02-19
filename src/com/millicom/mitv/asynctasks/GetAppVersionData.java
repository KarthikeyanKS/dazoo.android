
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.AppVersionData;
import com.millicom.mitv.models.gson.AppVersion;
import com.mitv.Consts;



public class GetAppVersionData 
	extends AsyncTaskWithRelativeURL<AppVersion[]> 
{
	private static final String TAG = "GetAppVersionData";
	
	private static final String URL_SUFFIX = Consts.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_VERSION, AppVersion[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		if(requestResultObjectContent != null)
		{
			AppVersion[] appVersionDataRawList = (AppVersion[]) requestResultObjectContent;
			
			AppVersionData appVersionDataObject = new AppVersionData(appVersionDataRawList);
			
			requestResultObjectContent = appVersionDataObject;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		
		return null;
	}
}