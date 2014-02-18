
package com.mitv.asynctasks;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import android.os.AsyncTask;
import android.util.Log;
import com.mitv.Consts;



public class DeleteLikeTask
	extends AsyncTask<String, Void, Integer>
{
	private static final String	TAG	= "DeleteLikeTask";
	
	
	
	@Override
	protected Integer doInBackground(String... params) 
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
			
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			Log.d(TAG, Consts.URL_LIKES + "/" + params[1] + "/" + params[2]);
			
			HttpDelete httpDelete = new HttpDelete(Consts.URL_LIKES + "/" + params[1] + "/" + params[2]);
			
			httpDelete.setHeader("Authorization", "Bearer " + params[0]);

			HttpResponse response = httpClient.execute(httpDelete);

			return response.getStatusLine().getStatusCode();
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
		
		return Consts.BAD_RESPONSE;
	}
}
