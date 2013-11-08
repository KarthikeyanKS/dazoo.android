package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;

public class SSGuidePage  extends SSPage {

	public static final String	TAG	= "SSGuidePage";
	public String				mPageUrl;

	// private constructor prevents instantiation from other classes
	private SSGuidePage() {};
	
	private static class SSGuidePageHolder{
		public static final SSGuidePage  INSTANCE = new SSGuidePage();
	}
	
	public static SSGuidePage getInstance(){
		return SSGuidePageHolder.INSTANCE;
	}

	public boolean getPage(String url, SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");
		// Remember the callback
		super.mPageCallback = pageCallback;
		mPageUrl = url;
		
		Link startPageLink = new Link();
		startPageLink.setUrl(mPageUrl);

		super.getPage(startPageLink, pageCallback);
		return true;
	}

	public ArrayList<Guide> getGuide() {
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

	protected void handleGetStartPageUriResult() {

		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mPageUrl, mPageCallback)) {

			Log.d(TAG, "Get start page uri or get start page failed");

			// If we have a callback
			if (mPageCallback != null) {

				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}

}