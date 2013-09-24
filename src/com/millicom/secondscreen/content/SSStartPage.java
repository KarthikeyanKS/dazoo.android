package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;

public class SSStartPage extends SSPage {

	public static final String	TAG			= "SSStartPage";
	private static SSStartPage	sInstance;
	public String				mStartPageUrl;
	public String mProgramTypeKey;

	public static SSStartPage getInstance() {
		if (sInstance == null) sInstance = new SSStartPage();
		return sInstance;
	}

	public boolean getPage(String programType, String url, SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");
		Log.d(TAG,"Program Type:" + programType);
		// Remember the callback
		super.mPageCallback = pageCallback;
		mStartPageUrl = url;
		mProgramTypeKey = programType;
		Link startPageLink = new Link();
		startPageLink.setUrl(mStartPageUrl);
		
		super.getPage(startPageLink, pageCallback);
		return true;
	}

	public ArrayList<Guide> getGuide() {
		Log.d(TAG, "get Guide");
		return super.getGuide();
	}

	@Override
	//protected void parseGetPageResult(JSONObject aJsonObject, SSPageGetResult aVPPageGetResult) {
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseGuide(jsonArray, mProgramTypeKey);
			
			// The resulting page is this
			pageGetResult.setPage(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleGetStartPageUriResult() {

		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mProgramTypeKey, mStartPageUrl, mPageCallback)) {

			Log.d(TAG, "Get start page uri or get start page failed");

			// If we have a callback
			if (mPageCallback != null) {

				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

}
