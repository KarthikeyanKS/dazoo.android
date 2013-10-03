package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.Tag;

public class SSTagsPage extends SSPage{

public static final String TAG = "SSTagsPage";
	
	private static SSTagsPage	sInstance;
	public String 				mTagsPageUrl;

	public static SSTagsPage getInstance() {
		if (sInstance == null) sInstance = new SSTagsPage();
		return sInstance;
	}

	public boolean getPage(SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");

		// Remember the callback
		super.mPageCallback = pageCallback;
		mTagsPageUrl = Consts.MILLICOM_SECONDSCREEN_TAGS_PAGE_API;
		Link tagsPageLink = new Link();
		tagsPageLink.setUrl(mTagsPageUrl);
		
		super.getPage(tagsPageLink, pageCallback);
		return true;
	}

	public ArrayList<Tag> getTags(){
		Log.d(TAG,"get tags");
		return super.getTags();
	}
	
	@Override
	//protected void parseGetPageResult(JSONObject aJsonObject, SSPageGetResult aVPPageGetResult) {
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseTags(jsonArray);

			// The resulting page is this
			pageGetResult.setPage(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void handleGetStartPageUriResult() {
		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mPageCallback)) {
			Log.d(TAG, "Get dates page uri or get dates page failed");

			// If we have a callback
			if (mPageCallback != null) {
				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}
}