
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.asynctasks.usertoken.RemoveUserLike;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class RemoveLikesBuilder
{
	private LikeTypeRequestEnum likeType;
	private String contentId;
	
	
	
	public RemoveUserLike build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			LikeTypeRequestEnum likeType,
			String contentId)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_LIKES);
		sb.append(likeType);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(contentId);
		
		RemoveUserLike removeUserLike = new RemoveUserLike(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return removeUserLike;
	}



	public LikeTypeRequestEnum getLikeType() {
		return likeType;
	}



	public void setLikeType(LikeTypeRequestEnum likeType) {
		this.likeType = likeType;
	}



	public String getContentId() {
		return contentId;
	}



	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
}