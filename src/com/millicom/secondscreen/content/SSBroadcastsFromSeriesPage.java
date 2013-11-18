package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Link;

import android.util.Log;

public class SSBroadcastsFromSeriesPage extends SSPage{
	public static final String	TAG	= "SSBroadcastsFromSeriesPage";
	public String				mSeriesId, mPageUrl;

	// private constructor prevents instantiation from other classes
	private SSBroadcastsFromSeriesPage() {};
	
	private static class SSBroadcastsFromSeriesPageHolder{
		public static final SSBroadcastsFromSeriesPage  INSTANCE = new SSBroadcastsFromSeriesPage();
	}
	
	public static SSBroadcastsFromSeriesPage getInstance(){
		return SSBroadcastsFromSeriesPageHolder.INSTANCE;
	}

	public boolean getPage(String url, SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");
		// Remember the callback
		super.mPageCallback = pageCallback;
		mPageUrl = Consts.MILLICOM_SECONDSCREEN_SERIES + mSeriesId + Consts.MILLICOM_SECONDSCREEN_API_UPCOMING_BROADCASTS;
		
		Link startPageLink = new Link();
		startPageLink.setUrl(mPageUrl);

		super.getPage(startPageLink, pageCallback);
		return true;
	}

	public ArrayList<Broadcast> getSeriesUpcomingBroadcasts() {
		Log.d(TAG, "get series upcoming broadcasts");
		return super.getSeriesUpcomingBroadcasts();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseSeriesUpcomingBroadcasts(jsonArray);
			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleGetStartPageUriResult() {

		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mPageUrl, mPageCallback)) {

			Log.d(TAG, "Get start page uri or get start page failed");

			// If we have a callback
			if (mPageCallback != null) {

				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}
}
