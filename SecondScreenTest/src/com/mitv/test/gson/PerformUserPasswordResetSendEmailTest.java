
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.gson.serialization.UserPasswordResetPasswordData;



public class PerformUserPasswordResetSendEmailTest 
	extends TestCore
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "PerformUserPasswordResetSendEmailTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		String url = Constants.URL_RESET_PASSWORD_SEND_EMAIL;
		
		UserPasswordResetPasswordData postData = new UserPasswordResetPasswordData();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		
		String bodyContentData = new Gson().toJson(postData);
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertTrue(httpCoreResponse.getStatusCode() == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}