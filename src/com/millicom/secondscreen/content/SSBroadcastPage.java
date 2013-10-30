package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.http.SSHttpClient;
import com.millicom.secondscreen.http.SSHttpClientCallback;
import com.millicom.secondscreen.http.SSHttpClientGetResult;
import com.millicom.secondscreen.manager.ContentParser;

public class SSBroadcastPage extends SSPage {

	public static final String				TAG				= "SSBroadcastPage";
	private static SSBroadcastPage			sInstance;
	public static String					mBroadcastPageUrl;

	public SSBroadcastPage() {
	}

	public static SSBroadcastPage getInstance() {
		if (sInstance == null) sInstance = new SSBroadcastPage();
		return sInstance;
	}

	public boolean getPage(String url, SSPageCallback pageCallback) {
		super.mPageCallback = pageCallback;

		mBroadcastPageUrl = url;

		Link broadcastPageLink = new Link();
		broadcastPageLink.setUrl(url);

		super.getPage(broadcastPageLink, pageCallback);
		return true;
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

	protected void handleGetStartPageUriResult() {
		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mBroadcastPageUrl, mPageCallback)) {
			Log.d(TAG, "Get dates page uri or get dates page failed");

			// If we have a callback
			if (mPageCallback != null) {
				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		// not necessery here
	}
}