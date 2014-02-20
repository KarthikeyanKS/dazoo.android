
package com.mitv.test;



import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.Broadcast;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;



public class RemoveUserLikeTest 
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
		Broadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String programId = tvProgram.getProgramId();
		
		LikeTypeRequestEnum likeType = LikeTypeRequestEnum.PROGRAM;
				
		httpCoreResponse = removeUserLike(userToken, likeType, programId);
	}
	
	
	
	private static HTTPCoreResponse removeUserLike(
			String token,
			LikeTypeRequestEnum likeType,
			String entityId)
	{
		RemoveUserLikeTest instance = new RemoveUserLikeTest();
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_LIKES);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
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