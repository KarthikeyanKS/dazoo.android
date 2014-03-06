
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserFacebookTokenData;



public class GetUserTokenUsingFBTokenTest 
	extends TestCore
{
	private static final String	TAG	= "PerformUserLoginTest";
	
	private UserLoginData receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		receivedData = getUserToken();
	}
	
	
	
	public static UserLoginData getUserToken()
	{
		PerformUserLoginTest instance = new PerformUserLoginTest();
		
		String facebookToken = DEFAULT_TEST_USER_FACEBOOK_TOKEN;
		
		String url = Constants.URL_FACEBOOK_TOKEN;

		UserFacebookTokenData postData = new UserFacebookTokenData();
		postData.setFacebookToken(facebookToken);
		
		String bodyContentData = new Gson().toJson(postData);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
		
		String responseString = httpCoreResponse.getResponseString();
		
		UserLoginData receivedData;
		
		try
		{
			receivedData = new Gson().fromJson(responseString, UserLoginData.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
		
		return receivedData;
	}
	
	
	
	@Test
	public void testNotNull()
	{	
		Assert.assertNotNull(receivedData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull() 
	{
		Assert.assertNotNull(receivedData);
		
		boolean areDataFieldsValid = receivedData.areDataFieldsValid();
		
		Assert.assertFalse(!areDataFieldsValid);
	}
}
