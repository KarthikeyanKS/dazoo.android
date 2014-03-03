
package com.millicom.mitv.asynctasks.usertoken;



import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
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
			UserLike userLike) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ADD_LIKE, UserLike.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserLikeData postData = new UserLikeData();
		postData.setLikeType(userLike.getLikeTypeForRequest());
		postData.setEntityId(userLike.getContentId());
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}


	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		Log.d(TAG, "Should we cast return value here?");
		
		return null;
	}
	
	
}