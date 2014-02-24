
package com.mitv.test;



import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVSearchResults;
import com.mitv.Consts;



public class TVSearch 
	extends TestCore
{
	private static final String	TAG	= "TVSearch";

	private TVSearchResults receivedData;

	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		String wordToSearchFor = "los";
		
		executeTVSearch(wordToSearchFor);
	}
	
	
	
	private void executeTVSearch(String wordToSearchFor)
	{
		String url = Consts.URL_SEARCH;
		
		URLParameters urlParameters = new URLParameters();
		
		StringBuilder querystringValueSB = new StringBuilder();
		querystringValueSB.append(wordToSearchFor.trim());
		querystringValueSB.append(Consts.SEARCH_WILDCARD);
		
		urlParameters.add(Consts.SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY, querystringValueSB.toString());
		
		Map<String, String> headerParameters = new HashMap<String, String>();
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
		
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			receivedData = new Gson().fromJson(responseString, TVSearchResults.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
	}
	
	
	
	@Test
	public void testNotNullOrEmpty()
	{	
		Assert.assertNotNull(receivedData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNullOrEmpty() 
	{
		Assert.assertNotNull(receivedData);
		
		boolean areDataFieldsValid = receivedData.areDataFieldsValid();
		
		Assert.assertFalse(!areDataFieldsValid);
	}
}