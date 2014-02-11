
package com.millicom.asynctasks;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import android.os.AsyncTask;
import android.util.Log;
import com.mitv.Consts;
import com.mitv.manager.LoginManager;



public class GetLikesTask 
	extends AsyncTask<String, Void, String> 
{
	private static final String	TAG	= "GetLikesTask";
	
	
	
	@Override
	protected String doInBackground(String... params) 
	{
		try 
		{
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

			HttpGet httpGet = new HttpGet();
			httpGet.setHeader("Authorization", "Bearer " + params[0]);
			httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
			httpGet.setURI(new URI(Consts.URL_LIKES_WITH_UPCOMING));

			HttpResponse response = httpClient.execute(httpGet);
			
			if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode())
			{
				HttpEntity entityHttp = response.getEntity();
				
				InputStream inputStream = entityHttp.getContent();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				
				StringBuilder sb = new StringBuilder();
				
				String line = null;
				
				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
				
				inputStream.close();
				
				return sb.toString();
			} 
			else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode())
			{
				Log.d(TAG, "Get My Likes: Invalid token");
				LoginManager.forceLogin();
				
				return Consts.ERROR_STRING;
			}
			else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode())
			{
				Log.d(TAG, "Get My Likes: Missing token");
				LoginManager.forceLogin();
				
				return Consts.ERROR_STRING;
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
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		
		return Consts.ERROR_STRING;
	}
}
