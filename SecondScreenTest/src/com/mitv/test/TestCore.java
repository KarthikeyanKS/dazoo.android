
package com.mitv.test;



import java.util.HashMap;
import java.util.Map;
import android.test.InstrumentationTestCase;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCore;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.serialization.UserRegistrationData;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;



public class TestCore 
	extends InstrumentationTestCase 
{
	protected static final long TIMESTAMP_OF_YEAR_2000 = 946684800L;
	protected static final String DEFAULT_TEST_USER_EMAIL = "oskar.tvjunkie@gmail.com";
	protected static final String DEFAULT_TEST_USER_PASSWORD = "ilovetv";
	protected static final String DEFAULT_TEST_USER_FACEBOOK_TOKEN = "CAAClasp4TkwBADDZAZA1HbYpSAoZCtoKZCFRToE3KxZAD53Qrm0x5kFNd6ipZC7XqBxwxhu01HgYRroFw0ikbJ8yvTsXjzX7oZAGvakAKiK8S8buSxe5TVsVtZBV2iy3n2XYBATkHAd2ITOZBOOn3ueLobreI0xCEfGOBZAQnHG3OQZB81bQ2p9GCuHspQ4MMC3lta0wb5OMW3O7nXo6pgX4f8QOBv8zHsyyxxZBQBSlhbwZAKwZDZD";
	
	protected static final String DEFAULT_NEW_TEST_USER_EMAIL_PREFIX = "test_user_";
	protected static final String DEFAULT_NEW_TEST_USER_EMAIL_SUFFIX = "@gmail.com";
	protected static final String DEFAULT_NEW_TEST_USER_PASSWORD = "ilovetvtoo";
	protected static final String DEFAULT_NEW_TEST_USER_FIRST_NAME = "User";
	
	
	
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
	
	
	protected HTTPCoreResponse executeRequestNoBody(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters) {
		Map<String, String> headerParameters = new HashMap<String, String>();
		
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, null);
		
		return httpCoreResponse;
	}
	
	protected HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters)
	{
		HTTPCoreResponse httpCoreResponse = this.executeRequest(httpRequestType, url, urlParameters, headerParameters, null);
		
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
	
	
	
	protected static Map<String, String> getHeaderParametersWithUserToken(String userToken)
	{
		Map<String, String> headerParameters = new HashMap<String, String>(1);
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.USER_AUTHORIZATION_HEADER_VALUE_PREFIX);
		sb.append(" ");
		sb.append(userToken);
		
		headerParameters.put(Consts.USER_AUTHORIZATION_HEADER_KEY, sb.toString());
		
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