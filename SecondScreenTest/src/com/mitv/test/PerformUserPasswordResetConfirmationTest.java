
package com.mitv.test;



import junit.framework.Assert;
import org.junit.Test;
import com.google.gson.Gson;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.serialization.UserPasswordResetConfirmationData;
import com.mitv.Consts;



public class PerformUserPasswordResetConfirmationTest 
	extends TestCore
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "PerformUserPasswordResetConfirmationTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		String url = Consts.URL_RESET_AND_CONFIRM_PASSWORD;
		
		UserPasswordResetConfirmationData postData = new UserPasswordResetConfirmationData();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		postData.setResetPasswordToken("token");
		postData.setNewPassword("password");
		
		String bodyContentData = new Gson().toJson(postData);
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS.getStatusCode());
	}
}