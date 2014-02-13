package com.millicom.mitv.asynctasks;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVChannel;
import com.mitv.model.TVTag;

public class GetTVChannels extends AsyncTaskWithRelativeURL<TVChannel> {
	
	public GetTVChannels(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String channelURLSuffix) {
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_CHANNEL, TVChannel.class, channelURLSuffix);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

}
