
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.gson.serialization.UserLikeData;
import com.mitv.Consts;



public class AddUserLike 
	extends AsyncTaskWithUserToken<UserLike> 
{
	private static final String TAG = AddUserLike.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	public AddUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			LikeTypeRequestEnum likeType,
			String contentId) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ADD_LIKE, UserLike.class, true, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLikeData postData = new UserLikeData();
		postData.setLikeType(likeType);
		postData.setEntityId(contentId);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}