
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.UserLoginData;



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
		url.append(Constants.URL_AUTH_TOKENS);
		url.append(token);
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_DELETE, url.toString(), urlParameters, headerParameters);
				
		return httpCoreResponse;
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}