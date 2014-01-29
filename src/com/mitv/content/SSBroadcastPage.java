package com.mitv.content;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.model.Broadcast;

public class SSBroadcastPage extends SSPage {

	public static final String TAG = "SSBroadcastPage";
	
	private static SSBroadcastPage	sInstance;
	public static SSBroadcastPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSBroadcastPage();
		}
		return sInstance;
	}

	public Broadcast getBroadcast() {
		Log.d(TAG, "get broadcast");
		return super.getBroadcast();
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseBroadcast(jsonObject);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		// not necessery here
	}
}