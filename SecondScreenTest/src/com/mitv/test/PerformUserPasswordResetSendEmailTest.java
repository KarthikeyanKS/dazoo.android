
package com.mitv.test;



import junit.framework.Assert;
import org.junit.Test;
import com.google.gson.Gson;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.serialization.UserPasswordResetPasswordData;
import com.mitv.Consts;



public class PerformUserPasswordResetSendEmailTest 
	extends Tests
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "PerformUserPasswordResetSendEmailTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		String url = Consts.URL_RESET_PASSWORD_SEND_EMAIL;
		
		UserPasswordResetPasswordData postData = new UserPasswordResetPasswordData();
		postData.setEmail(DEFAULT_TEST_USER_EMAIL);
		
		String bodyContentData = new Gson().toJson(postData);
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, bodyContentData);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS.getStatusCode());
	}
}