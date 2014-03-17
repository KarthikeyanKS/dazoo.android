
package com.mitv.asynctasks;



import android.os.AsyncTask;
import android.util.Log;

import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.utilities.NetworkUtils;



public class CheckNetworkConnectivity 
	extends AsyncTask<String, Void, Void> 
{
	private static final String TAG = CheckNetworkConnectivity.class.getName();
	
	private ContentCallbackListener contentCallbackListener;
	private ViewCallbackListener activityCallbackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	private Boolean isConnectedAndHostIsReachable;
	
	
	
	public CheckNetworkConnectivity(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			RequestIdentifierEnum requestIdentifier)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallbackListener = activityCallbackListener;
		this.requestIdentifier = requestIdentifier;
		
		this.isConnectedAndHostIsReachable = false;
	}
	
	

	@Override
	protected Void doInBackground(String... params) 
	{
		isConnectedAndHostIsReachable = NetworkUtils.isConnectedAndHostIsReachable();
		
		return null;
	}

	
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(contentCallbackListener != null)
		{
			if(isConnectedAndHostIsReachable)
			{
				contentCallbackListener.onResult(activityCallbackListener, requestIdentifier, FetchRequestResultEnum.INTERNET_CONNECTION_AVAILABLE, isConnectedAndHostIsReachable);
			}
			else
			{
				contentCallbackListener.onResult(activityCallbackListener, requestIdentifier, FetchRequestResultEnum.INTERNET_CONNECTION_NOT_AVAILABLE, isConnectedAndHostIsReachable);
			}
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}
}
