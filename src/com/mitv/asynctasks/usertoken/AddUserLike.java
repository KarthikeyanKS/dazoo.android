
package com.mitv.asynctasks.usertoken;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.UserLike;
import com.mitv.models.gson.serialization.UserLikeData;



public class AddUserLike 
	extends AsyncTaskWithUserToken<UserLike> 
{
	private static final String TAG = AddUserLike.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_LIKES;
	
	
	public AddUserLike(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener,
			UserLike userLike) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_ADD_LIKE, UserLike.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLikeData postData = new UserLikeData();
		postData.setLikeType(userLike.getLikeTypeForRequest());
		postData.setEntityId(userLike.getContentId());
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}	
}