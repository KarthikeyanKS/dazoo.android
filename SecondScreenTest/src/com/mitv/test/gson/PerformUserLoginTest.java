
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class PerformUserLoginTest 
	extends TestCore
{
	private static final String	TAG	= "PerformUserLoginTest";
	
	private UserLoginData receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		receivedData = login();
	}
	
	
	
	public static UserLoginData login()
	{
		PerformUserLoginTest instance = new PerformUserLoginTest();
		
		String url = Constants.URL_LOGIN_WITH_PLAINTEXT_PASSWORD;

		UserLoginDataPost postData = new UserLoginDataPost();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		postData.setPassword(DEFAULT_TEST_USER_PASSWORD);
		
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
