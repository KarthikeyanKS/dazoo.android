package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.Tag;

public class SSTagsPage extends SSPage {

	public static final String	TAG	= "SSTagsPage";

	private static SSTagsPage	sInstance;
	public static SSTagsPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSTagsPage();
		}
		return sInstance;
	}
	
	public SSTagsPage() {
		super(Consts.MILLICOM_SECONDSCREEN_TAGS_PAGE_URL);
	}

	public ArrayList<Tag> getTags() {
		Log.d(TAG, "get tags");
		return super.getTags();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseTags(jsonArray);

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