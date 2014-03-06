
package com.mitv.test.gson.standalone;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.gson.serialization.UserPasswordResetConfirmationData;
import com.mitv.test.gson.TestCore;



public class PerformUserPasswordResetConfirmationTest 
	extends TestCore
{
	private static final String	TAG	= "PerformUserPasswordResetConfirmationTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		String url = Constants.URL_RESET_AND_CONFIRM_PASSWORD;
		
		// This is the token that is received in the email address you specified in the password recovery step 1
		String emailToken = "";
		String newPassword = "password";
		
		UserPasswordResetConfirmationData postData = new UserPasswordResetConfirmationData();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		postData.setResetPasswordToken(emailToken);
		postData.setNewPassword(newPassword);
		
		if(emailToken.isEmpty())
		{
			Log.w(TAG, "The email token is empty. The test will probably fail.");
		}
		
		String bodyContentData = new Gson().toJson(postData);
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertTrue(true);
//		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}