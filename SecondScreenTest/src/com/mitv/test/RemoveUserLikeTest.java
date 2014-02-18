
package com.mitv.test;



import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.UserLoginData;
import com.mitv.Consts;



public class RemoveUserLikeTest 
	extends TestCore
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "RemoveUserLikeTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		ContentTypeEnum likeType = ContentTypeEnum.CHANNEL;
		String entityId = "5393752d-6df3-4673-989b-85b78d1bf7bc";
		
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
		
		httpCoreResponse = removeUserLike(token, likeType, entityId);
	}
	
	
	
	private static HTTPCoreResponse removeUserLike(
			String token,
			ContentTypeEnum likeType,
			String entityId)
	{
		RemoveUserLikeTest instance = new RemoveUserLikeTest();
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_LIKES);
		url.append(likeType);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(entityId);
		
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