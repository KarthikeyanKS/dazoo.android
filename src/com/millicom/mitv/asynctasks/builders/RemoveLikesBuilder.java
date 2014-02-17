
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.asynctasks.usertoken.RemoveUserLikes;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class RemoveLikesBuilder
{
	private ContentTypeEnum likeType;
	private String contentId;
	
	
	
	public RemoveUserLikes build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_LIKES);
		sb.append(likeType);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(contentId);
		
		
		RemoveUserLikes removeUserLikes = new RemoveUserLikes(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return removeUserLikes;
	}



	public ContentTypeEnum getLikeType() {
		return likeType;
	}



	public void setLikeType(ContentTypeEnum likeType) {
		this.likeType = likeType;
	}



	public String getContentId() {
		return contentId;
	}



	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
}