
package com.mitv.asynctasks.other;



import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.RequestParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import android.os.AsyncTask;
import android.util.Log;



public class CompetitionDataPostProcessingTask 
	extends AsyncTask<String, Void, Void> 
{
	private static final String TAG = CompetitionDataPostProcessingTask.class.getName();
	
	
	private ContentCallbackListener contentCallbackListener;
	private ViewCallbackListener activityCallbackListener;
	private Object requestResultObjectContent;
	private RequestParameters requestParameters;
	
	
	
	public CompetitionDataPostProcessingTask(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
		this.requestParameters = new RequestParameters();
	}
	
	

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
	}



	@Override
	protected Void doInBackground(String... params) 
	{
		setCompetitionData();
		
		return null;
	}
	

	
	@Override
	protected void onPostExecute(Void result)
	{	
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallbackListener, RequestIdentifierEnum.TV_BROADCASTS_POUPULAR_PROCESSING, FetchRequestResultEnum.SUCCESS, requestResultObjectContent, requestParameters);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}
	
	
	
	private void setCompetitionData() 
	{
		ContentManager.sharedInstance().setSelectedCompetitionProcessedData();
	}
}
