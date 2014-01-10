package com.millicom.secondscreen.content;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.AdzerkAd;

public class SSAdzerkAdPage extends SSPage {

	private static final String TAG = "SSAdzerkAdPage";
	private String divId;
	
	private static SSAdzerkAdPage	sInstance;
	public static SSAdzerkAdPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSAdzerkAdPage();
		}
		return sInstance;
	}
	
	public boolean getPage(String divId, SSPageCallback aSSPageCallback) {
		this.divId = divId;
		return super.getPage(Consts.MILLICOM_SECONDSCREEN_CONFIGURATION, aSSPageCallback);
	}

	public AdzerkAd getAd() {
		Log.d(TAG, "get ad");
		return super.getAd();
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseAdzerkAd(divId, jsonObject);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {}
}
