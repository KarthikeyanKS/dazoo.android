package com.mitv.content;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.SearchResult;

public class SSSearchResultPage extends SSPage {

	public static final String	TAG	= "SSSearchResultPage";

	private static SSSearchResultPage	sInstance;
	public static SSSearchResultPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSSearchResultPage();
		}
		return sInstance;
	}
	
	public SSSearchResultPage() {
		super(Consts.URL_SEARCH);
	}
	
	public SearchResult getSearchResult() {
		return super.getSearchResult();
	}
	
	public boolean getPage(String searchWord, SSPageCallback aSSPageCallback) {
		String completeSearchUrl = new StringBuilder(mPageUrl).append(searchWord).toString();
		Log.e(TAG, "Search query: " + completeSearchUrl);
		return super.getPage(completeSearchUrl, aSSPageCallback);
	}
	
	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseSearchResult(jsonObject);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}