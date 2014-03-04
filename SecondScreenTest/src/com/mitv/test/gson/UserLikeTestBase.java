
package com.mitv.test.gson;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.HeaderParameters;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.gson.serialization.UserLikeData;
import com.mitv.Consts;



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
			guideIndex = randInt(0, tvChannelGuides.size());
		}
		
		TVChannelGuide someGuide = tvChannelGuides.get(guideIndex);	
		ArrayList<TVBroadcast> broadcasts = someGuide.getBroadcasts();
		
		if(useRandomIndexes) {
			broadcastIndex = randInt(0, broadcasts.size());
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
		int randomIndex = randInt(0, size);
		return randomIndex;
	}
		
	/**
	 * Returns a pseudo-random number between min and max, exclusive.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, exclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    int randomNum = rand.nextInt(max - min) + min;

	    return randomNum;
	}
	
	private UserLike addUserLike(
			String token,
			LikeTypeRequestEnum likeType,
			String entityId)
	{
		UserLikeTestBase instance = new UserLikeTestBase();
				
		String url = Consts.URL_LIKES;
		
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