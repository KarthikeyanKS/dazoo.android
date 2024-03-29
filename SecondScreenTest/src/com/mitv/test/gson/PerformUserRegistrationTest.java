
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.gson.serialization.UserRegistrationData;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class PerformUserRegistrationTest 
	extends TestCore
{
	private static final String	TAG	= "PerformUserLoginTest";
	
	private HTTPCoreResponse httpCoreResponse;
	private UserLoginData receivedData;
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		register();
	}
	
	
	
	private void register()
	{
		String url = Constants.URL_REGISTER_WITH_PLAINTEXT_PASSWORD;
		
		UserRegistrationData postData = getNewRandomUserData();
		
		String bodyContentData = new Gson().toJson(postData);
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
		
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			receivedData = new Gson().fromJson(responseString, UserLoginData.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
	}
	
	
	
	@Test
	public void testNotNull()
	{	
		Assert.assertNotNull(receivedData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{
		Assert.assertNotNull(receivedData.getToken());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getToken()));
		
		Assert.assertNotNull(receivedData.getUser());
		
		Assert.assertNotNull(receivedData.getUser().getUserId());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getUser().getUserId()));
		
		Assert.assertNotNull(receivedData.getUser().getEmail());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getUser().getEmail()));
		
		Assert.assertNotNull(receivedData.getUser().getFirstName());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getUser().getFirstName()));
		
		Assert.assertNotNull(receivedData.getUser().getLastName());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getUser().getLastName()));
		
		Assert.assertNotNull(receivedData.getUser().isCreated());
	}
}
