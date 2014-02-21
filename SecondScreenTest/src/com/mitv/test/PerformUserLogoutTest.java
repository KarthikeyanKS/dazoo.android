
package com.mitv.test;



import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.UserLoginData;
import com.mitv.Consts;



public class PerformUserLogoutTest 
	extends TestCore
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "PerformUserLogoutTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		String token = "";
		
		if(token.isEmpty())
		{
			UserLoginData data = PerformUserLoginTest.login();
			
			if(data != null)
			{
				token = data.getToken();
			}
			// No need for else
		}
		// No need for else
		
		httpCoreResponse = logout(token);
	}
	
	
	
	private static HTTPCoreResponse logout(String token)
	{
		PerformUserLogoutTest instance = new PerformUserLogoutTest();
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_AUTH_TOKENS);
		url.append(token);
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_DELETE, url.toString(), urlParameters, headerParameters);
				
		return httpCoreResponse;
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}