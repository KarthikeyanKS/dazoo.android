
package com.mitv.asynctasks.mitvapi;



import android.util.Log;

import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVSearchResults;



public class GetTVSearchResults 
	extends AsyncTaskBase<TVSearchResults> 
{	
	private static final String TAG = GetTVSearchResults.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_SEARCH;

	private String searchQuery;
	
	
	
	public GetTVSearchResults(
			final ContentCallbackListener contentCallbackListener,
			final ViewCallbackListener activityCallbackListener,
			final String searchQuery)
	{
		super(contentCallbackListener, activityCallbackListener, RequestIdentifierEnum.SEARCH, TVSearchResults.class, true, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		this.searchQuery = searchQuery.trim();
				
		this.urlParameters.add(Constants.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, this.searchQuery);
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
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


	
	@Override
	protected void onPostExecute(Void result) 
	{
		super.onPostExecute(result);
		
		if(isCancelled()) 
		{
			requestResultObjectContent = null;
			
			this.requestResultStatus = FetchRequestResultEnum.SEARCH_CANCELED_BY_USER;
			
			Log.d(TAG, "SearchTask was canceled");
		} 
		else 
		{
			Log.d(TAG, "Search complete (success not garantueed), notifiying ContentManager");
		}
	}
}