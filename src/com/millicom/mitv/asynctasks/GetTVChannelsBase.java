package com.millicom.mitv.asynctasks;

import com.mitv.Consts;
import com.mitv.model.TVChannel;
import com.mitv.model.TVTag;

public class GetTVChannelsBase extends AsyncTaskBase<TVChannel> {
	
	public GetTVChannelsBase(String channelURLSuffix) {
		super(TVChannel.class, channelURLSuffix);
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
