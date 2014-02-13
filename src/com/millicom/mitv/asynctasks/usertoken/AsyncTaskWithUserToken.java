package com.millicom.mitv.asynctasks.usertoken;

import com.millicom.mitv.asynctasks.AsyncTaskWithRelativeURL;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;


public class AsyncTaskWithUserToken<T> extends AsyncTaskWithRelativeURL<T> {
	
	public AsyncTaskWithUserToken(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener, 
			RequestIdentifierEnum requestIdentifier, 
			Class<T> clazz, 
			String urlSuffix) {
		super(contentCallbackListener, activityCallBackListener, requestIdentifier, clazz, urlSuffix);

	}

	@Override
	protected Void doInBackground(String... params) {

		//TODO use user token here somehow
		return super.doInBackground(params);
	}
	
	
	
}
