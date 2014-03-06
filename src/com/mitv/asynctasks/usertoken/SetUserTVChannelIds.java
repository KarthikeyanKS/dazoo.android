
package com.mitv.asynctasks.usertoken;



import java.util.List;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.DummyData;
import com.mitv.models.TVChannelId;



public class SetUserTVChannelIds 
	extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String TAG = SetUserTVChannelIds.class.getName();
	
	
	private static final String URL_SUFFIX = Constants.URL_MY_CHANNEL_IDS;
	

	
	public SetUserTVChannelIds(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			List<TVChannelId> channelIds) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_SET_CHANNELS, DummyData.class, HTTPRequestTypeEnum.HTTP_POST, URL_SUFFIX);
				
		this.bodyContentData = gson.toJson(channelIds);
		
		Log.v(TAG, "Gson data for request: " + bodyContentData);
	}
}