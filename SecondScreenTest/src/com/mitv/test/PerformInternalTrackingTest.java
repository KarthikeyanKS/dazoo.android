
package com.mitv.test;



import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.Broadcast;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVProgram;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;



public class PerformInternalTrackingTest 
	extends TestBaseWithGuide
{
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp() 
			throws Exception
	{
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		Broadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String tvProgramId = tvProgram.getProgramId();
		
		String deviceId = GenericUtils.getDeviceId();
		
		String url = Consts.URL_INTERNAL_TRACKING;
		
		URLParameters urlParameters = new URLParameters();
		
		urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_KEY, Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_VALUE_VIEWS);
		urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_KEY, Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VALUE_PROGRAM_ID);
		urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_VALUE, tvProgramId);
		urlParameters.add(Consts.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_UID, deviceId);
		
		Map<String, String> headerParameters = new HashMap<String, String>();
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS.getStatusCode());
	}
}