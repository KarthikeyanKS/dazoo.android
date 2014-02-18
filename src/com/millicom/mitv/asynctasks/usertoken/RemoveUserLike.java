
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.asynctasks.builders.RemoveLikesBuilder;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;



public class RemoveUserLike 
	extends AsyncTaskWithUserToken<DummyData> 
{	
	
	public static RemoveUserLike newRemoveUserLikesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId)
	{
		RemoveLikesBuilder removeLikesBuilder = new RemoveLikesBuilder();
		removeLikesBuilder.setContentId(contentId);
		removeLikesBuilder.setLikeType(likeType);
		
		RemoveUserLike removeUserLike = removeLikesBuilder.build(contentCallbackListener, activityCallBackListener, likeType, contentId);
		
		return removeUserLike;
	}
	
	
	
	public RemoveUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, DummyData.class, HTTPRequestTypeEnum.HTTP_DELETE, url);
	}
}