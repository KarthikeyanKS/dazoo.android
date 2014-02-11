
package com.millicom.asynctasks;



import android.os.AsyncTask;
import com.millicom.interfaces.ApiVersionCallbackInterface;
import com.mitv.content.SSApiVersionPage;
import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;



public class ApiVersionTask 
	extends AsyncTask<String, Void, Void> 
{
	private ApiVersionCallbackInterface callback = null;

	
	
	public ApiVersionTask(ApiVersionCallbackInterface callback) 
	{
		this.callback = callback;
	}

	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		SSApiVersionPage.getInstance().getPage(new SSPageCallback() 
		{
			@Override
			public void onGetPageResult(SSPageGetResult pageGetResult) 
			{
				if (callback != null) 
				{
					callback.onApiVersionResult();
				}
			}
		});

		return null;
	}
}
