
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.AppVersionData;
import com.mitv.Consts;



public class GetAppVersionData extends AsyncTaskWithRelativeURL<AppVersionData> 
{
	private static final String URL_SUFFIX = Consts.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.APP_VERSION, AppVersionData.class, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		/* Parse JSON data using GSON */
		return null;
	}

}