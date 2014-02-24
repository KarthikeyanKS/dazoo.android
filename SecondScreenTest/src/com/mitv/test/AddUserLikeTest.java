
package com.mitv.test;



import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.serialization.UserLikeData;
import com.mitv.Consts;



public class AddUserLikeTest 
	extends TestBaseWithGuide
{
	private static final String	TAG	= "AddUserLikeTest";
	
	private UserLike receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String programId = tvProgram.getProgramId();
		
		LikeTypeRequestEnum likeType = LikeTypeRequestEnum.PROGRAM;
		
		receivedData = addUserLike(userToken, likeType, programId);
	}
	
	
	
	private UserLike addUserLike(
			String token,
			LikeTypeRequestEnum likeType,
			String entityId)
	{
		AddUserLikeTest instance = new AddUserLikeTest();
				
		String url = Consts.URL_LIKES;
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
		UserLikeData postData = new UserLikeData();
		postData.setLikeType(likeType);
		postData.setEntityId(entityId);
		
		String bodyContentData = new Gson().toJson(postData);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, urlParameters, headerParameters, bodyContentData);
				
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			receivedData = new Gson().fromJson(responseString, UserLike.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
		
		return receivedData;
	}
	
	
	
	public static void testUserLikeDataFields(UserLike userLike)
	{
		Assert.assertNotNull(userLike.getTitle());
		Assert.assertFalse(TextUtils.isEmpty(userLike.getTitle()));
		
		Assert.assertNotNull(userLike.getLikeType());
		
		switch (userLike.getLikeType())
		{
			case SERIES:
			{
				Assert.assertNotNull(userLike.getSeriesId());
				Assert.assertFalse(TextUtils.isEmpty(userLike.getSeriesId()));
			}
			break;
			
			case SPORT_TYPE:
			{
				Assert.assertNotNull(userLike.getSportTypeId());
				Assert.assertFalse(TextUtils.isEmpty(userLike.getSportTypeId()));
			}
			break;
			
			default:
			case PROGRAM:
			{
				Assert.assertNotNull(userLike.getProgramId());
				Assert.assertFalse(TextUtils.isEmpty(userLike.getProgramId()));
				
				Assert.assertNotNull(userLike.getProgramType());

				switch (userLike.getProgramType()) 
				{
					case MOVIE:
					{
						Assert.assertNotNull(userLike.getGenre());
						Assert.assertFalse(TextUtils.isEmpty(userLike.getProgramId()));
						
						Assert.assertNotNull(userLike.getYear());
					}
					break;
					
					default:
					case OTHER:
					{
						Assert.assertNotNull(userLike.getCategory());
						Assert.assertFalse(TextUtils.isEmpty(userLike.getCategory()));
					}
					break;
				}
			}
			break;
		}
	}
	
	
	
	@Test
	public void testNotNull()
	{	
		Assert.assertNotNull(receivedData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNullOrEmpty() 
	{
		testUserLikeDataFields(receivedData);
	}
}