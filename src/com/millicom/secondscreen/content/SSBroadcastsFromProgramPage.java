package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Link;

public class SSBroadcastsFromProgramPage extends SSPage{
	public static final String	TAG	= "SSBroadcastsFromSeriesPage";
	public String				mProgramId, mPageUrl;

	// private constructor prevents instantiation from other classes
	private SSBroadcastsFromProgramPage() {};
	
	private static class SSBroadcastsFromProgramPageHolder{
		public static final SSBroadcastsFromProgramPage  INSTANCE = new SSBroadcastsFromProgramPage();
	}
	
	public static SSBroadcastsFromProgramPage getInstance(){
		return SSBroadcastsFromProgramPageHolder.INSTANCE;
	}

	public boolean getPage(String url, SSPageCallback pageCallback) {
		// Remember the callback
		super.mPageCallback = pageCallback;
		mPageUrl = Consts.MILLICOM_SECONDSCREEN_PROGRAMS + mProgramId + Consts.MILLICOM_SECONDSCREEN_API_BROADCASTS;
		Log.d(TAG, "getPage");
		
		Link startPageLink = new Link();
		startPageLink.setUrl(mPageUrl);

		super.getPage(startPageLink, pageCallback);
		return true;
	}

	public ArrayList<Broadcast> getProgramBroadcasts() {
		Log.d(TAG, "get program broadcasts");
		return super.getProgramBroadcasts();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseProgramBroadcasts(jsonArray);
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