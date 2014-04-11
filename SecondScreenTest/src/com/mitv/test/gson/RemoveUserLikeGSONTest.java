
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.LikeTypeRequestEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVProgram;



public class RemoveUserLikeGSONTest 
	extends TestBaseWithGuide
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "RemoveUserLikeTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
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
				
		httpCoreResponse = removeUserLike(userToken, likeType, programId);
	}
	
	
	
	public static HTTPCoreResponse removeUserLike(
			String token,
			LikeTypeRequestEnum likeType,
			String entityId)
	{
		RemoveUserLikeGSONTest instance = new RemoveUserLikeGSONTest();
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_LIKES);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(likeType);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(entityId);
		
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