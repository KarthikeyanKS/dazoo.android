
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldTVChannel;



public class GetTVChannelsAll 
	extends AsyncTaskWithRelativeURL<OldTVChannel>
{	
	private static final String URL_SUFFIX = Consts.URL_CHANNELS_ALL;
	
	
	
	public GetTVChannelsAll(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL, OldTVChannel.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
	}
}