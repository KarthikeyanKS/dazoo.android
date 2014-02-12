package com.millicom.mitv.asynctasks;

import com.mitv.Consts;
import com.mitv.model.TVDate;

public class GetTVDates extends AsyncTaskBase<TVDate> {
	
	private static final String URL_SUFFIX = Consts.URL_DATES;
	
	public GetTVDates() {
		super(TVDate.class, URL_SUFFIX);
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
