package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.OldBroadcast;

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
		String url = Consts.URL_SERIES + seriesId + Consts.API_UPCOMING_BROADCASTS;
		return super.getPage(url, aSSPageCallback);
	}
		
	public ArrayList<OldBroadcast> getSeriesUpcomingBroadcasts() {
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
