
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.utilities.NetworkUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



public class CheckNetworkConnectivity 
	extends AsyncTask<String, Void, Void> 
{
	private static final String TAG = CheckNetworkConnectivity.class.getName();
	
	private ContentCallbackListener contentCallbackListener;
	private ActivityCallbackListener activityCallBackListener;
	private RequestIdentifierEnum requestIdentifier;
	
	private Context context;
	private Boolean isConnectedAndHostIsReachable;
	
	
	
	public CheckNetworkConnectivity(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			RequestIdentifierEnum requestIdentifier,
			final Context context)
	{
		this.contentCallbackListener = contentCallbackListener;
		this.activityCallBackListener = activityCallBackListener;
		this.requestIdentifier = requestIdentifier;
		
		this.context = context;
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
				contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, FetchRequestResultEnum.INTERNET_CONNECTION_AVAILABLE, isConnectedAndHostIsReachable);
			}
			else
			{
				contentCallbackListener.onResult(activityCallBackListener, requestIdentifier, FetchRequestResultEnum.INTERNET_CONNECTION_NOT_AVAILABLE, isConnectedAndHostIsReachable);
			}
		}
		else
		{
			Log.w(TAG, "Content callback listener is null. No result action will be performed.");
		}
	}
}
