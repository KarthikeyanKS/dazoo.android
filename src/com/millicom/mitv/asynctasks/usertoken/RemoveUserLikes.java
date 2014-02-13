package com.millicom.mitv.asynctasks.usertoken;

import com.millicom.mitv.asynctasks.AsyncTaskWithRelativeURL;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVLike;

public class RemoveUserLikes extends AsyncTaskWithUserToken<TVLike> 
{
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	
	public RemoveUserLikes(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			ContentTypeEnum likeType,
			String contentId) 
	{
		// TODO: Append something to url
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_REMOVE_LIKE, TVLike.class, URL_SUFFIX);
		
		// TODO: Complete this
		urlParameters.add(Consts.API_ENTITY_ID, contentId);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		return null;
	}

}