package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.model.OldTVChannelGuide;

public class SSGuidePage  extends SSPage {

	public static final String	TAG	= "SSGuidePage";

	private static SSGuidePage	sInstance;
	public static SSGuidePage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSGuidePage();
		}
		return sInstance;
	}
	
	public ArrayList<OldTVChannelGuide> getGuide() {
		Log.d(TAG, "get Guide");
		return super.getGuide();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseGuide(jsonArray);
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