
package com.mitv.test;



import junit.framework.Assert;
import org.junit.Test;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.UserLoginData;
import com.millicom.mitv.models.gson.serialization.UserLoginDataPost;
import com.mitv.Consts;



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
		
		String url = Consts.URL_LOGIN;

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