
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.model.OldTVChannel;



public class GetTVChannels 
	extends AsyncTaskWithRelativeURL<OldTVChannel>
{	
	public GetTVChannels(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String channelURLSuffix)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL, OldTVChannel.class, HTTPRequestTypeEnum.HTTP_GET, channelURLSuffix);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params)
	{
		super.doInBackground(params);
		
		// TODO; Execute the task itself
		
		return null;
	}
}