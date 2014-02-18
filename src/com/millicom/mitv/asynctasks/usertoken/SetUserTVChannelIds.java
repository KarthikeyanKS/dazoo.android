
package com.millicom.mitv.asynctasks.usertoken;



import java.util.List;
import android.util.Log;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.serialization.UserTVChannelIdsData;
import com.mitv.Consts;



public class SetUserTVChannelIds 
	extends AsyncTaskWithUserToken<TVChannelId> 
{
	private static final String TAG = "SetUserTVChannelIds";
	
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;
	

	
	public SetUserTVChannelIds(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			List<TVChannelId> channelIds) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SET_CHANNELS, TVChannelId.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
		
		UserTVChannelIdsData postData = new UserTVChannelIdsData();
		postData.setChannelIds(channelIds);
		
		this.bodyContentData = gson.toJson(postData);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}