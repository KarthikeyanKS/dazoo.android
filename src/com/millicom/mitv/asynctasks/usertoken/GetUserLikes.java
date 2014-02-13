
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVLike;



public class GetUserLikes extends AsyncTaskWithUserToken<TVLike> 
{
	private static final String URL_SUFFIX = Consts.URL_LIKES;
	
	
	
	public GetUserLikes(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_LIKES, TVLike.class, URL_SUFFIX);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		/* Parse JSON data using GSON */
		
		return null;
	}
}
