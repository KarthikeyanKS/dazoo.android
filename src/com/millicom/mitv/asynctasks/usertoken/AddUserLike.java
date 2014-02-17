
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.serialization.UserLikeData;
import com.mitv.Consts;
import com.mitv.model.OldTVLike;



public class AddUserLike 
	extends AsyncTaskWithUserToken<OldTVLike> 
{
	private static final String TAG = "AddUserLike";
	
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	public AddUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ADD_LIKE, OldTVLike.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLikeData userLikeData = new UserLikeData();
		userLikeData.setLikeType(likeType);
		userLikeData.setEntityId(contentId);
		
		this.bodyContentData = gson.toJson(userLikeData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}