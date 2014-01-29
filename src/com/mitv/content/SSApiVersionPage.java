package com.mitv.content;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;

public class SSApiVersionPage extends SSPage {

	private static final String TAG = "SSApiVersionPage";
	
	private static SSApiVersionPage	sInstance;
	public static SSApiVersionPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSApiVersionPage();
		}
		return sInstance;
	}
	
	public SSApiVersionPage() {
		super(Consts.MILLICOM_SECONDSCREEN_API_VERSION);
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseApiVersion(jsonArray);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
