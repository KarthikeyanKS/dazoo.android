
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
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_LIKES);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(likeType);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(contentId);
		
		RemoveUserLike removeUserLike = new RemoveUserLike(contentCallbackListener, activityCallBackListener, url.toString());
		
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