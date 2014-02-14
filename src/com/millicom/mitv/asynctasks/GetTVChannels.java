package com.millicom.mitv.asynctasks;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.OldTVChannel;
import com.mitv.model.OldTVTag;

public class GetTVChannels extends AsyncTaskWithRelativeURL<OldTVChannel> {
	
	public GetTVChannels(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String channelURLSuffix) {
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL, OldTVChannel.class, channelURLSuffix);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

}
