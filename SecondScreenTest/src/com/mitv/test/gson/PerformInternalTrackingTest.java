
package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVProgram;
import com.mitv.utilities.GenericUtils;



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
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String tvProgramId = tvProgram.getProgramId();
		
		String deviceId = GenericUtils.getDeviceId();
		
		String url = Constants.URL_INTERNAL_TRACKING;
		
		URLParameters urlParameters = new URLParameters();
		
		urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_KEY, Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_VALUE_VIEWS);
		urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_KEY, Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VALUE_PROGRAM_ID);
		urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_VALUE, tvProgramId);
		urlParameters.add(Constants.INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_UID, deviceId);
		
		HeaderParameters headerParameters = new HeaderParameters();
		
		httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
	}
	
	
	
	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS.getStatusCode());
	}
}