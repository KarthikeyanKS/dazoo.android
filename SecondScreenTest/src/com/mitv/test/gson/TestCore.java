
package com.mitv.test.gson;



import java.util.Locale;
import java.util.TimeZone;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCore;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.LanguageUtils;



public abstract class TestCore 
	extends InstrumentationTestCase 
{
	private static final String TAG = TestCore.class.getName();
	
	
	protected static final int YEAR_OF_2000 = 2000;
	protected static final String DEFAULT_TEST_USER_EMAIL = "junit_test@mi.tv";
	protected static final String DEFAULT_TEST_USER_PASSWORD = "junit_test";
	protected static final String DEFAULT_TEST_USER_FACEBOOK_TOKEN = "CAADx1RnSnyQBAKfO3m8ZALzlwa9O9ybGfH4nmM0BAW3zN2uWSecJZBURfspqZBYKZAQ2ZCATjcUFZCkxyr2ARZCIxsAQ67u63JtrL80oKZBvsRCTY0dqfebDUuPZBevROWPZCJyWutPOAq0ZAie2Spk6SgZBzd7OxBV80ejFs9Up6MqAFutYMeoSSEtD7iZCCqFwiGm4Ej8uKmP6hsMoGaeYfkwqazfZCHLxDYfDvR9SGqNFQ0oQZDZD";
	protected static final String DEFAULT_NEW_TEST_USER_EMAIL_PREFIX = "junit_test_";
	protected static final String DEFAULT_NEW_TEST_USER_EMAIL_SUFFIX = "@mi.tv";
	protected static final String DEFAULT_NEW_TEST_USER_PASSWORD = "junit_test";
	protected static final String DEFAULT_NEW_TEST_USER_FIRST_NAME = "junit_test";
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{}
	
	
	
	protected HTTPCoreResponse executeRequestGet(final String url) {
		return executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
	}
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url)
	{
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, null);
		
		return httpCoreResponse;
	}
	
	
	
	public HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			String bodyContentData)
	{
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = new HeaderParameters();
		
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
	
	
	protected HTTPCoreResponse executeRequestNoBody(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters) 
	{
		HeaderParameters headerParameters = new HeaderParameters();
		
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, null);
		
		return httpCoreResponse;
	}
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final HeaderParameters headerParameters)
	{
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, null);
		
		return httpCoreResponse;
	}
	
		
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final HeaderParameters headerParameters,
			final String bodyContentData)
	{
		HTTPCore httpCore = HTTPCore.sharedInstance();
		
		/* Add the locale to the header data */
		Locale locale = LanguageUtils.getCurrentLocale();
		TimeZone timeZone = TimeZone.getDefault();
		
		if(locale != null && timeZone != null)
		{
			int timeZoneOffsetInMinutesAsInt = (int) (timeZone.getRawOffset() / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
			Integer timeZoneOffsetInMinutes = Integer.valueOf(timeZoneOffsetInMinutesAsInt);
		
			headerParameters.add(Constants.HTTP_REQUEST_DATA_LOCALE, locale.toString());
			
			urlParameters.add(Constants.HTTP_REQUEST_DATA_TIME_ZONE_OFFSET, timeZoneOffsetInMinutes.toString());
		}
		else
		{
			Log.w(TAG, "Either locale or timeZone have null values.");
		}
		
		HTTPCoreResponse httpCoreResponse = httpCore.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
	
	
	
	protected static HeaderParameters getHeaderParametersWithUserToken(String userToken)
	{
		HeaderParameters headerParameters = new HeaderParameters();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.USER_AUTHORIZATION_HEADER_VALUE_PREFIX);
		sb.append(" ");
		sb.append(userToken);
		
		headerParameters.add(Constants.USER_AUTHORIZATION_HEADER_KEY, sb.toString());
		
		return headerParameters;
	}
	
	
	
	protected static UserRegistrationData getNewRandomUserData()
	{
		UserRegistrationData data = new UserRegistrationData();
		
		int randomNumber = GenericUtils.getRandomNumberBetween();
		
		StringBuilder sb = new StringBuilder();
		sb.append(DEFAULT_NEW_TEST_USER_EMAIL_PREFIX);
		sb.append(randomNumber);
		sb.append(DEFAULT_NEW_TEST_USER_EMAIL_SUFFIX);
		
		data.setFirstName(DEFAULT_NEW_TEST_USER_FIRST_NAME);
		data.setLastName(new Integer(randomNumber).toString());
		data.setEmail(sb.toString());
		data.setPassword(DEFAULT_NEW_TEST_USER_PASSWORD);
		
		return data;
	}
}