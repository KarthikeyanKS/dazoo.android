
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.UserLike;
import com.mitv.models.UserLoginData;



public class GetUserLikesGSONTest 
	extends UserLikeTestBase
{
	private static final String	TAG	= GetUserLikesGSONTest.class.getName();
	private static final int MAXIMUM_LIKES_ALLOWED = 5;
	
	private static String userToken;
	
	private List<UserLike> likesListData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		/* We want at least 5 likes and hopefully one of them have a nextBroadcast object */
		super.setUp();
		super.setUp();
		super.setUp();
		super.setUp();
		super.setUp();
		
		if(TextUtils.isEmpty(userToken))
		{
			UserLoginData data = PerformUserLoginTest.login();
			
			if(data != null)
			{
				userToken = data.getToken();
			}
			// No need for else
		}
		// No need for else
		
		if(!TextUtils.isEmpty(userToken))
		{
			likesListData = getUserLikes(userToken);
		}
		else
		{
			Log.w(TAG, "Login has failed.");
		}
	}
	
	
	
	/* Since this test class adds UserLikes running it multiple times can result in a huge list, thus we should remove likes */
	@Override
	protected void tearDown() throws Exception {
		for(int i = likesListData.size()-1; i > MAXIMUM_LIKES_ALLOWED; --i) {
			UserLike userLikeToRemove = likesListData.get(i);
			HTTPCoreResponse httpCoreResponse =  RemoveUserLikeGSONTest.removeUserLike(userToken, userLikeToRemove.getLikeTypeForRequest(), userLikeToRemove.getContentId());
			Assert.assertTrue(httpCoreResponse.getStatusCode() == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
			Log.d(TAG, String.format("tearDown, removing like status: %s", httpCoreResponse.getStatusCode() == FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode() ? "successful" : "fail"));
		}
		super.tearDown();
	}




	private static List<UserLike> getUserLikes(String token)
	{
		GetUserLikesGSONTest instance = new GetUserLikesGSONTest();
		
		List<UserLike> receivedData;
		
		String url = Constants.URL_LIKES_WITH_UPCOMING;
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
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
	public void testAllVariablesNotNullOrEmpty() 
	{
		Assert.assertNotNull(likesListData);
		Assert.assertFalse(likesListData.isEmpty());
		for(UserLike userLike : likesListData) 
		{
			Assert.assertTrue(userLike.areDataFieldsValid());
		}
	}
}