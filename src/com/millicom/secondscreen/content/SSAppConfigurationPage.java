package com.millicom.secondscreen.content;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;

public class SSAppConfigurationPage extends SSPage {

	private static final String TAG = "SSAdzerkAdPage";
	
	private static SSAppConfigurationPage	sInstance;
	public static SSAppConfigurationPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSAppConfigurationPage();
		}
		return sInstance;
	}
	
	public SSAppConfigurationPage() {
		super(Consts.MILLICOM_SECONDSCREEN_CONFIGURATION);
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseAndUpdateAppConfiguration(jsonObject);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {}
}
