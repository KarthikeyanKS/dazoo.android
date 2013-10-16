package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;

public class SSBroadcastPage  extends SSPage {

	public static final String	TAG			= "SSBroadcastPage";
	private static SSBroadcastPage	sInstance;
	public String				mBroadcastPageUrl;

	public SSBroadcastPage(){
	}
	
	public static SSBroadcastPage getInstance() {
		if (sInstance == null) sInstance = new SSBroadcastPage();
		return sInstance;
	}

	public boolean getPage(String programType, String url, SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");
		// Remember the callback
		super.mPageCallback = pageCallback;
		mBroadcastPageUrl = url;
		Link startPageLink = new Link();
		startPageLink.setUrl(mBroadcastPageUrl);
		
		super.getPage(startPageLink, pageCallback);
		return true;
	}

	public Broadcast getBroadcast() {
		Log.d(TAG, "get Broadcast");
		return super.getBroadcast();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseBroadcast(jsonArray);
			
			// The resulting page is this
			pageGetResult.setPage(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleGetStartPageUriResult() {

		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(null,mBroadcastPageUrl, mPageCallback)) {

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