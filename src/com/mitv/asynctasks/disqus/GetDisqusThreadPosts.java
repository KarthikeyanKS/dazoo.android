
package com.mitv.asynctasks.disqus;



import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.disqus.DisqusBaseResponse;
import com.mitv.models.objects.disqus.DisqusComments;
import com.mitv.models.objects.disqus.DisqusMessageResponse;



public class GetDisqusThreadPosts
	extends AsyncTaskBase<DisqusBaseResponse>
{
	private static final String TAG = GetDisqusThreadPosts.class.getName();

	
	
	private static String buildURL()
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.DISQUS_API_URL);
		url.append("/");
		url.append(Constants.DISQUS_API_VERSION);
		url.append(Constants.DISQUS_API_THREAD_POSTS);
		url.append(".");
		url.append(Constants.DISQUS_API_REQUESTS_OUTPUT_TYPE);
		
		return url.toString();
	}
	
	
	
	public GetDisqusThreadPosts(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			String contentID) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.DISQUS_THREAD_COMMENTS, DisqusBaseResponse.class, false, HTTPRequestTypeEnum.HTTP_GET, buildURL(), false);
		
		this.urlParameters.add(Constants.DISQUS_API_THREAD_IDENT_PARAMETER, contentID);
		this.urlParameters.add(Constants.DISQUS_API_FORUM_PARAMETER, Constants.DISQUS_API_FORUM_NAME);
		this.urlParameters.add(Constants.DISQUS_API_LIMIT_PARAMETER, Constants.DISQUS_API_LIMIT_VALUE);
		this.urlParameters.add(Constants.DISQUS_API_FORUM_SECRET_KEY_PARAMETER, Constants.DISQUS_API_FORUM_SECRET_KEY);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
			DisqusBaseResponse disqusJSON = (DisqusBaseResponse) requestResultObjectContent;
			
			String responseString = response.getResponseString();
			
			if(disqusJSON.wasSuccessful())
			{	
				try
				{
					requestResultObjectContent = gson.fromJson(responseString, DisqusComments.class);
				}
				catch(JsonSyntaxException jsex)
				{
					Log.e(TAG, jsex.getMessage(), jsex);

					requestResultStatus = FetchRequestResultEnum.JSON_PARSING_ERROR;
					requestResultObjectContent = null;
				}
			}
			else
			{
				try
				{
					requestResultObjectContent = gson.fromJson(responseString, DisqusMessageResponse.class);
				}
				catch(JsonSyntaxException jsex)
				{
					Log.e(TAG, jsex.getMessage(), jsex);

					requestResultStatus = FetchRequestResultEnum.JSON_PARSING_ERROR;
					requestResultObjectContent = null;
				}
			}
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		 
		return null;
	}
}
