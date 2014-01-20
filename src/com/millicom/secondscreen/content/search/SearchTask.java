package com.millicom.secondscreen.content.search;

import android.os.AsyncTask;
import android.util.Log;

import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSSearchResultPage;
import com.millicom.secondscreen.content.model.SearchResult;

public class SearchTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = "SearchTask";
	
	public static interface SearchResultCallback {
		public void onResult(SearchResult result);
	}
	
	private SearchResultCallback searchResultcallback;
	private String searchWord;
	
	public SearchTask(String searchWord, SearchResultCallback searchResultcallback) {
		this.searchWord = searchWord;
		this.searchResultcallback = searchResultcallback;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		if(!isCancelled()) {
			SSSearchResultPage.getInstance().getPage(searchWord, new SSPageCallback() {
	
				@Override
				public void onGetPageResult(SSPageGetResult pageGetResult) {
					if (searchResultcallback != null) {
						SearchResult result = SSSearchResultPage.getInstance().getSearchResult();
						searchResultcallback.onResult(result);
					}
				}
			});
		} else {
			Log.e(TAG, "Task was canceled");
		}
		
		return null;
	}
}