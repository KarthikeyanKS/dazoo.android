
package com.mitv.test;



import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.UserLike;
import com.millicom.mitv.models.gson.UserLoginData;
import com.mitv.Consts;



public class GetUserLikesTest 
	extends TestCore
{
	private static final String	TAG	= "GetUserLikesTest";
	
	private List<UserLike> receivedData;
	
	
	
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
		
		receivedData = getUserLikes(token);
	}
	
	
	
	private static List<UserLike> getUserLikes(String token)
	{
		GetUserLikesTest instance = new GetUserLikesTest();
		
		List<UserLike> receivedData;
		
		String url = Consts.URL_LIKES;
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
		
		String responseString = httpCoreResponse.getResponseString();
				
		try
		{
			receivedData = Arrays.asList(new Gson().fromJson(responseString, UserLike[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
		
		return receivedData;
	}
	
	

	@Test
	public void testNotNullOrEmpty()
	{	
		Assert.assertNotNull(receivedData);
		Assert.assertFalse(receivedData.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNullOrEmpty() 
	{
		for(UserLike userLike : receivedData) 
		{
			AddUserLikeTest.testUserLikeDataFields(userLike);
		}
	}
}