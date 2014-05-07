
package com.mitv.asynctasks.mitvapi.usertoken;



import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.objects.mitvapi.DummyData;
import com.mitv.models.objects.mitvapi.UserLike;



public class RemoveUserLike 
	extends AsyncTaskWithUserToken<DummyData> 
{	
	private UserLike userLike;
	
	
	
	private static String buildURL(UserLike userLike)
	{
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_LIKES);
		url.append(Constants.FORWARD_SLASH);
		url.append(userLike.getLikeTypeForRequest());
		url.append(Constants.FORWARD_SLASH);
		url.append(userLike.getContentId());
		
		return url.toString();
	}
	
	
	
	public RemoveUserLike(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			UserLike userLike)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, DummyData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL(userLike), false);
		
		this.userLike = userLike;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful())
		{
			requestResultObjectContent = userLike;
		}
		
		super.onPostExecute(result);
	}
}