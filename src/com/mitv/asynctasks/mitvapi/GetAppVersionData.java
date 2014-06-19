
package com.mitv.asynctasks.mitvapi;



import android.util.Log;
import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.AppVersion;
import com.mitv.models.objects.mitvapi.AppVersionElement;



public class GetAppVersionData 
	extends AsyncTaskBase<AppVersionElement[]> 
{
	private static final String TAG = GetAppVersionData.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_API_VERSION;
	
	
	
	public GetAppVersionData(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.APP_VERSION, AppVersionElement[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, retryThreshold);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		}
		
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

		if (Constants.USE_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		}
		
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{
		if (Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		}
			
		super.onPostExecute(result);

		if (Constants.USE_DETAILED_INITIAL_METRICS_ANALTYTICS)
		{
			TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
		}
	}
}