
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.asynctasks.builders.RemoveLikesBuilder;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;



public class RemoveUserLikes 
	extends AsyncTaskWithUserToken<DummyData> 
{	
	
	public static RemoveUserLikes newRemoveUserLikesTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId)
	{
		RemoveLikesBuilder removeLikesBuilder = new RemoveLikesBuilder();
		removeLikesBuilder.setContentId(contentId);
		removeLikesBuilder.setLikeType(likeType);
		
		RemoveUserLikes removeUserLikes = removeLikesBuilder.build(contentCallbackListener, activityCallBackListener, likeType, contentId);
		
		return removeUserLikes;
	}
	
	
	
	public RemoveUserLikes(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, DummyData.class, HTTPRequestTypeEnum.HTTP_DELETE, url);
	}
}