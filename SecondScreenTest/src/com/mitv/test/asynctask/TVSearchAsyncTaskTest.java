
package com.mitv.test.asynctask;



import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Test;

import android.test.InstrumentationTestCase;

import com.mitv.asynctasks.mitvapi.GetTVSearchResults;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.RequestParameters;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVSearchResults;



public class TVSearchAsyncTaskTest 
	extends InstrumentationTestCase 
	implements ContentCallbackListener
{
	@SuppressWarnings("unused")
	private static final String	TAG	= TVSearchAsyncTaskTest.class.getName();
	private final CountDownLatch signal = new CountDownLatch(1);
	private String searchString;
	
	
	
	private void executeTVSearch(String wordToSearchFor)
	{
		searchString = wordToSearchFor;
		
		GetTVSearchResults getTVSearchResults = new GetTVSearchResults(this, null, wordToSearchFor, 1);
		
		getTVSearchResults.execute();
	}
	

	
	@Test
	public void test1SearchForSeries() 
	{
		String seriesName = "los simpson";
		
		executeTVSearch(seriesName);
		
		try
		{
			signal.await();
		} 
		catch(InterruptedException e)
		{
			Assert.fail();
		}
	}
	
	
	
	@Test
	public void test2SearchForProgram() 
	{
		String programName = "treehouse of horror";
		
		executeTVSearch(programName);
		try 
		{
			signal.await();
		} 
		catch (InterruptedException e)
		{
			Assert.fail();
		}
	}
	
	
	
	@Test
	public void test3SearchForChannel() 
	{
		String channelName = "caracol";
		
		executeTVSearch(channelName);
		
		try 
		{
			signal.await();
		}
		catch (InterruptedException e)
		{
			Assert.fail();
		}
	}


	
	@Override
	public void onResult(ViewCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content, RequestParameters requestParameters) 
	{
		Assert.assertTrue(result.wasSuccessful());
		
		SearchResultsForQuery searchResultForQuery = (SearchResultsForQuery) content;
		
		Assert.assertNotNull(searchResultForQuery);
		
		String searchedQuery = searchResultForQuery.getQueryString();
		
		Assert.assertEquals(searchString, searchedQuery);
		
		TVSearchResults searchResults = searchResultForQuery.getSearchResults();
		
		Assert.assertNotNull(searchResults);
		
		Assert.assertTrue(searchResults.areDataFieldsValid());
		
		signal.countDown();
	}
}