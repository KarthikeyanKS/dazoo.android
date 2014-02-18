
package com.mitv.test;



import java.util.HashMap;
import java.util.Map;
import android.test.InstrumentationTestCase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCore;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;



public class Tests 
	extends InstrumentationTestCase 
{
	protected static final long TIMESTAMP_OF_YEAR_2000 = 946684800L;
	protected static final String DEFAULT_TEST_USER_EMAIL = "oskar.tvjunkie@gmail.com";
	protected static final String DEFAULT_TEST_USER_PASSWORD = "ilovetv";
	
	protected Gson gson;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		this.gson = gsonBuilder.create();
	}
	
	
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url)
	{
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = new HashMap<String, String>();
		
		String bodyContentData = null;
		
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
	
	
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			String bodyContentData)
	{
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = new HashMap<String, String>();
		
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
	
		
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters,
			final String bodyContentData)
	{
		HTTPCore httpCore = HTTPCore.sharedInstance();
		
		HTTPCoreResponse httpCoreResponse = httpCore.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
}