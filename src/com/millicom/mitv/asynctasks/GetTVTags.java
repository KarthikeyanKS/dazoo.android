package com.millicom.mitv.asynctasks;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;
import com.mitv.model.TVTag;

public class GetTVTags extends AsyncTaskWithRelativeURL<TVTag> {
	
	private static final String URL_SUFFIX = Consts.URL_TAGS_PAGE;
	
	public GetTVTags(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener) {
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.TV_TAG, TVTag.class, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

}
