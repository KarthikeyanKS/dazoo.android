package com.millicom.mitv.asynctasks;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVDate;

public class GetTVDates extends AsyncTaskWithRelativeURL<TVDate> {
	
	private static final String URL_SUFFIX = Consts.URL_DATES;
	
	public GetTVDates(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) {
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_DATE, TVDate.class, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

}
