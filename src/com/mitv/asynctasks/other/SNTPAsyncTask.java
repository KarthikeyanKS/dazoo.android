
package com.mitv.asynctasks.other;



import java.util.Calendar;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.SNTPClient;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;



public class SNTPAsyncTask
	extends AsyncTask<String, Void, Void>
{
	private static final String TAG = SNTPAsyncTask.class.getName();
	
	
	
	private ContentCallbackListener contentCallbackListener;
	private ViewCallbackListener activityCallbackListener;
	
	protected Object requestResultObjectContent;
	
	
	
	public SNTPAsyncTask(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
	}


	
	@Override
	protected Void doInBackground(String... params) 
	{
        SNTPClient client = new SNTPClient();
        
        boolean success = client.requestTime(Constants.HOST_FOR_NTP_CHECK, Constants.HOST_TIMEOUT_IN_MILISECONDS_FOR_NTP_CHECK);
        
        Calendar calendarInstance;
        
        if(success) 
        {
        	long nowAsLong = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
        	
        	calendarInstance = Calendar.getInstance();
        	calendarInstance.setTimeInMillis(nowAsLong);
        	
        	StringBuilder sb = new StringBuilder();
        	sb.append("An updated date/time was obtained from SNPT server ");
        	sb.append(Constants.HOST_FOR_NTP_CHECK);
        	
        	Log.d(TAG, sb.toString());
        }
        else
        {
        	calendarInstance = null;
        	
        	StringBuilder sb = new StringBuilder();
        	sb.append("Failed to fetch updated date/time SNPT server ");
        	sb.append(Constants.HOST_FOR_NTP_CHECK);
        	
        	Log.w(TAG, sb.toString());
        }
		
        requestResultObjectContent = calendarInstance;
        
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(contentCallbackListener != null)
		{
			contentCallbackListener.onResult(activityCallbackListener, RequestIdentifierEnum.SNTP_CALL, FetchRequestResultEnum.SUCCESS, requestResultObjectContent);
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}
}