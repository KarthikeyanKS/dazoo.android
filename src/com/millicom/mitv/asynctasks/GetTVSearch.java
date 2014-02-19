
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.gson.TVSearchResults;
import com.mitv.Consts;



public class GetTVSearch 
	extends AsyncTaskWithRelativeURL<TVSearchResults> 
{	
	private static final String URL_SUFFIX = Consts.URL_SEARCH;
	
	
	
	public GetTVSearch(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String wordToSearch)
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.SEARCH, TVSearchResults.class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		StringBuilder querystringValueSB = new StringBuilder();
		querystringValueSB.append(wordToSearch.trim());
		querystringValueSB.append(Consts.SEARCH_WILDCARD);
		
		this.urlParameters.add(Consts.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
	}
}