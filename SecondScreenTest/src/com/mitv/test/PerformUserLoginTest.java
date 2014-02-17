
package com.mitv.test;



import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;



public class PerformUserLoginTest 
	extends Tests
{
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		//tvTag = Arrays.asList(new Gson().fromJson(jsonString, TVTag[].class));
		
		String url = "";
		URLParameters urlParameters = new URLParameters();
	
		Map<String, String> headerParameters = new HashMap<String, String>();
		String bodyContentData = "";
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, urlParameters, headerParameters, bodyContentData);
	}
	
	
	
	@Test
	public void testNotNull()
	{	
		
	}
	
	
	
	@Test
	public void testAllVariablesNotNull() 
	{
		
	}
}
