package com.mitv.test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.w3c.dom.Text;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.TVDates;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts;

import android.test.InstrumentationTestCase;
import android.util.Log;

public class Tests extends InstrumentationTestCase {
		
	public static final long TIMESTAMP_OF_YEAR_2000 = 946684800L;
	
	public String deserializeJson(String url) {
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
