
package com.mitv.test;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import com.google.gson.Gson;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.mitv.Consts;



public class SetUserTVChannelsIdsTest 
	extends TestBaseWithGuide
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "SetUserTVChannelsIdsTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);		
		TVChannelId tvChannelId = someGuide.getChannelId();
		
		List<TVChannelId> channelIds = new ArrayList<TVChannelId>();
		channelIds.add(tvChannelId);
		
		httpCoreResponse = setUserTVChannelIds(userToken, channelIds);
	}
	
	
	
	private HTTPCoreResponse setUserTVChannelIds(
			String token,
			List<TVChannelId> channelIds)
	{
		SetUserTVChannelsIdsTest instance = new SetUserTVChannelsIdsTest();
				
		String url = Consts.URL_MY_CHANNEL_IDS;
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
		String bodyContentData = new Gson().toJson(channelIds);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, urlParameters, headerParameters, bodyContentData);
				
		return httpCoreResponse;
	}
	
	

	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}