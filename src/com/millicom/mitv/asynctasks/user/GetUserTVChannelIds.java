package com.millicom.mitv.asynctasks.user;

import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;

public class GetUserTVChannelIds extends AsyncTaskWithUserToken<TVChannelId> {
	
	private static final String URL_SUFFIX = Consts.URL_MY_CHANNEL_IDS;
	
	public GetUserTVChannelIds() {
		super(TVChannelId.class, URL_SUFFIX);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		/* Parse JSON data using GSON */
	}
}
