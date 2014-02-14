
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldTVLike;



public class AddUserLikes 
	extends AsyncTaskWithUserToken<OldTVLike> 
{
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	public AddUserLikes(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId) 
	{
		// TODO: Append something to url
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_ADD_LIKE, OldTVLike.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		// TODO: Complete this
		urlParameters.add(Consts.API_ENTITY_ID, contentId);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}