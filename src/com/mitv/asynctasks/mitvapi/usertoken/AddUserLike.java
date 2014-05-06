
package com.mitv.asynctasks.mitvapi.usertoken;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.gson.serialization.UserLikeData;
import com.mitv.models.objects.mitvapi.UserLike;



public class AddUserLike 
	extends AsyncTaskWithUserToken<UserLike> 
{
	private static final String TAG = AddUserLike.class.getName();
	
	
	private static final String URL_SUFFIX = Constants.URL_LIKES;
	
	
	
	public AddUserLike(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final UserLike userLike) 
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.USER_ADD_LIKE, UserLike.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
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
		
		Log.d(TAG, "addUserLike do in background");
		
		return null;
	}
}