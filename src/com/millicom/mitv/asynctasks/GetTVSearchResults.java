
package com.millicom.mitv.asynctasks;



import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.SearchResultsForQuery;
import com.millicom.mitv.models.TVSearchResults;
import com.millicom.mitv.models.gson.TVSearchResultsJSON;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.mitv.Consts;



public class GetTVSearchResults 
	extends AsyncTaskWithRelativeURL<TVSearchResults> 
{	
	private static final String TAG = GetTVSearchResults.class.getName();
	
	private static final String URL_SUFFIX = Consts.URL_SEARCH;
	
	private AjaxCallback<String> ajaxCallback;
	private String searchQuery;
	
	
	
	public GetTVSearchResults(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallbackListener,
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
		
		this.urlParameters.add(Consts.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
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