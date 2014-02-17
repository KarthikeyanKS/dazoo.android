package com.mitv.test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCore;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts;

public class Tests 
	extends InstrumentationTestCase 
{		
	public static final long TIMESTAMP_OF_YEAR_2000 = 946684800L;
	
	
	
	public HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters,
			final String bodyContentData)
	{
		HTTPCore httpCore = HTTPCore.sharedInstance();
		
		HTTPCoreResponse httpCoreResponse = httpCore.executeRequest(httpRequestType, url, urlParameters, headerParameters, bodyContentData);
		
		return httpCoreResponse;
	}
	
	
	
	public String deserializeJson(String url) 
	{
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
	
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-type", "application/json");

			HttpResponse response = client.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) 
			{
				HttpEntity entity = response.getEntity();
				
				String jsonString = null;
				
				if (entity != null) 
				{
					InputStream instream = entity.getContent();
					
					jsonString = NetworkUtils.convertStreamToString(instream, Charset.forName("UTF-8"));
					
					instream.close();
				}

				return jsonString;
			} 
			else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE)
			{
				Log.d("Tests http stuff", "bad response");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		return null;
	}
}
