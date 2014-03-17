
package com.mitv.test.timing;



import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.UserLoginData;
import com.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.test.gson.TestCore;



public class PerformUserLoginTest 
	extends TestCore
{
	private static final String	TAG	= PerformUserLoginTest.class.getSimpleName();
	
	private UserLoginData receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		for(int i=0; i<400; i++)
		{
			receivedData = login(true);
		}
	}
	
	
	
	public static UserLoginData login(boolean useSSL)
	{
		PerformUserLoginTest instance = new PerformUserLoginTest();
		
		String url;
		
		if(useSSL)
		{
			url = Constants.URL_SERVER_SECURE + "auth/login/dazoo";
		}
		else
		{
			url = Constants.URL_SERVER + "auth/login/dazoo";
		}
		
		UserLoginDataPost postData = new UserLoginDataPost();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		postData.setPassword(DEFAULT_TEST_USER_PASSWORD);
		
		String bodyContentData = new Gson().toJson(postData);
		
		Calendar c1 = Calendar.getInstance();
		
		Log.i(TAG, "Before request");
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
		
		Log.i(TAG, "After request");
		
		Calendar c2 = Calendar.getInstance();
		
		long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
		
		double diffInSeconds = diff/1000.0;
		
		Log.i(TAG, "Request took:" + diffInSeconds);
		
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
	public void testAllVariablesNotNull() 
	{
		Assert.assertNotNull(receivedData);
		
		boolean areDataFieldsValid = receivedData.areDataFieldsValid();
		
		Assert.assertFalse(!areDataFieldsValid);
	}
}
