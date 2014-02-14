package com.mitv;

import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;
import com.mitv.content.SSSearchResultPage;
import com.mitv.model.OldSearchResult;

public class SearchTask { //extends AsyncTask<String, Void, Void> {

	private static final String TAG = "SearchTask";
	
	public static interface SearchResultCallback {
		public void onResult(OldSearchResult result);
	}
	
	private SearchResultCallback searchResultcallback;

	
	public SearchTask(String searchWord, SearchResultCallback searchResultcallback) {
		this.searchResultcallback = searchResultcallback;
	}
	
	public SearchTask(SearchResultCallback searchResultcallback) {
		this.searchResultcallback = searchResultcallback;
	}
	
	public void search(String searchWord) {
//		execute();
		
		SSSearchResultPage.getInstance().getPage(searchWord, new SSPageCallback() {
			
			@Override
			public void onGetPageResult(SSPageGetResult pageGetResult) {
				if (searchResultcallback != null) {
					OldSearchResult result = SSSearchResultPage.getInstance().getSearchResult();
					searchResultcallback.onResult(result);
				}
			}
		});
	}
	
//	@Override
//	protected Void doInBackground(String... params) {
//		if(!isCancelled()) {
//			SSSearchResultPage.getInstance().getPage(searchWord, new SSPageCallback() {
//	
//				@Override
//				public void onGetPageResult(SSPageGetResult pageGetResult) {
//					if (searchResultcallback != null) {
//						SearchResult result = SSSearchResultPage.getInstance().getSearchResult();
//						searchResultcallback.onResult(result);
//					}
//				}
//			});
//		} else {
//			Log.e(TAG, "Task was canceled");
//		}
//		
//		return null;
//	}
}