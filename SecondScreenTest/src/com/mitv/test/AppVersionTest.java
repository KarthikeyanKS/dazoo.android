package com.mitv.test;



import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.AppVersionJSON;
import com.mitv.Consts;



public class AppVersionTest 
	extends TestCore 
{
	private List<AppVersionJSON> receivedData;
	

	
	@Override
	protected void setUp() 
			throws Exception
	{
		super.setUp();
		
		String url = Consts.URL_API_VERSION;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();
		
		receivedData = Arrays.asList(new Gson().fromJson(responseString, AppVersionJSON[].class));
	}

	
	
	@Test
	public void testNotNull()
	{
		Assert.assertNotNull(receivedData);
		Assert.assertFalse(receivedData.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNull() 
	{
		for(AppVersionJSON appVersionDataParts : receivedData) 
		{
			Assert.assertNotNull(receivedData);
			
			Assert.assertNotNull(appVersionDataParts.getName());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getName()));
			
			Assert.assertNotNull(appVersionDataParts.getValue());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getValue()));

			if(appVersionDataParts.hasExpires()) 
			{
				Assert.assertTrue(appVersionDataParts.getExpires().getTime() > YEAR_OF_2000);
			}
		}
	}
}