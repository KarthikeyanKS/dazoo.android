package com.millicom.mitv.asynctasks;

import com.mitv.Consts;
import com.mitv.model.TVTag;

public class GetTVTags extends AsyncTaskBase<TVTag> {
	
	private static final String URL_SUFFIX = Consts.URL_TAGS_PAGE;
	
	public GetTVTags() {
		super(TVTag.class, URL_SUFFIX);
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
