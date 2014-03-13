
package com.mitv.asynctasks;



import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.SearchResultsForQuery;
import com.mitv.models.TVSearchResults;
import com.mitv.models.gson.TVSearchResultsJSON;
import com.mitv.utilities.RegularExpressionUtils;



public class GetTVSearchResults 
	extends AsyncTaskWithRelativeURL<TVSearchResults> 
{	
	private static final String TAG = GetTVSearchResults.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_SEARCH;
	
	private AjaxCallback<String> ajaxCallback;
	private String searchQuery;
	
	
	
	public GetTVSearchResults(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			AjaxCallback<String> ajaxCallback,
			String searchQuery)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.SEARCH, TVSearchResults.class, TVSearchResultsJSON.class, true, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		this.ajaxCallback = ajaxCallback;
		
		searchQuery = RegularExpressionUtils.escapeSpaceChars(searchQuery);
		searchQuery = searchQuery.trim();
		this.searchQuery = searchQuery;
		
		StringBuilder querystringValueSB = new StringBuilder();
		querystringValueSB.append(searchQuery);
		
		this.urlParameters.add(Constants.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
	}
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
	//		ajaxCallback.block();
	
			/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
			TVSearchResults tvSearchResults = (TVSearchResults) requestResultObjectContent;
			
			SearchResultsForQuery searchResultsForQuery = new SearchResultsForQuery(searchQuery, tvSearchResults);
			
			requestResultObjectContent = searchResultsForQuery;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}

		return null;
	}
		
}