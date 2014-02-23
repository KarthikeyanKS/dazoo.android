
package com.millicom.mitv.asynctasks.usertoken;



import java.util.List;

import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class SetUserTVChannelIds 
	extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String TAG = SetUserTVChannelIds.class.getName();
	
	
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;
	

	
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