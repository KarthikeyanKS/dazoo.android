
package com.mitv.asynctasks.mitvapi;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.AppConfiguration;



public class GetAppConfigurationData 
	extends AsyncTaskBase<AppConfiguration> 
{
	private static final String URL_SUFFIX = Constants.URL_CONFIGURATION;
	
	
	
	public GetAppConfigurationData(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean isRetry)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.APP_CONFIGURATION, AppConfiguration.class, true, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX, Constants.USE_INITIAL_METRICS_ANALTYTICS, isRetry);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundStart(this.getClass().getSimpleName());
		
		super.doInBackground(params);
		
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskBackgroundEnd(this.getClass().getSimpleName());
		
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionStart(this.getClass().getSimpleName());
		
		super.onPostExecute(result);
		
		TrackingManager.sharedInstance().sendTestMeasureAsycTaskPostExecutionEnd(this.getClass().getSimpleName());
	}
}