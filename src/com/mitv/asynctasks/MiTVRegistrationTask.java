
package com.mitv.asynctasks;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.mitv.Consts;
import com.mitv.utilities.JSONUtilities;



public class MiTVRegistrationTask extends AsyncTask<String, Void, String> 
{
	private static final String TAG = MiTVRegistrationTask.class.getName();
	
	
	
	@Override
	protected String doInBackground(String... params) 
	{
		try 
		{
			// HttpClient client = new DefaultHttpClient();
			HttpClient client = new DefaultHttpClient();
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			registry.register(new Scheme("https", socketFactory, 443));
			SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);

			DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
			
			// Set verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			HttpPost httpPost = new HttpPost(Consts.URL_REGISTER);

			JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.API_EMAIL, Consts.API_PASSWORD,
					Consts.API_FIRSTNAME, Consts.API_LASTNAME), Arrays.asList(params[0], params[1], params[2], params[3]));

			StringEntity entity = new StringEntity(holder.toString());

			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// HttpResponse response = client.execute(httpPost);
			HttpResponse response = client.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE)
			{
				String responseBody = EntityUtils.toString(response.getEntity());
				
				return responseBody;
			} 
			else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE)
			{
				Log.d(TAG, "Invalid Token!");

				// Get response string (reason) from bad response
				HttpEntity httpentity = response.getEntity();
				
				return Consts.EMPTY_STRING;
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return Consts.EMPTY_STRING;
	}
}