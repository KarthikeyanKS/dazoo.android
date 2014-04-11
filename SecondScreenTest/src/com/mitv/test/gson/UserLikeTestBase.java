
package com.mitv.test.gson;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.LikeTypeRequestEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVProgram;
import com.mitv.models.UserLike;
import com.mitv.models.gson.serialization.UserLikeData;
import com.mitv.utilities.RandomNumberUtils;



public class UserLikeTestBase 
	extends TestBaseWithGuide
{
	private static final String	TAG	= UserLikeTestBase.class.getName();
	
	protected UserLike receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		boolean useRandomIndexes = useRandomIndexes();
		
		int guideIndex = 0;
		int broadcastIndex = 0;
		
		if(useRandomIndexes) {
			guideIndex = RandomNumberUtils.randomIntegerInRange(0, tvChannelGuides.size());
		}
		
		TVChannelGuide someGuide = tvChannelGuides.get(guideIndex);	
		ArrayList<TVBroadcast> broadcasts = someGuide.getBroadcasts();
		
		if(useRandomIndexes) {
			broadcastIndex = RandomNumberUtils.randomIntegerInRange(0, broadcasts.size());
		}
		
		TVBroadcast broadcast = broadcasts.get(broadcastIndex);
		TVProgram tvProgram = broadcast.getProgram();
		String programId = tvProgram.getProgramId();
		
		LikeTypeRequestEnum likeType = LikeTypeRequestEnum.PROGRAM;
		
		receivedData = addUserLike(userToken, likeType, programId);
	}
	
	private boolean useRandomIndexes() {
		boolean useRandomIndexes = (this instanceof GetUserLikesGSONTest);
		return useRandomIndexes;
	}
	
	public static int randomIndexForList(List<Object> list) {
		int size = list.size() - 1; //zero indexed;
		int randomIndex = RandomNumberUtils.randomIntegerInRange(0, size);
		return randomIndex;
	}
			
	private UserLike addUserLike(
			String token,
			LikeTypeRequestEnum likeType,
			String entityId)
	{
		UserLikeTestBase instance = new UserLikeTestBase();
				
		String url = Constants.URL_LIKES;
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
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
}