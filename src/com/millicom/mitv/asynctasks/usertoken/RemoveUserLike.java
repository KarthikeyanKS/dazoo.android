
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;



public class RemoveUserLike 
	extends AsyncTaskWithUserToken<DummyData> 
{	
	private UserLike userLike;
	
	
	
	private static String buildURL(UserLike userLike)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_LIKES);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(userLike.getLikeTypeForRequest());
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(userLike.getContentId());
		
		return url.toString();
	}
	
	
	
	public RemoveUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			UserLike userLike)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, DummyData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL(userLike));
		
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