
package com.mitv.test.asynctask;



import junit.framework.Assert;

import org.junit.Test;

import com.androidquery.callback.AjaxCallback;
import com.mitv.asynctasks.GetTVSearchResults;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.models.SearchResultsForQuery;
import com.mitv.models.TVSearchResults;



public class TVSearchAsyncTaskTest implements ContentCallbackListener
{
	private static final String	TAG	= TVSearchAsyncTaskTest.class.getName();

	private TVSearchResults receivedData;
	private String searchString;
	
	private void executeTVSearch(String wordToSearchFor)
	{
		searchString = wordToSearchFor;
		GetTVSearchResults getTVSearchResults = new GetTVSearchResults(this, null, new AjaxCallback<String>(), wordToSearchFor);
		getTVSearchResults.execute();
	}
	
	
	private void checkResult() {
		Assert.assertNotNull(receivedData);
		boolean areDataFieldsValid = receivedData.areDataFieldsValid();
		Assert.assertFalse(!areDataFieldsValid);
	}
	
	
	@Test
	public void testSearchForSeries() 
	{
		String seriesName = "los simpson";
		executeTVSearch(seriesName);
		checkResult();
	}
	
//	@Test
//	public void testSearchForProgram() 
//	{
//		String programName = "treehouse of horror";
//		executeTVSearch(programName);
//		checkResult();
//	}
//	
//	@Test
//	public void testSearchForChannel() 
//	{
//		String channelName = "caracol";
//		executeTVSearch(channelName);
//		checkResult();
//	}


	@Override
	public void onResult(ActivityCallbackListener activityCallBackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content) {
		Assert.assertTrue(result.wasSuccessful());
		SearchResultsForQuery searchResultForQuery = (SearchResultsForQuery) content;
		Assert.assertNotNull(searchResultForQuery);
		String searchedQuery = searchResultForQuery.getQueryString();
		Assert.assertEquals(searchString, searchedQuery);
		TVSearchResults searchResults = searchResultForQuery.getSearchResults();
		Assert.assertNotNull(searchResults);
	}
}