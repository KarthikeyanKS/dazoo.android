package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.Broadcast;

public class SSBroadcastsFromProgramPage extends SSPage {
	public static final String	TAG	= "SSBroadcastsFromSeriesPage";

	private static SSBroadcastsFromProgramPage	sInstance;
	public static SSBroadcastsFromProgramPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSBroadcastsFromProgramPage();
		}
		return sInstance;
	}
	
	public boolean getPage(String programId, SSPageCallback aSSPageCallback) {
		String url = Consts.MILLICOM_SECONDSCREEN_PROGRAMS + programId + Consts.MILLICOM_SECONDSCREEN_API_BROADCASTS;
		return super.getPage(url, aSSPageCallback);
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

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}
}