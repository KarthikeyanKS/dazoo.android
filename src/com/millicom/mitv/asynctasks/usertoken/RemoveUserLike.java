
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;



public class RemoveUserLike 
	extends AsyncTaskWithUserToken<DummyData> 
{	
	private static String buildURL(
			LikeTypeRequestEnum likeType,
			String contentId)
	{
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_LIKES);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(likeType);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(contentId);
		
		return url.toString();
	}
	
	
	
	public RemoveUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			LikeTypeRequestEnum likeType,
			String contentId)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, DummyData.class, HTTPRequestTypeEnum.HTTP_DELETE, buildURL(likeType, contentId));
	}
}