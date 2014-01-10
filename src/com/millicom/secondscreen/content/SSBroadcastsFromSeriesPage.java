package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;

public class SSBroadcastsFromSeriesPage extends SSPage{
	public static final String	TAG	= "SSBroadcastsFromSeriesPage";

	private static SSBroadcastsFromSeriesPage	sInstance;
	public static SSBroadcastsFromSeriesPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSBroadcastsFromSeriesPage();
		}
		return sInstance;
	}
	
	public boolean getPage(String seriesId, SSPageCallback aSSPageCallback) {
		String url = Consts.MILLICOM_SECONDSCREEN_SERIES + seriesId + Consts.MILLICOM_SECONDSCREEN_API_UPCOMING_BROADCASTS;
		return super.getPage(url, aSSPageCallback);
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

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}
}
